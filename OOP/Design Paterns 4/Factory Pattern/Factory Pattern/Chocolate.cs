namespace Factory_Pattern;

public class Chocolate : IceCream
{
    public Chocolate()
    {
        this.id = 1;
        this.name = "Chocolate";
        this.price = 2.00;
    }
}