package payment_system;

import payment_system.bad_design.PaymentService;

public class TestBadDesign {
    static void main() {
        PaymentService service = new PaymentService();

        // Using PayPal
        String tx1 = service.chargeCustomer("paypal", 99.99, "order-1001");
        System.out.println("PayPal tx: " + tx1);

        // Using Stripe
        String tx2 = service.chargeCustomer("stripe", 14.50, "order-1002");
        System.out.println("Stripe tx: " + tx2);

        // Trying to refund
        System.out.println("Refund PayPal: " + service.refundCustomer("paypal", tx1));
        System.out.println("Refund Stripe: " + service.refundCustomer("stripe", tx2));
    }
}
