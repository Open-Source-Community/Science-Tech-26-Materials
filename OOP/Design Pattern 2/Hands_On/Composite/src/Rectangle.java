public class Rectangle implements  Graphic {
    public String id;
    public int x, y, width, height;
    Rectangle(String id, int x, int y, int w, int h) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }
    @Override
    public void draw() {
        System.out.println("[Rectangle] " + id + " at ("+x+","+y+") w=" + width + " h=" + height);
    }

    @Override
    public void move(int dx, int dy) {
        x += dx; y += dy;
        System.out.println("[Rectangle] " + id + " moved to ("+x+","+y+")");
    }

    @Override public String getId() { return id; }
}
