package order_processing_system;

public class OrderLine {
    private int _itemId;
    private double _quantity;
    private double _price;
    public OrderLine(int itemId, double quantity, double price) {
        _itemId = itemId;
        _quantity = quantity;
        _price = price;
    }
    public int getItemId() {
        return _itemId;
    }
    public double getQuantity() {
        return _quantity;
    }
    public double getPrice() {
        return _price;
    }
    public void setItemId(int itemId) {
        _itemId = itemId;
    }
    public void setQuantity(double quantity) {
        _quantity = quantity;
    }
    public void setPrice(double price) {
        _price = price;
    }
}
