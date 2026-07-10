# C# Fundamental Concepts — Study Guide 


---

## Table of Contents

1. [Variables & Expression Rules](#1-variables--expression-rules)
2. [Boolean & Type Operators](#2-boolean--type-operators)
3. [Casting & Type Conversion](#3-casting--type-conversion)
4. [Enums](#4-enums)
5. [Tuples](#5-tuples)
6. [Strings & StringBuilder](#6-strings--stringbuilder)
7. [Nulls & Nullable Types](#7-nulls--nullable-types)
8. [Arrays](#8-arrays)
9. [List\<T\>](#9-listt)
10. [Dictionary\<TKey,TValue\>](#10-dictionarytkeytvalue)
11. [Stack\<T\> & Queue\<T\>](#11-stackt--queuet)
12. [LinkedList\<T\>](#12-linkedlistt)
13. [HashSet\<T\>](#13-hashsett)
14. [SortedSet\<T\>](#14-sortedsett)
15. [Struct vs Class](#15-struct-vs-class)
16. [Indexers](#16-indexers)
17. [Object Contracts — Equals, GetHashCode, IComparable](#17-object-contracts--equals-gethashcode-icomparable)
18. [Custom IEnumerable / IEnumerator Implementation](#18-custom-ienumerable--ienumerator-implementation)
19. [`yield return` & Deferred Execution](#19-yield-return--deferred-execution)

---

## 1. Variables & Expression Rules

### Code

```csharp
int myNum = 5;
double myDoubleNum = 5.99D;
char myLetter = 'D';
bool myBool = true;
string myText = "Hello";
```

```csharp
// --- Integer overflow ---
int maxInt = int.MaxValue;                    // 2,147,483,647
int overflowedUnchecked = maxInt + 1;          // silently wraps to int.MinValue
int overflowedChecked = checked(maxInt + 1);   // throws OverflowException
```
The **checked** keyword: By default, C# arithmetic overflows silently wrap around in an unchecked context (e.g., int.MaxValue + 1 normally becomes int.MinValue). Enclosing the expression in a checked statement or expression forces the .NET runtime to explicitly check for arithmetic overflows

```csharp
// --- Floating-point precision ---
float f1 = 1.00000001f;
float f2 = 1.00000002f;
bool floatsEqual = (f1 == f2); // true — Lost precision due to 32-bit binary representation

decimal d1 = 1.0000000000000000000000000001m;
decimal d2 = 1.0000000000000000000000000002m;
bool decimalsEqual = (d1 == d2); // false — decimal keeps full precision
```
### --- IEEE 754 Anomalies ---

```csharp
double positiveInfinity = 1.0 / 0.0;   // Infinity
double nan = 0.0 / 0.0;                // NaN
int crash = 1 / 0;                     // throws DivideByZeroException (int has no "infinity")

```
```csharp

// --- char is UTF-16, not ASCII ---
int charSize = sizeof(char); // 2 bytes
```

### 
### --- Integer division truncates ---
```csharp
int a = 5, b = 2;
Console.WriteLine(a / b);         // 2
Console.WriteLine((double)a / b); // 2.5 — cast BEFORE dividing
```
### 
###  --- Operator precedence ---
```csharp
int x = 2 + 3 * 4;       // 14, not 20
int y = (2 + 3) * 4;     // 20

```
### --- const ---
```csharp
const int myConst = 15;
// myConst = 20; // compile error
```

### 
###  --- var is static, not dynamic ---
```csharp
var name = "John";  // locked as string forever
// name = 5;        // compile error
```


### --- Value type vs reference type assignment ---
```csharp
int vx = 10;
int vy = vx;
vy = 20; // vx is untouched — independent stack copies
```

```csharp

int[] array1 = { 1, 2, 3 };
int[] array2 = array1;
array2[0] = 99; // array1[0] is ALSO 99 — same heap object
```


### --- var vs dynamic ---
```csharp
dynamic looseVariable = "Hello";
looseVariable = 5; // legal — type checked only at runtime
```



### Explanation

| Concept | Key idea |
|---|---|
| **Integer overflow** | C# doesn't check for overflow by default — `int.MaxValue + 1` silently wraps to `int.MinValue` (two's complement: the sign bit flips). Wrap the expression in `checked(...)` to force a thrown `OverflowException` instead. |
| **float/double precision** | Both are base-2 (binary) fractions. Numbers like `0.1` can't be represented exactly in binary — the same way `1/3` can't be represented exactly in decimal — so tiny values get rounded away, and two "different" floats can compare equal. |
| **decimal** | A 128-bit type built specifically for base-10 accuracy (96 bits of integer + a scale factor). Use it for money — never `float`/`double`. |
| **Infinity / NaN** | Only floating-point types have these IEEE 754 special values. Integers have no equivalent — `1/0` as an `int` throws `DivideByZeroException` immediately. |
| **char** | 2 bytes, UTF-16 — not 1-byte ASCII like C/C++. |
| **Integer division** | `a / b` between two `int`s always truncates. Cast to `double` **before** dividing if you want a fractional result. |
| **Precedence** | `*` and `/` bind tighter than `+` and `-`. Use parentheses to override. |
| **const** | A compile-time constant — the compiler substitutes the literal value everywhere it's used. Cannot be reassigned. |
| **var** | Type is inferred **once**, at compile time. It is NOT `dynamic` — after declaration it behaves exactly like an explicitly-typed variable. |
| **Value vs. reference types** | Assigning an `int` copies the *value* — fully independent afterward. Assigning an array copies the *reference* — both variables point at the same heap object. |
| **dynamic** | Opts out of compile-time type checking entirely — all checking deferred to runtime. Use sparingly. |

---

## 2. Boolean & Type Operators

### Code


### --- Short-circuiting: && / || vs & / | ---
```csharp
bool SimulateExpensiveCheck()
{
    Console.WriteLine("DB called!");
    return false;
}

bool isAdmin = false;

if (isAdmin && SimulateExpensiveCheck()) { }  // DB call SKIPPED — short-circuited
if (isAdmin & SimulateExpensiveCheck())  { }  // DB call ALWAYS RUNS — forced evaluation
```


###  --- is / as pattern matching ---
```csharp
object rawInput = "some payload";

if (rawInput is string)                    // old way (two operations)
{
    string legacyCast = (string)rawInput;
}

if (rawInput is string cleanString)        // modern way (one operation, scoped variable)
{
    Console.WriteLine(cleanString.Length);
}

object numericalInput = 2026;
string? failedCast = numericalInput as string; // 'as' never throws — returns null on failure
```

###  --- Logical pattern combinators ---
```csharp
int load = 85;
if (load is > 80 and <= 100) { /* ... */ }

object? ctx = null;
if (ctx is not null) { /* ... */ }
```

### Explanation

| Concept | Key idea |
|---|---|
| **`&&` / `\|\|`** | Short-circuit operators. If the left side already determines the result, the right side is **never evaluated**. |
| **`&` / `\|` on bools** | Bitwise operators force **both** sides to evaluate, no matter what — a real bug source when the right side has side effects. |
| **`is` pattern matching** | `x is string s` checks the type **and** declares a casted, scoped variable in one step. |
| **`as`** | Safe cast — returns `null` on failure instead of throwing. Only works on reference types / nullable value types. |
| **`is > 80 and <= 100`** | Modern relational pattern combinators (`and`, `or`, `not`) replace `x > 80 && x <= 100` chains. |
| **`is not null`** | Preferred modern style over `!= null`. |

---

## 3. Casting & Type Conversion

### Code


###  --- Implicit (safe) vs explicit (lossy) ---
```csharp
int sourceInt = 12345;
double implicitDouble = sourceInt;   // no cast needed — always safe

double pi = 3.14159;
int truncatedInt = (int)pi;          // 3 — truncates, does NOT round
```

###  --- Boxing / unboxing ---
```csharp
int stackValue = 2026;
object boxedObject = stackValue;          // BOXING: heap allocation happens here
int unboxedStackValue = (int)boxedObject; // UNBOXING: must match exact original type

try
{
    double invalidUnbox = (double)boxedObject; // throws — boxed as int, not double
}
catch (InvalidCastException) { }
```



###  --- Parse vs TryParse vs Convert ---
```csharp
string cleanInput = "42";
string corruptInput = "42A";

int validParsed = int.Parse(cleanInput); // throws on bad input

if (int.TryParse(corruptInput, out int parseResult)) { /* succeeds */ }
else { /* fails safely, no exception */ }

string? nullInput = null;
int nullConverted = Convert.ToInt32(nullInput); // returns 0, doesn't throw
```


### --- Banker's rounding trap ---
```csharp
double value1 = 2.5;
double value2 = 3.5;

Console.WriteLine((int)value1);             // 2 — truncation
Console.WriteLine((int)value2);             // 3 — truncation

Console.WriteLine(Convert.ToInt32(value1)); // 2 — rounds to nearest EVEN
Console.WriteLine(Convert.ToInt32(value2)); // 4 — rounds to nearest EVEN
```

### Explanation

| Concept | Key idea |
|---|---|
| **Implicit conversion** | Compiler-guaranteed safe — no syntax needed. |
| **Explicit conversion** | Requires `(type)` syntax because data loss is possible. Truncates, doesn't round. |
| **Boxing** | Wrapping a value type as `object` allocates a new object on the heap and copies the value in. |
| **Unboxing** | Requires an **exact** type match — throws `InvalidCastException` on mismatch, even if the value would numerically fit. |
| **`int.Parse`** | Throws on bad/null input. |
| **`TryParse`** | Returns a `bool` + `out` param — no exceptions, idiomatic for untrusted input. |
| **`Convert.ToInt32`** | Handles `null` gracefully (returns `0`). |
| **Banker's rounding** | `(int)` cast always truncates. `Convert.ToInt32` on an exact `.5` rounds to the **nearest even number** — reduces cumulative bias in large datasets. |

---

## 4. Enums

### Code

```csharp
public enum AccountStatus : byte
{
    Pending = 0,
    Active = 1,
    Suspended = 2
}

[Flags]
public enum UserPermissions
{
    None = 0,
    Read = 1,    // 0001
    Write = 2,   // 0010
    Delete = 4,  // 0100
    Admin = 8    // 1000
}

// --- Underlying storage ---
AccountStatus currentStatus = AccountStatus.Active;
byte rawValue = (byte)currentStatus; // 1 — enums are just integers underneath

// --- [Flags] combining ---
UserPermissions managerRights = UserPermissions.Read | UserPermissions.Write;
bool canWrite = managerRights.HasFlag(UserPermissions.Write); // true

// --- The undefined-value security trap ---
AccountStatus maliciousPayload = (AccountStatus)99; // compiles and runs fine!

if (Enum.IsDefined(typeof(AccountStatus), maliciousPayload)) { /* safe */ }
else { /* 99 isn't a real AccountStatus value — caught here */ }

// --- Parsing strings into enums ---
string incomingInput = "Active";

// Legacy: boxes, uses reflection internally
AccountStatus parsedLegacy = (AccountStatus)Enum.Parse(typeof(AccountStatus), incomingInput);

// Strongly-typed, safe against bad input
if (Enum.TryParse<AccountStatus>(incomingInput, ignoreCase: true, out AccountStatus safeParsed)) { }

// A NUMERIC string parses successfully even with no matching name!
if (Enum.TryParse<AccountStatus>("99", out AccountStatus numericParsed))
{
    // succeeds — numericParsed is (AccountStatus)99, an undefined value
}

// --- Iterating all enum values ---
foreach (AccountStatus status in Enum.GetValues(typeof(AccountStatus)))       // legacy, allocates an array
{
    Console.WriteLine($"{status} = {(byte)status}");
}

foreach (AccountStatus status in Enum.GetValues<AccountStatus>())            // modern, generic, less overhead
{
    Console.WriteLine(status);
}
```

### Explanation

| Concept | Key idea |
|---|---|
| **Underlying type** | Enums default to `int` but can be declared `byte`/`short`/`long` to control memory footprint. At runtime an enum **is** its underlying integer. |
| **`[Flags]`** | Requires every member to be a power of 2 so each occupies an isolated bit. `\|` combines states; `.HasFlag()` checks membership. |
| **The security trap** | C# does **not** validate that a cast integer maps to a defined enum member — `(AccountStatus)99` compiles and runs. Always guard external input with `Enum.IsDefined`. |
| **`Enum.Parse` / `TryParse` on numeric strings** | An enum is really just a named integer. When `TryParse` receives a string that looks numeric (like `"99"`), it doesn't bother checking names — it converts the digits directly to the underlying integer and succeeds, even though `99` has no defined label. This is the *same* underlying gap as the casting trap above — never trust a parsed enum from external input without an `Enum.IsDefined` check. |
| **`Enum.Parse` vs `Enum.TryParse`** | `Parse` returns a boxed `object` and throws on failure — more allocation, more risk. `TryParse<T>` is generic, strongly-typed, and returns `false` instead of throwing. |
| **`Enum.GetValues(typeof(T))`** | Legacy, reflection-based — allocates a new array on every call. |
| **`Enum.GetValues<T>()`** | Modern generic version — same result, lower overhead, preferred in hot paths (e.g. building dropdown lists repeatedly). |

---

## 5. Tuples

### Code


### --- Legacy Tuple (class) vs ValueTuple (struct) ---
```csharp
Tuple<int, string> legacyTuple = new Tuple<int, string>(1, "Legacy");         // heap-allocated, Item1/Item2

ValueTuple<int, string> newTuple = new ValueTuple<int, string>(1, "Legacy");  // explicit ValueTuple syntax

(int, string) modern1Tuple = (200, "Success");                               // unnamed fields (Item1, Item2)
(int StatusCode, string Message) modernTuple = (200, "Success");             // named fields
```

###  --- Multi-value return without a custom class ---
```csharp
(int Sum, double Average, bool IsValid) CalculateTelemetry(int[] metrics)
{
    if (metrics.Length == 0) return (0, 0.0, false);
    int sum = 0;
    foreach (var val in metrics) sum += val;
    return (sum, (double)sum / metrics.Length, true);
}

int[] dataPoints = { 10, 20, 30, 40 };
var result = CalculateTelemetry(dataPoints);
Console.WriteLine($"{result.Sum}, {result.Average}");
```

### --- Deconstruction & discards ---
```csharp
var (total, _, isValid) = CalculateTelemetry(dataPoints); // '_' discards the Average value
```

### Explanation

| Concept | Key idea |
|---|---|
| **`Tuple<T1,T2>`** | A `class` — heap-allocated, garbage-collected, generic `Item1`/`Item2` field names. |
| **`ValueTuple<T1,T2>`** | The explicit struct form — same thing the `(int, string)` shorthand compiles to. Written out fully here to show it's a real, ordinary generic struct, not compiler magic. |
| **`(int, string)` — unnamed** | Still gets default field names `Item1`, `Item2` since no labels were given. |
| **`(int StatusCode, string Message)` — named** | Same struct underneath, but the compiler lets you address fields by the names you chose instead of `Item1`/`Item2`. |
| **Use case** | Best for **private/internal** helper methods returning 2-3 related values without a one-off class. |
| **Deconstruction / Discards (`_`)** | Shreds a tuple into named locals in one line; `_` explicitly throws away a value you don't need. |

---

## 6. Strings & StringBuilder

### Code

###  --- Immutability cost ---
```csharp
using System.Text;


string report = "Report Header\n";
for (int i = 0; i < 1000; i++)
{
    report += $"Line Item Row #{i}\n"; // allocates a NEW string every iteration
}
```

###  --- StringBuilder avoids the allocation churn ---
```csharp
StringBuilder optimizedBuilder = new StringBuilder(4096); // pre-sized capacity
optimizedBuilder.AppendLine("Report Header");
for (int i = 0; i < 1000; i++)
{
    optimizedBuilder.Append("Line Item Row #").Append(i).AppendLine();
}
string finalReport = optimizedBuilder.ToString(); // exactly ONE final allocation
```
###  --- String interning ---
```csharp
string literal1 = "AinShams";
string literal2 = "AinShams";
bool sameRef = ReferenceEquals(literal1, literal2); // true — compile-time literals share memory

string dynamicPart1 = "Ain";
string dynamicPart2 = "Shams";
string computedString = dynamicPart1 + dynamicPart2;
bool computedSameRef = ReferenceEquals(literal1, computedString); // false — built at runtime

string forcedIntern = string.Intern(computedString);
bool internedSameRef = ReferenceEquals(literal1, forcedIntern); // true — manually interned
```

### Explanation

| Concept | Key idea |
|---|---|
| **Immutability** | `report += ...` doesn't modify `report` — it allocates a brand new string containing old + new content and discards the old one as garbage. |
| **`StringBuilder`** | Maintains one resizable internal buffer, mutated in place. `.ToString()` does exactly one final allocation. |
| **Constructor capacity** | `new StringBuilder(4096)` pre-sizes the internal buffer to avoid *internal* resizing too. |
| **String interning** | Compile-time literal text automatically shares the same object (`ReferenceEquals` is `true`). |
| **Runtime-built strings** | Do **not** get auto-interned — distinct heap objects even with identical content. |
| **`string.Intern()`** | Manually forces a runtime string into the intern pool — rarely needed. |

---

## 7. Nulls & Nullable Types

### Code
###  --- Nullable<T> for value types ---
```csharp
#nullable enable


int? databaseId = null; // really System.Nullable<int>
if (databaseId.HasValue) { Console.WriteLine(databaseId.Value); }

// --- Nullable Reference Types (NRT) ---
string nonNullableString = "Ain Shams";   // compiler assumes never null
string? nullableString = null;             // explicitly allowed to be null

// Console.WriteLine(nullableString.Length); // compiler WARNING here

if (nullableString is not null)
{
    Console.WriteLine(nullableString.Length); // safe — compiler tracked the check
}
```

###  --- Null operators ---
```csharp
string? externalInput = null;
string safeContent = externalInput ?? "Default Fallback Content"; // ??  = fallback

string[]? serverPayload = null;
int? payloadLength = serverPayload?.Length; // ?. = short-circuits to null, no throw

string? cachedToken = null;
cachedToken ??= "GENERATED_TOKEN"; // ??= assigns ONLY if currently null
```

### Explanation

| Concept | Key idea |
|---|---|
| **`Nullable<T>` (`int?`)** | Wraps the real value in a struct with a `HasValue` flag. |
| **Nullable Reference Types** | `string` means "never null," `string?` means "might be null." Compiler performs flow analysis and warns at compile time on unchecked dereferences. |
| **`??`** | Null-coalescing — supplies a default if the left side is null. |
| **`?.`** | Null-conditional — short-circuits to `null` instead of throwing. |
| **`??=`** | Assigns only if the variable is currently null — ideal for lazy-initializing a cache. |

---

## 8. Arrays

### Code


### --- Contiguous memory, fixed size ---
```csharp
int[] sequentialMemory = new int[5] { 10, 20, 30, 40, 50 };
Array.Resize(ref sequentialMemory, 6); // secretly allocates a NEW array + copies everything
```
###  --- Multidimensional (rectangular) vs Jagged ---

```csharp
int[,] rectangularMatrix = new int[2, 3] { { 1, 2, 3 }, { 4, 5, 6 } }; // ONE contiguous block

int[][] jaggedMatrix = new int[2][];
jaggedMatrix[0] = new int[] { 1, 2, 3 };
jaggedMatrix[1] = new int[] { 4, 5 };       // rows can be different lengths

int[][] jaggedArray = [
    [1, 2],
    [3, 4, 5, 6],
    [7, 8, 9],
    [10]
];
```
### --- Modern indices & ranges ---
```csharp
int[] timeline = { 100, 200, 300, 400, 500 };
int finalElement = timeline[^1];      // "1 from the end" = last element
int[] subRange = timeline[1..4];      // indices 1,2,3 — end index is EXCLUSIVE

// --- Ranges on jagged arrays ---
int[][] jagged = [
    [1, 2, 3],
    [4, 5, 6, 7],
    [8, 9],
    [10, 11, 12]
];

int[][] rowSlice = jagged[1..3];        // slice the OUTER array: rows at index 1,2
                                         // Result: [[4,5,6,7], [8,9]]

int[] elementSlice = jagged[1][1..3];   // slice an INNER row: elements 1..2 of row 1
                                         // Result: [5, 6]

// NOTE: this range syntax works on jagged arrays (arrays of arrays) because each
// row is its own real array. It does NOT work directly on a rectangular int[,] —
// a true multidimensional array has no built-in range-slicing support in C#.
```

### Explanation

| Concept | Key idea |
|---|---|
| **Contiguous memory** | An array is one unbroken block on the heap — *why* `arr[i]` is O(1). |
| **Fixed size** | `Array.Resize` allocates a new array and copies every element over — O(n) disguised as a simple call. |
| **Rectangular (`int[,]`)** | One contiguous block, row-major. Every row must be the same length. |
| **Jagged (`int[][]`)** | An array of pointers — each row is its own separately-allocated array. Rows can differ in length. |
| **`^` (index-from-end)** | `^1` means "1 from the end," equivalent to `array.Length - 1`. |
| **`..` (range)** | Start-**inclusive**, end-**exclusive**. |
| **Slicing jagged arrays** | `jagged[1..3]` slices the outer array (which rows). `jagged[1][1..3]` slices *inside* a specific row (which elements). Both work because each row is a genuine, independent `int[]`. |
| **Slicing rectangular arrays** | `int[,]` has no native range-slicing support — you'd need to manually copy a sub-block with nested loops, or use `Span2D`-style third-party helpers. This is a real, open gap in the language worth remembering. |

---

## 9. List\<T\>

### Code


### --- Count vs Capacity, the doubling algorithm ---
```csharp
List<int> dynamicList = new List<int>();
// Capacity: 0

dynamicList.Add(10);
// Capacity: 4 (default base capacity on first add)

dynamicList.Add(20);
dynamicList.Add(30);
dynamicList.Add(40);
// Count: 4, Capacity: 4 — full

dynamicList.Add(50);
// Count: 5, Capacity: 8 — DOUBLED, old array copied, old array garbage-collected
// This is why Add() is "amortized O(1)": free most calls, occasionally O(n) on resize.
```

###  --- Pre-allocation avoids repeated doubling ---
```csharp
List<int> optimizedList = new List<int>(500); // ONE allocation instead of ~8 doublings


// --- TrimExcess reclaims dormant memory ---
List<int> largePayload = new List<int>(10000);
largePayload.Add(1);
largePayload.Add(2);
// Count: 2, Capacity: still 10000

largePayload.TrimExcess();
// Capacity shrinks down to match actual Count
```

### --- Insert/Remove structural cost ---
```csharp
List<string> prioritizedWorklines = new List<string> { "LineItem_A", "LineItem_B", "LineItem_C" };

prioritizedWorklines.Insert(1, "URGENT_INTERCEPTOR");
// "LineItem_B" and "LineItem_C" both physically shift one slot RIGHT — O(n)

prioritizedWorklines.Remove("URGENT_INTERCEPTOR");
// Search is O(n), then every subsequent item shifts one slot LEFT — O(n)

// Appending/removing from the VERY END avoids shifting entirely:
// dynamicList.RemoveAt(dynamicList.Count - 1); // pure O(1)
```

### Explanation

| Concept | Key idea |
|---|---|
| **`Count` vs `Capacity`** | `Count` = actual elements. `Capacity` = size of the real underlying array, usually with headroom. |
| **Doubling algorithm** | Exceeding capacity allocates a new array **double the size**, copies every element, discards the old array — amortized O(1) `Add`. |
| **Cache-friendliness** | Because the backing store is one contiguous array, adjacent elements load into CPU cache together — a real, hardware-level performance advantage over node-based structures like `LinkedList<T>`. |
| **Pre-allocation** | `new List<int>(500)` skips repeated reallocate-and-copy cycles if you can estimate size ahead of time. |
| **`TrimExcess()`** | Removing items shrinks `Count` but never automatically shrinks `Capacity` — call this explicitly to give memory back. |
| **`Insert`/`Remove` in the middle** | Forces a physical shift of every subsequent element (`Array.Copy` under the hood) — an explicit O(n) structural cost, separate from the resize cost above. |
| **End-of-list operations** | Adding/removing at the very end never requires shifting — stays O(1). This asymmetry is *the* reason to prefer appending over inserting at the front/middle whenever possible. |

---

## 10. Dictionary\<TKey,TValue\>

### Code


###  --- Hashing pipeline ---
```csharp
Dictionary<string, string> sessionCache = new Dictionary<string, string>();
sessionCache.Add("SessionXYZ", "User_Data_Payload");

string lookupKey = "SessionXYZ";
int generatedHash = lookupKey.GetHashCode(); // determines the internal bucket index
```

###  --- Collision handling ---
```csharp
sessionCache["SessionABC"] = "Secondary_Payload"; // handled via internal chaining if collision occurs
```

###  --- ContainsKey (double lookup) vs TryGetValue (single lookup) ---
```csharp
string targetKey = "MissingToken";

if (sessionCache.ContainsKey(targetKey))          // lookup #1
{
    string data = sessionCache[targetKey];         // lookup #2 — redundant work
}

if (sessionCache.TryGetValue(targetKey, out string? securePayload)) // ONE lookup total
{
    // found
}

```
### --- Iteration patterns ---
```csharp
foreach (var key in sessionCache.Keys)
{
    var val = sessionCache[key]; //  re-hashes and re-searches for EVERY key — redundant work
    Console.WriteLine($"Key: {key}, Val: {val}");
}

foreach (var (key, value) in sessionCache) //  deconstructs each KeyValuePair directly — no re-lookup
{
    Console.WriteLine($"Key: {key}, Val: {value}");
}
```

### Explanation

| Concept | Key idea |
|---|---|
| **Hashing** | Calls `.GetHashCode()` on the key, runs it through a modulo against the bucket-array size, lands on an exact index — O(1) average lookup regardless of size. |
| **Collisions** | Resolved via **chaining** — each bucket points to a chain of entries; on collision the runtime walks the chain calling `.Equals()` until it finds the exact match. |
| **`ContainsKey` + indexer** | Hashes and searches **twice** — once to check, once to fetch. |
| **`TryGetValue`** | Same work, only **once** — always prefer this pattern. |
| **Iterating `.Keys` and re-indexing** | `foreach (var key in dict.Keys) { dict[key]; }` re-hashes and re-searches the dictionary once *per key* — pure wasted work, since you're right next to a `Dictionary<TKey,TValue>` that already knows both halves of the pair. |
| **Iterating the dictionary directly** | `foreach (var (key, value) in dict)` deconstructs each internal `KeyValuePair<TKey,TValue>` as you walk past it — zero extra hashing, zero extra lookups. Always prefer this when you need both key and value. |

---

## 11. Stack\<T\> & Queue\<T\>

### Code

###  --- Stack<T>: LIFO ---

```csharp
Stack<string> navigationHistory = new Stack<string>();

navigationHistory.Push("Marketplace_Home");
navigationHistory.Push("Product_Details_Id_45");
navigationHistory.Push("Checkout_Form");

string top = navigationHistory.Peek();   // inspect top WITHOUT removing
string popped = navigationHistory.Pop(); // remove and return the top
```

###  --- Queue<T>: FIFO ---

```csharp
Queue<string> telemetryStream = new Queue<string>();

telemetryStream.Enqueue("Packet_01_Auth");
telemetryStream.Enqueue("Packet_02_GPS");
telemetryStream.Enqueue("Packet_03_Sensor");

string processed = telemetryStream.Dequeue(); // remove and return the OLDEST item
string next = telemetryStream.Peek();

```

### --- Defensive emptiness handling ---

```csharp
navigationHistory.Clear();

if (navigationHistory.Count > 0)   // traditional check-then-act
{
    navigationHistory.Pop();
}

if (navigationHistory.TryPop(out string? activeScreen)) // modern: check-and-act atomically
{
    Console.WriteLine($"Popped: {activeScreen}");
}
else
{
    Console.WriteLine("Stack empty — bypassed cleanly, no exception.");
}
```

### Explanation

| Concept | Key idea |
|---|---|
| **`Stack<T>`** | Last In, First Out — like a stack of plates. `Push`/`Pop`/`Peek` are all O(1). No random access — you cannot do `stack[2]`. |
| **`Queue<T>`** | First In, First Out — like a checkout line. Internally uses a **circular array buffer** tracking `head`/`tail` indices, so `Dequeue` is a true O(1) operation — unlike removing index 0 from a `List<T>`, which would require shifting every remaining element. |
| **`.Peek()`** | Inspects the next item without removing it — works on both `Stack<T>` and `Queue<T>`. |
| **Empty-collection crash** | Calling `.Pop()`/`.Dequeue()` on an empty collection throws `InvalidOperationException`. |
| **`TryPop`/`TryDequeue`** | Combine the existence-check and the extraction into a single atomic step, returning `bool` + `out` — no exception, no separate `Count` check needed first. |

---

## 12. LinkedList\<T\>

### Code

```csharp
LinkedList<string> processingPipeline = new LinkedList<string>();

// Adding wraps each value in a LinkedListNode<T>, scattered across the heap
LinkedListNode<string> firstNode = processingPipeline.AddFirst("Central_Processor");
LinkedListNode<string> finalNode = processingPipeline.AddLast("Database_Sink");
LinkedListNode<string> middleNode = processingPipeline.AddAfter(firstNode, "Validation_Interceptor");

// Manually walking the pointer chain
LinkedListNode<string>? current = processingPipeline.First;
while (current != null)
{
    Console.WriteLine(current.Value);
    current = current.Next;
}

// O(1) insertion if you already hold a node reference — no shifting, no resizing
processingPipeline.AddBefore(middleNode, "Log_Publisher");

// Finding a node requires a full O(n) linear walk — no index access exists
LinkedListNode<string>? targetNode = processingPipeline.Find("Database_Sink");
```

### Explanation

| Concept | Key idea |
|---|---|
| **Node-based structure** | Unlike `List<T>`'s single flat array, `LinkedList<T>` allocates separate `LinkedListNode<T>` objects scattered across the heap. Each node holds the `Value`, a `Next` pointer, and a `Previous` pointer (it's *doubly* linked). |
| **No index access** | You cannot write `list[3]` — there's no way to jump directly to a position, only walk node by node from `First` or `Last`. |
| **O(1) insertion/removal — with a caveat** | If you already hold a reference to the neighboring node (like `middleNode` here), inserting or removing next to it is a pure pointer-rewiring operation — zero shifting, zero resizing. |
| **O(n) search** | `.Find()` still has to walk the chain one node at a time — this is the real trade-off versus `List<T>`'s cache-friendly contiguous layout. `LinkedList<T>` wins on *known-position* insert/remove, loses on *lookup*. |

---

## 13. HashSet\<T\>

### Code

```csharp
// --- O(1) uniqueness ---
HashSet<string> uniqueBlacklist = new HashSet<string>();
bool firstAdd = uniqueBlacklist.Add("malicious-ip-123");     // true
bool duplicateAdd = uniqueBlacklist.Add("malicious-ip-123"); // false — silently ignored

// --- O(1) Contains vs O(n) List.Contains ---
bool isBlacklisted = uniqueBlacklist.Contains("malicious-ip-123"); // instant, regardless of size

// --- Set theory operations ---
HashSet<int> activeUserIds = new HashSet<int> { 101, 102, 103, 104 };
HashSet<int> premiumUserIds = new HashSet<int> { 103, 104, 105, 106 };

HashSet<int> activePremiumUsers = new HashSet<int>(activeUserIds);
activePremiumUsers.IntersectWith(premiumUserIds); // 103, 104 — inner join

HashSet<int> standardActiveUsers = new HashSet<int>(activeUserIds);
standardActiveUsers.ExceptWith(premiumUserIds);   // 101, 102 — left anti-join (a "WHERE NOT IN" filter)

HashSet<int> totalUniqueUsers = new HashSet<int>(activeUserIds);
totalUniqueUsers.UnionWith(premiumUserIds);       // 101,102,103,104,105,106 — full outer join
```

### Explanation

| Concept | Key idea |
|---|---|
| **Uniqueness via hashing** | Same hashing machinery as `Dictionary`, but stores only the key. `.Add()` returns `false` on duplicates — no exception, no manual checking. |
| **`Contains` speed** | O(1) via hashing, versus O(n) for `List<T>.Contains`. De-duplicating at scale: `List` becomes O(n²) in a loop, `HashSet` stays O(n). |
| **`IntersectWith`** | Keeps only elements in **both** sets — SQL inner join. |
| **`ExceptWith`** | Keeps elements in the first set **not** in the second — like a `WHERE NOT IN (...)` filter / left anti-join. |
| **`UnionWith`** | Combines both sets with automatic de-duplication — full outer join. |

---

## 14. SortedSet\<T\>

### Code

```csharp
public record ProductAllocation(string Name, int PriorityLevel);

public class ProductPriorityComparer : IComparer<ProductAllocation>
{
    public int Compare(ProductAllocation? x, ProductAllocation? y)
    {
        if (x == null || y == null) return 0;
        return y.PriorityLevel.CompareTo(x.PriorityLevel); // descending
    }
}
```

### --- Auto-sorting via a balanced tree ---

```csharp
SortedSet<int> priorityScores = new SortedSet<int>();
priorityScores.Add(85);
priorityScores.Add(12);
priorityScores.Add(45);
priorityScores.Add(12); // duplicate — ignored

foreach (int score in priorityScores) { /* 12, 45, 85 — always sorted */ }

// --- Range queries ---
SortedSet<int> timelineIndices = new SortedSet<int> { 10, 20, 30, 40, 50, 60, 70 };
SortedSet<int> criticalWindow = timelineIndices.GetViewBetween(25, 55); // 30, 40, 50

// --- Custom objects need an IComparer ---
SortedSet<ProductAllocation> productQueue = new SortedSet<ProductAllocation>(new ProductPriorityComparer());
productQueue.Add(new ProductAllocation("Brake Pad Batch A", 2));
productQueue.Add(new ProductAllocation("Engine Gearbox Cluster", 5));
productQueue.Add(new ProductAllocation("Alternator Assembly", 1));
// iterates in descending PriorityLevel order
```

### Explanation

| Concept | Key idea |
|---|---|
| **Tree-based, not hash-based** | Uses `IComparable`/`IComparer` to place elements into a self-balancing binary search tree (red-black tree). Stays sorted automatically. |
| **Performance trade-off** | Insert/lookup/remove are O(log n) — slower than `HashSet<T>`'s O(1) — in exchange for guaranteed sorted iteration order. |
| **`GetViewBetween`** | An efficient range query directly against the tree — `HashSet` has no concept of order and cannot offer this. |
| **Custom types** | Need an explicit `IComparer<T>` implementation (like `ProductPriorityComparer`) passed to the constructor — otherwise the tree has no way to rank instances and throws at runtime. |

---


## 16. Indexers

### Code

```csharp
public class DataBufferCache
{
    private readonly string[] _internalStorage = new string[10];

    public string? this[int index]
    {
        get
        {
            if (index < 0 || index >= _internalStorage.Length) return null; // safe bounds check
            return _internalStorage[index];
        }
        set
        {
            if (index >= 0 && index < _internalStorage.Length && value is not null)
                _internalStorage[index] = value;
        }
    }
}

public class HttpHeaderCollection
{
    private readonly Dictionary<string, string> _headerStore = new Dictionary<string, string>();

    public string this[string headerName]
    {
        get => _headerStore.TryGetValue(headerName, out var val) ? val : "Not Found";
        set => _headerStore[headerName] = value;
    }
}

// --- Usage ---
DataBufferCache cache = new DataBufferCache();
cache[0] = "Telemetry_Packet_A";
Console.WriteLine(cache[0]);      // "Telemetry_Packet_A"
Console.WriteLine(cache[99]);     // null — safe, no exception

HttpHeaderCollection headers = new HttpHeaderCollection();
headers["Authorization"] = "Bearer JWT_TOKEN_XYZ_2026";
Console.WriteLine(headers["Authorization"]);
```

### Explanation

| Concept | Key idea |
|---|---|
| **Syntax** | `public string? this[int index] { get; set; }` enables bracket syntax on a custom class. Compiles to `get_Item`/`set_Item` methods. |
| **Not limited to `int`** | `HttpHeaderCollection` uses a `string` key. A class can even overload multiple indexer signatures. |
| **Defensive design** | Because you write the getter/setter, you control invalid-access behavior — `DataBufferCache` returns `null` instead of throwing `IndexOutOfRangeException`. |

---

## 17. Object Contracts — Equals, GetHashCode, IComparable

### Code

```csharp
public class Temperature : IComparable<Temperature>
{
    public int Value { get; }
    public Temperature(int value) { Value = value; }

    // EQUALS: tells C# to compare data, not memory addresses
    public override bool Equals(object? obj)
    {
        if (obj is Temperature other) return this.Value == other.Value;
        return false;
    }

    // GETHASHCODE: MUST return the same hash whenever Equals() is true
    public override int GetHashCode() => Value.GetHashCode();

    // ICOMPARABLE: defines natural sort order for .Sort() / SortedSet
    public int CompareTo(Temperature? other)
    {
        if (other == null) return 1;
        return this.Value.CompareTo(other.Value); // ascending
    }
}

var temp1 = new Temperature(25);
var temp2 = new Temperature(25);
var temp3 = new Temperature(40);

bool areEqual = temp1.Equals(temp2);        // true — same VALUE, different objects
int comparison = temp1.CompareTo(temp3);    // -1  — 25 is "less than" 40


// A slightly richer example: multi-field equality with HashCode.Combine
public class InventoryItem : IComparable<InventoryItem>
{
    public string Sku { get; }
    public int Cost { get; }
    public InventoryItem(string sku, int cost) { Sku = sku; Cost = cost; }

    public override bool Equals(object? obj)
    {
        if (obj is InventoryItem other)
            return this.Sku == other.Sku && this.Cost == other.Cost;
        return false;
    }

    // HashCode.Combine mixes multiple fields into one well-distributed hash
    public override int GetHashCode() => HashCode.Combine(Sku, Cost);

    public int CompareTo(InventoryItem? other)
    {
        if (other == null) return 1;
        return this.Cost.CompareTo(other.Cost); // sort ascending by Cost
    }
}
```

### Explanation

| Concept | Key idea |
|---|---|
| **`Equals()`** | By default, a `class` compares by *reference* (are these the same object?). Overriding `Equals()` switches this to *value* comparison — `temp1.Equals(temp2)` is `true` even though they're two separate objects, because their `Value` fields match. |
| **`GetHashCode()`** | **Hard rule**: if `a.Equals(b)` is `true`, then `a.GetHashCode()` **must** equal `b.GetHashCode()`. This is what `HashSet<T>` and `Dictionary<TKey,_>` rely on internally — breaking this rule causes silent, hard-to-debug bugs where "equal" objects behave as different dictionary keys. |
| **`HashCode.Combine(...)`** | The modern, built-in way to mix multiple fields into one well-distributed hash — avoids manually writing bit-shifting hash formulas, and avoids hash clustering. |
| **`IComparable<T>` / `CompareTo()`** | Defines *natural ordering* — return negative if `this` comes first, `0` if equal, positive if `this` comes after. This is exactly what `.Sort()`, `SortedSet<T>`, and `SortedList<T>` call internally when no custom comparer is supplied. |
| **Why override all three together** | `Equals`, `GetHashCode`, and `CompareTo` are a related contract — get one wrong (e.g. override `Equals` but not `GetHashCode`) and collections built on hashing (`HashSet`, `Dictionary`) will misbehave even though the type still compiles fine. |

---

## 18. Custom IEnumerable / IEnumerator Implementation

### Code

```csharp
using System.Collections;

public class WeatherReport : IEnumerable
{
    private int[] _readings;
    public WeatherReport(int[] readings) { _readings = readings; }

    // Factory method: hands out a fresh cursor every time someone starts a foreach
    public IEnumerator GetEnumerator() => new WeatherReportEnumerator(_readings);
}

public class WeatherReportEnumerator : IEnumerator
{
    private int[] _data;
    private int _position = -1; // starts BEFORE index 0

    public WeatherReportEnumerator(int[] data) { _data = data; }

    public bool MoveNext()
    {
        _position++;
        return (_position < _data.Length);
    }

    public object Current
    {
        get
        {
            if (_position < 0 || _position >= _data.Length)
                throw new InvalidOperationException();
            return _data[_position];
        }
    }

    public void Reset() => _position = -1;
}

// --- Usage: this is what `foreach` compiles into ---
var dailyTemps = new int[] { 22, 28, 31 };
var report = new WeatherReport(dailyTemps);

IEnumerator enumerator = report.GetEnumerator();
try
{
    while (enumerator.MoveNext())
    {
        int currentTemp = (int)enumerator.Current;
        Console.WriteLine(currentTemp);
    }
}
finally
{
    if (enumerator is IDisposable disposable) disposable.Dispose();
}

// The line above is EXACTLY what this sugar compiles to:
// foreach (int temp in report) { Console.WriteLine(temp); }
```

### Explanation

| Concept | Key idea |
|---|---|
| **`IEnumerable`** | The "factory" contract — a single method, `GetEnumerator()`, that hands out a fresh cursor. Implementing this on `WeatherReport` is what makes `foreach (var t in report)` legal. |
| **`IEnumerator`** | The actual "cursor" contract — `MoveNext()` advances the position and returns `false` once exhausted; `Current` reads the value at the current position; `Reset()` rewinds it. |
| **`_position = -1` start** | The cursor deliberately starts *before* the first element — the very first `MoveNext()` call is what advances it to index 0. This mirrors exactly how built-in collection enumerators behave. |
| **Why a separate enumerator class** | Splitting `WeatherReport` (the collection) from `WeatherReportEnumerator` (the cursor) means multiple independent `foreach` loops over the same `WeatherReport` each get their own fresh position — they don't interfere with each other. |
| **This is literally what `foreach` compiles to** | Every `foreach` loop over *any* `IEnumerable` — arrays, `List<T>`, your own custom types — desugars into exactly the `GetEnumerator()` / `while(MoveNext())` / `Current` / `finally { Dispose() }` pattern shown in the usage block above. |

---

## 19. `yield return` & Deferred Execution

### Code

```csharp
private static IEnumerable<int> StreamTemperaturesLazy()
{
    Console.WriteLine("Calculating reading 1...");
    yield return -5;

    Console.WriteLine("Calculating reading 2...");
    yield return 18;

    Console.WriteLine("Calculating reading 3..."); // never runs if the consumer breaks early!
    yield return 42;
}

// Calling this does NOT execute any code inside it yet — just builds a state machine.
IEnumerable<int> dynamicStream = StreamTemperaturesLazy();

Console.WriteLine("Starting consumer loop:");
foreach (int temperature in dynamicStream)
{
    Console.WriteLine($"Consumer received: {temperature}");
    if (temperature >= 15) break; // halts here — "reading 3" line above never prints
}
```

### Explanation

| Concept | Key idea |
|---|---|
| **`yield return`** | Tells the compiler to generate an entire hidden state machine for this method. Calling `StreamTemperaturesLazy()` does **not** run any of its code — it just constructs that state machine object. |
| **Deferred / lazy execution** | Code inside the method only executes as the consumer actually pulls values via `MoveNext()`. Watch the console output order: "Calculating reading 1" prints right when the `foreach` asks for the first value, not before. |
| **Early exit saves real work** | Because the consumer `break`s after receiving `18` (`>= 15`), "Calculating reading 3" **never prints** — the method genuinely never resumes execution past that `yield return`. |
| **Why this matters** | This exact mechanism — state machine + lazy pull — is what every LINQ method (`.Where()`, `.Select()`, etc.) is built on. Understanding this method-level example is what makes LINQ's laziness make sense later. |

---

## Quick Self-Check Questions

1. Why does `int.MaxValue + 1` not throw by default, and what keyword makes it throw?
2. Why can two different `float` values compare as equal with `==`?
3. Why is unboxing to the wrong type an `InvalidCastException`, even if the value would numerically fit?
4. Why does `Enum.TryParse<AccountStatus>("99", out var x)` succeed even though `99` isn't a defined member?
5. What's the real difference between `Tuple<T>`, `ValueTuple<T>`, and the `(int, string)` shorthand?
6. Why does concatenating strings in a loop hurt performance, and what fixes it?
7. What's the difference between `List<T>.Count` and `List<T>.Capacity`, and when does `Capacity` change?
8. Why is inserting into the middle of a `List<T>` an O(n) operation, but appending to the end is O(1)?
9. Why is `Queue<T>.Dequeue()` O(1) when removing `list[0]` from a `List<T>` is O(n)?
10. Why is `LinkedList<T>` fast at insertion but slow at search, exactly the opposite trade-off of `List<T>`?
11. What's the hard rule connecting `Equals()` and `GetHashCode()`, and what breaks if you violate it?
12. What are the two interfaces you need to implement to make your own class support `foreach`?
13. Why doesn't calling a `yield return` method run any code immediately?
14. Why can you slice a jagged array with ranges (`jagged[1..3]`) but not a rectangular `int[,]`?

---



## Self-Study !!
## Exceptions
- [Metigator -Exceptions](https://youtu.be/mA1pOrYAHCU?si=-BrrhQtn9O7s5QTm)
- [Piece of cake dev - Exceptions](https://youtu.be/1O5DQC0gYcY?si=bW2pUnT4ghMGPqYf)
- [Codeacademy - exceptions (cheatsheet)](https://www.codecademy.com/learn/learn-intermediate-c-sharp/modules/c-sharp-exceptions/cheatsheet)
- [Raygun blog - common exceptions](https://raygun.com/blog/common-c-sharp-exceptions/)

## Debugging
- [Rules of debugging](https://dev.to/codemouse92/the-rules-of-debugging-2hf5)
- [Rules_for_Finding_Even_the_Most_Elusive_Hardware_and_Software_Problem - (book ) P.S: its a fun read, give it a try in your free time](https://atakua.org/old-wp/wp-content/uploads/2016/03/David_J._Agans-Debugging-The_9_Indispensable_Rules_for_Finding_Even_the_Most_Elusive_Hardware_and_Software_Problem__2002.pdf)
- [Metigator - (very important video)](https://youtu.be/KM7oJW_XW8I?si=1hS7knp5lkbIrSi5)
- [Tech with Pat - debugging visual studio](https://youtu.be/__3wlUxMXlA?si=3ur4jAjrQHBzZYNN)
- [JetBrains - debugging Rider](https://youtu.be/F3yN9HirSQo?si=XHuBD2dIFYFtWRSl)

---

## Resources
[Metigator C# fundamntals (very important)](https://youtu.be/P1j1PI0YL-M?si=sVai6ghNMqAo-Xcn)
