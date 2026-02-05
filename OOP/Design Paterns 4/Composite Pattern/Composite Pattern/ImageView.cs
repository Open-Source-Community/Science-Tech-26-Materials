namespace Composite_Pattern;

public class ImageView : Component
{
    public string Name { get; set; }

    public ImageView()
    {
        this.Name = "ImageView";
    }
}