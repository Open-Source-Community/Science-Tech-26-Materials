import order_processing_system.*;


void main() {
    var order = new Order();
    order.addOrderLine(new OrderLine(1,5,1000));
    order.addOrderLine(new OrderLine(2,5,1000));
    order.addOrderLine(new OrderLine(3,5,1000));

    IOrderProcessor processor = new OrderProcessor();
    processor = new OrderProcessorProfilingDecorator(processor);
    processor.process(order);
    processor = new OrderProcessorQueuingDecorator(processor);
    processor.process(order);
}

