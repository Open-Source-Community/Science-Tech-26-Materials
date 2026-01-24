package order_processing_system;

import java.time.Duration;
import java.time.Instant;

public class OrderProcessorProfilingDecorator implements IOrderProcessor{
    private IOrderProcessor _orderProcessor;
    public OrderProcessorProfilingDecorator(IOrderProcessor orderProcessor) {
        _orderProcessor = orderProcessor;
    }

    @Override
    public void process(Order order) {
        Instant start = Instant.now();
        _orderProcessor.process(order);
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        System.out.println("Order Processor Process Duration: " + duration.toMillis());
    }
}
