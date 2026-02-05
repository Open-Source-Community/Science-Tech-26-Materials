namespace Factory_Pattern;

public class Program
{
    public static void Main(string[] args)
    {
        IceCream iceCream = IceCreamFactory.CreateIceCream(1);
        Console.WriteLine(iceCream.name);
    }
}