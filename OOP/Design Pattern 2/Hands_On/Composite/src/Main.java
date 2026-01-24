
void main() {
    // Build a drawing: some shapes and nested groups
    Graphic circle1 = new Circle("c1", 10, 10, 5);
    Graphic rect1   = new Rectangle("r1", 0, 0, 20, 10);

    Group rootGroup = new Group("root");
    rootGroup.add(circle1);
    rootGroup.add(rect1);

    // nested group
    Group subgroup = new Group("sub");
    subgroup.add(new Circle("c2", 5, 5, 3));
    subgroup.add(new Rectangle("r2", 2, 2, 4, 6));
    rootGroup.add(subgroup);

    // client treats rootGroup and individual shapes the same
    System.out.println("== Draw everything ==");
    rootGroup.draw();

    System.out.println("\n== Move entire group by (+10, +10) ==");
    rootGroup.move(10, 10);
    rootGroup.draw();

    System.out.println("\n== Remove a shape from subgroup and redraw ==");
    subgroup.remove("c2");
    rootGroup.draw();
}
