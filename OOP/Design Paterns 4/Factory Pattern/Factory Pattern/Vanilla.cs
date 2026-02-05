namespace Factory_Pattern;

public class Vanilla : IceCream
{
    public Vanilla()
    {
        this.id = 2;
        this.name = "Vanilla Ice Cream";
        this.price = 3.50;
    }
}