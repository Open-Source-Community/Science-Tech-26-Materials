namespace Factory_Pattern;

public class IceCreamFactory
{
    public static IceCream CreateIceCream(int id)
    {
        if (id == 1)
            return new Chocolate();
        else if (id == 2)
            return new Vanilla();
        else if (id == 3)
            return new BlueBerry();
        return null;
    }
}