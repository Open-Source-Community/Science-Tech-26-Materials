
using System.Linq;
namespace Session4
{
    internal class Program
    {
        static void Main(string[] args)
        {
            var orders = new List<Order>
                {
                    new Order { Id = 1, CustomerName = "Mariam", TotalAmount = 150.0m, IsDelivered = true,  Items = new List<string>{ "Laptop", "Mouse" } },
                    new Order { Id = 2, CustomerName = "Poula",  TotalAmount = 45.0m,  IsDelivered = false, Items = new List<string>{ "Book" } },
                    new Order { Id = 3, CustomerName = "Marwa",   TotalAmount = 210.0m, IsDelivered = true,  Items = new List<string>{ "Monitor", "Keyboard", "HDMI Cable" } },
                    new Order { Id = 4, CustomerName = "Ibrahin",   TotalAmount = 80.0m,  IsDelivered = false, Items = new List<string>{ "Headphones" } }
                };

            #region eager
            Console.WriteLine("\n--- Execution Finished ---");
            var step1 = orders
                .Where(o =>
                {
                    Console.WriteLine($"[Filter 1 Check] Order #{o.Id} (Amount: ${o.TotalAmount})");
                    return o.TotalAmount >= 50;
                })
                .ToList();

            
            //Console.WriteLine("--- Filter 1 Complete. Moving to Filter 2 ---");

            //List<Order> step2 = step1
            //    .Where(o =>
            //    {
            //        Console.WriteLine($"[Filter 2 Check] Order #{o.Id} (Delivered: {o.IsDelivered})");
            //        return o.IsDelivered;
            //    })
            //    .ToList();

            #endregion
            #region Deffered
            var query = orders
                .Where(o =>
                {
                    Console.WriteLine($"[Filter 1 Check] Order #{o.Id} (Amount: ${o.TotalAmount})");
                    return o.TotalAmount >= 50;
                })
                .Where(o =>
                {
                    Console.WriteLine($"  └─> [Filter 2 Check] Order #{o.Id} (Delivered: {o.IsDelivered})");
                    return o.IsDelivered;
                });

            //Console.WriteLine("--- Query defined. Nothing executed yet! ---");

            //Console.WriteLine("\n--- Starting .ToList() ---");
            //List<Order> results = query.ToList();


            #endregion

            #region Select
            var nestedResult = orders.Select(o => o.Items);

            foreach (var itemList in nestedResult)
            {

                foreach (string item in itemList)
                {
                    Console.WriteLine(item);
                }
            }
            #endregion

            #region select many

            var orderItemPairs = orders.SelectMany(
                    order => order.Items
                //(order, item) => new { order.CustomerName, item }   
                );

            foreach (var x in orderItemPairs)
            {
                Console.WriteLine($"{x}");
            }

            #endregion


            #region handson 1
            // 1 & 2. Deferred query setup
            //var pendingOrdersQuery = orders
            //    .Where(o => !o.IsDelivered)
            //    .Select(o => $"Order #{o.Id} for {o.CustomerName}");

            //// 3. Modifying source AFTER query declaration
            //orders.Add(new Order { Id = 5, CustomerName = "Kareem", TotalAmount = 120.0m, IsDelivered = false, Items = new List<string> { "Webcam" } });

            //// Enumeration triggers execution — Kareem is included!
            //foreach (var summary in pendingOrdersQuery)
            //{
            //    Console.WriteLine(summary);
            //}
            #endregion

            #region handson 2
            var items = orders.SelectMany(or => or.Items);

            var topOr = orders.Where(o => o.IsDelivered == true).
                            OrderByDescending(o => o.TotalAmount)
                            .FirstOrDefault();
            #endregion

        }
    }
}