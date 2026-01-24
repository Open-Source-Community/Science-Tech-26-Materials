public interface Graphic {
    void draw();
    void move(int dx, int dy);
    String getId();

    default void add(Graphic g) {
        throw new UnsupportedOperationException("add not supported");
    }
    default void remove(String id) {
        throw new UnsupportedOperationException("remove not supported");
    }
    default Graphic getChild(String id) {
        return null;
    }

}
