# Advanced C# — Async, Serialization, Attributes, Assemblies & Reflection

**Topics:** Async Programming (`async`/`await`, `Task`) · Serialization · Attributes · Assemblies · Reflection

**Special thanks to Mahmoud Ahmed for presenting and explaining this session**



---

## Table of Contents

1. [Async Programming](#1-async-programming)
2. [Serialization](#2-serialization)
3. [Attributes](#3-attributes)
4. [Assemblies](#4-assemblies)
5. [Reflection](#5-reflection)
6. [Putting It All Together — A Worked Example](#6-putting-it-all-together--a-worked-example)
7. [Quick Reference — Keyword Glossary](#7-quick-reference--keyword-glossary)

---

## 1. Async Programming

### 1.1 What Is a Thread? (Starting From Zero)

Before any of `async`/`await` will make sense, you need a mental picture of what your program is actually running *on*.

**A process** is a running instance of your program. when you launch your app, the OS creates a process for it, gives it its own private chunk of memory, and starts executing your code inside it.

**A thread** is a single sequential path of execution *within* that process( literally, one line of code running after another, in order, one instruction at a time.) Every process starts with exactly one thread (often called the "main thread"), which is what actually runs your `Main()` method line by line.

```csharp
// This is what a single thread does: one instruction, then the next, then the next.
Console.WriteLine("Step 1");
Console.WriteLine("Step 2");
Console.WriteLine("Step 3");
// A single thread cannot be in two places in this list at once — it runs top to bottom.
```

**A process can have multiple threads.** Each additional thread is its own independent sequence of instructions, running *concurrently* with the others (genuinely in parallel if you have multiple CPU cores; rapidly time-sliced between if you don't). This is how a program can, for example, keep a UI responsive to clicks while a separate thread crunches numbers in the background — two different sequences of instructions progressing at the same time, instead of one long sequence where the UI has to wait for the number-crunching to finish first.

```csharp
// Manually starting a second thread — rare in day-to-day C# (you'll almost always
// use Task/async instead, see below), but this is what's happening underneath.
Thread backgroundThread = new Thread(() =>
{
    Console.WriteLine("Running on a separate thread!");
});
backgroundThread.Start();

Console.WriteLine("This might print before OR after the line above —");
Console.WriteLine("the two threads are running independently.");
```

**Why threads aren't free : the resource cost:** the OS reserves real memory for every thread's call stack whether the thread is busy or not, and the OS scheduler has to spend time switching the CPU's attention between threads (called a *context switch*), which itself has overhead. A machine can comfortably run many thousands of lightweight objects, but only a much smaller number of OS threads before performance degrades

**The thread pool — reusing threads instead of constantly creating new ones:** creating and destroying an OS thread is relatively expensive, so .NET keeps a **thread pool** — a standing collection of already-created threads that sit ready, waiting to be handed a piece of work. When you queue work onto the thread pool, an existing thread picks it up, runs it, and goes back to waiting for the next job, instead of the runtime spinning up a brand-new OS thread every single time. `Task.Run` hands work to this pool rather than creating a dedicated thread of its own.

**The one key idea to hold onto before moving on:** a thread that is *blocked* ( synchronously waiting for something, like a network response) is still a full thread, still occupying its OS resources, and still unavailable to do any other work, even though it isn't actually computing anything. That "expensive thread doing nothing" problem is precisely what the next section exists to solve.

>**Q: Is a `Task` the same thing as a thread?**
No — this is the single most important distinction to get right before continuing. A `Task` represents a *unit of work that will complete at some point*; it may run on a thread-pool thread, but for I/O-bound async work (Section 1.2), it often uses **no dedicated thread at all** while it's waiting. Conflating "task" and "thread" is the source of a lot of confusion about what `async`/`await` actually does — Section 1.2 addresses this directly.

### 1.2 The Problem — Why Does Async Exist At All?

Imagine a web server handling a request that needs to call a database and wait 200ms for the response. If that call is **synchronous** (blocking), the thread handling the request just sits there — doing nothing — for the entire 200ms. Threads are not free: each one reserves roughly 1MB of stack space and costs the OS scheduler time to switch between. A server with a fixed thread pool of, say, 50 threads can only have 50 *blocked-waiting-on-I/O* requests in flight at once, even though the CPU itself is almost entirely idle during that wait.

```csharp
// SYNCHRONOUS — the calling thread is blocked for the entire duration of the call
public string GetWeatherReport(string city)
{
    string result = httpClient.GetString($"https://api.weather.com/{city}");  // Thread blocked here
    return result;
}
```

**Async solves a specific problem: freeing up the thread during I/O waits**, not making the work itself faster. The database, the disk, and the network all do their work independently of your CPU — async lets the thread go do something else (like handle another incoming request) while waiting, instead of sitting idle.

```csharp
// ASYNCHRONOUS — the thread is released back to the pool while waiting for the network
public async Task<string> GetWeatherReportAsync(string city)
{
    string result = await httpClient.GetStringAsync($"https://api.weather.com/{city}");
    return result;
}
```

**Why this matters in an interview:** if you say "async makes code run faster," that's wrong for CPU-bound work. Async is about **scalability and responsiveness** — handling more concurrent I/O-bound operations with fewer threads, and keeping a UI thread free to redraw the screen while a network call is in flight. Section 1.11 covers the CPU-bound case separately.

### 1.3 `Task` and `Task<T>` — The Building Blocks

`Task` represents an operation that will complete at some point in the future. `Task<T>` is the same thing, but the operation produces a result of type `T` when it finishes.

```csharp
Task DoSomething();          // Represents "an operation that will finish" — no result value
Task<int> ComputeSomething(); // Represents "an operation that will finish and hand back an int"
```

Think of `Task<T>` as a *promise*: "I don't have the `int` yet, but I guarantee I'll either produce one or fail with an exception." `await` is how you unwrap that promise once it's ready.

### 1.4 `async` and `await` — Syntax and What They Actually Do

```csharp
public async Task<int> GetTotalGoalsAsync(int teamId)
{
    List<Player> players = await _repository.GetPlayersAsync(teamId);  // suspend here
    int total = players.Sum(p => p.Goals);                             // resumes here once ready
    return total;
}
```

- `async` marks a method as containing `await` expressions and changes how the compiler builds it.
- `await` suspends the method at that point **without blocking the calling thread**. The thread is returned to whatever pool it came from. When the awaited `Task` completes, the rest of the method resumes — potentially on a different thread (more on this in 1.10).
- The method's return type becomes `Task` or `Task<T>` — the *caller* gets a `Task<int>` back immediately, representing "the eventual int," and can `await` it in turn.

**Q: Does `await` block the thread?**
No , that's the entire point. `await` schedules the rest of the method as a continuation to run once the awaited operation finishes, and immediately gives the thread back to the caller (or the thread pool). This is the opposite of `Thread.Sleep()`, which genuinely blocks.

### 1.5 What's Actually Happening Under the Hood — The State Machine

This should look familiar: `async`/`await` uses the **exact same state-machine technique** as `yield return` (Session 2, Section 10?). The compiler rewrites your method into a class that implements a state machine with a `MoveNext()`-style method, tracking exactly where execution paused so it can resume later.

```csharp
// What YOU write:
public async Task<int> GetTotalGoalsAsync(int teamId)
{
    var players = await _repository.GetPlayersAsync(teamId);
    return players.Sum(p => p.Goals);
}

// What the compiler conceptually generates (simplified):
// - A generated struct/class implementing IAsyncStateMachine
// - A MoveNext() method with a switch on the current "state"
// - Each 'await' becomes a state transition point
// - The generated code registers a continuation with the awaited Task
//   so MoveNext() gets called again automatically when it completes
```

**Why this matters:** knowing this explains *why* local variables survive across an `await` (they become fields on the generated state machine object, not stack variables) and *why* `async` methods have overhead compared to plain synchronous ones — there's a real object being allocated and a state machine being driven. This overhead is negligible for I/O-bound work but is a reason not to sprinkle `async` on tight, CPU-bound loops for no reason.

### 1.6 `async void` vs `async Task` — Why `async void` Is Dangerous

```csharp
// DANGEROUS — avoid this except for event handlers
public async void ProcessOrder()
{
    await _paymentService.ChargeAsync();  // if this throws...
}

// SAFE — the standard shape for an async method
public async Task ProcessOrderAsync()
{
    await _paymentService.ChargeAsync();
}
```

**Why `async void` is dangerous:** an `async Task` method's exceptions are captured on the returned `Task` — the caller can `await` it and get the exception via a normal `try`/`catch`. An `async void` method has no `Task` for anyone to await, so its exceptions **cannot be caught by the caller at all**. They're thrown directly onto the synchronization context, which for most apps means the process crashes or the exception vanishes into a log no one is watching. `async void` also can't be awaited, so the caller has no way to know when it's actually finished — it's "fire and forget" whether you meant it to be or not.

**The one legitimate use of `async void`:** UI event handlers (`button.Click += async (s, e) => await SaveAsync();`), because the event delegate signature is fixed as `void` and you have no choice. Everywhere else, return `Task`.

### 1.7 Exception Handling in Async Code

```csharp
public async Task RunReportAsync()
{
    try
    {
        await _reportService.GenerateAsync();
    }
    catch (ReportGenerationException ex)
    {
        _logger.LogError(ex, "Report generation failed");
    }
}
```

Exceptions thrown inside an `async Task` method are stored on the returned `Task` and **re-thrown when that task is awaited**. This is why `try`/`catch` around an `await` works exactly like it would in synchronous code — a key reason to always return `Task`/`Task<T>` instead of `async void`.

>**Q: What happens if you `await Task.WhenAll(...)` and multiple tasks fail?**
Only the *first* exception is re-thrown by `await`, but all exceptions are captured in an `AggregateException` accessible via the `Task`'s `.Exception` property if you need to inspect every failure rather than just the first.

### 1.8 Running Work Concurrently — `Task.WhenAll` and `Task.WhenAny`

Sequential `await`s run one after another, even if the operations are unrelated:

```csharp
// SLOW — sequential; total time = sum of both calls
var homeTeam = await _repo.GetTeamAsync(homeId);   // waits fully before starting the next
var awayTeam = await _repo.GetTeamAsync(awayId);
```

```csharp
// FAST — both requests are in flight at the same time; total time = the slower of the two
Task<Team> homeTask = _repo.GetTeamAsync(homeId);   // started, not yet awaited
Task<Team> awayTask = _repo.GetTeamAsync(awayId);   // started, not yet awaited

await Task.WhenAll(homeTask, awayTask);             // wait for BOTH to finish

Team homeTeam = homeTask.Result;   // safe to read .Result — the task is already complete
Team awayTeam = awayTask.Result;
```

`Task.WhenAny` returns as soon as **any one** of the given tasks completes [[useful for timeouts ("whichever finishes first: the real call, or a delay") or racing redundant calls to multiple mirrors/replicas.]]


### 1.9 Cancellation — `CancellationToken`

```csharp
public async Task<List<Player>> SearchPlayersAsync(string query, CancellationToken cancellationToken)
{
    return await _httpClient.GetFromJsonAsync<List<Player>>(
        $"/players/search?q={query}", cancellationToken);
}

// Caller side:
var cts = new CancellationTokenSource(TimeSpan.FromSeconds(5));  // auto-cancel after 5s
try
{
    var results = await SearchPlayersAsync("Salah", cts.Token);
}
catch (OperationCanceledException)
{
    Console.WriteLine("Search timed out or was cancelled.");
}
```

`CancellationToken` is a cooperative cancellation signal — the async method has to actually check it (or pass it into something that does, like `HttpClient`) for cancellation to take effect. Nothing forcibly kills the operation; well-behaved async APIs simply throw `OperationCanceledException` once they notice the token was cancelled. **Convention:** any async method that might run for a while should accept an optional `CancellationToken` parameter, typically last, so callers can opt into cancellation without every caller being forced to pass one.

### 1.10 `ConfigureAwait(false)` and the Deadlock You Need to Know About

Some environments (classic ASP.NET, WPF, WinForms) have a **synchronization context** — a mechanism that ensures code resumes on a specific thread after an `await` (e.g., back on the UI thread, so you can safely touch UI controls). By default, `await` captures this context and marshals the continuation back onto it.

This becomes dangerous when synchronous code blocks on an async call:

```csharp
// DANGEROUS — classic deadlock in a context-sensitive environment (e.g. old ASP.NET, WPF)
public void ButtonClick(object sender, EventArgs e)
{
    var result = LoadDataAsync().Result;  // blocks the UI thread, waiting synchronously
}

public async Task<string> LoadDataAsync()
{
    await httpClient.GetStringAsync(url);  // wants to resume back on the UI thread...
    // ...but the UI thread is blocked above, waiting on .Result. Deadlock.
}
```

The UI thread blocks on `.Result`, waiting for `LoadDataAsync` to finish. But `LoadDataAsync`'s continuation is trying to resume *on that same UI thread*, which is stuck waiting. Neither can proceed — a deadlock.

**Two fixes:**

```csharp
// Fix 1 — never block on async code; await it properly, all the way up the call stack
public async void ButtonClick(object sender, EventArgs e)
{
    var result = await LoadDataAsync();  // no deadlock — the UI thread isn't blocked
}

// Fix 2 — ConfigureAwait(false) tells the awaited call "don't bother resuming on
// the original context — any thread pool thread is fine." Common in library code
// that doesn't need to touch the UI and wants to avoid forcing a context hop.
public async Task<string> LoadDataAsync()
{
    return await httpClient.GetStringAsync(url).ConfigureAwait(false);
}
```

**Why this matters today:** ASP.NET Core has **no synchronization context** by default, so this specific deadlock is much less common than it used to be. But it's still a very common interview question because it tests real understanding of *how* `await` resumes execution, not just the surface syntax — and `ConfigureAwait(false)` is still standard practice inside library/NuGet package code that has no idea what kind of app will consume it.

### 1.11 CPU-Bound vs I/O-Bound — `Task.Run` vs `await`

This is the single most common conceptual mix-up with async in C#.

| | I/O-bound (network, disk, DB) | CPU-bound (heavy computation) |
|---|---|---|
| What's happening | Waiting on something *external* | The CPU itself is doing the work |
| Correct tool | `await someIoOperationAsync()` — no thread is consumed while waiting | `Task.Run(() => DoHeavyWork())` — deliberately hands the work to a thread-pool thread |
| Why | The OS/network stack notifies .NET when it's done — no thread needs to sit and wait | There's genuinely nothing to "wait" for; the work has to run *somewhere*, so you pick a background thread so it doesn't block the caller (e.g. the UI thread) |

```csharp
// WRONG — Task.Run doesn't help here; GetStringAsync is already non-blocking I/O.
// This just burns a thread-pool thread to babysit an operation that didn't need one.
await Task.Run(() => httpClient.GetStringAsync(url));

// RIGHT — await the I/O operation directly
await httpClient.GetStringAsync(url);

// RIGHT — CPU-bound work genuinely benefits from Task.Run, to keep it off the calling thread
public async Task<byte[]> GenerateThumbnailAsync(byte[] image)
{
    return await Task.Run(() => ResizeImageExpensively(image));
}
```

>**Q: Does `async`/`await` create new threads?**
No, not by itself. `await`ing I/O doesn't use a thread at all while waiting — it's not "a thread is paused," it's "no thread is needed until the operation completes." `Task.Run` is the piece that explicitly hands work to a thread-pool thread, and that's specifically for CPU-bound work.

### 1.12 Common Async Anti-Patterns

```csharp
// ANTI-PATTERN 1 — "async over sync": wrapping a blocking call in Task.Run just to
// make a method technically return a Task. Gains nothing; still consumes a thread.
public Task<string> ReadFileAsync(string path)
{
    return Task.Run(() => File.ReadAllText(path));  // File.ReadAllText is already blocking
}
// BETTER: use the real async API
public Task<string> ReadFileProperlyAsync(string path) => File.ReadAllTextAsync(path);

// ANTI-PATTERN 2 — "sync over async": calling .Result or .Wait() on a Task.
// Defeats the purpose of async entirely and risks the deadlock from 1.10.
var data = GetDataAsync().Result;   // avoid

// ANTI-PATTERN 3 — fire-and-forget without meaning to: calling an async method
// without awaiting it. Exceptions are silently swallowed; the caller has moved on
// before the work is done.
SaveLogAsync(entry);  // not awaited — if this throws, nobody will ever know
```

>**Q: Is it ever OK to not await a `Task`?**
Rarely, and only deliberately — for genuine "fire and forget" background work, where you explicitly don't care about the result or errors (and ideally still attach error handling, e.g. `task.ContinueWith(t => Log(t.Exception), TaskContinuationOptions.OnlyOnFaulted)`). Forgetting to `await` by accident is a bug, and most IDEs warn about it (`CS4014`).

---

## 2. Serialization

### 2.1 What Is Serialization, and Why Do We Need It?

**Serialization** converts an in-memory object graph into a format that can be stored or transmitted — bytes, text, JSON, XML — and **deserialization** reverses the process, rebuilding the object graph from that format. Objects in memory only exist as long as the process runs, and they can't cross a network boundary as-is: you need a shared, agreed-upon representation both sides understand.

You reach for serialization any time an object needs to leave the process it was created in: sending a request body over HTTP, writing configuration to a file, caching a value in Redis, publishing a message to a queue, saving application state to disk. **The "why" is always the same** —> bridging the gap between "structured objects living in one process's memory" and "a byte stream that can travel or persist."

### 2.2 JSON Serialization with `System.Text.Json`

`System.Text.Json` is the built-in, modern serializer in .NET (as of .NET Core 3.0+), designed to be fast and allocate less than the older third-party `Newtonsoft.Json`, while covering the same core scenarios.

```csharp
public class Player
{
    public string Name { get; set; }
    public int JerseyNumber { get; set; }
    public int Goals { get; set; }
}

var salah = new Player { Name = "Salah", JerseyNumber = 10, Goals = 32 };

// Serialize — object to JSON string
string json = JsonSerializer.Serialize(salah);
// {"Name":"Salah","JerseyNumber":10,"Goals":32}

// Deserialize — JSON string back to object
Player restored = JsonSerializer.Deserialize<Player>(json);
```

### 2.3 Controlling Serialization with Attributes

This is where **Section 3 (Attributes)** connects directly to serialization: the serializer looks for specific attributes on your class at runtime (via reflection — Section 5) to decide how to map properties to JSON.

```csharp
public class Player
{
    [JsonPropertyName("full_name")]     // JSON key differs from the C# property name
    public string Name { get; set; }

    public int JerseyNumber { get; set; }

    [JsonIgnore]                        // Never included in the JSON output at all
    public string InternalNotes { get; set; }

    [JsonPropertyOrder(1)]              // Control serialization order explicitly
    public int Goals { get; set; }
}
```

**Why this matters (the "why" behind attribute-driven serialization):** the serializer is generic — it has no idea your class exists ahead of time. Attributes let *you* attach metadata to your own types that a completely generic piece of library code (which has never seen your class before) can read and act on at runtime. This pattern — generic library + attributes on your types + reflection reading those attributes — is the backbone of serializers, validation frameworks, ORMs, and dependency injection, and it recurs throughout Sections 3–5.

### 2.4 Records and Serialization — Why They Fit Naturally

Records (Study Guide 2, Section 11?) serialize cleanly because `System.Text.Json` supports constructor-based deserialization: it matches JSON property names to constructor parameter names.

```csharp
public record PlayerRecord(string Name, int JerseyNumber, int Goals);

string json = JsonSerializer.Serialize(new PlayerRecord("Salah", 10, 32));
// {"Name":"Salah","JerseyNumber":10,"Goals":32}

PlayerRecord player = JsonSerializer.Deserialize<PlayerRecord>(json);
// The serializer calls the record's constructor with values matched by parameter name
```

This is a good example of **why immutability and serialization pair well**: since the record's properties can only be set once (via `init` or the constructor), the serializer builds a fully-formed, valid object in one step rather than constructing an empty shell and mutating it property by property — which matters if your object has validation logic in its constructor (Study Guide 2, Section ?).

### 2.5Extra: Custom Converters — When the Default Mapping Isn't Enough

Sometimes a type doesn't map naturally to JSON (an `enum` you want serialized as a string instead of a number, a `DateTime` in a nonstandard format). A custom `JsonConverter<T>` lets you control that mapping explicitly.

```csharp
public class GoalTallyConverter : JsonConverter<int>
{
    public override int Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
        => int.Parse(reader.GetString());          // reads "32" (as a JSON string) into an int

    public override void Write(Utf8JsonWriter writer, int value, JsonSerializerOptions options)
        => writer.WriteStringValue(value.ToString());  // writes the int back out as a JSON string
}

public class Player
{
    [JsonConverter(typeof(GoalTallyConverter))]
    public int Goals { get; set; }
}
```

### 2.6 Serialization and Versioning — A Real Production Concern

Serialized data often outlives the code that wrote it — a JSON payload saved to disk today might be read by a newer version of your application months from now. **Why this matters:** if you rename or remove a property, old serialized data with the old property name simply won't populate the renamed property (it's silently ignored, not an error, by default). Practical guidance:

- Adding a new property is safe — old JSON just won't have that field, and it takes its default value.
- Renaming a property breaks old data unless you add `[JsonPropertyName]` pointing at the old name, or keep both old and new names supported during a migration window.
- Changing a property's *type* (e.g. `int` → `string`) breaks deserialization outright for old data.
- Removing a property from JSON output is usually safe for *readers* going forward, but breaks anything downstream still expecting that field.

### 2.7 A Word on Binary (`BinaryFormatter`) Serialization

Older .NET code sometimes uses `BinaryFormatter` for serialization. **This is now considered obsolete and a security risk**, and Microsoft has removed it from modern .NET entirely — deserializing untrusted binary data with it is a well-known remote code execution vector, because it can reconstruct arbitrary types with arbitrary constructors from the byte stream.

### 2.8 `System.Text.Json` vs `Newtonsoft.Json` — Why Choose One Over the Other

| | `System.Text.Json` | `Newtonsoft.Json` |
|---|---|---|
| Built in? | Yes, part of the .NET base class library | No, third-party NuGet package (still extremely widely used) |
| Performance | Faster, lower allocations | Slower, more allocations |
| Feature maturity | Newer; catching up on edge cases | Older, extremely feature-rich, more flexible converters |
| Use when | New projects, performance-sensitive APIs | Legacy codebases already using it, or a specific feature it supports that `System.Text.Json` doesn't (yet) |

>**Q: Why not just use whichever one is "better"?**
Because "better" depends on constraints you don't control in a real job — an existing codebase already standardized on Newtonsoft has real switching costs, and interviewers often want to hear that you understand *both* exist and can articulate the tradeoff, not that you have a single dogmatic answer.

---

## 3. Attributes

### 3.1 What Is an Attribute?

An **attribute** is a piece of declarative metadata you attach to code — a class, method, property, parameter, or assembly — using `[SquareBrackets]`. By itself, an attribute **does nothing**. It doesn't run any code and doesn't change how your program behaves at runtime. It's purely data that gets baked into the compiled assembly's metadata, waiting for something else — usually reflection (Section 5) — to read it and act on it.

```csharp
[Obsolete("Use CalculateBonus2 instead")]
public decimal CalculateBonus(Employee emp) { /* ... */ }
```

**This is the single most important thing to understand about attributes for an interview:** `[Obsolete]` doesn't make the method stop working. The *compiler* specifically looks for `[Obsolete]` and emits a warning when the method is called — that behavior is hardcoded into the C# compiler for this one attribute. For attributes you define yourself, nothing looks at them automatically; **you** have to write the reflection code that reads them and decides what to do (Section 3.6?).

### 3.2 Built-In Attributes You Already Know

| Attribute | What reads it | What it does |
|---|---|---|
| `[Obsolete]` | The C# compiler | Emits a build warning (or error) when the marked member is used |
| `[Serializable]` | Legacy binary serializers | Marks a type as eligible for binary serialization (mostly legacy today) |
| `[JsonPropertyName]` | `System.Text.Json` | Maps a JSON key to a differently-named property (Section 2.3) |
| `[Required]`, `[Range]` | ASP.NET Core model validation | Drives automatic input validation on API request models |
| `[HttpGet]`, `[Route]` | ASP.NET Core routing | Maps an HTTP request to a controller action |
| `[Fact]`, `[Test]` | Test runners (xUnit, NUnit) | Marks a method as a test the runner should discover and execute |

Notice the pattern: **every one of these is inert on its own.** A `[Fact]`-marked method is a completely ordinary method until the xUnit test runner scans the assembly with reflection, finds every method carrying `[Fact]`, and calls them one by one.

### 3.3 Defining a Custom Attribute

```csharp
// Attributes are just classes that inherit from System.Attribute — that's the whole trick
[AttributeUsage(AttributeTargets.Property, AllowMultiple = false)]
public class ColumnNameAttribute : Attribute
{
    public string Name { get; }

    // Attribute "arguments" in [ColumnName("player_name")] are just constructor arguments
    public ColumnNameAttribute(string name)
    {
        Name = name;
    }
}
```

```csharp
public class Player
{
    [ColumnName("player_name")]
    public string Name { get; set; }

    [ColumnName("jersey_no")]
    public int JerseyNumber { get; set; }
}
```

**`AttributeUsage`** controls where the attribute is legal to place, and whether it can be applied more than once to the same target:

```csharp
[AttributeUsage(
    AttributeTargets.Class | AttributeTargets.Method,  // legal on classes AND methods
    AllowMultiple = true,                              // can be stacked more than once
    Inherited = true)]                                 // derived classes inherit it too
public class AuditableAttribute : Attribute { }
```

### 3.4 Attribute Targets

You can restrict — or explicitly specify — which kind of code element an attribute decorates:

```csharp
[assembly: AssemblyVersion("1.0.0.0")]   // targets the whole assembly

public class Report
{
    [return: NotNull]                     // targets a method's return value
    public string Generate() => "...";
}
```

Common `AttributeTargets` values: `Class`, `Method`, `Property`, `Field`, `Parameter`, `Constructor`, `Assembly`, `All`.

### 3.5 Attributes Alone Do Nothing — Why This Is the Key Insight

This point is worth repeating because it's the most common gap in a junior engineer's understanding: writing `[ColumnName("player_name")]` on a property changes **nothing** about how that property behaves. No validation runs. No mapping happens. It's inert metadata sitting in the compiled assembly, exactly like a comment, except a comment gets stripped at compile time and an attribute survives into the compiled binary where it can be read back out at runtime — that's the entire value proposition.

### 3.6 Reading Attributes with Reflection

```csharp
public static Dictionary<string, string> GetColumnMappings(Type type)
{
    var mappings = new Dictionary<string, string>();

    foreach (PropertyInfo property in type.GetProperties())
    {
        // GetCustomAttribute<T>() reads the attribute instance off this specific property
        var columnAttr = property.GetCustomAttribute<ColumnNameAttribute>();
        if (columnAttr != null)
        {
            mappings[property.Name] = columnAttr.Name;
        }
    }

    return mappings;
}

// Usage:
var mappings = GetColumnMappings(typeof(Player));
// { "Name" -> "player_name", "JerseyNumber" -> "jersey_no" }
```

This is the missing half of the picture from : the attribute declares *intent* ("this property maps to this database column"), and a separate piece of reflection code — written once, generically, for *any* type — reads that intent and acts on it. Neither half is useful alone; together they let you write a data-mapping layer that works for every class in your codebase without writing per-class mapping code.

### 3.7 Real-World Use Cases (to know only)

| Framework pattern | Attribute example | What the reflection layer does with it |
|---|---|---|
| ORMs (Entity Framework, Dapper) | `[Column("player_name")]`, `[Key]` | Maps C# properties to database columns and identifies primary keys |
| Validation (ASP.NET Core) | `[Required]`, `[Range(1, 99)]` | Inspects a submitted model's properties before a controller action runs, rejecting invalid input automatically |
| Dependency injection | `[Inject]` (in some DI frameworks) | Locates members that need a dependency supplied automatically |
| Testing frameworks | `[Fact]`, `[TestMethod]` | Discovers every method the test runner should execute |
| Serialization | `[JsonPropertyName]`, `[JsonIgnore]` | Controls how the serializer maps to/from JSON (Section 2.3) |

>**Q: Could you build any of the above without attributes, using plain code instead?**
Yes — you could pass mapping configuration explicitly through code (a fluent API, a config file, a convention like "always match by name"). Attributes are simply a convenient, colocated way to keep the metadata right next to the member it describes, instead of maintaining it separately. Entity Framework, for instance, supports *both* attributes (`[Column]`) and a separate fluent configuration API for exactly this reason — they're two ways to supply the same metadata.

---

## 4. Assemblies

### 4.1 What Is an Assembly?

An **assembly** is the unit of compiled output, deployment, and versioning in .NET — typically a `.dll` or `.exe` file. When you build a C# project, the compiler doesn't just translate your code to machine code directly; it compiles to **Intermediate Language (IL)** and packages that IL, plus a **manifest** describing the assembly itself, into one assembly file. The .NET runtime (the CLR) then JIT-compiles that IL to native machine code at run time, on the specific machine it's running on.

**Why this two-step compilation matters:** it's what makes .NET assemblies portable across different CPU architectures and OSes (with .NET Core/.NET 5+) — the same compiled `.dll` can run on Windows, Linux, or macOS, x64 or ARM, because the final translation to native machine code happens on the target machine, not at your build time.

### 4.2 What's Inside an Assembly — The Manifest

Every assembly carries a **manifest**: metadata describing the assembly's own identity (name, version, culture) and everything it depends on.

```csharp
using System.Reflection;

Assembly current = Assembly.GetExecutingAssembly();

Console.WriteLine(current.FullName);
// e.g. "MyApp, Version=1.2.0.0, Culture=neutral, PublicKeyToken=null"

foreach (AssemblyName reference in current.GetReferencedAssemblies())
{
    Console.WriteLine($"Depends on: {reference.Name} v{reference.Version}");
}
```

This is why a `.dll` is self-describing  you can hand someone a compiled assembly with no source code, and both humans and tools (including the CLR itself, at load time) can inspect exactly what version it is and what it needs to run.

### 4.3 Namespaces vs Assemblies — A Common Point of Confusion

These are two completely different, **orthogonal** concepts, and conflating them is a common junior mistake:

| | Namespace | Assembly |
|---|---|---|
| What it is | A logical grouping of type names in source code, to avoid naming collisions | A physical compiled output file (`.dll`/`.exe`) |
| Controlled by | `namespace` keyword in your `.cs` files | Project structure / build configuration |
| Example | `System.Collections.Generic` | `System.Private.CoreLib.dll` |

A single assembly can contain many namespaces, and (less commonly, via `partial` classes or careful project setup) types from the same namespace can be split across multiple assemblies. `using System.Collections.Generic;` tells the compiler where to *look up a type name*; it says nothing about which `.dll` that type physically lives in — the compiler resolves that separately via assembly references.

### 4.4 Loading Assemblies at Runtime

```csharp
// Get the assembly the currently executing code lives in
Assembly current = Assembly.GetExecutingAssembly();

// Get every Type defined in that assembly
Type[] types = current.GetTypes();

// Load a completely different assembly by file path, at runtime
Assembly pluginAssembly = Assembly.LoadFrom(@"C:\Plugins\ReportPlugin.dll");

// Find a specific type by its fully-qualified name inside that loaded assembly
Type reportType = pluginAssembly.GetType("ReportPlugin.MonthlyReport");
```

**Why load an assembly at runtime instead of just referencing it at compile time?** This is the foundation of **plugin architectures**: your application ships with a fixed core, but at startup it scans a `Plugins` folder, loads whatever `.dll` files it finds there, and looks for types matching a known interface or attribute (Section 3) — all without the core application ever having compiled against those plugin assemblies directly. Section 6 builds exactly this kind of example end-to-end.

### 4.5 `AssemblyLoadContext` — The Modern Isolation Mechanism

Older .NET Framework code used `AppDomain`s to isolate and unload groups of loaded assemblies (e.g., to reload a plugin without restarting the whole process). Modern .NET (Core and later) replaced that with `AssemblyLoadContext`, which lets you load a set of assemblies into an isolated, unloadable context:

```csharp
var loadContext = new AssemblyLoadContext("PluginContext", isCollectible: true);
Assembly plugin = loadContext.LoadFromAssemblyPath(@"C:\Plugins\ReportPlugin.dll");

// ... use the plugin ...

loadContext.Unload();  // releases the plugin assembly, allowing it to be garbage collected
```

**Why this matters practically:** without isolation, every assembly you load stays loaded for the lifetime of the process — you can't "unload" a single `.dll` from a normal load context. `AssemblyLoadContext` with `isCollectible: true` is what makes hot-reloadable plugin systems (and things like `dotnet watch`) possible.

### 4.6 Versioning and Strong Naming — Brief Overview

An assembly's identity includes a version number (`AssemblyVersion` attribute, `[assembly: AssemblyVersion("1.2.0.0")]`), and can optionally be **strong-named** ( signed with a cryptographic key pair so consumers can verify it hasn't been tampered with and comes from a known publisher). This matters most when publishing shared libraries (NuGet packages) that many independent consumers rely on, where you need a reliable way to reason about binary compatibility across versions.

>**Q: What's the difference between an assembly's version and a NuGet package's version?**
They're related but not identical — a single NuGet package can bundle multiple assemblies (e.g. different target frameworks), and package versioning follows semantic versioning conventions for consumer-facing compatibility promises, while the assembly's own `AssemblyVersion` is what the CLR itself uses for binding at load time.

---

## 5. Reflection

### 5.1 What Is Reflection?

**Reflection** is the ability to inspect and interact with types, members, and metadata **at runtime** ;such as discovering what properties a class has, what methods it exposes, what attributes decorate it, and even creating instances or invoking methods dynamically, all without knowing the concrete type at compile time.

**Why does this exist?** Ordinary C# code is deeply *static* — when you write `player.Name`, the compiler already knows exactly what `Name` is and generates direct code to access it. Reflection exists for the cases where you genuinely don't know the type ahead of time: writing a generic serializer that works for *any* class, building a plugin system that loads types it's never seen before (Section 4.4), or writing a test runner that has to discover and call test methods across an entire assembly.

### 5.2 The `Type` Class — Your Entry Point

```csharp
Player player = new Player();

Type typeFromInstance = player.GetType();  // Type, obtained from a runtime instance
Type typeFromKeyword = typeof(Player);      // Type, obtained at compile time from the type name

Console.WriteLine(typeFromInstance.Name);        // "Player"
Console.WriteLine(typeFromInstance.FullName);    // "MyApp.Models.Player"
Console.WriteLine(typeFromInstance.Namespace);   // "MyApp.Models"
```

**`typeof(X)` vs `x.GetType()`:** `typeof` needs the type name at compile time — you must literally know `Player` when you write the code. `GetType()` works on any object reference at runtime, which is essential when you have a variable typed as a base class or interface but need the *actual* runtime type — exactly like the `is`/pattern-matching scenario in the Study Guide's Section 1.7, but reflection gives you the full `Type` object to interrogate further, not just a boolean check.

### 5.3 Inspecting Members

```csharp
Type type = typeof(Player);

// All public properties
foreach (PropertyInfo prop in type.GetProperties())
{
    Console.WriteLine($"{prop.Name} : {prop.PropertyType.Name}");
}

// All public methods
foreach (MethodInfo method in type.GetMethods())
{
    Console.WriteLine(method.Name);
}

// Including non-public members requires explicit BindingFlags
var privateFields = type.GetFields(BindingFlags.NonPublic | BindingFlags.Instance);
```

### 5.4 Creating Instances Dynamically

```csharp
// Equivalent to 'new Player()', but the type is only known as a Type object at runtime
object instance = Activator.CreateInstance(typeof(Player));

// With constructor arguments
object playerWithArgs = Activator.CreateInstance(typeof(Player), "Salah", 10, 32);

// Cast back to the concrete type once you know what you're working with
Player player = (Player)instance;
```

This is exactly what powers dependency injection containers: given a `Type` for a service, the container reflects over its constructor, resolves each parameter's dependency recursively, and calls `Activator.CreateInstance` (or a compiled, faster equivalent — see 5.6) to build the object graph — all without you ever writing `new SomeService(...)` yourself.

### 5.5 Invoking Members Dynamically

```csharp
Type type = typeof(Player);
object instance = Activator.CreateInstance(type, "Salah", 10, 32);

// Read a property's value dynamically
PropertyInfo goalsProperty = type.GetProperty("Goals");
int goals = (int)goalsProperty.GetValue(instance);

// Set a property's value dynamically
goalsProperty.SetValue(instance, 35);

// Invoke a method dynamically
MethodInfo method = type.GetMethod("GetRating");
string rating = (string)method.Invoke(instance, parameters: null);
```

### 5.6 Combining Attributes and Reflection (Tying Sections 3 & 5 Together)

```csharp
public static void PrintObsoleteWarnings(Type type)
{
    foreach (MethodInfo method in type.GetMethods())
    {
        var obsoleteAttr = method.GetCustomAttribute<ObsoleteAttribute>();
        if (obsoleteAttr != null)
        {
            Console.WriteLine($"WARNING: {method.Name} is obsolete — {obsoleteAttr.Message}");
        }
    }
}
```

 reflection is *how* attributes go from being inert metadata to actually driving behavior. Neither piece works without the other — this pairing is worth being able to explain fluently in an interview, because it's the foundation of nearly every extensible .NET framework (ASP.NET model binding, EF Core mapping, test runners, JSON serializers, DI containers).

### 5.7 Performance — Why Reflection Is a Tool, Not a Default

Reflection is significantly slower than direct code — `MethodInfo.Invoke()` typically runs many times slower than calling the method directly, because the runtime has to look up metadata, validate arguments, and dispatch dynamically instead of jumping straight to a known address. This matters for anything reflection-heavy running in a hot path (a request-per-second web API, a tight loop).

**Practical mitigations:**

```csharp
// MITIGATION 1 — cache reflection lookups instead of repeating them
private static readonly PropertyInfo GoalsProperty = typeof(Player).GetProperty("Goals");
// Look up once (e.g. in a static field or a static constructor), reuse forever

// MITIGATION 2 — compile a delegate once, invoke it many times, near-native speed after that
Func<Player, int> getGoals = CompileGetter<Player, int>("Goals");  // built once via Expression trees
int goals = getGoals(somePlayer);  // fast — no reflection on the hot path
```

>**Q: If reflection is slow, why do frameworks like ASP.NET Core and Entity Framework use it so heavily and still perform well?**
Because they front-load the reflection cost — inspecting a type's shape happens once per type, typically cached in a static dictionary keyed by `Type`, and the actual per-request work uses that cached metadata (often compiled down to delegates) rather than calling `GetProperty()`/`Invoke()` fresh every single time.

---

## 6. Putting It All Together — A Worked Example

The real payoff of these four topics is that they're rarely used in isolation — production frameworks combine them. Below is a small but complete "mini-mapper" that ties **attributes**, **reflection**, **assemblies**, and **async** together, the way a real library would.

**Scenario:** you want a generic function that can take *any* object, read `[ColumnName]` attributes off its properties (Section 3), map them into a dictionary using reflection (Section 5), and asynchronously "save" that dictionary somewhere — say, a fake remote API — without the mapping code ever knowing about `Player` specifically.

```csharp
// ── The attribute (Section 3) ────────────────────────────────────────
[AttributeUsage(AttributeTargets.Property)]
public class ColumnNameAttribute : Attribute
{
    public string Name { get; }
    public ColumnNameAttribute(string name) => Name = name;
}

// ── The domain type, decorated with metadata ─────────────────────────
public class Player
{
    [ColumnName("player_name")]
    public string Name { get; set; }

    [ColumnName("jersey_no")]
    public int JerseyNumber { get; set; }

    public int Goals { get; set; }  // no attribute — falls back to the property name
}

// ── The generic mapper — works for ANY type, using reflection (Section 5) ────
public static class EntityMapper
{
    // MITIGATION from 5.7: cache per-type reflection results instead of repeating them
    private static readonly Dictionary<Type, Dictionary<string, PropertyInfo>> _cache = new();

    public static Dictionary<string, object> ToColumnDictionary(object entity)
    {
        Type type = entity.GetType();

        if (!_cache.TryGetValue(type, out var columnToProperty))
        {
            columnToProperty = new Dictionary<string, PropertyInfo>();
            foreach (PropertyInfo prop in type.GetProperties())
            {
                var attr = prop.GetCustomAttribute<ColumnNameAttribute>();
                string columnName = attr?.Name ?? prop.Name;  // fall back to the property name
                columnToProperty[columnName] = prop;
            }
            _cache[type] = columnToProperty;  // cache it — this reflection cost is paid once per type
        }

        var result = new Dictionary<string, object>();
        foreach (var (columnName, prop) in columnToProperty)
        {
            result[columnName] = prop.GetValue(entity);
        }
        return result;
    }
}

// ── The async "save" step (Section 1) — serializes the mapped data (Section 2) ────
public class RemoteRepository
{
    public async Task SaveAsync(object entity, CancellationToken cancellationToken = default)
    {
        Dictionary<string, object> columns = EntityMapper.ToColumnDictionary(entity);

        // Serialization (Section 2) — turn the mapped dictionary into JSON
        string json = JsonSerializer.Serialize(columns);

        // Async I/O (Section 1) — send it, without blocking the calling thread
        await _httpClient.PostAsync("/save",
            new StringContent(json, Encoding.UTF8, "application/json"),
            cancellationToken);
    }

    private readonly HttpClient _httpClient = new();
}

// ── Usage ──────────────────────────────────────────────────────────
var repository = new RemoteRepository();
var salah = new Player { Name = "Salah", JerseyNumber = 10, Goals = 32 };

await repository.SaveAsync(salah);
// Sends: {"player_name":"Salah","jersey_no":10,"Goals":32}
// — WITHOUT SaveAsync ever having compile-time knowledge of the Player class's shape.
```

---

## 7. Quick Reference — Keyword Glossary

| Keyword / Concept | Meaning |
|---|---|
| `async` | Marks a method as containing `await` expressions; compiler builds it as a state machine |
| `await` | Suspends execution until the awaited `Task` completes, without blocking the thread |
| `Task` / `Task<T>` | Represents an operation that will complete in the future, optionally producing a result |
| `async void` | An async method with no returned `Task` — exceptions can't be caught by the caller; avoid except for event handlers |
| `CancellationToken` | A cooperative signal an async operation can check to stop early |
| `ConfigureAwait(false)` | Tells `await` not to resume on the original synchronization context |
| `Task.WhenAll` | Awaits multiple tasks concurrently, completing when all of them finish |
| `Task.WhenAny` | Completes as soon as the first of several tasks finishes |
| `Task.Run` | Explicitly schedules CPU-bound work onto a thread-pool thread |
| Serialization | Converting an in-memory object into a storable/transmittable format (e.g. JSON) |
| Deserialization | Reconstructing an object from its serialized form |
| `JsonSerializer` | The `System.Text.Json` API for converting objects to/from JSON |
| `[JsonPropertyName]` | Maps a JSON key to a differently-named C# property |
| `[JsonIgnore]` | Excludes a property from serialization entirely |
| Attribute | Declarative metadata attached to code via `[Brackets]`; inert until something reads it via reflection |
| `System.Attribute` | The base class every custom attribute must inherit from |
| `[AttributeUsage]` | Controls where an attribute may be applied and whether it can repeat |
| Assembly | The compiled, deployable unit (`.dll`/`.exe`) containing IL and a metadata manifest |
| Manifest | An assembly's self-describing metadata: name, version, referenced assemblies |
| IL (Intermediate Language) | The intermediate, platform-neutral code the C# compiler produces; JIT-compiled to native code at runtime |
| `AssemblyLoadContext` | Modern mechanism for loading (and optionally unloading) assemblies in isolation, e.g. for plugins |
| Reflection | Inspecting and interacting with types and members at runtime |
| `Type` | The runtime representation of a type; the entry point for reflection |
| `typeof(X)` | Gets a `Type` at compile time from a known type name |
| `x.GetType()` | Gets a `Type` at runtime from an object instance — reflects its *actual* runtime type |
| `PropertyInfo` / `MethodInfo` / `FieldInfo` | Reflection objects describing a specific member of a `Type` |
| `Activator.CreateInstance` | Creates an object instance dynamically from a `Type`, without a compile-time `new` |
| `BindingFlags` | Flags controlling which members `GetProperties()`/`GetMethods()`/etc. return (public, non-public, static, instance) |

---

