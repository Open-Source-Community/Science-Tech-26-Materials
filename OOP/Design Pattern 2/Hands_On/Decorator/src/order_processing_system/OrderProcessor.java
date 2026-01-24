package order_processing_system;


import java.util.concurrent.ThreadLocalRandom;

public class OrderProcessor implements IOrderProcessor {
    @Override
    public void process(Order order) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 4000));
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Order has been Processed" );
    }
}
