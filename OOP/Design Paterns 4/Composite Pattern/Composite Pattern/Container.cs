namespace Composite_Pattern;

public class Container : Component
{
    public string Name { get; set; }
    
    public List<Component> Components { get; set; }
    
    public Container()
    {
        this.Name = "Container";
    }
}