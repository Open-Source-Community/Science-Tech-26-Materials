namespace Composite_Pattern;

public class TextView : Component
{
    public string Name { get; set; }

    public TextView()
    {
        this.Name = "TextView";
    }
}