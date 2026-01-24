public class Circle implements Graphic{
    public String id;
    public int x, y, radius;
    public Circle(String id, int x, int y, int radius) { this.id = id; this.x = x; this.y = y; this.radius = radius; }
    public void draw() {
        System.out.println("[Circle] " + id + " at ("+x+","+y+") r=" + radius);
    }
    public void move(int dx, int dy) {
        x += dx; y += dy;
        System.out.println("[Circle] " + id + " moved to ("+x+","+y+")");
    }
    @Override public String getId() { return id; }
}
