# Session 4 Hands-On Labs



## Lab 1: Advanced Queries

### Data Model
```csharp
Order { Id, CustomerName, TotalAmount, IsDelivered, Items }
Customer { Name, City, IsActive }
```

### Exercises

#### Exercise 1: City Order Analysis
**Task:** Group customers by city and calculate order statistics

**Requirements:**
- Join orders with customers
- Group by customer city
- Calculate: Total orders, Average order value, Total revenue

```csharp
// HINT: Start with join, then group by city
// Expected Output: Cairo: 3 orders, $280 avg
```

####  Exercise 2: Active Customer Orders
**Task:** Find all orders from active customers only

**Requirements:**
- Join orders with customers
- Filter only active customers
- Show customer details with order info

```csharp
// HINT: Use where clause after join
// Expected: Only Mariam, Poula, Ibrahin appear (Marwa is inactive)
```

####  Exercise 3: Set Operations
**Task:** Combine existing orders with new orders

**Requirements:**
- Use Union to combine distinct orders
- Find unique customers across all orders

```csharp
// HINT: orders.Union(newOrders)
// Expected: 7 unique orders combined
```

####  Exercise 4: Item Analysis
**Task:** Find common and unique items between delivered and undelivered orders

**Requirements:**
- Extract items from delivered orders
- Extract items from undelivered orders
- Use Intersect and Except

```csharp
// HINT: SelectMany to flatten items
// Expected: Common items between both groups
```

####  Exercise 5: Customer Spending Summary
**Task:** Create comprehensive customer spending report

**Requirements:**
- Group by customer
- Calculate: Total spent, Average order, Order count
- List all unique items per customer

```csharp
// HINT: GroupBy with multiple aggregations
// Expected: Full customer spending profile
```

---
## Bonus Challenges

### Lab 1 Bonus: Data Quality Check
Find customers with orders but not registered in the customer list
```csharp
// HINT: Use Except
// Expected: John appears as unmatched
```

---

## Lab 2: Operators in Action

### Data Model
```csharp
Order { Id, CustomerName, TotalAmount, IsDelivered, Items, OrderDate, Category }
Product { Name, Price, Category, StockQuantity }
```

### Exercises

####  Exercise 1: Zip Orders with Products
**Task:** Pair orders with products by index

**Requirements:**
- Use Zip to combine sequences
- Calculate price difference

```csharp
// HINT: orders.Zip(products, (order, product) => ...)
// Expected: Order #1 ($150) - Laptop ($899.99): Diff: -$749.99
```

####  Exercise 2: Combine All Items
**Task:** Create a master list of all items from orders and products

**Requirements:**
- Use SelectMany to flatten order items
- Use Concat to combine with product names

```csharp
// HINT: orders.SelectMany(o => o.Items).Concat(products.Select(p => p.Name))
// Expected: Combined list with all items
```

####  Exercise 3: Sequence Comparison
**Task:** Compare different order sequences

**Requirements:**
- Compare delivered orders vs electronics orders
- Use SequenceEqual

```csharp
// HINT: Compare sorted lists
// Expected: True or False based on match
```

####  Exercise 4: Custom Aggregation
**Task:** Use Aggregate for custom calculations

**Requirements:**
- Calculate sum using Aggregate
- Calculate product using Aggregate

```csharp
// HINT: Aggregate(seed, accumulator)
// Expected: Sum and Product of all order amounts
```

####  Exercise 5: Multi-Aggregation
**Task:** Get comprehensive order statistics in one go

**Requirements:**
- Total orders, revenue, average
- Max/min order values
- Delivery count

```csharp
// HINT: Use anonymous type with multiple aggregations
// Expected: Complete statistics object
```

#### Exercise 6: Dictionary Conversion
**Task:** Convert orders to a dictionary

**Requirements:**
- Use ToDictionary with custom key
- Preserve relevant order data

```csharp
// HINT: orders.ToDictionary(o => o.Id, o => new { ... })
// Expected: Dictionary with OrderId as key
```

####  Exercise 7: Type Filtering
**Task:** Filter mixed collection by type

**Requirements:**
- Use OfType to filter numbers
- Use OfType to filter strings

```csharp
// HINT: mixedList.OfType<int>()
// Expected: Separated numbers and strings
```

#### Exercise 8: Sequence Generation
**Task:** Generate and combine sequences

**Requirements:**
- Use Range to generate order IDs
- Zip with existing orders

```csharp
// HINT: Enumerable.Range(100, 10)
// Expected: Generated IDs paired with orders
```

---


## Bonus Challenges
### Lab 2 Bonus: Complete Analysis Pipeline
Create an end-to-end data analysis pipeline:
1. Filter recent orders (last 7 days)
2. Group by category
3. Calculate revenue per category
4. Convert to dictionary
5. Display formatted results

---

## Quick Reference: Common LINQ Operators

| Category | Operators | Purpose |
|----------|-----------|---------|
| **Grouping** | `GroupBy` | Group elements by key |
| **Joining** | `Join`, `GroupJoin` | Combine sequences |
| **Set** | `Distinct`, `Union`, `Intersect`, `Except` | Set operations |
| **Combining** | `Concat`, `Zip` | Combine sequences |
| **Comparison** | `SequenceEqual` | Compare sequences |
| **Aggregation** | `Sum`, `Average`, `Min`, `Max`, `Aggregate` | Calculate values |
| **Conversion** | `ToList`, `ToDictionary`, `Cast`, `OfType` | Convert types |
| **Generation** | `Range`, `Repeat` | Generate sequences |

