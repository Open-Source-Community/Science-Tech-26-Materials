import java.util.ArrayList;
import java.util.List;

public class Group implements Graphic{
    private final String id;
    private final List<Graphic> children = new ArrayList<>();

    Group(String id) { this.id = id; }

    @Override
    public void draw() {
        System.out.println("[Group] Drawing group " + id + " with " + children.size() + " children");
        for (Graphic g : children) {
            g.draw();
        }
    }
    @Override
    public void move(int dx, int dy) {
        System.out.println("[Group] Moving group " + id + " by ("+dx+","+dy+")");
        for (Graphic g : children) {
            g.move(dx, dy);
        }
    }
    @Override
    public Graphic getChild(String id) {
        for (Graphic g : children) {
            if (id.equals(g.getId())) return g;
            Graphic nested = g.getChild(id);
            if (nested != null) return nested;
        }
        return null;
    }

    @Override public String getId() { return id; }
    @Override
    public void add(Graphic g) { children.add(g); }

    @Override
    public void remove(String id) {
        for(Graphic g : children) {
            if (id.equals(g.getId())) {
                children.remove(g);
                System.out.println("[Group] Removed " + id + " from group " + this.id);
                return;
            }
        }
    }
}
