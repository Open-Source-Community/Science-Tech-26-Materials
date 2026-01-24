package payment_system.good_design;

public interface PaymentProcessor {
    String charge(double amount, String orderId);
    boolean refund(String transactionId);
}
