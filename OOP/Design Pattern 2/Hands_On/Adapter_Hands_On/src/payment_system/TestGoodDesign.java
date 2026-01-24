package payment_system;

import payment_system.good_design.PaymentProcessor;
import payment_system.good_design.PaymentProcessorFactory;
import payment_system.good_design.PaymentService;

public class TestGoodDesign {
    static void main() {
        PaymentProcessor paypalProcessor = PaymentProcessorFactory.create("paypal");
        PaymentService service = new PaymentService(paypalProcessor);

        String tx1 = service.chargeCustomer(99.99, "order-1001");
        System.out.println("PayPal tx: " + tx1);
        System.out.println("Refund PayPal: " + service.refundCustomer(tx1));

        System.out.println("\n--- Switch to Stripe ---\n");
        PaymentProcessor stripeProcessor = PaymentProcessorFactory.create("stripe");
        service.setProcessor(stripeProcessor);

        String tx2 = service.chargeCustomer(14.50, "order-1002");
        System.out.println("Stripe tx: " + tx2);
        System.out.println("Refund Stripe: " + service.refundCustomer(tx2));
    }
}
