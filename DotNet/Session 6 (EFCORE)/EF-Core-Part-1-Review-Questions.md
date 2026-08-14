# EF Core Part 1 — Review Questions

20 questions covering fundamentals, `DbContext`, migrations, CRUD, and the change tracker. Mixed format: conceptual, scenario/tradeoff, and "what's wrong with this code" — modeled on the kind of questions that actually come up in a junior .NET interview, not just recall.

---

**1. What is an ORM, and what specific problems does it solve that raw ADO.NET doesn't?**

An ORM maps C# classes to database tables and properties to columns, so that mapping is declared once instead of hand-written in every query. Concretely, it removes three recurring sources of bugs from raw ADO.NET: manually building SQL strings (typo-prone, not checked by the compiler), manually reading columns out of a `DataReader` by index or name (breaks silently if a column is reordered or renamed), and manually constructing objects from those values. It's worth being precise in an interview that an ORM isn't "magic instead of SQL" — it still generates SQL under the hood; the value is that your queries are checked against your C# classes instead of untyped strings.

**2. What's the difference between Code-First, Database-First, and Model-First? When would you choose Database-First over Code-First?**

Code-First starts from C# entity classes and generates the schema via migrations; Database-First starts from an existing database and scaffolds C# classes from it; Model-First (a visual designer) is largely legacy. You'd reach for Database-First specifically when EF Core is being added on top of a database that already exists and is owned by another team or system — you don't control the schema, so there's nothing to generate from your classes; the classes need to follow the schema, not the other way around.

**3. Your team is adding EF Core to a 5-year-old production database owned by another team. Which approach do you use, and why?**

Database-First. The schema already exists, is already in production, and isn't yours to redesign — Code-First's migrations assume your C# classes are the source of truth, which isn't true here. Scaffolding classes from the existing schema keeps your model an accurate reflection of what's actually there, and avoids generating a migration that tries to "fix" a schema someone else owns and depends on.

**4. What's wrong with this, and how would you fix it?**
```csharp
public class LeagueDbContext : DbContext
{
    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    {
        optionsBuilder.UseSqlServer("Server=.;Database=League;Trusted_Connection=True;");
    }
}
```
It compiles and runs, but it hardcodes a connection string directly into a class that has no business knowing about environment-specific configuration — the same string would apply in development, testing, and production, which is never actually correct. The fix is registering `DbContext` through dependency injection with `AddDbContext`, reading the connection string from `IConfiguration` (`appsettings.json`, environment variables) instead of a literal.

**5. What are EF Core conventions? Give three examples of how EF Core infers your schema with zero configuration.**

Conventions are the default mapping rules EF Core applies without any explicit configuration. Three examples: a property named `Id` (or `<ClassName>Id`) is inferred as the primary key; the table name is taken from the `DbSet<T>` property name, pluralized; column types are inferred from the CLR type (`string` → `nvarchar`, `int` → `int`). Conventions cover the common case — anything they get wrong for a specific model is overridden with Data Annotations or the Fluent API (Part 2).

**6. Explain both of these, and whether one is "more correct":**
```csharp
public DbSet<Player> Players { get; set; }
public DbSet<Player> Players => Set<Player>();
```
Both work and both are valid EF Core conventions. The first is the older, more common style — a plain auto-property. The second avoids exposing a public setter that nobody should actually call, since replacing a `DbSet<T>` at runtime isn't a meaningful operation. Neither is "more correct" — the right answer in an interview is that a real codebase should pick one and stay consistent, not relitigate it on every new entity.

**7. Why is `DbContext` registered as `Scoped` by default? What could go wrong with `Singleton`? With `Transient`?**

`Scoped` means one instance per request (or per unit of work), matching how `DbContext` actually behaves: it's not thread-safe, and it accumulates change-tracking state as it's used. `Singleton` would share one instance across every concurrent request for the app's entire lifetime — a genuine thread-safety violation, and one request's tracked entities would leak into another's. `Transient` (a new instance every time something asks for one) would mean two services within the *same* request each get their own separate change tracker and can't see each other's pending changes, breaking the "one coherent unit of work per request" model.

**8. A background service running on a timer needs to query the database, not per-request. What's the lifetime problem, and what are your two options?**

A background service isn't scoped to an HTTP request, so it can't simply inject a `Scoped LeagueDbContext` — there's no request to scope it to. The two options: inject `IServiceScopeFactory` and manually create a scope per unit of work, or inject `IDbContextFactory<LeagueDbContext>` (registered via `AddDbContextFactory`) and call `CreateDbContextAsync()` to get a fresh, independent instance whenever needed. The factory approach is generally the cleaner fit and is revisited in Part 3 as a concrete example of the Factory pattern.

**9. A developer registers a service depending on `LeagueDbContext` as `Singleton` by mistake. What happens?**

In development, ASP.NET Core's DI container validates service lifetimes at startup and throws an `InvalidOperationException` immediately — "Cannot consume scoped service ... from singleton" — rather than letting the app run. This is deliberate: a `Singleton` would capture the very first request's `DbContext` and share that same stale instance, and its change tracker, across every subsequent request for the life of the app, which is exactly the concurrency danger `Scoped` exists to prevent.

**10. What are migrations, and why does EF Core generate both `Up()` and `Down()`?**

A migration is a generated, versioned description of a schema change, keeping the database schema in sync with the evolving entity classes. `Up()` applies the migration; `Down()` reverses it, letting you roll a database back to its previous schema state if a migration turns out to be wrong. Both are generated from the same model diff automatically, so you're never expected to hand-write the reverse operation yourself — and they can't silently drift out of sync with each other the way a hand-written rollback script could.

**11. What's the purpose of the `__EFMigrationsHistory` table? What breaks without it?**

It records which migrations have already been applied to a given database. Without it, `dotnet ef database update` would have no way to know whether a migration like `InitialCreate` had already run against this specific database — reapplying it would try to create tables that already exist and fail outright. The history table is what makes migrations idempotent and safe to run repeatedly across environments (a fresh dev machine, a CI pipeline, production).

**12. Two developers each add a migration on separate branches, touching different tables, then merge both. What happens when `dotnet ef database update` runs? What if they'd touched the same table incompatibly?**

Both migrations apply in whatever order they're merged and run, and each is recorded independently in the history table — if they touch different tables, this is generally uneventful. If both migrations modify the same part of the schema in incompatible ways, you get a real conflict — one migration's `Up()` may fail against the state the other already left the database in. This is a genuine, common source of merge pain on a team, and part of why migrations should be small, frequent, and reviewed rather than large and infrequent.

**13. You added a property, generated a migration, then spotted a mistake before ever running `database update`. What commands fix it — and how does that differ from fixing a mistake after the migration has already shipped to a shared database?**

Before it's applied anywhere: `dotnet ef migrations remove` deletes the unapplied migration file, you fix the model, and regenerate — no trace of the mistake remains. Once a migration has been applied to a shared or production database, it should be treated as permanent — rewriting an already-shipped migration risks that other environments have already applied the old version. Instead, you write a *new* migration that corrects the mistake going forward.

**14. What's wrong with this, and when does the `Id` actually get populated?**
```csharp
var player = new Player { Name = "Salah", JerseyNumber = 10 };
_dbContext.Players.Add(player);
Console.WriteLine(player.Id);   // expecting the generated Id here
```
`Add` only stages the entity in the change tracker as `Added` — nothing is sent to the database until `SaveChangesAsync()` actually runs, and that's also the exact point EF Core assigns the database-generated `Id` back onto the object. Reading `player.Id` before `await _dbContext.SaveChangesAsync()` reads whatever default value the property had (`0` for an `int`), not a real database-assigned id.

**15. Explain the difference between `FindAsync` and `Where(...).FirstOrDefaultAsync()`. When does each hit the database?**

`FindAsync` is specifically a primary-key lookup, and it checks the `DbContext`'s in-memory change tracker *first* — if the entity was already loaded earlier in this same context's lifetime, it's returned instantly with zero database round trips. `Where(...).FirstOrDefaultAsync()` is an ordinary LINQ query and always translates to SQL and hits the database, regardless of whether a matching entity happens to already be tracked. `Find` only works for primary-key lookups; it can't be used for an arbitrary filter.

**16. Is this `Update()` call actually necessary?**
```csharp
var player = await _dbContext.Players.FindAsync(playerId);
player.Goals += 1;
_dbContext.Players.Update(player);   // <-- necessary?
await _dbContext.SaveChangesAsync();
```
No — and calling it here is at best redundant, at worst can cause EF Core to mark *every* property as modified rather than just the one that actually changed (since `Update()` is meant for reattaching a previously-detached entity, not for an already-tracked one). Because `player` came from `FindAsync` on this same `DbContext`, it's already tracked; mutating its property is enough — the change tracker compares the current values against its original snapshot and generates the right `UPDATE` on `SaveChangesAsync` without any explicit call.

**17. What are the five entity states, and what does `SaveChangesAsync` actually do with them?**

`Added`, `Unchanged`, `Modified`, `Deleted`, `Detached`. `SaveChangesAsync` doesn't re-scan every property of every entity in the database — it asks the change tracker for everything currently in `Added`, `Modified`, or `Deleted` state, and generates exactly the SQL those states imply (`INSERT`, `UPDATE`, `DELETE` respectively). `Unchanged` and `Detached` entities are skipped entirely.

**18. Why is `DbContext` described as implementing the Unit of Work pattern? Give an example.**

A single `DbContext` instance accumulates every `Add`, every tracked mutation, and every `Remove` across its lifetime, and one call to `SaveChangesAsync()` commits all of them together, in one transaction, atomically — either everything succeeds or nothing does. Example: renaming a team, adding a new signing, and removing a retired player in the same `DbContext`, then calling `SaveChangesAsync()` once — all three changes commit together as a single unit, even though they're three logically different operations.

**19. Does this line produce an `UPDATE` statement? Why or why not?**
```csharp
player.Goals = player.Goals;
```
No. EF Core's change tracker compares a property's current value against the original snapshot it took when the entity was loaded — not against "was a setter called." Since the value didn't actually change, the entity's state doesn't flip to `Modified`, and `SaveChangesAsync` generates no `UPDATE` for it at all.

**20. Walk through, step by step, what happens from `new Player()` to a row appearing in the database.**

`new Player()` just creates a plain C# object — at this point it's `Detached`, completely unknown to any `DbContext`. Calling `_dbContext.Players.Add(player)` registers it with the change tracker and flips its state to `Added`, but still nothing has touched the database. Calling `await _dbContext.SaveChangesAsync()` is the point everything actually happens: EF Core asks the change tracker for everything in `Added`/`Modified`/`Deleted` state, generates the corresponding SQL (an `INSERT` here), executes it inside an implicit transaction (Part 3, §1.1), and — specifically for an added entity — writes the database-generated `Id` back onto the object. After that call completes, the entity's state flips to `Unchanged`, now reflecting the row that actually exists in the database.

---

*Pairs with EF Core Part 1 — Fundamentals: Mapping, DbContext & CRUD.*
