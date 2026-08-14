# Task 3 
Task 1 built a football management system that lives entirely in memory - close the console window and the entire league, every player, every stat, is gone. This task is about making it survive. You're taking the domain you already built and giving it a real home in a database, using everything from EF Core Parts 1–3.

**You are not being told which EF Core feature to use for each requirement.** Part of what's being tested here is recognizing *which tool fits which problem* — that judgment is the actual skill, not the syntax. If a requirement describes a scenario, your job is to figure out what breaks without the right tool, and which one from the course actually fixes it.

Difficulty is marked per item: **[Easy]**, **[Medium]**, **[Hard]**. Do them roughly in order — most Medium and Hard items build on an Easy one earlier in the list.

---

## Getting Started

- **[Easy] Wire Up the League**
  Set up a new console project alongside your Task 2 code, add the EF Core packages you'll need, and get a `DbContext` registered through dependency injection rather than constructed by hand inline. You haven't covered ASP.NET Core's web hosting yet, but the same `builder.Services.Add...` pattern from EF Core Part 1, §4 is available through `Microsoft.Extensions.Hosting`'s generic host — that's what you want here, not a hardcoded `OnConfiguring`. Connection string comes from configuration, not a literal string.

---

## The Core Model

- **[Easy] Give Players and Teams a Home**
  Turn your `Player` and `Team` classes into entities EF Core can persist, and make sure a team's roster is something you can actually query back out — not just something that happens to exist in a `List<T>` at runtime.

- **[Easy] Represent Position Cleanly**
  Task 2 used inheritance for `Goalkeeper`/`Defender`/`Midfielder`/`Forward`. For this task, represent a player's position as a single property on `Player` — but store it in a way that a database administrator glancing at the table can actually tell what it means, not just a bare number.

- **[Easy] First Migration**
  Get your model into an actual database. Prove it worked by inspecting the generated schema, not just by the command exiting without an error.

- **[Easy] Guard the Basics**
  Nothing currently stops someone from creating a `Player` with no name or a 500-character one. Fix that, and make the choice deliberately between the two mechanisms the course covered for this.

---

## Reading and Writing

- **[Easy] CRUD Roundtrip**
  Add a team with a handful of players, save it, close the app, and run it again to prove the data is still there and queryable — not just that `SaveChangesAsync` didn't throw.

- **[Easy] Read Efficiently**
  Build a "display full roster" screen. Since nothing here gets edited or saved back, make sure you're not paying for bookkeeping you don't need.

- **[Easy] Update Player Stats**
  Implement updating a player's goals and assists after a match. Think carefully about whether you need to explicitly tell EF Core "this changed" — you covered exactly why that call is or isn't necessary.

- **[Easy] Retire a Player**
  Implement removing a player from a team, and separately, removing an entire team. For the team case: decide, on purpose, what should happen to its players when it's deleted — don't just accept whatever EF Core does by default without checking what that default actually is.

- **[Easy] Seed the League**
  Get 2–3 starter teams with full rosters into a fresh database automatically, with no manual data entry. Two approaches were covered — pick one, and be ready to explain why it fits this specific data better than the other.

---

## Modeling Real Relationships

- **[Medium] Organize the Configuration**
  Once your model has more than two or three entities, one giant configuration method gets hard to navigate. Split it up the way real projects do.

- **[Medium] Contract Talk**
  Every player has a contract — a salary and a length, at minimum. Model it. Then decide: does a contract deserve to be its own fully independent thing in your database, or does it only ever make sense glued to exactly one player? The course drew a specific line for exactly this decision — find it and apply it.

- **[Medium] The Club's Home Ground**
  Give each `Team` a home stadium — name, city, capacity. This one doesn't need its own identity or its own table row independent of the team; it needs to travel with the team it belongs to.

- **[Medium] Track Awards Across Seasons**
  Players win awards, and the same player can win the same award in different seasons — Player of the Month, more than once, in different months. Model that relationship so it can actually record *which season* an award was won, not just *that* it was won.

- **[Medium] Nobody Wears Someone Else's Number**
  Two players on the same team should never be able to share a jersey number. Enforce this so the database itself refuses it — and when it does, make sure whatever's calling your data layer gets a clear, specific reason why the save failed, not a generic crash.

---

## Querying Like It Matters

- **[Medium] Build a Leaderboard**
  Implement a "top scorers" screen that supports paging — page 1, page 2, and so on — with a stable, predictable order every time it's called. Get this subtly wrong and it'll work fine in testing and misbehave unpredictably later.

- **[Medium] Eager vs. Lean**
  Write two different read methods against your model: one where you genuinely need the full related entities (to act on them further), and one where you only need a couple of fields off a related entity for display. Make the two methods look different from each other, on purpose, and leave a one-line comment on each explaining why you chose the approach you did.

- **[Medium] Go Fully Async**
  Every method in your data-access layer that touches the database should be `async`, accept a `CancellationToken`, and actually pass that token all the way down to the EF Core call — not just declare the method `async` and stop there.

---

## When Things Go Wrong

- **[Hard] The Coach and the Analyst Are Both Editing**
  Simulate this: two different parts of your program each load the *same* player at roughly the same time. One updates the player's fitness status; the other, working from its own separately-loaded copy, updates that player's goal tally. Whoever saves second should not be allowed to silently erase whatever the first one changed — the system needs to actually notice the conflict and do something deliberate about it, not just let the second write win by accident.

- **[Hard] Bench the Whole Squad**
  A team gets relegated, and every single player on that team needs to be marked inactive in one shot — potentially dozens of players. Implement this in a way that doesn't load every one of those players into memory just to flip one flag on each — and be ready to explain, in a comment, exactly what you're giving up by doing it this way instead of the ordinary load-and-save approach.

---

## Optional / Bonus (not required, not graded on the same curve)

These aren't expected of everyone — they're here for anyone who wants to push further, the same spirit as Task 2's bonus section.

- **Repository Layer** — Wrap your data access behind a repository interface instead of calling `DbContext` directly from wherever you need data. The course was explicit that this isn't automatically the "better" choice — if you do this, be ready to argue whether it was actually worth it *for this specific project*, not just that you did it.
- **Bring Back the Inheritance** — Task 2's `Goalkeeper`/`Defender`/`Midfielder`/`Forward` hierarchy is genuine polymorphism, and mapping it properly in EF Core (rather than collapsing it to a `Position` enum, as this task requires) needs an inheritance-mapping strategy that wasn't covered in class. If you're curious, this is a legitimate rabbit hole — look into how EF Core maps a class hierarchy to a single table.
- **Concurrent Bulk Retirement** — Combine the two Hard requirements: what would it take to apply a bulk operation safely when concurrency actually matters? (Hint: think about why this is harder than either requirement alone.)

---

## Submission Expectations

- A working console project that builds and runs migrations cleanly from a fresh database.
- Every requirement above reflected somewhere in actual, callable code — not just present in a comment describing what you'd do.
- Short comments at the two or three trickiest decision points (delete behavior, owned type vs. entity, `Include` vs. projection) explaining *why* you chose what you chose — one sentence is enough, but it needs to be there.
