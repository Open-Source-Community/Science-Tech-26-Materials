# EF Core Part 1 — Fundamentals: Mapping, DbContext & CRUD

**Topics:** Why an ORM · Project Setup · Entities & `DbContext` · Dependency Injection · Migrations · Basic CRUD · The Change Tracker.
A special thanks to **Poula Saber** for explaining this session.

---

## Table of Contents

1. [Why an ORM, and Why EF Core](#1-why-an-orm-and-why-ef-core)
2. [Setting Up an EF Core Project](#2-setting-up-an-ef-core-project)
3. [Entities and `DbContext`](#3-entities-and-dbcontext)
4. [Registering `DbContext` with Dependency Injection](#4-registering-dbcontext-with-dependency-injection)
5. [Migrations](#5-migrations)
6. [Basic CRUD Operations](#6-basic-crud-operations)
7. [The Change Tracker](#7-the-change-tracker)
8. [Quick Reference — Glossary](#8-quick-reference--glossary)

---

## 1. Why an ORM, and Why EF Core

### 1.1 The Problem Before an ORM

Without any tooling, talking to a database from C# means writing raw SQL as strings, manually opening a connection, manually reading each column out of a `DataReader` by index or name, and manually constructing an object from those values.

```csharp
// WITHOUT an ORM — every step is manual, and every step is a place to introduce a bug
using var connection = new SqlConnection(connectionString);
connection.Open();

using var command = new SqlCommand("SELECT Id, Name, JerseyNumber FROM Players WHERE TeamId = @teamId", connection);
command.Parameters.AddWithValue("@teamId", teamId);

using var reader = command.ExecuteReader();
var players = new List<Player>();
while (reader.Read())
{
    players.Add(new Player
    {
        Id = reader.GetInt32(0),
        Name = reader.GetString(1),
        JerseyNumber = reader.GetInt32(2)
    });
}
```

Every query repeats this same shape: build SQL, bind parameters, read columns back by position, construct an object. None of it is checked by the compiler — a renamed column or a typo in the SQL string only surfaces at runtime, and only if that exact code path gets exercised.

### 1.2 What an ORM Actually Does

An **ORM (Object-Relational Mapper)** maps C# classes to database tables and C# properties to columns, so the mapping above is written once (as configuration) instead of by hand in every query. EF Core specifically also:

- Translates LINQ queries into SQL (exactly the `IQueryable<T>` mechanism from session 5, Section 7 — this guide assumes that section already makes sense)
- Tracks which objects came from the database and what's changed on them, so it knows what SQL to generate when you save
- Manages schema changes over time through migrations (Section 5)

**Why this matters in an interview:** an ORM isn't "magic that replaces SQL." It's still generating SQL under the hood — the value is that the mapping between your objects and the schema is declared once, and the compiler checks your queries against your C# classes instead of against untyped strings.

### 1.3 Code-First, Database-First, and Model-First

| Approach | Starting point | How the schema is produced |
|---|---|---|
| Code-First | C# entity classes | EF Core generates migrations from your classes |
| Database-First | An existing database | Tooling scaffolds C# classes from the schema |
| Model-First | A visual designer | Largely legacy, rarely used with EF Core |

This series uses **Code-First**, because it's the default a new project reaches for and it keeps the C# classes as the source of truth — the same mental model as everything in the OOP guide, where a class defines what an object *is*. Database-First is common when EF Core is added on top of a database that already exists and is owned by another team or system.

---

## 2. Setting Up an EF Core Project

### 2.1 NuGet Packages

A typical Code-First setup needs three packages:

```
Microsoft.EntityFrameworkCore
Microsoft.EntityFrameworkCore.SqlServer      (or .Sqlite, .Npgsql for PostgreSQL, etc.)
Microsoft.EntityFrameworkCore.Tools           (enables the dotnet ef CLI commands)
```

**Why the provider is a separate package:** EF Core's core package knows how to track changes, build expression trees, and manage a model, but it has no idea how to talk to any specific database engine. The provider package (SQL Server, SQLite, PostgreSQL, and others) is what translates that provider-neutral model into the specific SQL dialect and connection behavior a given database needs. This is the same separation of concerns as an interface (Session 2, Section 2) describing a capability while a specific implementation provides it — `DbContext` describes the capability, the provider supplies the implementation.

### 2.2 The `dotnet ef` CLI Tool

```
dotnet tool install --global dotnet-ef
dotnet ef --version
```

This is a separate command-line tool from the `Microsoft.EntityFrameworkCore.Tools` package — the package hooks into the build so the CLI can find your `DbContext`; the CLI itself is what you actually run from the terminal to create and apply migrations (Section 5).

---

## 3. Entities and `DbContext`

### 3.1 Entity Classes Are Just POCOs

An **entity** is an ordinary C# class — no base class, no required interface. EF Core calls these **POCOs** (Plain Old CLR Objects).

```csharp
public class Player
{
    public int Id { get; set; }
    public string Name { get; set; } = "";
    public int JerseyNumber { get; set; }
    public int Goals { get; set; }
}
```

**Why no base class is required:** unlike some older ORMs that forced entities to inherit from a framework base class, EF Core deliberately keeps entities as plain classes so they stay usable outside a database context entirely — the same `Player` class can be constructed in a unit test, serialized to JSON (Session 3, Section 2), or passed through ordinary LINQ to Objects (Session 5) without ever touching a database.

### 3.2 Conventions — How EF Core Infers the Schema

Without any configuration at all, EF Core follows a set of default rules called **conventions**:

| Convention | Rule |
|---|---|
| Primary key | A property named `Id`, or `<ClassName>Id`, is treated as the primary key |
| Table name | The `DbSet<T>` property's name (see 3.3), pluralized |
| Column name | The property name, unchanged |
| Column type | Inferred from the CLR type (`string` → `nvarchar`, `int` → `int`, and so on) |
| Nullability | A non-nullable reference/value type becomes a `NOT NULL` column |

Conventions cover the common case. Section 2 of Part 2 covers overriding them with Data Annotations and the Fluent API for anything a convention gets wrong for your model.

### 3.3 `DbContext` and `DbSet<T>`

```csharp
public class LeagueDbContext : DbContext
{
    public LeagueDbContext(DbContextOptions<LeagueDbContext> options) : base(options) { }

    public DbSet<Player> Players => Set<Player>();
    public DbSet<Team> Teams => Set<Team>();
}
```

`DbContext` represents a **session** with the database — a unit of work (a concept this guide returns to explicitly in Part 3) that tracks the objects you've loaded or changed and turns them into SQL when you ask it to save. `DbSet<Player>` is the queryable, addressable handle to the `Players` table — this is exactly the `IQueryable<T>` from Session 5, Section 7: writing `dbContext.Players.Where(p => p.Goals > 20)` builds an expression tree, not a loop, and nothing executes until it's enumerated or awaited.

>**Q: Why is `Players` a property returning `Set<Player>()` instead of a plain auto-property?**
Both work — `public DbSet<Player> Players { get; set; }` is the more common, older style, and EF Core still supports it. The `Set<Player>()` version is a newer, equally valid convention that avoids a public setter nobody should actually call; either is acceptable, and a real codebase should just pick one and stay consistent, the same "pick a convention and don't relitigate it" instinct as choosing method syntax over query syntax in LINQ Part 1, Section 2.3.

### 3.4 Configuring the Connection — `OnConfiguring` vs Dependency Injection

```csharp
// Works, but hardcodes the connection string inside the DbContext itself
public class LeagueDbContext : DbContext
{
    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    {
        optionsBuilder.UseSqlServer("Server=.;Database=League;Trusted_Connection=True;");
    }
}
```

This compiles and runs, but it hardcodes configuration that should come from the environment (a different connection string in development, testing, and production) directly into a class that has no business knowing about environment configuration. Section 4 covers the approach every real ASP.NET Core project uses instead.

---

## 4. Registering `DbContext` with Dependency Injection

```csharp
var builder = WebApplication.CreateBuilder(args);

builder.Services.AddDbContext<LeagueDbContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("League")));

var app = builder.Build();
```

`AddDbContext` registers `LeagueDbContext` with the DI container, wired up to read its connection string from configuration (`appsettings.json`, environment variables, or whatever the hosting environment provides) instead of a hardcoded literal. Anywhere in the app that needs a `LeagueDbContext` — a controller, a service — declares it as a constructor parameter, and the DI container supplies a correctly configured instance.

```csharp
public class PlayerService
{
    private readonly LeagueDbContext _dbContext;

    public PlayerService(LeagueDbContext dbContext)
    {
        _dbContext = dbContext;
    }
}
```

### 4.1 Why `DbContext` Is Registered `Scoped`

`AddDbContext` registers `LeagueDbContext` with a **Scoped** lifetime by default — one instance per HTTP request (or per background job, per unit of work), not one shared instance for the whole application, and not a brand-new one per class that asks for it within that request.

**Why this matters:** `DbContext` is genuinely not thread-safe, and it accumulates change-tracking state (Section 7) as it's used. Sharing a single instance across every request would risk concurrent access from multiple requests at once, and would mean one request's tracked entities leaking into another's. A single instance per class within one request would mean two services each start their own separate change-tracking session and can't see each other's pending changes. `Scoped` is the balance that matches how a request naturally forms one coherent unit of work.

>**Q: What happens if a background service (which isn't scoped to a request) needs a `DbContext`?**
It can't just inject a scoped `LeagueDbContext` directly, since there's no HTTP request to scope it to. Two options: inject `IServiceScopeFactory` and manually create a scope per unit of work, or inject `IDbContextFactory<LeagueDbContext>` (registered via `AddDbContextFactory`) and call `CreateDbContext()` to get a fresh instance whenever needed. Part 3, Section 4 revisits `IDbContextFactory<T>` as a factory-pattern example.

---

## 5. Migrations

### 5.1 The Problem — Keeping the Schema in Sync With the Code

As `Player` and `Team` evolve — a new property added, a type changed — the actual database schema needs to change to match. Doing this by hand (writing `ALTER TABLE` scripts, tracking which ones have run where) is exactly the kind of repetitive, error-prone bookkeeping migrations exist to automate.

### 5.2 Creating a Migration

```
dotnet ef migrations add InitialCreate
```

This inspects the current model (every entity reachable from a `DbSet<T>` on the `DbContext`) and generates a migration file with two methods:

```csharp
public partial class InitialCreate : Migration
{
    protected override void Up(MigrationBuilder migrationBuilder)
    {
        migrationBuilder.CreateTable(
            name: "Teams",
            columns: table => new
            {
                Id = table.Column<int>(nullable: false).Annotation("SqlServer:Identity", "1, 1"),
                Name = table.Column<string>(nullable: false)
            },
            constraints: table => table.PrimaryKey("PK_Teams", x => x.Id));

        // ... Players table, foreign key constraints, and so on
    }

    protected override void Down(MigrationBuilder migrationBuilder)
    {
        migrationBuilder.DropTable(name: "Teams");
        // ... the exact reverse of Up()
    }
}
```

**Why both `Up()` and `Down()` exist:** `Up()` is what runs to apply the migration; `Down()` is what runs to undo it, letting you roll a database back to its previous schema state if a migration turns out to be wrong. EF Core generates both from the same model diff, so they stay in sync automatically — you're not expected to hand-write the reverse operation yourself.

### 5.3 Applying a Migration

```
dotnet ef database update
```

This runs any migrations not yet applied to the target database, in order, and records each one in a special `__EFMigrationsHistory` table so EF Core knows exactly which migrations a given database has already seen.

**Why the history table matters:** without it, `dotnet ef database update` would have no way to know whether `InitialCreate` had already run against this specific database — reapplying it would try to create tables that already exist and fail. The history table is what makes migrations idempotent and safe to run repeatedly across environments.

### 5.4 Rolling Back and Regenerating During Development

```
dotnet ef database update PreviousMigrationName   # roll the DATABASE back to an earlier migration
dotnet ef migrations remove                          # delete the LATEST migration FILE (only if unapplied, or after rolling the database back first)
```

**A realistic development pattern:** while a model is still being iterated on early in a project, it's common to add a property, realize the migration needs a small fix, remove the just-added migration file, adjust the model, and regenerate — rather than accumulating a long chain of small corrective migrations. Once a migration has shipped to a shared or production database, it should be treated as permanent; fix mistakes with a new migration instead of rewriting history.

>**Q: What happens if two developers each add a migration on separate branches?**
Both migrations apply in whatever order they're merged and run, and each is recorded independently in the history table. Conflicts are possible if both migrations touch the same part of the schema in incompatible ways — this is a real, common source of merge pain on a team, and part of why migrations should be small, frequent, and reviewed rather than large and infrequent.

---

## 6. Basic CRUD Operations

### 6.1 Create

```csharp
var player = new Player { Name = "Salah", JerseyNumber = 10, Goals = 32 };

_dbContext.Players.Add(player);          // marks it as a new entity to insert (Section 7)
await _dbContext.SaveChangesAsync();      // this is the point the INSERT actually runs

_dbContext.Players.AddRange(playerA, playerB);   // adding several at once
await _dbContext.SaveChangesAsync();
```

**Why `Add` alone doesn't touch the database:** exactly like a deferred LINQ query (Session 4, Section 3), calling `Add` only registers intent — it stages the entity in the change tracker. Nothing is sent to the database until `SaveChangesAsync()` runs, which is also the point EF Core assigns the new, database-generated `Id` back onto the object.

### 6.2 Read

```csharp
// Find — looks up by primary key; checks the change tracker FIRST before querying the database
Player? player = await _dbContext.Players.FindAsync(playerId);

// Ordinary LINQ query — this is the exact same IQueryable<T> mechanism from LINQ Part 2
Player? topScorer = await _dbContext.Players
    .Where(p => p.TeamId == teamId)
    .OrderByDescending(p => p.Goals)
    .FirstOrDefaultAsync();
```

**Why `Find`/`FindAsync` is worth knowing specifically:** unlike a `Where`/`FirstOrDefault` query, `Find` checks the `DbContext`'s in-memory change tracker for an entity with that key *before* issuing any SQL at all. If the entity was already loaded earlier in this same `DbContext`'s lifetime, `Find` returns it instantly with zero database round trips. This only works for primary-key lookups, not arbitrary filters.

### 6.3 Update

```csharp
Player? player = await _dbContext.Players.FindAsync(playerId);
if (player is not null)
{
    player.Goals += 1;                    // just an ordinary property mutation
    await _dbContext.SaveChangesAsync();   // EF Core notices the change and generates an UPDATE
}
```

**Why there's no explicit `Update()` call here:** because `player` came from this `DbContext` (via `FindAsync`), it's already being tracked (Section 7). Mutating a tracked entity's property is enough — the change tracker compares the entity's current values against the snapshot it took when the entity was loaded, and `SaveChangesAsync` generates an `UPDATE` for whatever actually changed.

### 6.4 Delete

```csharp
Player? player = await _dbContext.Players.FindAsync(playerId);
if (player is not null)
{
    _dbContext.Players.Remove(player);
    await _dbContext.SaveChangesAsync();
}
```

---

## 7. The Change Tracker

### 7.1 Entity States

Every entity a `DbContext` knows about is in exactly one of five states:

| State | Meaning |
|---|---|
| `Added` | New; will produce an `INSERT` on `SaveChanges` |
| `Unchanged` | Loaded and tracked, nothing has changed since |
| `Modified` | Loaded, tracked, and at least one property has changed; will produce an `UPDATE` |
| `Deleted` | Marked for removal; will produce a `DELETE` |
| `Detached` | Not tracked by this `DbContext` at all |

```csharp
var player = new Player { Name = "Marmoush", JerseyNumber = 11 };
Console.WriteLine(_dbContext.Entry(player).State);   // Detached — not yet added to the context

_dbContext.Players.Add(player);
Console.WriteLine(_dbContext.Entry(player).State);   // Added

await _dbContext.SaveChangesAsync();
Console.WriteLine(_dbContext.Entry(player).State);   // Unchanged — saved, and now the new baseline
```

**Why this is worth understanding, not just memorizing:** `SaveChangesAsync` doesn't re-scan every property of every entity in the database to figure out what to do. It asks the change tracker for everything in `Added`, `Modified`, or `Deleted` state, and generates exactly the SQL those states imply. This is the mechanism behind Section 6.3's "no explicit `Update()` call" — the state transition to `Modified` happens automatically the moment a tracked property's value differs from its original snapshot.

### 7.2 Why `DbContext` Is a Unit of Work

A single `DbContext` instance, across its lifetime, accumulates every `Add`, every mutation to a tracked entity, and every `Remove` — and a single call to `SaveChangesAsync()` commits all of them together, in one database transaction, atomically. This is precisely the **Unit of Work** design pattern: a way of grouping several individual operations so they either all succeed together or all fail together, rather than each one committing independently.

```csharp
var team = await _dbContext.Teams.FindAsync(teamId);
team!.Name = "Al Ahly SC";                                    // change 1
_dbContext.Players.Add(new Player { Name = "New Signing" });   // change 2
_dbContext.Players.Remove(retiredPlayer);                      // change 3

await _dbContext.SaveChangesAsync();   // all three commit together, in one transaction
```

Understanding `DbContext` as a Unit of Work up front makes Part 3's discussion of explicit transactions and the Repository/Unit-of-Work pattern much easier to reason about: `DbContext` is already doing this job. Part 3 covers when it's worth building an additional layer on top of it, and when that layer is unnecessary duplication.

>**Q: Does every property mutation get tracked, even ones that don't actually change the value?**
EF Core is precise about this: setting a property to the exact value it already holds does not flip the entity's state to `Modified` — the change tracker compares against the original snapshot, not against "was a setter called." Assigning `player.Goals = player.Goals` produces no `UPDATE` at all.

---

## 8. Quick Reference — Glossary

| Term | Meaning |
|---|---|
| ORM | Object-Relational Mapper — maps classes to tables and translates queries to SQL |
| Code-First | The database schema is generated from C# entity classes via migrations |
| Entity | A plain C# class (POCO) mapped to a database table |
| Convention | A default mapping rule EF Core applies without explicit configuration |
| `DbContext` | Represents a session with the database; tracks changes and turns them into SQL |
| `DbSet<T>` | The queryable, addressable handle to a single table, implementing `IQueryable<T>` |
| Provider | The package that translates EF Core's provider-neutral model into a specific database's SQL dialect |
| Migration | A generated, versioned description of a schema change, with `Up()` and `Down()` methods |
| `__EFMigrationsHistory` | The table EF Core uses to track which migrations have already run against a database |
| Change tracker | The mechanism that records each tracked entity's state and original values |
| Entity state | `Added` / `Unchanged` / `Modified` / `Deleted` / `Detached` |
| `SaveChanges` / `SaveChangesAsync` | Commits every tracked `Added`/`Modified`/`Deleted` entity as one database transaction |
| Unit of Work | A pattern grouping several operations so they commit or fail together as one; `DbContext` already implements this |
| `Find` / `FindAsync` | Primary-key lookup that checks the change tracker before querying the database |

---
