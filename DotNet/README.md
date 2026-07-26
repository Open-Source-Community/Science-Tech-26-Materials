#  .NET & C# Student Study Guide & Reference Companion

Welcome to **OSC .NET & C# Study Guide & Helper** repository! This repository is designed to serve as a comprehensive, structured reference for computer science students and developers looking to master modern C# programming, object-oriented software design, asynchronous paradigms, LINQ data processing, and advanced .NET runtime mechanics.

---

##  Table of Contents

1. [About The Repository](#-1-About-The-Repository)
2. [Module Overview](#-2-Module-Overview)
    * [1. C# Syntax Fundamentals, Data Structures & Object Contracts](#1-c-syntax-fundamentals-data-structures--object-contracts)
    * [2. Advanced C#](#-2-Advanced-C#)
    * [3. Async Programming & Runtime Systems](#-3-Async-Programming-&-Runtime-Systems)
    * [4. LINQ Advanced Operations & Performance](#-4-LINQ-Fundamentals-&-Deferred-Execution)

3. [Future Additions & Roadmap](#-Future-Additions-&-Roadmap)
4. [Contributing](#-Contributing)

---

## 1 About The Repository
 Whether you are preparing for technical interviews, studying university curriculum topics, or building scalable .NET backend applications, this helper repository provides clear breakdowns and code samples.

---


## 2 Module Overview

### 1 C# Syntax Fundamentals Data Structures & Object Contracts
Covers foundational C# language features, memory management semantics, core collection performance tradeoffs, and custom sequence control flow:

- **Primitives, Typing & Conversions**: Rules governing variables, type operators `(is, as, typeof)`, casting vs. safe conversion, enums, tuples, and handling nullability `(Nullable<T>, ?, ??, ?.)`.

- **String Processing & Memory Usage:** Comparing immutable string manipulation against heap allocation management using` StringBuilder`.

- **Collections & Data Structures:** Internal mechanics, complexity tradeoffs, and optimal usage of generic structures:

  - ***Linear Structures:*** Fixed Arrays, dynamic` List<T>`, doubly-linked `LinkedList<T>`, LIFO `Stack<T>`, and FIFO `Queue<T>`.
 
  - ***Associative & Set Structures**:* Key-value lookups via `Dictionary<TKey, TValue>`, hash-based uniqueness with `HashSet<T>`, and self-balancing tree ordering with `SortedSet<T>`.

- **Type System & Object Contracts:** Value vs. reference semantics (struct vs. class), custom collection indexing (Indexers), and implementing core equality/ordering protocols (`Equals, GetHashCode, IComparable`).

- **Custom Iteration & Enumeration:** Implementing custom `IEnumerable<T>` and `IEnumerator<T>` state mechanics, alongside lazy evaluation using` yield `return for deferred sequence execution.
- --
### 2 Advanced C#

Focuses on modern C# object-oriented paradigms, component design patterns, memory lifecycle strategies, and structural language abstractions:

* **OOP Foundations & Type Identity:** Encapsulation, Inheritance, Polymorphism, and Abstraction. Navigating design choices between standard class hierarchies, `abstract` base types, and capability contracts via `interfaces`.
* **Value Integrity & Identity Protocols:** Overriding `Equals` and `GetHashCode` for proper value-equality semantics, custom operator overloading, and utilizing C# `records` for immutable, thread-safe data transfer objects.
* **Events, Actions & Functional Patterns:** Building decoupled architectures using custom Delegates and standard Generic Delegates (`Action`, `Func`, `Predicate`). Implementing reactive Publisher–Subscriber designs with built-in C# `events`.
* **Type Extensions & Custom Sequences:** Crafting flexible collections using `Indexers` and strongly-typed `Generics`. Augmenting existing APIs via `Extension Methods`, and managing custom stream iteration using `IEnumerable` and `yield`.
* **Resource Lifecycle & Language Constructs:** Managing deterministic and non-deterministic memory cleanup using `IDisposable`, the Dispose Pattern, destructors, and finalizers. Simplifying entry points with Top-Level Statements and encapsulating auxiliary logic using Nested Types.
---
### 3 Async Programming & Runtime Systems

Covers concurrent programming, metadata handling, binary architecture, and reflection mechanisms:

* **Asynchronous Programming:** Master `Task`, `Task<T>`, `async`/`await`, non-blocking I/O execution, and state machines.
* **Serialization & Versioning:** Modern JSON mapping using `System.Text.Json`, custom attributes, and security boundaries.
* **Declarative Metadata & Attributes:** Implementing custom square-bracket `[Attribute]` decorations and runtime parsers.
* **Assemblies & Reflection:** Dynamic type loading, intermediate language (IL), manifests, and `AssemblyLoadContext` isolation
---
### 4 LINQ Fundamentals & Deferred Execution

Explores Language Integrated Query syntax and foundational operators:

* **Execution Paradigms:** Deferred execution (lazy evaluation) versus immediate materialization.
* **Filtering & Projection:** Conditional query evaluation via `Where`, one-to-one `Select` projections, and nested `SelectMany` flattening.
* **Ordering & Element Selection:** Stable sorting using `OrderBy`/`ThenBy`, defensive element extraction using `First`, `Single`, and their `OrDefault` variants.
* **Partitioning & Quantifiers:** Collection pagination via `Skip`/`Take`, condition evaluation with `Any` and `All`.
* **Pitfall Mitigation:** Avoiding multiple-enumeration overheads and closure variable capturing bugs.


---


##  Future Additions & Roadmap

* [ ] **Module 5: Entity Framework Core Fundamentals**
* *Space reserved for DbContext lifecycle, Fluent API configurations, migrations, and tracking behavior.*


* [ ] **Module 6: ASP.NET Core Web API Architecture**
* *Space reserved for Middleware pipelines, Dependency Injection lifecycles (Transient, Scoped, Singleton), and DTO validation.*


* [ ] **Module 7: ...**



---

##  Contributing

Special thanks to OSC members for their support!!
