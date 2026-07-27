namespace handsonSes4
{
    internal partial class Program
    {
        static void Main(string[] args)
        {
            #region DATA
            // Existing orders
            var orders = new List<Order>
        {
            new Order { Id = 1, CustomerName = "Mariam", TotalAmount = 150.0m, IsDelivered = true, Items = new List<string>{ "Laptop", "Mouse" } },
            new Order { Id = 2, CustomerName = "Poula", TotalAmount = 45.0m, IsDelivered = false, Items = new List<string>{ "Book" } },
            new Order { Id = 3, CustomerName = "Marwa", TotalAmount = 210.0m, IsDelivered = true, Items = new List<string>{ "Monitor", "Keyboard", "HDMI Cable" } },
            new Order { Id = 4, CustomerName = "Ibrahin", TotalAmount = 80.0m, IsDelivered = false, Items = new List<string>{ "Headphones" } }
        };

            // New data for exercises
            var customers = new List<Customer>
        {
            new Customer { Name = "Mariam", City = "Cairo", IsActive = true },
            new Customer { Name = "Poula", City = "Alexandria", IsActive = true },
            new Customer { Name = "Marwa", City = "Giza", IsActive = false },
            new Customer { Name = "Ibrahin", City = "Cairo", IsActive = true },
            new Customer { Name = "Sara", City = "Alexandria", IsActive = true }
        };

            var newOrders = new List<Order>
        {
            new Order { Id = 5, CustomerName = "Sara", TotalAmount = 300.0m, IsDelivered = true, Items = new List<string>{ "Printer", "Paper", "Ink" } },
            new Order { Id = 6, CustomerName = "Mariam", TotalAmount = 95.0m, IsDelivered = false, Items = new List<string>{ "Tablet" } },
            new Order { Id = 7, CustomerName = "John", TotalAmount = 120.0m, IsDelivered = true, Items = new List<string>{ "Desk" } }
        };
            var products = new List<Product>
        {
            new Product { Name = "Laptop", Price = 899.99m, Category = "Electronics", StockQuantity = 5 },
            new Product { Name = "Mouse", Price = 29.99m, Category = "Electronics", StockQuantity = 20 },
            new Product { Name = "Book", Price = 15.99m, Category = "Books", StockQuantity = 50 },
            new Product { Name = "Monitor", Price = 199.99m, Category = "Electronics", StockQuantity = 3 },
            new Product { Name = "Keyboard", Price = 49.99m, Category = "Electronics", StockQuantity = 8 },
            new Product { Name = "HDMI Cable", Price = 12.99m, Category = "Accessories", StockQuantity = 30 },
            new Product { Name = "Headphones", Price = 59.99m, Category = "Accessories", StockQuantity = 12 }
        };
            #endregion

            #region HANDSON 1
            // ----- YOUR EXERCISES START HERE -----
            Console.WriteLine("=== Hands-On Lab 1: Advanced LINQ Queries ===\n");

            // TODO 1: Group customers by city and count total orders per city



            // TODO 2: Join orders with customers and filter active customers only
            // Create a query that returns all orders from active customers


            // TODO 3: Set Operations - Combine orders with new orders
            // Use Union to combine all orders and handle duplicates


            // TODO 4: Find common items between delivered and undelivered orders


            // TODO 5: Group orders by customer and aggregate
            // Create a grouping that shows each customer's total spending and average order value


            // Bonus TODO: Find customers who ordered but aren't in the customer list
            // Use Except to find mismatched customer names
            #endregion

            #region HANDSON 2
            // TODO 1: Use Zip to pair orders with products by index


            // TODO 2: Use Concat to combine item lists


            // TODO 3: Use SequenceEqual to compare lists


            // TODO 4: Complex aggregation with Aggregate


            // TODO 5: Multiple aggregations at once


            // TODO 6: Convert to Dictionary with custom key


            // TODO 7: Cast and OfType examples

            // Bonus: Reverse and custom ordering

            // ---- Final Challenge: Combine everything ----

            // Your challenge: Create a pipeline that:
            // 1. Filters orders from the last 7 days
            // 2. Groups by category
            // 3. Calculates total revenue per category
            // 4. Converts to a dictionary
            // 5. Formats the output nicely



            #endregion
        }
    }
    
}
