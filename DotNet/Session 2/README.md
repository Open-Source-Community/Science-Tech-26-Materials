# Advanced C# —  Study Guide

**Topics:** OOP · Equality & Operator Overloading · Events · Indexers · Generics · Delegates · Generic Delegates · Extension Methods · IEnumerable & `yield` · Records · Nested Types · Destructors, Finalizers & the Dispose Pattern · Top-Level Statements

---

## Table of Contents

1. [Object-Oriented Programming — The Four Pillars](#1-object-oriented-programming--the-four-pillars)
2. [Abstract Classes vs Interfaces](#2-abstract-classes-vs-interfaces)
3. [Equals, GetHashCode, and Operator Overloading](#3-equals-gethashcode-and-operator-overloading)
4. [Events and the Publisher–Subscriber Pattern](#4-events-and-the-publishersubscriber-pattern)
5. [Indexers](#5-indexers)
6. [Generics](#6-generics)
7. [Delegates](#7-delegates)
8. [Generic Delegates — `Action`, `Func`, `Predicate`](#8-generic-delegates--action-func-predicate)
9. [Extension Methods](#9-extension-methods)
10. [IEnumerable and `yield`](#10-ienumerable-and-yield)
11. [Records — The Immutable Data Carrier](#11-records--the-immutable-data-carrier)
12. [Nested Types](#12-nested-types)
13. [Destructors, Finalizers, and the Dispose Pattern](#13-destructors-finalizers-and-the-dispose-pattern)
14. [Top-Level Statements](#14-top-level-statements)
15. [Quick Reference — Keyword Glossary](#15-quick-reference--keyword-glossary)

---
[Metigator - mastering C#](https://youtube.com/playlist?list=PL4n1Qos4Tb6SWPbJNpiznp-Ok4A8J_23l&si=x5z1IYnFzFRF1cJq) you can find almost all the topics we talked about here.

## 1. Object-Oriented Programming — The Four Pillars

OOP is a way of organizing code around **objects** — things that bundle together *data* (fields/properties) and *behavior* (methods). C# is built on four core OOP pillars.

### 1.1 The Four Pillars

| Pillar | One-line definition | Where you see it in our code |
|---|---|---|
| **Encapsulation** | Hide internal data; expose only what is needed | `private` fields + `public` properties in `FootballPlayer` |
| **Inheritance** | A child class reuses and extends a parent class | `GoalKeeper : FootballPlayer`, `Defender : OutfieldPlayer` |
| **Polymorphism** | One interface, many implementations | Calling `player.Play()` on a list of mixed player types |
| **Abstraction** | Expose *what* something does, hide *how* | `abstract void Play()` — callers don't care about the implementation |

### 1.2 Encapsulation — Private Fields with Public Properties

```csharp
public abstract class FootballPlayer
{
    // PRIVATE FIELD — the actual storage. Nobody outside can touch this directly.
    private string name;
    private int jerseyNumber;
    private bool isInjured;

    // PUBLIC PROPERTY — the controlled doorway into the private field.
    public string Name
    {
        get { return name; }   // anyone can READ
        set { name = value; }  // anyone can WRITE (you can add validation here later)
    }

    public bool IsInjured
    {
        get { return isInjured; }
        set { isInjured = value; }
    }
}
```

**Why bother with properties if get/set just pass the value through?**
Right now they do. But the moment you need validation — "jersey number must be between 1 and 99" — you add it *once* inside the setter and every caller benefits automatically. Direct field access gives you no such hook.

**Q: What is `value` inside a setter?**
`value` is a hidden keyword C# injects automatically. It holds whatever the caller is trying to assign. `player.Name = "Salah"` → inside the setter, `value` equals `"Salah"`.

**Q: Can I make a property read-only?**
Yes — omit the `set` block entirely, or write `private set` to allow setting only from within the class.

**Q: Encapsulation vs Data Hiding**

| Concept | Core Objective | C# Mechanism Used |
|---|---|---|
| Encapsulation | Grouping fields and behaviors together. | `class`, `struct` |
| Data Hiding | Preventing direct, unauthorized access. | `private`, `protected`, C# properties |

### 1.3 Access Modifier Matrix

| Access Modifier | Same Class | Derived Class (Same Assembly) | Non-Derived Class (Same Assembly) | Derived Class (Different Assembly) | Non-Derived Class (Different Assembly) |
|---|:---:|:---:|:---:|:---:|:---:|
| `public` | Yes | Yes | Yes | Yes | Yes |
| `internal` | Yes | Yes | Yes | No | No |
| `protected` | Yes | Yes | No | Yes | No |
| `private` | Yes | No | No | No | No |
| `protected internal` | Yes | Yes | Yes | Yes | No |
| `private protected` | Yes | Yes | No | No | No |

### 1.4 Inheritance

```csharp
// FootballPlayer is the BASE CLASS (parent)
public abstract class FootballPlayer { /* ... */ }

// GoalKeeper INHERITS from FootballPlayer using the colon syntax
internal class GoalKeeper : FootballPlayer
{
    private int cleanSheets;
    public int CleanSheets { get { return cleanSheets; } set { cleanSheets = value; } }

    // The 'base(name, jerseyNumber)' call forwards arguments to the parent constructor
    public GoalKeeper(string name, int jerseyNumber, int cleanSheets)
        : base(name, jerseyNumber)
    {
        CleanSheets = cleanSheets;
    }
}
```

**Key keyword: `base`**
- In a constructor: `base(name, jerseyNumber)` calls the parent's constructor. You *must* do this if the parent has no parameterless constructor.
- In a method: `base.ScoreGoal()` calls the parent's version of the method before adding extra behavior.

**Inheritance chain in our code:**

```
FootballPlayer  (abstract base)
    ├── GoalKeeper
    └── OutfieldPlayer
            └── Defender
```

`Defender` inherits from `OutfieldPlayer`, which inherits from `FootballPlayer`. This means `Defender` automatically has `Name`, `JerseyNumber`, `Goals`, `Assists`, and can also be stored in a `List<FootballPlayer>`.

**Q: Can a class inherit from two base classes at once?**
No. C# only allows single inheritance for classes. A class can, however, implement multiple interfaces (Section 2).

### 1.5 Polymorphism and `abstract` / `virtual` / `override`

These three keywords are the engine of polymorphism:

| Keyword | Where it goes | What it means |
|---|---|---|
| `abstract` | Base class method | "I have no body here. Every child class MUST provide one." |
| `virtual` | Base class method | "I have a default body, but child classes MAY replace it." |
| `override` | Child class method | "I am replacing the parent's abstract or virtual version." |
| `sealed` | Base class method | "I am overriding the parent version, but no deeper child class may override me again." |

```csharp
// In FootballPlayer (the abstract base):
public abstract void Play();        // No body — MUST be overridden
public virtual void Train()         // Has a body — CAN be overridden
{
    Console.WriteLine($"{Name} is training hard!");
}

// In GoalKeeper (the child):
public override void Play()         // Replaces the abstract version
{
    Console.WriteLine($"{Name}: Saving goals!");
}

public override void Train()        // Replaces the virtual version
{
    Console.WriteLine($"{Name} is practicing penalty saves!");
}
```

**Polymorphism in action — `StartMatch()`:**

```csharp
public void StartMatch()
{
    foreach (var player in Players)  // Players is List<FootballPlayer>
    {
        if (!player.IsInjured)
        {
            player.Play();  // C# calls the RIGHT version at runtime!
        }
    }
}
```

Even though the list is typed as `FootballPlayer`, C# looks at the *actual object* at runtime and calls its specific `Play()`. A `GoalKeeper` prints "Saving goals!"; an `OutfieldPlayer` prints "Playing on the field!". This is **runtime polymorphism** (dynamic dispatch).

**Q: What happens if I forget `override` and just write `public void Play()` in the child?**
The compiler warns you, and the method **hides** the parent's version instead of replacing it. If the object is stored as `FootballPlayer`, the parent version runs instead of yours. Always use `override` explicitly.

### 1.6 The `Team` Class — Composition and List Management

```csharp
internal class Team
{
    private string teamName;
    private List<FootballPlayer> players;  // Team CONTAINS players — this is composition

    public void AddPlayer(FootballPlayer player)
    {
        // Guard: jersey number must be unique
        foreach (var p in Players)
        {
            if (p.JerseyNumber == player.JerseyNumber)
            {
                Console.WriteLine($"Jersey #{player.JerseyNumber} is already taken!");
                return;
            }
        }
        Players.Add(player);
    }

    public bool RemovePlayer(int jerseyNumber)
    {
        // List.Find() searches using a lambda (covered in the Delegates section)
        FootballPlayer player = Players.Find(p => p.JerseyNumber == jerseyNumber);
        if (player != null)
        {
            Players.Remove(player);
            return true;
        }
        return false;
    }
}
```

**The encapsulation problem in `GetPlayers()`:**

```csharp
// DANGEROUS version (what the code currently has)
public List<FootballPlayer> GetPlayers()
{
    return players;  // Returns the ACTUAL internal list
}

// Caller can now destroy your team:
var players = team.GetPlayers();
players.Clear();  // Your list is now empty!
```

**The fix — return a read-only copy:**

```csharp
public IReadOnlyList<FootballPlayer> GetPlayers()
{
    return players.AsReadOnly();  // Caller can read but NOT modify
}
```

### 1.7 Type Checking: `is` and Pattern Matching

```csharp
// In DisplaySquad():
if (player is GoalKeeper gk)
{
    // 'gk' is already cast — no separate cast line needed
    Console.WriteLine($"Clean Sheets: {gk.CleanSheets}");
}
else if (player is OutfieldPlayer of)
{
    Console.WriteLine($"Goals: {of.Goals}, Assists: {of.Assists}");
}
```

`is` checks the runtime type AND simultaneously casts into a new variable (`gk`, `of`). This is **pattern matching** (C# 7+). It replaces the older `as` keyword + null-check pattern.

---

## 2. Abstract Classes vs Interfaces

### 2.1 Abstract Classes — "What You ARE"

An abstract class defines the **identity** of a family of objects. It can contain:
- Concrete (implemented) methods
- Abstract (unimplemented) methods
- Fields and constructors
- `protected` members shared between parent and children

```csharp
public abstract class NotificationChannel
{
    // STATE — an abstract class can hold data
    protected string _connectionString;

    // CONSTRUCTOR — abstract classes can have constructors
    protected NotificationChannel(string connectionString)
    {
        _connectionString = connectionString;
    }

    // CONCRETE METHOD — shared logic every child inherits for free
    protected string FormatMessage(string message)
    {
        return $"[{DateTime.UtcNow:yyyy-MM-dd}]: {message}";
    }

    // ABSTRACT METHOD — child classes must implement their own version
    public abstract void Send(string message);
}
```

**Rule:** A class can inherit from **only one** abstract class.

### 2.2 Interfaces — "What You CAN DO"

An interface defines a **capability** or **contract**. It contains only method/property *signatures* — no implementation, no fields, no constructor.

```csharp
public interface IResilientLogger
{
    void LogDiagnostic(string logData);  // No body — just the contract
}
```

**A class can implement multiple interfaces:**

```csharp
// EmailNotification IS a NotificationChannel (abstract class — identity)
// AND it CAN DO logging (interface — capability)
public class EmailNotification : NotificationChannel, IResilientLogger
{
    public override void Send(string message)
    {
        string readyMessage = FormatMessage(message);  // calls inherited concrete method
        Console.WriteLine($"Sending Email to {RecipientEmail}: {readyMessage}");
    }

    public void LogDiagnostic(string logData)  // implements the interface
    {
        Console.WriteLine($"[LOG-DISK]: {logData}");
    }
}
```

**Using the interface type:**

```csharp
IResilientLogger logger = emailService;  // emailService IS an IResilientLogger
logger.LogDiagnostic("Email pipeline verified.");
// Through this variable, you can ONLY call LogDiagnostic — nothing else
```

### 2.3 Decision Table

| Question | Abstract Class | Interface |
|---|---|---|
| Can it hold fields/state? | Yes | No |
| Can it have a constructor? | Yes | No |
| Can it have concrete methods? | Yes | Only as `default` implementations (C# 8+) |
| Multiple inheritance? | One base class only | As many interfaces as you need |
| Use when... | Objects share identity + code | Objects share a capability/contract |

**Q: Why does the interface name start with `I`?**
It's a C# naming convention (Hungarian notation for interfaces). The compiler does not enforce it, but every professional C# codebase follows it. `IEnumerable`, `IDisposable`, `IList` — you will see this everywhere in the .NET standard library.

**Q: Can an abstract class implement an interface?**
Yes. The abstract class can implement it partially, and the first concrete child must implement the rest.

[Dev Community - OOP concepts](https://dev.to/thedsdev/oop-concepts-c-mastering-4bn5)
[tutorialsEU - abstract vs interfaces (English Video)](https://youtu.be/5p415gz2KBY?si=ZtAxhVpQVU0-Ir4b)
[Codegraphia - Abstract Vs interfaces (Arabic Video)](https://youtu.be/BpOVuBkDDRA?si=HXit2V1Ocm4gJpDs)
[GeeksForGeeks -difference-between-abstract-class-and-interface-in-c-sharp (English Web)](https://www.geeksforgeeks.org/c-sharp/difference-between-abstract-class-and-interface-in-c-sharp/)

---

## 3. Equals, GetHashCode, and Operator Overloading

### 3.1 Why This Matters

Every C# object inherits three virtual members from `System.Object`: `Equals(object)`, `GetHashCode()`, and `ToString()`. By default, `Equals` on a reference type compares **references** — two objects with identical data are still "not equal" unless they are the literal same object in memory.

```csharp
public class Player
{
    public string Name { get; set; }
    public int JerseyNumber { get; set; }
}

var p1 = new Player { Name = "Salah", JerseyNumber = 10 };
var p2 = new Player { Name = "Salah", JerseyNumber = 10 };

Console.WriteLine(p1 == p2);        // False — different references
Console.WriteLine(p1.Equals(p2));   // False — default Equals is also reference equality
```

For many real-world types (DTOs, value objects, coordinates, money amounts) you want **value equality** instead: two objects with the same data should be considered equal. That requires overriding `Equals` and `GetHashCode` together — and optionally the `==`/`!=` operators.

### 3.2 Overriding `Equals`

```csharp
public class Player
{
    public string Name { get; set; }
    public int JerseyNumber { get; set; }

    public override bool Equals(object obj)
    {
        // 1. Reference check — fast path, also handles comparing to itself
        if (ReferenceEquals(this, obj)) return true;

        // 2. Type check — must be the same runtime type
        if (obj is not Player other) return false;

        // 3. Field-by-field value comparison
        return Name == other.Name && JerseyNumber == other.JerseyNumber;
    }
}
```


### 3.3 Overriding `GetHashCode` — The Contract

**The rule that must never be broken:** if `a.Equals(b)` is `true`, then `a.GetHashCode()` **must** equal `b.GetHashCode()`. Hash-based collections (`Dictionary<TKey,TValue>`, `HashSet<T>`) rely on this — they use the hash to find the right bucket, then `Equals` to confirm the match. Break the contract and items silently "disappear" from dictionaries and sets.

The reverse is **not** required: two unequal objects are allowed to share a hash code (a "collision") — it just makes lookups slightly slower, not incorrect.

```csharp
public override int GetHashCode()
{
    // HashCode.Combine mixes the fields used in Equals into one well-distributed hash
    return HashCode.Combine(Name, JerseyNumber);
}
```

**Rule of thumb:** `GetHashCode` should combine exactly the same fields that `Equals` compares — no more, no less. Never build a hash from a mutable field that can change after the object is placed in a `HashSet` or used as a `Dictionary` key; the object becomes unfindable once the field changes.

### 3.4 Overriding `==` and `!=`

Overriding `Equals` does **not** change what `==` does for a `class`. You must overload the operators separately if you want `==` to use value semantics:

```csharp
public class Player
{
    public string Name { get; set; }
    public int JerseyNumber { get; set; }

    public override bool Equals(object obj) => Equals(obj as Player);

    public bool Equals(Player other) =>
        other is not null && Name == other.Name && JerseyNumber == other.JerseyNumber;

    public override int GetHashCode() => HashCode.Combine(Name, JerseyNumber);

    // Operator overloads — the 'operator' keyword, always static
    public static bool operator ==(Player left, Player right)
    {
        if (left is null) return right is null;
        return left.Equals(right);
    }

    public static bool operator !=(Player left, Player right) => !(left == right);
}
```

**Rules for operator overloading:**
- Operators are always `public static`.
- If you overload `==` you **must** also overload `!=` — the compiler forces this.
- If you overload `==` you **should** also override `Equals` and `GetHashCode`, so all three ways of comparing (`==`, `.Equals()`, use as a dictionary key) agree with each other.
- Implementing `IEquatable<T>` (the typed `Equals(Player other)` above) avoids boxing and is the pattern most of the .NET base class library follows.

### 3.5 `struct` Types Get Value Equality for Free — Almost

A `struct`'s default `Equals` already compares field values (via reflection), not references — but it is slow, because it uses reflection unless you override it. For any `struct` used in performance-sensitive code (loops, collections), override `Equals`/`GetHashCode` yourself rather than relying on the default.

### 3.6 The Easy Way Out — Records

Manually keeping `Equals`, `GetHashCode`, `==`, and `!=` in sync is easy to get wrong (forgetting a field in one but not the other is a classic bug). **Records** (Section 11) generate all of this automatically and correctly from the properties you declare — which is exactly why records exist. If a type's whole job is to hold comparable data, prefer a record over hand-rolling this section's code.

**Q: Do I need to override `Equals`/`GetHashCode` on every class?**
No — only on types where **value equality** makes sense (DTOs, value objects, keys). Types with identity (a `Session`, a `Team`, an entity with a database ID) usually keep reference equality, which is the default.

**Q: What if I override `Equals` but forget `GetHashCode`?**
The compiler only issues a warning, not an error. At runtime, the object may behave correctly with `.Equals()` but corrupt in a `HashSet<T>` or as a `Dictionary` key. Always override both together.

[Coding Stage (English Video)](https://youtu.be/zbLXiPYqbUY?si=5PJNH4eQoLw2wWcL)
[Operator overloading (English Video)](https://youtu.be/Q1fdxmwriB4?si=UNLoZSoBodU2cTDg)
[AlgoMaster - Equals vs GetHashCode (English Web) very very nice](https://algomaster.io/learn/csharp/equals-gethashcode)

---

## 4. Events and the Publisher–Subscriber Pattern

### 4.1 The Concept

The **publisher–subscriber** (pub/sub) pattern decouples the thing that *raises* an event from the things that *react* to it. The publisher does not know who is listening — it just fires the event. Subscribers sign up and handle it independently.

In our football code: `Match` is the publisher. `Coach`, `Fans`, and `Commentator` are subscribers.

### 4.2 Declaring Events

```csharp
public class Match
{
    // An event is declared with the 'event' keyword + a delegate type
    // Action<string> means "a method that takes one string and returns void"
    public event Action<string> GoalScored;
    public event Action<string> PlayerInjured;
    public event Action MatchStarted;   // Action with no parameters
    public event Action MatchEnded;
}
```

**`event` keyword:** Wraps a delegate to restrict access. From outside the class, you can only `+=` (subscribe) or `-=` (unsubscribe). You cannot invoke the event or overwrite all subscribers from outside.

### 4.3 Raising Events

```csharp
public void ScoreGoal(string scorer)
{
    Console.WriteLine($"GOAL! {scorer} scores!");
    GoalScored?.Invoke(scorer);  // Raise event — notify all subscribers
}
```

**`?.Invoke()`** — the `?.` is a **null-conditional operator**. If nobody has subscribed, `GoalScored` is `null`, and calling `null.Invoke()` would crash. The `?.` skips the call entirely when null. Always use this pattern when raising an event.

### 4.4 Subscribing and Unsubscribing

```csharp
var match = new Match();
var coach = new Coach("Koller");

// SUBSCRIBE — += adds a method to the event's invocation list
match.GoalScored += coach.OnGoalScored;
match.GoalScored += fans.OnGoalScored;
match.GoalScored += commentator.OnGoalScored;

// INLINE LAMBDA SUBSCRIBER — no need to write a separate method
match.GoalScored += (scorer) =>
    Console.WriteLine($"TWEET: {scorer} scores! #Football");

// UNSUBSCRIBE — -= removes a specific method from the list
match.GoalScored -= coach.OnGoalScored;  // Coach stops listening
```

When `GoalScored` fires, *every* subscribed method is called in the order it was added. After the unsubscribe, `coach.OnGoalScored` is no longer in the list and will not be called again.

**Q: What happens if I forget to unsubscribe?**
The event holds a reference to the subscriber object, which prevents the garbage collector from cleaning it up. This is a common source of **memory leaks** in C#. Always unsubscribe when you no longer need to listen, especially with long-lived publishers.

**Q: Can I raise an event from outside the class?**
No — the `event` keyword prevents this. Only the class that declares the event can call `.Invoke()` on it.

[Piece of Cake dev - Events & observer pattern (Arabic video)](https://youtu.be/kdMJcL5BUdk?si=KjkKsramYG4fl5ir)
[C# Corner-  publisher-or-subscriber-pattern-with-event-or-delegate-and-e (english web)](https://www.c-sharpcorner.com/UploadFile/pranayamr/publisher-or-subscriber-pattern-with-event-or-delegate-and-e/)

---

## 5. Indexers

### 5.1 The Problem — Methods Are Clunky for Array-Like Access

```csharp
// Without an indexer — you have to use named methods
public class DataBufferCache
{
    private readonly string[] _internalStorage = new string[10];

    public string GetValue(int index)
    {
        if (index < 0 || index >= _internalStorage.Length) return null;
        return _internalStorage[index];
    }

    public void SetValue(int index, string value)
    {
        if (index >= 0 && index < _internalStorage.Length && value is not null)
            _internalStorage[index] = value;
    }
}

// Usage — clunky!
DataBufferCache cache = new DataBufferCache();
cache.SetValue(0, "Data A");
cache.SetValue(1, "Data B");
string data = cache.GetValue(0);
```

This works, but it does not look or feel like array access. An **indexer** lets you use `[]` bracket syntax directly on your own class, just like an array or dictionary.

### 5.2 Defining an Indexer

```csharp
public class DataBufferCache
{
    private readonly string[] _internalStorage = new string[10];

    // INDEXER SYNTAX: access modifier + return type + 'this' + [parameter type]
    public string? this[int index]
    {
        get
        {
            if (index < 0 || index >= _internalStorage.Length) return null;
            return _internalStorage[index];
        }
        set
        {
            // 'value' here is the right-hand side of the assignment, same as a property setter
            if (index >= 0 && index < _internalStorage.Length && value is not null)
            {
                _internalStorage[index] = value;
            }
        }
    }
}
```

**Using it:**

```csharp
cache[0] = "Telemetry_Packet_A";  // Calls the setter — index=0, value="Telemetry_Packet_A"
cache[1] = "Telemetry_Packet_B";

string datum = cache[0];          // Calls the getter — returns "Telemetry_Packet_A"
string? invalid = cache[99];      // Getter returns null (out-of-range guard)
```

### 5.3 String-Keyed Indexer

Indexers are not limited to integers. You can use any type as the parameter:

```csharp
public class HttpHeaderCollection
{
    private readonly Dictionary<string, string> _headerStore = new Dictionary<string, string>();

    // String key indexer — works like Dictionary but with [] syntax on the class itself
    public string this[string headerName]
    {
        get => _headerStore.TryGetValue(headerName, out var val) ? val : "Not Found";
        set => _headerStore[headerName] = value;
    }
}

// Usage:
var headers = new HttpHeaderCollection();
headers["Content-Type"] = "application/json";  // calls setter
string ct = headers["Content-Type"];            // calls getter → "application/json"
```

**Q: Can a class have more than one indexer?**
Yes — as long as the parameter types differ (method overloading rules apply). You could have `this[int index]` and `this[string key]` on the same class.

**Q: What is the difference between an indexer and a property?**
A property has a fixed name and no parameters. An indexer uses the keyword `this` and takes at least one parameter. Both use `get`/`set` blocks and the `value` keyword in the setter.
[Piece Of cake dev - Indexer](https://youtu.be/IidK7FW1j3w?si=0z68acdquOX7zE0C)
[Tutorials Point- indexers (english Web)](https://www.tutorialspoint.com/csharp/csharp_indexers.htm)

---

## 6. Generics

### 6.1 The Problem Generics Solve

Imagine writing a stack for integers. Then you need one for strings. Then for `SystemUser`. Without generics, you write three nearly identical classes — or you use `object`, which throws away type safety and requires casting.

```csharp
// WITHOUT GENERICS — dangerous, casting errors only appear at runtime
public class UnsafeStack
{
    private object[] _storage = new object[10];
    public void Push(object item) { /* ... */ }
    public object Pop() { /* ... */ }
}

UnsafeStack s = new UnsafeStack();
s.Push(42);
string val = (string)s.Pop();  // Compiles fine. Crashes at runtime!
```

### 6.2 Defining a Generic Class

```csharp
// T is the TYPE PARAMETER — a placeholder the compiler replaces at compile time
public class SecureStack<T>
{
    private readonly T[] _storage;
    private int _topIndex = -1;

    public SecureStack(int capacity)
    {
        _storage = new T[capacity];  // Allocates the right-sized, right-typed array
    }

    public void Push(T item)
    {
        if (_topIndex >= _storage.Length - 1)
            throw new StackOverflowException("Stack allocation boundary breached.");
        _topIndex++;
        _storage[_topIndex] = item;
    }

    public T Pop()
    {
        if (_topIndex < 0)
            throw new InvalidOperationException("Stack is empty.");

        T item = _storage[_topIndex];
        _storage[_topIndex] = default(T);  // Reset slot to default value (0 for int, null for objects)
        _topIndex--;
        return item;
    }
}
```

### 6.3 Using the Generic Class

```csharp
// The compiler substitutes int everywhere T appears
SecureStack<int> transactionIds = new SecureStack<int>(3);
transactionIds.Push(1042);
transactionIds.Push(5081);
int lastId = transactionIds.Pop();  // Returns int — no casting needed

// transactionIds.Push("text");  // Compiler error — caught immediately

// The SAME class blueprint with a completely different type
SecureStack<SystemUser> userSessionStack = new SecureStack<SystemUser>(5);
userSessionStack.Push(new SystemUser("Mariam", "Admin"));
SystemUser activeUser = userSessionStack.Pop();  // Returns SystemUser — no casting needed
```

**`default(T)`** — returns the default value for whatever type T is:
- `default(int)` → `0`
- `default(bool)` → `false`
- `default(string)` → `null`
- `default(SystemUser)` → `null`

### 6.4 Generic Constraints

You can restrict what types `T` can be:

```csharp
// T must be a class (reference type)
public class Repository<T> where T : class { }

// T must implement IComparable<T>
public class SortedCollection<T> where T : IComparable<T> { }

// T must have a parameterless constructor
public class Factory<T> where T : new() { }

// Multiple constraints
public class AdvancedStore<T> where T : class, IComparable<T>, new() { }
```

**Q: What is the difference between `<T>` and `<object>`?**
Using `<object>` accepts anything but loses type safety — you must cast on the way out and can get runtime errors. `<T>` preserves the exact type throughout, and the compiler checks everything at compile time.

**Q: Can methods also be generic, not just classes?**
Yes. `public T GetMax<T>(T a, T b) where T : IComparable<T>` is a generic method on a non-generic class.

[Tutorials Point - Generics](https://www.tutorialspoint.com/csharp/csharp_generics.htm)

---

## 7. Delegates

### 7.1 What Is a Delegate?

A **delegate** is a type that holds a reference to a method — like a variable that stores a function. It defines a *contract*: any method plugged in must match the signature (same return type, same parameter types).

**Why use delegates?**

```csharp
// Problem: we want to filter employees differently
public class EmployeeReportGenerator
{
    // BAD: hard-coded filtering — not flexible!
    public void GenerateSeniorReport(List<Employee> employees)
    {
        foreach (var emp in employees)
        {
            if (emp.YearsOfService >= 5)  // Filter is FIXED
            {
                Console.WriteLine($"{emp.Name}");
            }
        }
    }

    // BAD: another hard-coded filter!
    public void GenerateHighSalaryReport(List<Employee> employees)
    {
        foreach (var emp in employees)
        {
            if (emp.Salary >= 60000)  // Filter is FIXED
            {
                Console.WriteLine($"{emp.Name}");
            }
        }
    }
    // We'd need a new method for every filter!
}
```

```csharp
// DECLARING a delegate type
// "Any method that takes an Employee and returns bool can be stored here"
public delegate bool EmployeeFilterCriteria(Employee emp);
```

Think of it as a "method shape". Once defined, you can store any matching method in a variable of this type.

### 7.2 Using a Delegate with a Named Method

```csharp
// TARGET METHOD — matches the delegate signature exactly
static bool IsSeniorStaff(Employee emp)
{
    return emp.YearsOfService >= 5;
}

// STORING the method reference in a delegate variable
EmployeeFilterCriteria seniorFilter = IsSeniorStaff;

// PASSING the delegate as a parameter
reportEngine.GenerateReport(staff, "Tenure Report", seniorFilter);
```

### 7.3 The Report Engine — Delegates Enable Decoupling

```csharp
public class EmployeeReportGenerator
{
    // The method accepts a delegate — it doesn't know what the filter logic is!
    public void GenerateReport(List<Employee> employees, string reportTitle,
                                EmployeeFilterCriteria filter)
    {
        foreach (var emp in employees)
        {
            if (filter != null && filter(emp))  // INVOKE the delegate like a method call
            {
                Console.WriteLine($" -> {emp.Name} | {emp.Department} | ${emp.Salary:N0}");
            }
        }
    }
}
```

The engine is completely decoupled from the filtering logic. You can reuse it with any filter you write — today and in the future — without modifying `GenerateReport` at all.

### 7.4 Lambda Expressions — Inline Delegate Bodies

Instead of writing a separate named method, you can define the method logic inline:

```csharp
// Lambda syntax: (parameters) => expression_or_block
reportEngine.GenerateReport(staff, "High-Earner Tech Report",
    emp => emp.Salary >= 60000 && emp.Department == "Tech");
```

This is exactly equivalent to writing:

```csharp
static bool HighEarnerTechFilter(Employee emp)
{
    return emp.Salary >= 60000 && emp.Department == "Tech";
}
```

Lambda expressions are not a different feature — they are shorthand for creating anonymous delegate implementations.

### 7.5 Multicast Delegates

A delegate can hold references to *multiple* methods. When invoked, all of them run.

```csharp
EmployeeFilterCriteria filter = IsSeniorStaff;
filter += IsHighEarner;  // Now holds two methods
filter(emp);             // BOTH methods are called in order
```

**Note:** for delegates with return values, only the *last* method's return value is captured. This is why events (Section 4) use `void`-returning delegates — they are designed specifically for multicast use.

**Q: What is the difference between a delegate and an event?**
A delegate is just a type that stores method references. An event *wraps* a delegate and adds safety: external code can only `+=` or `-=`, never replace all subscribers or invoke the event directly. Events are the safe, convention-enforced way to use multicast delegates.

**Q: Is `List.Find(p => p.JerseyNumber == jerseyNumber)` using a delegate?**
Yes. `List<T>.Find()` takes a `Predicate<T>` parameter, a built-in generic delegate. The lambda you pass is the method body — you are creating and passing a delegate on the spot.

---

## 8. Generic Delegates — `Action`, `Func`, `Predicate`

.NET provides three ready-made generic delegate types so you rarely need to declare your own.

### 8.1 `Action<T>` — "Do something, return nothing"

```csharp
// Action with no parameters
public event Action MatchStarted;
MatchStarted?.Invoke();

// Action<string> — takes one string, returns void
public event Action<string> GoalScored;
GoalScored?.Invoke("Salah");

// Action<string, int> — takes two parameters, returns void
Action<string, int> printScore = (name, score) =>
    Console.WriteLine($"{name}: {score}");
```

### 8.2 `Func<T, TResult>` — "Do something, return a value"

```csharp
// Func<Employee, bool> — takes Employee, returns bool
// The LAST type parameter is always the return type
Func<Employee, bool> isHighEarner = emp => emp.Salary > 70000;
bool result = isHighEarner(someEmployee);

// Func<int, int, int> — takes two ints, returns an int
Func<int, int, int> add = (a, b) => a + b;
int sum = add(3, 4);  // sum = 7
```

### 8.3 `Predicate<T>` — "Is this true for T?"

```csharp
// Predicate<T> is equivalent to Func<T, bool>
// It's used by List<T>.Find(), List<T>.RemoveAll(), etc.
Predicate<Employee> isSenior = emp => emp.YearsOfService >= 5;
Employee found = staff.Find(isSenior);

// Or inline:
Employee found2 = staff.Find(emp => emp.YearsOfService >= 5);
```

### 8.4 Comparison Table

| Type | Signature equivalent | Used for |
|---|---|---|
| `Action` | `void Method()` | Side effects, no return value |
| `Action<T>` | `void Method(T arg)` | Side effects with input |
| `Func<T, TResult>` | `TResult Method(T arg)` | Transformations, calculations |
| `Predicate<T>` | `bool Method(T arg)` | Filtering, testing a condition |

**Q: When should I declare my own delegate type vs using `Action`/`Func`?**
Use `Action`/`Func` for simple cases — they cover most scenarios. Declare a named delegate type when you want to give the concept a meaningful name in your domain (like `EmployeeFilterCriteria`) for readability, or when the signature is too complex to read clearly as `Func<..., ..., ..., bool>`.
[Piece of Cake dev - all about Delegates](https://youtube.com/playlist?list=PLfHpC6JZ316dwb3MN8W6XuBuyaTA8RVaW&si=d_EehMfCg_I5BQA_)


---

## 9. Extension Methods

### 9.1 The Problem — You Cannot Modify Sealed Classes

Sometimes you want to add methods to a class you do not own — a third-party library class, or a sealed .NET class. Extension methods let you add methods to existing types *without inheriting from them or modifying their source*.

### 9.2 Syntax Rules

An extension method must be:
1. In a **`static` class**
2. Itself a **`static` method**
3. Its first parameter must have the **`this`** keyword followed by the type being extended

```csharp
public static class DecimalExtensions
{
    // 'this decimal amount' — extends the decimal type
    public static string ToCurrency(this decimal amount)
    {
        return $"${amount:F2}";
    }

    public static decimal GetTax(this decimal salary)
    {
        if (salary > 10000) return salary * 0.30m;
        if (salary > 5000)  return salary * 0.20m;
        return salary * 0.10m;
    }
}
```

**Using them — they look like instance methods:**

```csharp
decimal salary = 7500m;

// Calling as extension methods — the compiler rewrites these internally:
Console.WriteLine(salary.ToCurrency());      // → "$7500.00"
Console.WriteLine(salary.GetTax());          // → 1500.00

// These are exactly equivalent to:
Console.WriteLine(DecimalExtensions.ToCurrency(salary));
Console.WriteLine(DecimalExtensions.GetTax(salary));
```

### 9.3 The C# 14 `extension` Block Syntax

A newer syntax exists for grouping extension methods:

```csharp
public static class EmployeeExtensions
{
    extension(Employee employee)   // Groups all extensions for Employee together
    {
        public bool IsSenior()
        {
            return employee.YearsOfService >= 5;
        }

        public decimal GetBonus()
        {
            if (employee.YearsOfService >= 10) return employee.Salary * 0.20m;
            if (employee.YearsOfService >= 5)  return employee.Salary * 0.10m;
            return employee.Salary * 0.05m;
        }

        public void GiveRaise(decimal percentage)
        {
            decimal raiseAmount = employee.Salary * (percentage / 100);
            employee.Salary += raiseAmount;
        }
    }
}
```

Inside the block, `employee` is the instance being extended. Usage is the same: `someEmployee.IsSenior()`, `someEmployee.GetBonus()`.

**Q: Can extension methods access private members of the class they extend?**
No. They can only access `public` and `internal` members. They are syntactic sugar — behind the scenes they are just static method calls.

**Q: What if the class already has a method with the same name?**
The class's own method always wins. Extension methods only apply when the class has no matching instance method.

**Q: Where does .NET itself use extension methods?**
Everywhere in LINQ. `list.Where(...)`, `list.Select(...)`, `list.OrderBy(...)` are all extension methods defined in `System.Linq.Enumerable` on `IEnumerable<T>`.

[Tutorials Point - Extension Methods](https://www.tutorialspoint.com/csharp/csharp_extension_methods.htm)

---

## 10. IEnumerable and `yield`

### 10.1 The Problem — Exposing Internal Collections Safely

The `Team` class has a private `List<FootballPlayer>`. Currently:

```csharp
public List<FootballPlayer> GetPlayers()
{
    return players;  // DANGER: caller gets the real list and can destroy it
}

// Caller can do this:
var list = team.GetPlayers();
list.Clear();  // Team is now empty!
```

Also, because `Team` does not implement `IEnumerable`, you cannot use `foreach` on it directly:

```csharp
foreach (var p in team) { }  // Compiler error
```

### 10.2 What Is `IEnumerable<T>`?

`IEnumerable<T>` is an interface from the .NET base class library. It has one method:

```csharp
public interface IEnumerable<T>
{
    IEnumerator<T> GetEnumerator();
}
```

`foreach` is compiled by C# into calls to `GetEnumerator()`. If your class implements `IEnumerable<T>`, `foreach` works on it.

### 10.3 The Hard Way — Manual `IEnumerator` Implementation

```csharp
// Nested enumerator class — lots of boilerplate!
private class TeamEnumerator : IEnumerator<FootballPlayer>
{
    private Team _team;
    private int _position = -1;

    public TeamEnumerator(Team team) { _team = team; }

    public FootballPlayer Current
    {
        get
        {
            if (_position < 0 || _position >= _team._count)
                throw new InvalidOperationException();
            return _team.players[_position];
        }
    }

    object IEnumerator.Current => Current;

    public bool MoveNext()
    {
        _position++;
        return _position < _team._count;
    }

    public void Reset() { _position = -1; }
    public void Dispose() { }
}
```

This is correct — but it is a lot of code for a simple problem.

### 10.4 The Easy Way — `yield return`

`yield` lets C# generate the `IEnumerator` state machine automatically:

```csharp
// Make Team implement IEnumerable<FootballPlayer>
internal class Team : IEnumerable<FootballPlayer>
{
    private List<FootballPlayer> players;

    // C# generates the entire state machine from this — no nested class needed
    public IEnumerator<FootballPlayer> GetEnumerator()
    {
        foreach (var player in players)
        {
            yield return player;  // Return one player, PAUSE, remember position
        }
    }

    // Required for legacy non-generic IEnumerable support
    IEnumerator IEnumerable.GetEnumerator()
    {
        return GetEnumerator();
    }
}
```

**Now `foreach` works AND the internal list is protected:**

```csharp
foreach (var player in team)
{
    Console.WriteLine(player.Name);  // Works!
}

// Caller cannot get a reference to the internal list — they only get players one at a time
```

### 10.5 How `yield` Works — The State Machine

`yield return` is a compiler feature that transforms your method into a state machine:

1. On the first call to `MoveNext()`, execution starts at the top of the method.
2. When `yield return player` is hit, the value is handed to the caller and execution **pauses**.
3. On the next call to `MoveNext()`, execution **resumes from where it paused**.
4. When the method ends (or hits `yield break`), the enumerator signals "done".

This is **lazy evaluation** — items are produced one at a time, on demand, not all at once. For large datasets, this is much more memory-efficient than building a full list and returning it.

```csharp
// yield break — stop enumeration early
public IEnumerator<FootballPlayer> GetFitPlayers()
{
    foreach (var player in players)
    {
        if (player.IsInjured) yield break;  // Stop the whole enumeration
        yield return player;
    }
}
```

**Q: What is `IEnumerator` vs `IEnumerable`?**
`IEnumerable` is the collection — it knows how to produce an enumerator. `IEnumerator` is the cursor — it keeps track of the current position. `foreach` calls `GetEnumerator()` on the collection to get a cursor, then calls `MoveNext()` and `Current` repeatedly.

**Q: Does `yield` work with `async`/`await`?**
Not directly in the same method. For async enumeration, C# 8+ introduced `IAsyncEnumerable<T>` and `await foreach`.

[dev leader -  Beginner CRASH COURSE for IEnumerable in .NET C#](https://youtu.be/RR7Cq0iwNYo?si=mLKskNheuKt-p_a7)
[Piece of Cake dev - IEnumerator vs IEnumerable](https://youtu.be/VW_TKdeH0jg?si=u0C_2V-Iq0_EBK9l)
[Aura Theme -  IQueryable vs IEnumerable vs List](https://youtu.be/6iLRVvO9wU8?si=oWFBndc3Oo_6XOHv)
---

## 11. Records — The Immutable Data Carrier

### 11.1 The Problem Records Solve

Before records, creating a simple data container required a lot of boilerplate:

```csharp
// OLD WAY — lots of boilerplate for a simple data class
public class Player
{
    public string Name { get; set; }
    public int JerseyNumber { get; set; }
    public int Goals { get; set; }

    public Player(string name, int jerseyNumber, int goals)
    {
        Name = name;
        JerseyNumber = jerseyNumber;
        Goals = goals;
    }

    // Need to override Equals, GetHashCode, ToString for value semantics (Section 3)
    public override bool Equals(object obj)
    {
        if (obj is Player other)
            return Name == other.Name &&
                   JerseyNumber == other.JerseyNumber &&
                   Goals == other.Goals;
        return false;
    }

    public override int GetHashCode()
        => HashCode.Combine(Name, JerseyNumber, Goals);

    public override string ToString()
        => $"Player {{ Name = {Name}, JerseyNumber = {JerseyNumber}, Goals = {Goals} }}";
}

// Usage
var p1 = new Player("Salah", 10, 32);
var p2 = new Player("Salah", 10, 32);
Console.WriteLine(p1 == p2);        // False! (reference equality — no == overload)
Console.WriteLine(p1.Equals(p2));   // True (value equality after override)
```

**Problems with this approach:**
- Lots of boilerplate — you must write the constructor, `Equals`, `GetHashCode`, `ToString`
- Mutable by default — properties can be changed unless you add `init` or `private set`
- Reference equality by default for `==` unless you overload the operator (Section 3.4)
- No non-destructive mutation — to change one property you must build a new object manually

### 11.2 What Is a Record?

A **record** is a reference type (class) that provides **value semantics** for equality and **immutability by default**. It is designed specifically for **data-carrying objects** — things defined by their data, not their identity. Everything in Section 3 (`Equals`, `GetHashCode`, `==`/`!=`) is generated for you.

```csharp
// With records — all that boilerplate in ONE line!
public record Player(string Name, int JerseyNumber, int Goals);

// Usage
var p1 = new Player("Salah", 10, 32);
var p2 = new Player("Salah", 10, 32);
Console.WriteLine(p1 == p2);  // True! (value equality — built-in)
Console.WriteLine(p1);        // Player { Name = Salah, JerseyNumber = 10, Goals = 32 }
```

**What the compiler generates automatically:**
- Constructor with all parameters
- Properties with `init` accessors (immutable)
- `Equals()` override (value comparison)
- `GetHashCode()` override
- `ToString()` override
- `IEquatable<T>` implementation
- `==` and `!=` operators
- `Deconstruct()` method for pattern matching

**The same record with explicit members:**

```csharp
public record Player
{
    public string Name { get; init; }
    public int JerseyNumber { get; init; }
    public int Goals { get; init; }

    public Player(string name, int jerseyNumber, int goals)
    {
        Name = name;
        JerseyNumber = jerseyNumber;
        Goals = goals;
    }
}
```

### 11.3 Record Features in Detail

#### 11.3.1 Immutability — `init` Accessors

Records use `init` accessors by default. `init` is like `set` but **only works during object initialization**:

```csharp
public record Player
{
    public string Name { get; init; }
    public int JerseyNumber { get; init; }
    public int Goals { get; init; }
}

var player = new Player { Name = "Salah", JerseyNumber = 10, Goals = 32 };
player.Goals = 40;  // Compiler error — cannot modify an init-only property
```

**Why immutability matters:** thread-safe (no concurrent modification issues), predictable (state never changes after creation), and easy to reason about.

#### 11.3.2 Value Equality — Comparing by Content, Not Reference

```csharp
var p1 = new Player("Salah", 10, 32);
var p2 = new Player("Salah", 10, 32);
var p3 = new Player("Salah", 11, 32);

Console.WriteLine(p1 == p2);                  // True (same data)
Console.WriteLine(p1 == p3);                  // False (different jersey number)
Console.WriteLine(p1.Equals(p2));             // True (same data)
Console.WriteLine(ReferenceEquals(p1, p2));   // False (different references)
```

#### 11.3.3 `with` Expression — Non-Destructive Mutation

To change a record, you create a **new copy** with the modified property:

```csharp
var salah = new Player("Salah", 10, 32);

// Create a NEW record with the same data, but different Goals
var salahWithMoreGoals = salah with { Goals = 40 };

Console.WriteLine(salah.Goals);               // 32 (unchanged)
Console.WriteLine(salahWithMoreGoals.Goals);  // 40 (new record)

// Multiple properties can be changed at once
var updatedPlayer = salah with { Goals = 45, JerseyNumber = 11 };
```

**Under the hood:** `with` creates a copy of the record, then applies the changes to the copy. The original remains unchanged.

#### 11.3.4 Deconstruction — Pattern Matching

```csharp
var player = new Player("Salah", 10, 32);

// Deconstruct into variables
var (name, number, goals) = player;
Console.WriteLine($"Name: {name}, Number: {number}, Goals: {goals}");

// Or in a foreach with pattern matching
foreach (var p in team)
{
    if (p is Player { Goals: > 20 } star)
    {
        Console.WriteLine($"{star.Name} - {star.Goals} goals");
    }
}
```

### 11.4 Records in the Football Example

```csharp
public record PlayerRecord(string Name, int JerseyNumber, int Goals, int Assists);
public record TeamRecord(string Name, string Coach, List<PlayerRecord> Players);
public record MatchResult(string HomeTeam, string AwayTeam, int HomeGoals, int AwayGoals);

class Program
{
    static void Main()
    {
        var salah = new PlayerRecord("Mohamed Salah", 10, 32, 18);
        var trezeguet = new PlayerRecord("Trezeguet", 7, 15, 8);
        var hegazi = new PlayerRecord("Ahmed Hegazi", 6, 5, 2);

        var alAhly = new TeamRecord("Al Ahly SC", "Marcel Koller",
            new List<PlayerRecord> { salah, trezeguet, hegazi });

        // Immutability in action
        var salahUpdated = salah with { Goals = 35, Assists = 20 };
        Console.WriteLine(salahUpdated);
        // Output: PlayerRecord { Name = Mohamed Salah, JerseyNumber = 10, Goals = 35, Assists = 20 }
        Console.WriteLine(salah);
        // Output: PlayerRecord { Name = Mohamed Salah, JerseyNumber = 10, Goals = 32, Assists = 18 }
        // Original is unchanged!

        // Value equality
        var salahCopy = new PlayerRecord("Mohamed Salah", 10, 32, 18);
        Console.WriteLine(salah == salahCopy);  // True! (same data)

        // Deconstruction
        var (name, number, goals, assists) = salah;
        Console.WriteLine($"Deconstructed: {name} #{number} - {goals} goals, {assists} assists");

        // Pattern matching
        if (salah is PlayerRecord { Goals: > 30 } star)
        {
            Console.WriteLine($"Star player: {star.Name}");
        }
    }
}
```

### 11.5 Record Structs — Value Type Records

Records can also be `struct` (value type) instead of `class` (reference type):

```csharp
// Record struct — allocated on the stack, copied by value
public record struct Position(int X, int Y);

var pos1 = new Position(10, 20);
var pos2 = pos1;  // Copy by value (new struct)
pos1.X = 99;      // Compiler error — immutable by default

// Mutable record struct (rare, but possible)
public record struct MutablePosition
{
    public int X { get; set; }
    public int Y { get; set; }
}
```

| | Record Class | Record Struct |
|---|---|---|
| **Memory** | Heap (reference) | Stack (value) |
| **Copy** | Reference copied | Value copied |
| **Size** | Good for large data | Good for small data (< 16 bytes) |
| **Use when** | Large data, need identity | Small data, value semantics |

### 11.6 Records vs Classes — Decision Guide

| Feature | Class | Record |
|---|---|---|
| **Identity** | Reference identity | Value identity |
| **Equality** | Reference equality (`==`) by default | Value equality (`==`) by default |
| **Mutability** | Mutable by default | Immutable by default (`init`) |
| **Boilerplate** | Lots (manual `Equals`, `GetHashCode`, `ToString`) | Minimal (compiler generates it) |
| **Inheritance** | Full inheritance | Supports inheritance, but limited |
| **Use when** | Complex behavior, identity matters | Simple data, content matters |

**When to use records:**

```csharp
public record PlayerDTO(string Name, int Goals, string Team);              // DTOs
public record ApiResponse<T>(bool Success, string Message, T Data);        // API responses
public record Money(decimal Amount, string Currency);                      // Value objects
public record AppConfig(string ConnectionString, int TimeoutSeconds, bool EnableLogging); // Config
public record GoalScoredEvent(string Scorer, int Minute, string Team);     // Domain events
```

**When NOT to use records:**

```csharp
// Complex business logic with behavior
public class GameEngine
{
    private List<Player> _players;
    public void ProcessMatch() { /* Complex logic */ }
    public void UpdateScore() { /* More logic */ }
}

// Objects with identity (not just data)
public class Session
{
    public string SessionId { get; }
    public User User { get; }
    public DateTime CreatedAt { get; }
    public void Extend() { }
}

// Complex polymorphic hierarchies
public class PaymentProcessor
{
    public virtual void Process() { }
}

public class CreditCardProcessor : PaymentProcessor
{
    public override void Process() { }
}
```

### 11.7 Record Inheritance

```csharp
public record Player(string Name, int JerseyNumber);

public record Forward(string Name, int JerseyNumber, int Goals) : Player(Name, JerseyNumber);
public record Defender(string Name, int JerseyNumber, int Tackles) : Player(Name, JerseyNumber);

var salah = new Forward("Salah", 10, 32);
var hegazi = new Defender("Hegazi", 6, 45);

var salah2 = new Forward("Salah", 10, 32);
Console.WriteLine(salah == salah2);  // True (same data)

var updatedSalah = salah with { Goals = 35 };
```

**Important limitation:** records cannot inherit from classes, and classes cannot inherit from records. The inheritance chain must be records all the way.

### 11.8 Common Record Patterns

```csharp
// Pattern 1 — Positional records (most common)
public record Player(string Name, int JerseyNumber, int Goals);

// Pattern 2 — Records with validation
public record Player
{
    public string Name { get; init; }
    public int JerseyNumber { get; init; }
    public int Goals { get; init; }

    public Player(string name, int jerseyNumber, int goals)
    {
        if (string.IsNullOrWhiteSpace(name))
            throw new ArgumentException("Name cannot be empty");
        if (jerseyNumber < 1 || jerseyNumber > 99)
            throw new ArgumentException("Jersey number must be 1-99");

        Name = name;
        JerseyNumber = jerseyNumber;
        Goals = goals;
    }
}

// Pattern 3 — Records with methods (behavior)
public record Player(string Name, int JerseyNumber, int Goals)
{
    public bool IsStar() => Goals > 20;

    public string GetRating() => Goals switch
    {
        > 30 => "Elite",
        > 20 => "Star",
        > 10 => "Good",
        _ => "Developing"
    };

    public Player AddGoals(int goalsToAdd) => this with { Goals = Goals + goalsToAdd };
}

// Pattern 4 — Records with static factory methods
public record Player(string Name, int JerseyNumber, int Goals)
{
    public static Player CreateStarPlayer(string name, int number) => new Player(name, number, 30);
    public static Player CreateRookie(string name, int number) => new Player(name, number, 0);
}
```

### 11.9 Records vs Anonymous Types

| Feature | Anonymous Type | Record |
|---|---|---|
| **Syntax** | `new { Name = "Salah", Goals = 32 }` | `new Player("Salah", 32)` |
| **Named type** | No (compiler-generated name) | Yes |
| **Reusable** | No (can't pass across methods) | Yes |
| **Equality** | Value semantics | Value semantics |
| **Immutability** | Yes | Yes |
| **`with` expression** | No | Yes |
| **Inheritance** | No | Yes (records only) |

### 11.10 Summary

| Feature | What It Does | Example |
|---|---|---|
| **Positional syntax** | Defines a record in one line | `public record Player(string Name, int Goals);` |
| **Immutability** | Properties are `init`-only | `player.Goals = 40;` fails to compile |
| **Value equality** | `==` compares content | `p1 == p2` → `true` (same data) |
| **`with` expression** | Creates a modified copy | `salah with { Goals = 40 }` |
| **Deconstruction** | Unpacks into variables | `(var name, var goals) = player;` |
| **`ToString`** | Auto-generated readable output | `Player { Name = Salah, Goals = 32 }` |

### 11.11 Quick Reference

| When you need... | Use... |
|---|---|
| Simple data container | Record |
| Complex behavior + identity | Class |
| Tiny immutable data (stack allocated) | Record struct |
| Temporary data in one method | Anonymous type |

---

## 12. Nested Types

### 12.1 What Is a Nested Type?

A **nested type** is a class, struct, interface, enum, or delegate declared *inside* another type. The outer type is the **enclosing type**. The nested type lives within the enclosing type's scope and — the key benefit — has direct access to the enclosing type's `private` members.

```csharp
public class OuterClass
{
    private int _secret = 42;

    private class InnerClass
    {
        public void Reveal(OuterClass outer)
        {
            // Inner can read OuterClass private members directly
            Console.WriteLine(outer._secret);  // Works — even though it's private
        }
    }
}
```

This is what distinguishes a nested type from an ordinary helper class: it gets privileged access to the enclosing type's private data, without that data needing to be made `public` or `internal`.

### 12.2 The Real Example — `TeamEnumerator` Inside `Team`

The `TeamEnumerator` from Section 10.3 is the textbook case. Here is the full annotated version:

```csharp
internal class Team : IEnumerable<FootballPlayer>
{
    private List<FootballPlayer> players;  // private — nobody outside sees this
    private int _count = 0;

    public IEnumerator<FootballPlayer> GetEnumerator()
    {
        return new TeamEnumerator(this);  // creates the nested type, passes itself
    }

    IEnumerator IEnumerable.GetEnumerator() => GetEnumerator();

    // ── NESTED CLASS ─────────────────────────────────────────────
    // Declared INSIDE Team. It is a private implementation detail.
    // No code outside Team can even name this type.
    private class TeamEnumerator : IEnumerator<FootballPlayer>
    {
        private Team _team;          // reference back to the enclosing Team instance
        private int _position = -1;  // cursor — starts before the first element

        public TeamEnumerator(Team team)
        {
            _team = team;
        }

        public FootballPlayer Current
        {
            get
            {
                if (_position < 0 || _position >= _team._count)
                    throw new InvalidOperationException("Enumerator is out of range.");

                // _team.players is PRIVATE on Team.
                // This line only compiles because TeamEnumerator is nested inside Team.
                return _team.players[_position];
            }
        }

        object IEnumerator.Current => Current;

        public bool MoveNext()
        {
            _position++;
            return _position < _team._count;  // false when the list is exhausted
        }

        public void Reset()
        {
            _position = -1;  // rewind to before the first element
        }

        public void Dispose()
        {
            // IEnumerator<T> extends IDisposable, so we must implement this.
            // Nothing unmanaged to release here — empty body is correct.
        }
    }
}
```

**Why nest it?** `TeamEnumerator` needs to read `_team.players` and `_team._count` — both `private` fields. Making them `internal` or `public` just to support a helper class would expose internals to the entire codebase. Nesting gives `TeamEnumerator` exactly the access it needs, and nothing outside `Team` ever knows the type exists.

### 12.3 Access Modifiers on Nested Types

Nested types accept any access modifier. The modifier controls who can *name* the type — instantiate it, inherit from it, use it as a parameter type:

| Modifier | Who can use the nested type |
|---|---|
| `private` | Only the enclosing class body |
| `protected` | The enclosing class and its subclasses |
| `internal` | Anywhere within the same project |
| `public` | Anywhere |

```csharp
public class DataProcessor
{
    // private — invisible to all callers; used only inside DataProcessor
    private class InternalBuffer { }

    // public — callers receive this as a return value and can inspect it
    public class ProcessingResult
    {
        public bool Success { get; init; }
        public string Message { get; init; }
    }

    public ProcessingResult Run()
    {
        var buffer = new InternalBuffer();  // fine — we are inside DataProcessor
        return new ProcessingResult { Success = true, Message = "Done" };
    }
}

// From outside DataProcessor:
var result = new DataProcessor().Run();            // OK — ProcessingResult is public
var bad = new DataProcessor.InternalBuffer();       // Compile error — private
```

### 12.4 Nested Types vs Separate Helper Classes

| | Nested Type | Separate Top-Level Class |
|---|---|---|
| Access to enclosing private members | Direct | Must use `public`/`internal` APIs |
| Referenced as | `Outer.Inner` | Its own name |
| Appropriate when | Implementation detail of exactly one class | Reused by multiple classes |
| Risk | Can make the outer class file long | Creates more files to maintain |

**Rule of thumb:** if the helper type exists solely to serve one specific class and needs its private data, nest it. If it is genuinely reusable across multiple classes, make it a top-level class.

### 12.5 Other Nestable Types

Any type definition can live inside a class — not just classes:

```csharp
public class FootballMatch
{
    // Nested ENUM — referenced as FootballMatch.MatchStatus
    public enum MatchStatus { NotStarted, InProgress, HalfTime, Ended }

    // Nested STRUCT — lightweight value type tightly coupled to this class
    public struct ScoreSnapshot
    {
        public int HomeGoals;
        public int AwayGoals;
    }

    // Nested INTERFACE — a contract that only makes sense inside this context
    public interface IMatchObserver
    {
        void OnGoalScored(string scorer);
    }

    // Nested DELEGATE — a delegate type scoped to this class
    public delegate void MatchEventHandler(string eventDescription);

    private MatchStatus _status = MatchStatus.NotStarted;
    private ScoreSnapshot _score;
}

// From outside:
var status = FootballMatch.MatchStatus.InProgress;
var snap = new FootballMatch.ScoreSnapshot { HomeGoals = 2, AwayGoals = 1 };
```

### 12.6 `static` Nested Classes

A nested class can be marked `static`. This means it holds **no implicit reference to an instance of the enclosing type**. It can still access `static` private members of the outer class, but it cannot touch instance members without being given an explicit reference.

```csharp
public class Team
{
    private static int _teamIdCounter = 0;   // static — belongs to the type, not an instance
    private string _name;

    private Team(string name) { _name = name; }

    // static nested class — no Team instance needed to use it
    public static class Factory
    {
        public static Team Create(string name)
        {
            _teamIdCounter++;    // static private member — accessible
            return new Team(name);
        }
    }

    // non-static nested class — needs an explicit Team reference
    private class Roster
    {
        private Team _owner;
        public Roster(Team owner) { _owner = owner; }

        public void Print()
        {
            Console.WriteLine(_owner._name);  // instance private member via reference
        }
    }
}

// Usage:
Team fc = Team.Factory.Create("FC Example");  // no Team instance needed to call Factory
```

Use a `static` nested class when the helper logic is conceptually related to the outer class but does not need to operate on a specific instance of it.

### 12.7 `yield` and the Hidden Nested Type

When you write `GetEnumerator()` using `yield return`, the compiler secretly generates a nested class — structurally identical to the manual `TeamEnumerator`:

```csharp
// What YOU write:
public IEnumerator<FootballPlayer> GetEnumerator()
{
    foreach (var player in players)
        yield return player;
}

// What the compiler silently generates behind the scenes (simplified):
private sealed class GeneratedEnumerator : IEnumerator<FootballPlayer>
{
    private Team _outerThis;          // reference back to the Team instance
    private int _state;               // tracks where execution paused
    private FootballPlayer _current;

    public bool MoveNext() { /* ... */ }
    public FootballPlayer Current => _current;
    public void Reset() { }
    public void Dispose() { }
}
```

The manual `TeamEnumerator` and the compiler-generated version do exactly the same job. `yield` exists so you never have to write the nested enumerator class by hand. This is also why nested types matter at the compiler level — C# itself uses them to implement language features.

**Q: Can a nested class inherit from its enclosing class?**
Technically yes — `private class SpecialTeam : Team { }` compiles. In practice this is almost always a design smell. Nesting is for implementation detail, not for building inheritance hierarchies inside a class.

**Q: Can the enclosing class access the nested class's `private` members?**
Yes — the relationship is mutual. The enclosing class can access `private` members of its own nested types.

**Q: Is a new `TeamEnumerator` created every time `foreach` runs?**
Yes — each `foreach` call invokes `GetEnumerator()`, which creates a fresh enumerator starting at position `-1`. This is why two simultaneous `foreach` loops over the same collection work independently and never interfere with each other.

**Q: Why is `TeamEnumerator` private but `ProcessingResult` is public?**
It comes down to whether the caller ever needs to name the type. `TeamEnumerator` is returned as the interface `IEnumerator<FootballPlayer>` — callers interact with the interface, never the concrete class. `ProcessingResult` is a data container the caller must receive, store, and inspect, so it needs to be `public`.

**Q: Does a nested class create tighter coupling than a separate class?**
Yes, and that is intentional when you nest. You are deliberately saying "this type belongs to exactly this outer class and nowhere else." If you later find the nested type being referenced from many places, that is a signal to promote it to a top-level class.

---

## 13. Destructors, Finalizers, and the Dispose Pattern

This is one of the most commonly misused parts of C#. The goal of this section is not just to show you the mechanics, but to tell you **which pattern to reach for in real production code**, because the "textbook" full pattern is overkill for the vast majority of classes you will actually write.

### 13.1 Two Kinds of Resources

Every object your program creates falls into one of two buckets:

| Type | Examples | Who cleans it up? |
|---|---|---|
| **Managed** | `List<T>`, `string`, any ordinary C# class | The **Garbage Collector (GC)** — automatically |
| **Unmanaged** | OS file handles, network sockets, database connections, raw memory (`Marshal.AllocHGlobal`) | **You** — the GC has no idea these exist |

The Garbage Collector is excellent at tracking managed objects. It is completely blind to unmanaged resources. If you allocate an OS socket and your object is collected without releasing it, that socket stays open until the process exits — or forever on a long-running server.

**In practice**, most classes you write don't allocate raw unmanaged handles directly — you allocate them *indirectly*, by owning other `IDisposable` objects: a `FileStream`, a `SqlConnection`, an `HttpClient`, a `Socket`. Those types already do the unmanaged cleanup internally. Your job is simply to make sure `Dispose()` gets called on them. This distinction drives which pattern below you actually need.

### 13.2 The Garbage Collector — How It Works

The GC is a background system that:

1. Tracks every managed object via reachability — is anything still holding a reference to it?
2. When memory pressure builds, it runs a **collection** — finds objects with no live references and reclaims their memory.
3. Runs **non-deterministically** — you cannot predict when, or even if, it will run during a short-lived program.

```csharp
// This object becomes eligible for collection the moment Main() returns,
// but the GC decides WHEN to actually reclaim the memory.
SmsNotification leakyChannel = new SmsNotification("gateway.eg", "+201...");
leakyChannel.Send("Alert...");
// leakyChannel goes out of scope — the GC will eventually collect it.
// But WHEN? Could be milliseconds. Could be much later.
```

This non-determinism is fine for managed memory — but a problem for resources (file handles, sockets, DB connections) that need to be released on a precise schedule, or you run out of them.

### 13.3 `IDisposable` — The Cleanup Contract

```csharp
public interface IDisposable
{
    void Dispose();
}
```

`IDisposable` is a **promise**: "This class holds resources. Call `Dispose()` when you are done with me and I will release them immediately." Any class that holds unmanaged resources — directly or indirectly, through other `IDisposable` objects it owns — should implement this.

### 13.4 The Pattern You'll Use Most Often — No Finalizer

If your class only owns **other managed `IDisposable` objects** (a `FileStream`, a `StreamReader`, an `HttpClient`), you do **not** need a finalizer. You have no raw unmanaged handle of your own — you're just responsible for telling the objects you own to clean up. This covers the large majority of real-world `IDisposable` classes.

```csharp
public class ReportExporter : IDisposable
{
    private readonly FileStream _fileStream;
    private readonly StreamWriter _writer;
    private bool _isDisposed;

    public ReportExporter(string path)
    {
        _fileStream = new FileStream(path, FileMode.Create);
        _writer = new StreamWriter(_fileStream);
    }

    public void WriteLine(string line) => _writer.WriteLine(line);

    public void Dispose()
    {
        if (_isDisposed) return;

        // Dispose owned managed resources — order matters: wrapper before what it wraps
        _writer.Dispose();
        _fileStream.Dispose();

        _isDisposed = true;
    }
}

// Usage:
using (var exporter = new ReportExporter("report.csv"))
{
    exporter.WriteLine("Name,Goals");
}   // Dispose() runs automatically here, even if an exception is thrown above
```

No finalizer, no `Dispose(bool disposing)` overload, no `GC.SuppressFinalize`. This class holds nothing the GC is blind to — it only holds other `IDisposable` objects, and disposing them is enough.

### 13.5 The Full Dispose Pattern — When You Hold Unmanaged Resources Directly

Only reach for the full pattern — with a finalizer — when your class **directly** owns an unmanaged resource: a raw `IntPtr`, a P/Invoke handle, or similar. This is the industry-standard implementation. Read it layer by layer.

#### Layer 1 — The Abstract Base Class

```csharp
public abstract class NotificationChannel : IDisposable
{
    private bool _isDisposed = false;       // Guard flag — prevents double-disposal
    protected string _connectionString;

    // ── PUBLIC ENTRY POINT ──────────────────────────────────────
    // This is what callers and 'using' blocks invoke.
    public void Dispose()
    {
        Dispose(true);   // "I am cleaning up intentionally — include managed resources"

        // CRITICAL: remove this object from the GC's finalizer queue.
        // Without this, the GC would call the finalizer AGAIN later — double cleanup!
        GC.SuppressFinalize(this);
    }

    // ── TEMPLATE METHOD ─────────────────────────────────────────
    // 'disposing = true'  → called from Dispose() — safe to touch managed objects
    // 'disposing = false' → called from the finalizer — UNSAFE to touch managed objects
    protected virtual void Dispose(bool disposing)
    {
        if (_isDisposed) return;   // Already cleaned up — do nothing

        if (disposing)
        {
            // CLEAN UP MANAGED RESOURCES HERE
            // (other IDisposable objects this class owns)
            Console.WriteLine($"[Base Cleanup] Severing connection string for {_connectionString}.");
        }

        // CLEAN UP BASE UNMANAGED RESOURCES HERE (if the base class has any — none in this example)

        _isDisposed = true;
    }

    // Guard method — prevents using an object after it has been disposed
    protected void ThrowIfDisposed()
    {
        if (_isDisposed)
            throw new ObjectDisposedException(this.GetType().Name,
                "Cannot use a closed notification channel.");
    }
}
```

#### Layer 2 — The Concrete Child Class with an Unmanaged Resource

```csharp
public class SmsNotification : NotificationChannel
{
    private bool _isSmsDisposed = false;

    // IntPtr holds a raw OS memory address — this is an UNMANAGED resource.
    // The GC cannot track or free this. We must do it ourselves.
    private IntPtr _nativeSocketHandle;

    public string PhoneNumber { get; }

    public SmsNotification(string apiGateway, string phoneNumber) : base(apiGateway)
    {
        PhoneNumber = phoneNumber;

        // Allocating 512 bytes of raw, unmanaged OS memory
        _nativeSocketHandle = Marshal.AllocHGlobal(512);
        Console.WriteLine($"[SMS] Native socket allocated at: 0x{_nativeSocketHandle.ToInt64():X}");
    }

    public override void Send(string message)
    {
        ThrowIfDisposed();  // Guard — fail fast with a clear error
        Console.WriteLine($"[SMS] Transmitted to {PhoneNumber}: {message}");
    }

    // ── OVERRIDE THE TEMPLATE METHOD ────────────────────────────
    protected override void Dispose(bool disposing)
    {
        if (_isSmsDisposed) return;

        if (disposing)
        {
            // 1. CLEAN UP CHILD'S OWN MANAGED RESOURCES
            Console.WriteLine("[SMS Cleanup] Releasing internal telemetry timers.");
        }

        // 2. CLEAN UP CHILD'S UNMANAGED RESOURCES
        // This block runs whether disposing=true OR false — unmanaged cleanup is always safe.
        if (_nativeSocketHandle != IntPtr.Zero)   // IntPtr.Zero = "already freed" sentinel
        {
            Marshal.FreeHGlobal(_nativeSocketHandle);   // Actually release the OS memory
            _nativeSocketHandle = IntPtr.Zero;           // Mark as freed
            Console.WriteLine("[SMS Cleanup] Raw OS socket dropped from hardware memory.");
        }

        // 3. CHAIN UP to the base class so it can clean up its own resources
        base.Dispose(disposing);

        _isSmsDisposed = true;
    }
}
```

### 13.6 The Finalizer (Destructor) — Your Safety Net

The **finalizer** (also called a destructor) is what makes this class part of the *full* pattern rather than the simple one from Section 13.4. It only belongs on a class that directly holds an unmanaged resource, and it exists purely as a fallback for the case where a caller forgets to call `Dispose()`.

```csharp
public class SmsNotification : NotificationChannel
{
    // ── FINALIZER (DESTRUCTOR) ───────────────────────────────────
    // Syntax: ~ + class name. No access modifier, no parameters, and you
    // never call it yourself — the CLR calls it for you, on its own schedule.
    ~SmsNotification()
    {
        // Pass FALSE — we are on the GC's finalizer thread.
        // It is UNSAFE to reference other managed objects here;
        // they may already have been collected.
        Dispose(false);
    }

    // ... rest of the class as shown in 13.5 ...
}
```

**How the finalizer actually behaves:**

- Any class with a `~ClassName()` finalizer is automatically placed on the GC's **finalization queue** the moment it's constructed.
- If `Dispose()` is never called, the GC eventually runs the finalizer on a dedicated finalizer thread before reclaiming the object's memory — but "eventually" could be seconds, minutes, or in a short-lived console app, effectively never before the process exits.
- If `Dispose()` **is** called, it ends with `GC.SuppressFinalize(this)`, which removes the object from the finalization queue entirely — so the finalizer never runs, and the object is collected in the normal, faster pass.

**Rules for writing a finalizer safely:**

| Rule | Why |
|---|---|
| Only reference `IntPtr`/unmanaged handles inside it | Other managed objects may already be finalized/collected by the time your finalizer runs — there's no guaranteed order |
| Never throw an exception from a finalizer | An unhandled exception on the finalizer thread crashes the whole process |
| Never call `Dispose(true)` from a finalizer | Pass `false` — the `disposing` parameter is what tells `Dispose(bool)` it's unsafe to touch managed members |
| Keep it fast | It runs on a dedicated, single finalizer thread shared by the whole process; a slow finalizer backs up finalization for every other object waiting behind it |
| Avoid adding one unless you truly hold an unmanaged handle | Every finalizable object survives an extra GC generation before it's actually freed — this has a real, measurable cost |

That last rule is the one production engineers get wrong most often: **do not add a finalizer "just in case."** If your class only owns other `IDisposable` objects (Section 13.4), it should have no finalizer at all — let the objects you own handle their own unmanaged cleanup.

### 13.7 The `using` Statement — The Ideal Path

The `using` statement guarantees `Dispose()` is called the moment the block exits, even if an exception is thrown inside it.

```csharp
// SCENARIO 1: the ideal, deterministic path
using (SmsNotification smsChannel = new SmsNotification("gateway.eg", "+2010000000"))
{
    smsChannel.Send("Your OTP is 4321.");
}   // Dispose(true) is called HERE, automatically, by the compiler
    // Managed + unmanaged resources freed immediately. No finalizer overhead.
```

**What the compiler actually generates from `using`:**

```csharp
SmsNotification smsChannel = new SmsNotification("gateway.eg", "+2010000000");
try
{
    smsChannel.Send("Your OTP is 4321.");
}
finally
{
    // finally ALWAYS runs — even if an exception is thrown inside try
    if (smsChannel != null)
        ((IDisposable)smsChannel).Dispose();
}
```

**Modern C# 8+ `using` declaration (no braces needed):**

```csharp
using var smsChannel = new SmsNotification("gateway.eg", "+2010000000");
smsChannel.Send("Your OTP is 4321.");
// Dispose() is called automatically when 'smsChannel' goes out of scope
// (end of the enclosing method or block)
```

### 13.8 The Dangerous Path — What Happens Without `using`

```csharp
// SCENARIO 2: developer forgets to dispose
static void ExecuteBadAllocation()
{
    SmsNotification leakyChannel = new SmsNotification("fallback.eg", "+2011111111");
    leakyChannel.Send("Sending alert...");

    // leakyChannel goes out of scope here.
    // Dispose() was NEVER called.
    // The 512 bytes of OS memory are still allocated!
}

// Later, when the GC decides to collect the object:
// It sees the finalizer (~SmsNotification) and calls it.
// Dispose(false) runs — unmanaged memory is eventually freed.
// But WHEN? Could be seconds, could be minutes, could be much later in a long-running process.

// Force GC for demonstration purposes ONLY — never do this in production code:
GC.Collect();
GC.WaitForPendingFinalizers();
```

### 13.9 The Full Flow Diagram

```
Object Created
      │
      ▼
Developer calls Dispose()         GC notices object is unreachable
or 'using' block exits            (no Dispose() was called)
      │                                         │
      ▼                                         ▼
Dispose(true)                          Finalizer ~SmsNotification()
      │                                         │
      ├─ Clean managed resources               Dispose(false)
      ├─ Clean unmanaged resources              │
      ├─ base.Dispose(true)                     ├─ Skip managed resources (unsafe)
      └─ GC.SuppressFinalize(this)              ├─ Clean unmanaged resources
           │                                    └─ base.Dispose(false)
           ▼
   Object removed from
   finalizer queue —
   GC never calls finalizer       Object freed by GC (eventually)
```

### 13.10 A Note on Async Code — `IAsyncDisposable`  EXTRA!! (we will discuss async code in session 3)


Modern codebases that clean up resources via `await` (closing a network connection gracefully, flushing an async stream) should also implement `IAsyncDisposable`:

```csharp
public class AsyncReportExporter : IAsyncDisposable
{
    private readonly Stream _stream;

    public async ValueTask DisposeAsync()
    {
        await _stream.FlushAsync();
        await _stream.DisposeAsync();
    }
}

// Usage:
await using var exporter = new AsyncReportExporter();
```

A class can implement both `IDisposable` and `IAsyncDisposable` if it needs to support both call sites — this is common for stream and connection wrapper types.

### 13.11 Rules Summary

| Rule | Why |
|---|---|
| Prefer the simple pattern (13.4) — no finalizer — unless you hold a raw unmanaged handle | Finalizers have a real GC performance cost and are easy to misuse |
| Always use `using` (or `await using`) when consuming an `IDisposable`/`IAsyncDisposable` object | Guarantees cleanup even on exceptions |
| Never access other managed objects inside a finalizer | They may already be collected — only touch raw unmanaged handles there |
| Always call `base.Dispose(disposing)` in child classes | The base class has its own resources to release |
| Always check `_isDisposed` at the top of `Dispose(bool)` | Prevents double-disposal errors — `Dispose()` must be idempotent |
| Always call `GC.SuppressFinalize(this)` after `Dispose(true)` | Removes the object from the finalizer queue, avoiding the extra GC pass |
| Set `IntPtr` handles to `IntPtr.Zero` after freeing | Prevents a double-free of the same OS handle |
| Call `ThrowIfDisposed()` at the top of public methods | Gives the caller a clear error instead of a cryptic crash |

**Q: Does every `IDisposable` class need a finalizer?**
No — only classes that *directly* hold unmanaged resources (raw OS handles, `Marshal.AllocHGlobal`, P/Invoke handles). If your class only holds managed `IDisposable` objects, implement `IDisposable` with the simple pattern from Section 13.4 and skip the finalizer entirely.

**Q: What is `ObjectDisposedException`?**
An exception thrown when you call a method on an object that has already been disposed. `ThrowIfDisposed()` produces this — a clear, descriptive error instead of a confusing `NullReferenceException` from a freed handle.

**Q: Why can't I access managed objects in the finalizer?**
The GC runs finalizers on a dedicated background thread, in no guaranteed order. By the time your finalizer runs, other managed objects your class holds references to may already have been collected. Accessing them is undefined behavior. Unmanaged handles (raw `IntPtr` values) are just numbers — safe to use regardless of GC state.

**Q: Is a finalizer the same as a destructor?**
In C#, yes — the same thing, written with `~ClassName()` syntax. "Destructor" comes from C++, where destruction is deterministic. In C# it is non-deterministic (GC-driven), so "finalizer" is the more accurate term, but both refer to the same method.

**Q: What if I call `Dispose()` twice by accident?**
The `if (_isDisposed) return;` guard at the top of `Dispose(bool)` makes it safe. The second call is silently ignored — this is the documented contract for `IDisposable`.

[Code Radiance - Finalize Vs dispose](https://youtu.be/6_Upud25iFQ?si=-yn7YwICd681iyv_)
[CodeProseries - nmanaged Memory in .NET | Finalizer, Dispose Pattern & Using Block Explained with C# Code](https://youtu.be/NKUSCyhWbaI?si=TnTJjdO7605uvo4T)
[David Anderson -  How IDisposable, Dispose, and Finalizers work in C#](https://youtu.be/e0G5X3bu6hY?si=VZEQNMnF2QsTPs2l)

---

## 14. Top-Level Statements

### 14.1 What Are They?

Before C# 9, every program needed this boilerplate:

```csharp
namespace MyApp
{
    class Program
    {
        static void Main(string[] args)
        {
            // actual code here
        }
    }
}
```

With **top-level statements** (C# 9+), you can write the entry-point code directly at the top of a file, with no class or method wrapper:

```csharp
// Program.cs — no namespace, no class, no Main!
Console.WriteLine("Hello from top-level statements!");

var team = new Team("FC Example");
team.AddPlayer(new GoalKeeper("El-Shenawy", 1, 12));
team.DisplaySquad();
```

### 14.2 Rules

- Only **one** file in the project can use top-level statements.
- That file is implicitly the entry point (the `Main` method).
- You can still have namespaces, classes, and `using` directives in the same file — but they must come *after* the top-level statements.
- `args` is still available (automatically in scope as `string[] args`).

### 14.3 What the Compiler Generates

The compiler wraps your top-level code in a `Main` method behind the scenes — you just don't have to write it. Both styles (top-level statements and an explicit `Main`) are valid C#.

**Q: Should I use top-level statements in real projects?**
They are great for small utilities, scripts, and learning projects. For large applications, the traditional style with an explicit `Main` and `Program` class is often preferred for clarity — especially when adding middleware, services, or startup configuration.

---

## 15. Quick Reference — Keyword Glossary

| Keyword / Concept | Meaning |
|---|---|
| `abstract` | A class or method that cannot be used directly; must be inherited/overridden |
| `virtual` | A method with a default implementation that child classes *may* override |
| `override` | Replaces a parent's `virtual` or `abstract` method in a child class |
| `base` | Refers to the parent class — used to call parent constructors or methods |
| `sealed` | Prevents a class from being inherited, or a method from being further overridden |
| `private` | Accessible only within the same class |
| `protected` | Accessible within the class and all its descendants |
| `internal` | Accessible within the same project/assembly |
| `public` | Accessible from anywhere |
| `static` | Belongs to the type, not to any instance — no `new` needed to use it |
| `readonly` | Field can only be set in the constructor or at declaration; never changed after |
| `this` | Refers to the current instance. In indexers, it defines the `[]` syntax |
| `event` | A multicast delegate wrapper with restricted external access |
| `delegate` | A type that holds a reference to one or more methods |
| `yield return` | Pauses a method and returns one value at a time; resumes on the next iteration |
| `yield break` | Ends an iterator method early |
| `is` | Tests runtime type; with pattern matching, also casts into a new variable |
| `as` | Attempts a cast; returns `null` on failure instead of throwing |
| `default(T)` | The zero/null value for type `T` |
| `?.` | Null-conditional operator — skips the call if the object is null |
| `??` | Null-coalescing — returns the left side if not null, otherwise the right side |
| `where T :` | Generic constraint — restricts what types can be used for `T` |
| `IEnumerable<T>` | Interface that allows `foreach` iteration |
| `IEnumerator<T>` | Interface that provides the actual iteration cursor (`MoveNext`, `Current`) |
| `IDisposable` | Interface for deterministic cleanup — implement `Dispose()` |
| `IAsyncDisposable` | Async counterpart of `IDisposable` — implement `DisposeAsync()` |
| `Action<T>` | Built-in generic delegate — takes parameters, returns void |
| `Func<T, TResult>` | Built-in generic delegate — takes parameters, returns a value |
| `Predicate<T>` | Built-in generic delegate — takes `T`, returns `bool` |
| Lambda `=>` | Concise anonymous method syntax: `(params) => expression` |
| `extension(T t)` | C# 14 syntax for grouping extension methods for type `T` |
| `record` | Reference type with built-in value equality and immutability |
| `init` | Property accessor settable only during object initialization |
| `with` | Creates a modified copy of a record |
| Top-level statements | Writing `Main` code directly in a file, without a class or method wrapper |

---
## Knowledge check

**1. What are the four pillars of OOP, and which C# keyword is most directly associated with abstraction?**
 
**2. Why does C# give you properties (`get`/`set`) instead of just letting you use public fields directly?**

**3. What's the difference between `virtual` and `abstract` on a base class method?**

**4. If you forget to write `override` when replacing a parent's `virtual` method, what happens?**
 
**5. What can an abstract class do that an interface cannot?**
 
**6. Why does the default `Equals` on a reference type return `false` for two objects with identical data?**
 
**7. What is the rule that `GetHashCode` must never break?**

**8. If you overload the `==` operator on a class, what else are you required to do, and what's strongly recommended?**

**9. What does the `event` keyword add on top of a plain delegate field?**

**10. Why should you always use `?.Invoke()` when raising an event?**

**11. What is an indexer, and what keyword defines one?**

**12. What problem do generics solve that using `object` for a container does not?**

**13. What does `where T : new()` mean as a generic constraint?**

**14. What is a delegate, in one sentence?**

**15. What's the difference between `Func<T, TResult>` and `Predicate<T>`?**

**16. What four requirements must an extension method satisfy?**

**17. What does `yield return` actually do, mechanically?**

**18. Name three things the compiler auto-generates for a positional record that you'd otherwise have to write by hand.**

**19. What does the `with` expression do to a record, and does it modify the original?**

**20. When should you use a `Dispose(bool disposing)` finalizer versus the simple `Dispose()`-only pattern?**

---


