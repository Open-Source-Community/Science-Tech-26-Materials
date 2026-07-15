 
## Part 2 — Interview-Style Questions
[tips for Tech Interviews](https://matthewdbill.medium.com/tips-for-tech-job-interview-preparation-760c8b44ef22)
[Technical interview Prep](https://youtube.com/playlist?list=PLJBO5eOxvWcAytirt2nBWvFyZogOFm-wA&si=J29F8G1-o-epWswg)

**1. "Walk me through what happens, step by step, when you call an overridden method through a base-class reference." Why does this matter in practice?**
> At compile time, C# checks that the base-class type has a method with that signature. At runtime, the CLR looks at the object's *actual* type (not the variable's declared type) and dispatches to the most-derived `override`. This is why a `List<FootballPlayer>` containing a mix of `GoalKeeper` and `Defender` objects correctly calls each one's own `Play()` — it's the mechanism that makes polymorphism useful for things like plugin systems, strategy patterns, and framework callback hooks.
 
**2. "I have a class with a `public List<T> GetItems()` method that just returns the private backing list. What's wrong with that, and how would you fix it?"**
> It leaks a live reference to the internal collection — any caller can `Clear()`, `Add()`, or `Remove()` on it and silently corrupt the object's internal state from outside. Fixes: return `list.AsReadOnly()` (an `IReadOnlyList<T>`), return a defensive copy, or better, implement `IEnumerable<T>` with a `yield return` iterator so callers can only read items one at a time and never touch the backing collection at all.
 
**3. "When would you choose an abstract class over an interface, and vice versa, in a real design?"**
> Abstract class when a family of types shares both an *identity* and reusable implementation — common fields, a constructor, shared helper logic — and you're comfortable committing to single inheritance. Interface when you're describing a *capability* that unrelated types might all need (e.g. `ILogger`, `IDisposable`), since a class can implement any number of interfaces but only extend one abstract class. Many real designs use both together, exactly like `EmailNotification : NotificationChannel, IResilientLogger`.
 
**4. "You override `Equals` on a class but forget `GetHashCode`. What's the actual, concrete bug this causes?"**
> Put the object in a `HashSet<T>` or use it as a `Dictionary<TKey,TValue>` key, then look it up again with an equal-but-different-reference instance. The lookup computes a hash using the *default* (reference-based) `GetHashCode`, lands in the wrong bucket, and the item appears "missing" even though `.Equals()` would say it's there. It's a silent bug — no exception, just wrong behavior, which makes it dangerous in production.
 
**5. "Why would a team choose to use a `record` instead of a `class` for a DTO, and is there ever a downside?"**
> Records give you value equality, immutability, and a readable `ToString()` for free, which matches what a DTO should be — data defined by its content, not its identity. The tradeoff: `with` expressions create shallow copies, so if a record holds a mutable reference type (like a `List<T>`), the "copy" still shares that inner list — you can get surprising aliasing bugs if you're not careful. Also, records aren't a good fit when you need deep, complex inheritance hierarchies with polymorphic behavior — a plain class serves that better.
 
**6. "Explain the difference between a delegate and an event to someone who's only ever used C-style function pointers."**
> A delegate is the strongly-typed function-pointer variable itself — you can assign to it, reassign it entirely, invoke it, or pass it around freely. An event is a delegate *wrapped* with restricted access: outside the declaring class, code can only `+=`/`-=` to the subscriber list. This is deliberate — it stops any external code from calling the event directly (spoofing a publisher) or wiping out other subscribers by assigning `myEvent = someHandler` instead of `+=`.
 
**7. "You subscribe a short-lived object to a long-lived publisher's event and never unsubscribe. What happens, and what's it called?"**
> The event holds a reference to the subscriber's method, which keeps the subscriber object reachable from the publisher's perspective — the GC can't collect it even though your code has otherwise "let go" of it. This is a classic memory leak pattern in C#, sometimes called the "lapsed listener" problem. Fix: explicitly `-=` when done, or use weak event patterns for long-lived publisher / short-lived subscriber relationships.
 
**8. "What's actually happening under the hood with `yield return` — why is it more memory-efficient than building and returning a `List<T>`?"**
> `yield return` gets compiled into a state machine implementing `IEnumerator<T>`. It produces exactly one item per `MoveNext()` call and then suspends — nothing is computed or held in memory ahead of time. Building a `List<T>` up front means materializing and holding *every* item in memory before returning anything, which is wasteful (or infeasible) for large or infinite sequences. `yield` supports true lazy, on-demand iteration.
 
**9. "When should you write your own named delegate type instead of just using `Func<>`/`Action<>`?"**
> When the signature is domain-specific and a name adds real readability — `EmployeeFilterCriteria` communicates intent far better than `Func<Employee, bool>` scattered across a codebase — or when the signature is long enough (`Func<T1, T2, T3, T4, TResult>`) that a named type is genuinely easier to read. Otherwise, prefer the built-in generic delegates; introducing a custom delegate type for a trivial one-off case is unnecessary ceremony.
 
**10. "Give an example of where extension methods make sense over adding a method to the class directly — and one limitation that trips people up."**
> They make sense when you don't own the type — extending a sealed BCL type like `string` or `decimal`, or a type from a NuGet package you can't modify. The limitation: extension methods can't access `private` or `protected` members of the type they extend — they're pure syntactic sugar over static method calls, so they only ever see the type's public/internal surface, same as any outside caller.
 
**11. "Explain generic constraints. Why can't you just write `new T()` inside a generic method without one?"**
> Without a constraint, the compiler doesn't know that whatever type gets substituted for `T` actually *has* a parameterless constructor — some types might only have parameterized constructors, or none at all. `where T : new()` is a promise to the compiler that any type used for `T` will support `new T()`, which the compiler then verifies at every call site.
 
**12. "What's wrong with this code: `public class Repo<T> where T : new() { public T Latest; public T[] All(); }` — from a generics-best-practice standpoint?"**
> Nothing is syntactically wrong, but as a design smell: a bare, unconstrained `T` combined with `new()` means the caller can substitute *any* default-constructible type, which loses meaningful type safety for a repository that presumably should hold entities of a specific shape (e.g. having an ID). A stronger design would constrain `T` to an interface like `IEntity` so the repository can rely on shared members (`T.Id`, etc.) rather than treating `T` as opaque data.
 
**13. "Why does `IEnumerator<T>` extend `IDisposable`?"**
> Because some enumerators hold resources that need cleanup between iterations — a database cursor or a file-backed enumerator, for example. `foreach` automatically calls `Dispose()` on the enumerator when the loop finishes (via a compiler-generated `try`/`finally`), so even enumerators that don't need cleanup must implement `Dispose()` (often as a no-op) to satisfy the interface.
 
**14. "In your own words: why doesn't a garbage-collected language need `Dispose()` for *everything*, only for some things?"**
> The GC only knows how to reclaim *managed* memory — objects it allocated and tracks. Unmanaged resources (OS file handles, sockets, native memory) live outside its knowledge entirely; if you don't explicitly release them, they stay held until the process exits, regardless of whether the wrapping managed object gets collected. `Dispose()` exists specifically to give you a deterministic, on-demand way to release those unmanaged resources instead of waiting on the GC's non-deterministic finalizer pass.
 
**15. "A junior engineer adds a finalizer to every class that implements `IDisposable`, 'just to be safe.' What do you tell them?"**
> That's actively harmful, not just unnecessary: any object with a finalizer survives an extra GC generation before it's actually freed, because it has to be queued and processed by the dedicated finalizer thread first. If the class only owns other `IDisposable` objects (like a `FileStream` or `HttpClient`), those objects already manage their own unmanaged cleanup — the class should just implement `Dispose()`, call `Dispose()` on what it owns, and skip the finalizer entirely. A finalizer belongs only on a class that directly allocates something the GC is blind to, like a raw `IntPtr`.
 
**16. "Why is it unsafe to touch other managed objects from inside a finalizer?"**
> The GC runs finalizers on a dedicated background thread with no guaranteed ordering relative to other objects' finalization. By the time your finalizer runs, other managed objects it might reference could already have been finalized and had their memory reclaimed — touching them is accessing undefined state. Only raw unmanaged handles (like an `IntPtr`, which is just a number) are safe to clean up inside a finalizer.
 
**17. "What does `GC.SuppressFinalize(this)` actually buy you, performance-wise?"**
> Once `Dispose()` has already run the full cleanup, there's nothing left for the finalizer to do — but without `SuppressFinalize`, the object would still sit on the GC's finalization queue, survive an extra collection cycle, and get finalized redundantly. `SuppressFinalize` removes it from that queue immediately, so it's reclaimed in the current, faster GC pass instead of the slower finalizer path.
 
**18. "Difference between `IDisposable` and `IAsyncDisposable` — when would a class need both?"**
> `IDisposable.Dispose()` is synchronous; `IAsyncDisposable.DisposeAsync()` returns a `ValueTask` and lets cleanup await asynchronous operations (like flushing a network stream). A class that wraps a resource used in both sync and async contexts — a stream or connection wrapper consumed by both legacy synchronous code and newer async call sites — often implements both, so callers can pick whichever `using` form (`using` vs `await using`) fits where they are.
 
**19. "What's the practical difference between top-level statements and a traditional `Main` method — does it affect runtime behavior?"**
> No runtime difference — the compiler wraps top-level statements in an implicit `Main` method behind the scenes, so the compiled output is functionally identical either way. The difference is purely about source ergonomics: top-level statements remove boilerplate for small scripts and learning exercises, while an explicit `Main`/`Program` class is often preferred in larger applications for clarity when wiring up DI containers, middleware, or startup configuration.
 
**20. "How would you explain records' 'value equality' to someone who thinks `==` always compares references in C#?"**
> `==` compares references by default for `class` types — that's true for ordinary classes. But it's an operator that *can* be overloaded, and `record` types do exactly that automatically: the compiler generates an `==` overload (plus `Equals`/`GetHashCode`) that compares every declared property's value instead of the reference. So two separately-constructed records with identical property values are `==` to each other, while two ordinary classes constructed the same way would not be, unless someone explicitly overloaded their `==` too.
 
---
