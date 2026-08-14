# EF Core Part 2 — Relationships, Loading Strategies & Querying

**Topics:** Data Annotations vs Fluent API · Organizing Configuration · Modeling Relationships · Delete Behavior · Value Converters & Owned Types · Loading Related Data · Querying as `IQueryable<T>` · `AsNoTracking` & Async Querying · Seeding Data

---

## Table of Contents

1. [Data Annotations vs Fluent API](#1-data-annotations-vs-fluent-api)
2. [Modeling Relationships](#2-modeling-relationships)
3. [Delete Behavior](#3-delete-behavior)
4. [Value Converters and Owned Types](#4-value-converters-and-owned-types)
5. [Loading Related Data](#5-loading-related-data)
6. [Querying as `IQueryable<T>`](#6-querying-as-iqueryablet)
7. [`AsNoTracking` and Async Querying](#7-asnotracking-and-async-querying)
8. [Seeding Data](#8-seeding-data)
9. [Quick Reference — Glossary](#9-quick-reference--glossary)

---

## 1. Data Annotations vs Fluent API

Session 7, Section 3.2 covered conventions — the defaults EF Core applies with no configuration at all. Conventions cover a lot, but not everything: a string that must have a maximum length, a property that maps to a differently-named column, a composite key. There are two ways to say more than a convention can infer.

### 1.1 Data Annotations

```csharp
public class Player
{
    public int Id { get; set; }

    [Required]
    [MaxLength(100)]
    public string Name { get; set; } = "";

    [Column("jersey_no")]
    public int JerseyNumber { get; set; }
}
```

This is the exact same mechanism as the Session 3's custom-attribute section: `[Required]`, `[MaxLength]`, and `[Column]` are ordinary C# attributes, inert metadata on their own, that EF Core reads via reflection when it builds the model — the same "attribute declares intent, something else reads it" pattern as `[JsonPropertyName]` or a hand-written `[ColumnName]` attribute.

### 1.2 Fluent API

```csharp
public class LeagueDbContext : DbContext
{
    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Player>(entity =>
        {
            entity.Property(p => p.Name).IsRequired().HasMaxLength(100);
            entity.Property(p => p.JerseyNumber).HasColumnName("jersey_no");
            entity.HasIndex(p => p.JerseyNumber);
        });
    }
}
```

`ModelBuilder` is a **Builder pattern** in the classic sense: rather than one constructor call trying to accept every possible configuration option as parameters, `OnModelCreating` assembles the model step by step through a fluent, chainable API, and only the finished result is used. The same shape shows up all over .NET — `OptionsBuilder`, `HttpRequestMessageBuilder`, and `StringBuilder` are all the same pattern applied to a different problem.

### 1.3 When Each Is Necessary

| | Data Annotations | Fluent API |
|---|---|---|
| Lives | On the entity class itself | In `OnModelCreating`, separate from the entity |
| Covers | Most common cases: required, max length, column name/type, simple keys | Everything Data Annotations can do, plus what they can't |
| Composite keys | Not possible | `entity.HasKey(p => new { p.TeamId, p.JerseyNumber })` |
| Relationship precision (Section 2) | Limited | Full control over foreign keys, delete behavior, join tables |
| Keeping entities free of EF-specific references | No — the entity now depends on `System.ComponentModel.DataAnnotations` | Yes — the entity stays a plain POCO; all EF-specific configuration lives in the `DbContext` |

**Why a real project often prefers Fluent API even where an attribute would work:** the last row is the crux of it. An entity decorated with `[Required]`, `[Column]`, and friends still compiles and runs fine outside of EF Core, but it now visibly depends on an EF-adjacent namespace for something that's arguably persistence configuration, not a fact about the domain object itself. Keeping that configuration entirely in `OnModelCreating` keeps the entity classes honestly plain, which matters more as a codebase grows and entities get reused in contexts (tests, other layers) that shouldn't need to care how they're persisted.

**Q: What happens if the same thing is configured both ways?**

>Fluent API configuration always wins over a conflicting Data Annotation. This is rarely intentional and usually a sign the two were set inconsistently by different people at different times — worth treating as a code smell to clean up, not a feature to rely on.

### 1.4 Organizing Fluent API With `IEntityTypeConfiguration<T>`

A single `OnModelCreating` method configuring every entity in a real application — dozens of entities, each with several `Property`/`HasOne`/`HasIndex` calls — quickly becomes a very long method that's hard to navigate and prone to merge conflicts when several people touch the model in the same sprint. EF Core provides a way to split that configuration into one class per entity:

```csharp
public class PlayerConfiguration : IEntityTypeConfiguration<Player>
{
    public void Configure(EntityTypeBuilder<Player> builder)
    {
        builder.Property(p => p.Name).IsRequired().HasMaxLength(100);
        builder.Property(p => p.JerseyNumber).HasColumnName("jersey_no");
        builder.HasIndex(p => p.JerseyNumber);

        builder.HasOne(p => p.Team)
            .WithMany(t => t.Players)
            .OnDelete(DeleteBehavior.Restrict);
    }
}
```

```csharp
public class LeagueDbContext : DbContext
{
    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        // Applies every IEntityTypeConfiguration<T> found in this assembly —
        // add a new configuration class and it's picked up automatically, nothing to register by hand
        modelBuilder.ApplyConfigurationsFromAssembly(typeof(LeagueDbContext).Assembly);

        // Or, for just one or two entities:
        // modelBuilder.ApplyConfiguration(new PlayerConfiguration());
    }
}
```

**Why this is the shape most real projects converge on:** it's the same motivation as separating a large class into smaller, single-responsibility ones — each `IEntityTypeConfiguration<T>` implementation owns exactly one entity's mapping, is independently readable, and can be found by filename instead of scrolled to inside a hundred-line method. `ApplyConfigurationsFromAssembly` means adding a new entity's configuration is just adding a new file — nothing else to wire up or remember to call.

**Q: Does this change what gets configured, or just where it's written?**

>Just where it's written. `IEntityTypeConfiguration<T>` is purely an organizational tool — everything it does is exactly the same Fluent API from Section 1.2, just called from `Configure(builder)` instead of directly inside `OnModelCreating`. For a small teaching project with two or three entities, putting everything directly in `OnModelCreating` (as the rest of this guide does, for brevity) is completely reasonable; this section is about what changes once a model has real size.

---

## 2. Modeling Relationships

### 2.1 One-to-Many

The most common relationship: one `Team` has many `Player`s, and each `Player` belongs to exactly one `Team`.

```csharp
public class Team
{
    public int Id { get; set; }
    public string Name { get; set; } = "";

    public List<Player> Players { get; set; } = new();   // navigation property: "the many side"
}

public class Player
{
    public int Id { get; set; }
    public string Name { get; set; } = "";

    public int TeamId { get; set; }        // foreign key
    public Team Team { get; set; } = null!; // navigation property: "the one side"
}
```

**Why both a foreign key property and a navigation property:** `TeamId` is the actual column EF Core uses to enforce the relationship at the database level. `Team`/`Players` are **navigation properties** — they let you traverse the relationship in C# (`player.Team.Name`, `team.Players.Count`) without writing a join yourself. EF Core can usually infer the whole relationship from the navigation properties alone (this is convention-based, like Session 6, Section 3.2), but declaring the foreign key explicitly makes the relationship unambiguous and gives you a property to filter or sort on without loading the related entity at all.

### 2.2 One-to-One

```csharp
public class Player
{
    public int Id { get; set; }
    public string Name { get; set; } = "";

    public PlayerContract? Contract { get; set; }
}

public class PlayerContract
{
    public int Id { get; set; }
    public int PlayerId { get; set; }   // also the foreign key AND (via configuration) the shared primary key
    public Player Player { get; set; } = null!;
    public decimal Salary { get; set; }
}
```

```csharp
modelBuilder.Entity<Player>()
    .HasOne(p => p.Contract)
    .WithOne(c => c.Player)
    .HasForeignKey<PlayerContract>(c => c.PlayerId);
```

**Why one-to-one needs more explicit configuration than one-to-many:** EF Core's conventions are built around the much more common one-to-many shape, so a genuine one-to-one relationship — where the dependent side's key both identifies its own row and points back to its principal — usually needs `HasOne().WithOne()` spelled out explicitly rather than inferred.

### 2.3 Many-to-Many

```csharp
public class Player
{
    public int Id { get; set; }
    public List<Award> Awards { get; set; } = new();
}

public class Award
{
    public int Id { get; set; }
    public string Name { get; set; } = "";
    public List<Player> Winners { get; set; } = new();
}
```

Since EF Core 5, this compiles and works with **no explicit join entity at all** — EF Core creates a hidden join table behind the scenes automatically, because both sides just declare a `List<T>` of the other.

**When an explicit join entity is still necessary:** the moment the relationship itself needs its own data — for example, *which season* a player won an award — a plain implicit join table has nowhere to put that column, and an explicit join entity is required:

```csharp
public class PlayerAward
{
    public int PlayerId { get; set; }
    public Player Player { get; set; } = null!;

    public int AwardId { get; set; }
    public Award Award { get; set; } = null!;

    public int Season { get; set; }   // extra data ON the relationship itself
}
```

```csharp
modelBuilder.Entity<PlayerAward>().HasKey(pa => new { pa.PlayerId, pa.AwardId });   // composite key
```

**Q: How do you decide between the implicit and explicit join entity?**

>If the relationship is purely "these two things are associated," the implicit join table is simpler and does the job. The instant you find yourself wanting to store any fact *about* the association rather than about either side of it, that's the signal you need an explicit join entity with its own properties — the same "does this concept need its own identity and data, or is it just a link" judgment call as choosing between composition and a simple reference in the OOP guide.

---

## 3. Delete Behavior

```csharp
modelBuilder.Entity<Player>()
    .HasOne(p => p.Team)
    .WithMany(t => t.Players)
    .OnDelete(DeleteBehavior.Restrict);
```

| `DeleteBehavior` | What happens when the "one" side is deleted |
|---|---|
| `Cascade` | Every related row is deleted too (the EF Core default for required relationships) |
| `Restrict` | The delete is blocked at the database level while related rows still exist |
| `SetNull` | The foreign key on related rows is set to `NULL` (only valid if the foreign key is nullable) |
| `NoAction` | No automatic behavior; the database enforces whatever constraint exists, or nothing does |

**Why the default (`Cascade`) is often the wrong choice to leave unexamined:** deleting a `Team` and silently deleting every `Player` on it, every `PlayerContract`, and everything else that cascades from there, is rarely what was actually intended — it's an easy way to lose data no one meant to lose. Explicitly choosing `Restrict` for relationships where an accidental cascade would be dangerous, and reserving `Cascade` for relationships where the dependent genuinely has no meaning without its parent (deleting a `Team` should probably also delete season-specific `TeamStanding` rows tied only to that team, for instance), is a deliberate design decision, not something to leave on autopilot.

**Q: Where does `DeleteBehavior` actually get enforced — C# or the database?**

>The database. `OnDelete(...)` configures the foreign key constraint itself (`ON DELETE CASCADE`/`ON DELETE RESTRICT` in SQL), so the behavior holds even for a delete issued outside of EF Core entirely, not just ones that go through `DbContext.Remove`.

---

## 4. Value Converters and Owned Types

Not everything worth modeling maps cleanly onto "one property, one column of a matching type." This section covers two closely related tools for the cases where it doesn't.

### 4.1 Value Converters — `HasConversion`

```csharp
public enum PlayerStatus { Active, Injured, Suspended }

public class Player
{
    public int Id { get; set; }
    public PlayerStatus Status { get; set; }
}
```

By convention, EF Core stores an enum as its underlying `int` value — which works, but produces a column full of `0`, `1`, `2` that means nothing without the C# enum definition open next to it in another window. A value converter changes what's actually stored without changing the C# property's type at all:

```csharp
modelBuilder.Entity<Player>()
    .Property(p => p.Status)
    .HasConversion<string>();   // stores "Active" / "Injured" / "Suspended" instead of 0 / 1 / 2
```

A conversion can be arbitrary, not just enum-to-string — a `ValueConverter<TModel, TProvider>` is really just a pair of lambdas describing how to go from the C# type to whatever the database stores, and back:

```csharp
var moneyConverter = new ValueConverter<decimal, long>(
    salary => (long)(salary * 100),      // C# → database: store as integer cents
    cents => cents / 100m);              // database → C# : convert back to decimal

modelBuilder.Entity<PlayerContract>()
    .Property(c => c.Salary)
    .HasConversion(moneyConverter);
```

**Why bother, instead of just storing whatever type is convenient in the database directly:** the C# side of the model gets to stay expressive and type-safe (`PlayerStatus.Injured`, a `decimal` salary) while the database side gets whatever representation is actually appropriate for storage, indexing, or interoperability with other systems reading that same column. Neither side has to compromise for the other.

### 4.2 Owned Types — Modeling Value Objects

Some concepts genuinely belong to an entity but don't deserve to be a full entity themselves — they have no independent identity or lifetime apart from their owner. An address is the textbook example: a `Player`'s home address isn't a "thing" queried on its own, it's just a cluster of fields that belong to exactly one `Player`.

```csharp
public class Address
{
    public string Street { get; set; } = "";
    public string City { get; set; } = "";
    public string Country { get; set; } = "";
}

public class Player
{
    public int Id { get; set; }
    public string Name { get; set; } = "";
    public Address HomeAddress { get; set; } = new();
}
```

```csharp
modelBuilder.Entity<Player>().OwnsOne(p => p.HomeAddress);
```

By default, `OwnsOne` stores the owned type's properties as ordinary columns on the *same* table (`HomeAddress_Street`, `HomeAddress_City`, `HomeAddress_Country`) — no separate table, no foreign key, no independent primary key. `Address` has no `Id` of its own because it doesn't need one: it can't exist without a `Player`, and it's never looked up except through one.

**Why this is different from a one-to-one relationship (Section 2.2):** a one-to-one related entity like `PlayerContract` has its own identity and its own row — it could, conceptually, be queried, updated, or deleted independently. An owned type like `Address` can't be tracked, saved, or deleted on its own at all; it only ever exists as part of its owner, and `SaveChangesAsync` treats changes to it as changes to the `Player` row itself.

**Q: When is something an owned type versus its own entity with a one-to-one relationship?**

>Ask whether the concept has any meaning or lifetime independent of its owner. If it would ever need to be queried directly, referenced from somewhere else, or outlive its "owner" being deleted, it's a real entity (Section 2.2). If it's purely a structured cluster of values that only ever makes sense attached to one specific owner — money, a date range, an address — an owned type is the better fit, and it's the same "does this need its own identity" judgment call as Section 2.3's join-entity question.

---

## 5. Loading Related Data

### 5.1 The Problem: Navigation Properties Are Empty by Default

```csharp
var team = await _dbContext.Teams.FirstAsync(t => t.Id == teamId);
Console.WriteLine(team.Players.Count);   // 0 — even if the team genuinely has players in the database!
```

By default, querying a `Team` does **not** automatically populate its `Players` navigation property. EF Core has no way to know, from `Teams.FirstAsync(...)` alone, whether the caller needs the roster or not — loading every related table on every query "just in case" would be wasteful for the (very common) case where the caller only wanted the team's name.

### 5.2 Eager Loading — `Include`/`ThenInclude`

```csharp
var team = await _dbContext.Teams
    .Include(t => t.Players)
    .FirstAsync(t => t.Id == teamId);

Console.WriteLine(team.Players.Count);   // correct now — Players was loaded in the SAME query

// ThenInclude reaches one level further — a player's contract, off an already-included player
var teamWithContracts = await _dbContext.Teams
    .Include(t => t.Players)
        .ThenInclude(p => p.Contract)
    .FirstAsync(t => t.Id == teamId);
```

`Include` tells EF Core to fetch the related data as part of the same query, via a SQL `JOIN`, rather than leaving the navigation property empty. This is precisely the fix for the N+1 problem from LINQ Part 2, Section 10.1 — one query up front instead of one query per team inside a loop.

### 5.3 Explicit Loading

```csharp
var team = await _dbContext.Teams.FirstAsync(t => t.Id == teamId);

// Load Players AFTER the fact, only if some later branch of logic actually needs it
if (needsRosterDetails)
{
    await _dbContext.Entry(team).Collection(t => t.Players).LoadAsync();
}
```

**Why this is useful over just always using `Include`:** sometimes whether related data is needed depends on logic that only runs after the initial query — explicit loading lets that decision happen later, without either over-fetching data that turns out to be unnecessary or writing two separate queries for the two branches.

### 5.4 Lazy Loading, and Why It's Usually Turned Off

Lazy loading (via proxy entities and the `Microsoft.EntityFrameworkCore.Proxies` package) makes a navigation property populate itself automatically, transparently, the first time it's accessed:

```csharp
var team = await _dbContext.Teams.FirstAsync(t => t.Id == teamId);
Console.WriteLine(team.Players.Count);   // with lazy loading enabled, this SILENTLY triggers a NEW query, right here
```
```csharp
// Step 1: Fetch 10 teams from the database
var teams = await _dbContext.Teams.ToListAsync();

// Step 2: Loop through each team
foreach (var team in teams)
{
    
    Console.WriteLine(team.Players.Count); 
}
```
**Why most real projects avoid it:** the query that runs is invisible at the call site — `team.Players.Count` looks like an ordinary in-memory property access, giving no visual signal that it's about to hit the database. This makes the N+1 problem from LINQ Part 2 dramatically easier to introduce by accident: a loop over teams, each one innocently reading `.Players`, silently fires one query per iteration with no line of code that looks like a query call at all. Eager loading (`Include`) and explicit loading both keep the database access visible in the code that requests it, which is worth the small amount of extra ceremony.

### 5.5 Projection Instead of `Include` — Revisited From LINQ Part 1

```csharp
// Loads EVERY column of every Player, just to read two of them
var team = await _dbContext.Teams.Include(t => t.Players).FirstAsync(t => t.Id == teamId);
var names = team.Players.Select(p => p.Name).ToList();

// Better — the SQL SELECT only ever asks for the columns actually used
var names2 = await _dbContext.Players
    .Where(p => p.TeamId == teamId)
    .Select(p => p.Name)
    .ToListAsync();
```

This is Session 4, Section 5.1 and Session 5, Section 10.2, now applied specifically to relationships: `Include` is the right tool when the related *entities* themselves are genuinely needed (to mutate them, to pass them to code expecting the full `Player` shape). When only a few fields off the related data are needed, projecting directly — without `Include` at all — produces a smaller, cheaper query.

**Q: How do you choose between `Include` and a projection?**

>Ask what the caller actually does with the result. If it needs to call methods on the related entities, or mutate and save them, `Include` is correct — you need the real, trackable objects. If the caller only reads a few values to display or return as a DTO, project directly and skip `Include` entirely; it's less data moved, and often a simpler generated query.

---

## 6. Querying as `IQueryable<T>`

This section is a direct continuation of SEssion 5, Section 7 — read that first if it's not fresh. Everything there about expression trees, SQL translation, and the danger of materializing too early applies exactly as written; this section is about seeing it against a real, relational schema with actual joins.

```csharp
// Still entirely IQueryable<T> — nothing has run yet
var query = _dbContext.Players
    .Where(p => p.Team!.Name == "Al Ahly SC")   // becomes part of the WHERE clause, via the join to Teams
    .OrderByDescending(p => p.Goals)
    .Skip(0)
    .Take(10);

var topTen = await query.ToListAsync();   // ONE query actually runs here, including the join and the paging
```

**Why filtering through a navigation property (`p.Team!.Name`) works at all:** EF Core's SQL translator understands navigation properties as part of the expression tree it's translating — `p.Team!.Name == "..."` becomes a join to `Teams` plus a `WHERE` on its `Name` column, entirely inside the generated SQL, without ever loading a `Team` object into memory in your process.

### 6.1 Paging Against a Real Table

```csharp
public async Task<List<Player>> GetPageAsync(int pageNumber, int pageSize)
{
    return await _dbContext.Players
        .OrderBy(p => p.JerseyNumber)      // required before Skip/Take — LINQ Part 1, Section 9.1
        .Skip((pageNumber - 1) * pageSize)
        .Take(pageSize)
        .ToListAsync();
}
```

LINQ Part 1's warning about ordering before `Skip`/`Take` matters even more here than it did against an in-memory list: a relational database has no inherent row order at all without an explicit `ORDER BY`, so skipping this step doesn't just risk an *inconsistent* page order across calls — it risks a genuinely undefined one.

---

## 7. `AsNoTracking` and Async Querying

### 7.1 `AsNoTracking` — Skipping the Change Tracker on Purpose

```csharp
// Tracked by default — EF Core snapshots every returned entity to detect later changes
var players = await _dbContext.Players.Where(p => p.Goals > 20).ToListAsync();

// Read-only — no snapshot, no change-tracking overhead
var readOnlyPlayers = await _dbContext.Players
    .AsNoTracking()
    .Where(p => p.Goals > 20)
    .ToListAsync();
```

**Why this is a genuine performance practice, not a micro-optimization:** Part 1, Section 7 covered the change tracker as the mechanism that lets `SaveChangesAsync` know what to update. That bookkeeping — snapshotting every loaded entity's original values so they can be compared later — has a real cost in both memory and CPU, and it's entirely wasted work for a query whose result will only ever be read and displayed, never modified and saved back. `AsNoTracking()` is the right default for read-only queries: API responses, reports, dashboards — essentially any query the guide's earlier N+1/projection sections would already flag as read-focused.

**Q: Is `AsNoTracking` safe to use everywhere?**

>No — only where the entities returned genuinely won't be modified and saved through *this* `DbContext` instance afterward. If code later tries to mutate a no-tracking entity and call `SaveChangesAsync`, EF Core has no baseline to compare against and won't generate the expected `UPDATE`. The rule of thumb: read-only query, use `AsNoTracking`; about to edit and save, don't.

### 7.2 Async All the Way Through

Every terminal LINQ operator against a `DbSet<T>`/`IQueryable<T>` has an async counterpart — this is exactly Session 5, Section 8, and it applies without exception once relationships and `Include` enter the picture:

```csharp
public async Task<Team?> GetTeamWithRosterAsync(int teamId, CancellationToken cancellationToken)
{
    return await _dbContext.Teams
        .Include(t => t.Players)
        .FirstOrDefaultAsync(t => t.Id == teamId, cancellationToken);
}
```

Nothing about adding `Include` changes the async story — the entire chain, joins included, still becomes one SQL statement, executed non-blockingly, the moment `FirstOrDefaultAsync` is awaited.

---

## 8. Seeding Data

```csharp
protected override void OnModelCreating(ModelBuilder modelBuilder)
{
    modelBuilder.Entity<Team>().HasData(
        new Team { Id = 1, Name = "Al Ahly SC" },
        new Team { Id = 2, Name = "Zamalek" }
    );
}
```

`HasData` becomes part of the model itself, which means it's captured in a migration (Part 1, Section 5.2) — running `dotnet ef migrations add` after adding seed data generates `INSERT` statements as part of that migration, so the seed data ships and applies exactly like a schema change, consistently across every environment the migration runs in.

**Why not just seed at runtime with an `if` check instead?**

```csharp
// An alternative — seeding imperatively, typically once at startup
if (!await _dbContext.Teams.AnyAsync())
{
    _dbContext.Teams.AddRange(new Team { Name = "Al Ahly SC" }, new Team { Name = "Zamalek" });
    await _dbContext.SaveChangesAsync();
}
```

Both are valid, and the choice depends on what the seed data actually represents. `HasData` fits fixed, structural, rarely-changing reference data (a list of positions, a set of default roles) that's really part of the schema conceptually. A runtime check fits data that's more like ordinary application data, or that needs logic beyond "insert if missing" — and it avoids baking specific row values into a migration file that then has to be edited or superseded if that seed data ever needs to change.

---

## 9. Quick Reference — Glossary

| Term | Meaning |
|---|---|
| Data Annotation | An attribute (`[Required]`, `[MaxLength]`, `[Column]`) configuring how a property maps to the schema |
| Fluent API | Configuration expressed through `ModelBuilder` inside `OnModelCreating`, a Builder-pattern API |
| `IEntityTypeConfiguration<T>` | A class per entity holding that entity's Fluent API configuration, applied via `ApplyConfigurationsFromAssembly` |
| Navigation property | A property that lets you traverse a relationship in C# (`player.Team`, `team.Players`) |
| Foreign key | The column that actually enforces a relationship at the database level |
| `DeleteBehavior` | What happens to related rows when the referenced row is deleted: `Cascade`, `Restrict`, `SetNull`, `NoAction` |
| Join entity | An explicit entity representing a many-to-many relationship, needed when the relationship itself has data |
| Value converter (`HasConversion`) | Configuration describing how a property's C# type is translated to and from the type actually stored in the database |
| Owned type (`OwnsOne`) | A value object with no independent identity, mapped onto its owner's table; can't be tracked or queried on its own |
| `Include` / `ThenInclude` | Eager loading — fetches related data in the same query, via a SQL join |
| Explicit loading | Loading a navigation property on demand, after the initial query, via `Entry(...).Collection(...).LoadAsync()` |
| Lazy loading | Navigation properties populate themselves automatically on first access; usually avoided because the query is invisible at the call site |
| `AsNoTracking` | Skips change-tracking snapshotting for a query whose results won't be modified and saved |
| `HasData` | Seed data captured as part of the model and applied through a migration |

---
