
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


        }
    }
}