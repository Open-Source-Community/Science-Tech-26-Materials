package order_processing_system;

import java.util.LinkedList;
import java.util.Queue;

public class OrderProcessorQueuingDecorator implements IOrderProcessor{
    private IOrderProcessor _orderProcessor;
    private Queue<Order> _orderQueue;
    public OrderProcessorQueuingDecorator(IOrderProcessor orderProcessor){
        _orderProcessor = orderProcessor;
        _orderQueue = new LinkedList<Order>();
    }
    @Override
    public void process(Order order) {
        _orderQueue.add(order);
        System.out.println("Order has been queued!");
    }

}
