package Strategy;

 interface PaymentStrategy {
    void processPayment(double amount);
}

public class PaymentProcessorAfter {

    private PaymentStrategy paymentStrategy;

    public PaymentProcessorAfter(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void processPayment(double amount) {
        paymentStrategy.processPayment(amount);
    }










    public static void main(String[] args) {

        PaymentProcessorAfter creditProcessor =
                new PaymentProcessorAfter(new CreditCardPaymentStrategy());
        creditProcessor.processPayment(100.0);

        PaymentProcessorAfter debitProcessor =
                new PaymentProcessorAfter(new DebitCardPaymentStrategy());
        debitProcessor.processPayment(200.0);

        PaymentProcessorAfter paypalProcessor =
                new PaymentProcessorAfter(new PaypalPaymentStrategy());
        paypalProcessor.processPayment(300.0);
    }

}

 class CreditCardPaymentStrategy implements PaymentStrategy {
    public void processPayment(double amount) {
        System.out.println("Processing credit card payment of amount " + amount);
    }
}

 class DebitCardPaymentStrategy implements PaymentStrategy {
    public void processPayment(double amount) {
        System.out.println("Processing debit card payment of amount " + amount);
    }
}

 class PaypalPaymentStrategy implements PaymentStrategy {
    public void processPayment(double amount) {
        System.out.println("Processing PayPal payment of amount " + amount);
    }
}
