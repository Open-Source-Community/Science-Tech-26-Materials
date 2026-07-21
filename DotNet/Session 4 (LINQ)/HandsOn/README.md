

## Hands-On 1: Filtering, Projection & Execution Proof

### Task Instructions 

1. Write a deferred LINQ query using **Method Syntax** that gets all **undelivered** orders (`IsDelivered == false`).


2. Use `.Select()` to transform each matching order into a simple string formatted as: `"Order #[Id] for [CustomerName]"`.


3. **Prove Deferred Execution:**
* Store your query in a variable (do **not** call `.ToList()` yet!).


* Add a **new undelivered order** to the `orders` list *after* writing the query:


```csharp
orders.Add(new Order { Id = 5, CustomerName = "Kareem", TotalAmount = 120.0m, IsDelivered = false, Items = new List<string>{ "Webcam" } });

```


* Loop over your query with `foreach` and print the strings. Observe if Kareem shows up!




##  Hands-On 2: Flattening, Ordering & Element Fetching


### Task Instructions 

1. **Flattening:** Extract a single, flat list of **all items** across every order using `SelectMany`. Print them out.


2. **Sorting & Finding:**
* Sort the original `orders` sequence by `TotalAmount` in **descending** order.


* Safely retrieve the **single highest-value order** that has been **delivered** using `FirstOrDefault()`.


* Print the customer name and total amount of that top delivered order (handling potential `null` safely).


