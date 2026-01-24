package order_processing_system;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private List<OrderLine> _lines;
    public Order(List<OrderLine> lines) {
        this._lines = lines;
    }
    public Order() {
        this._lines = new ArrayList<>();
    }
    public List<OrderLine> getLines() {
        return _lines;
    }
    public void addOrderLine(OrderLine orderLine) {
        _lines.add(orderLine);
    }

}
