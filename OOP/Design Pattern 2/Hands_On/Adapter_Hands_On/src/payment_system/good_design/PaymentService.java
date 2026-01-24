package payment_system.good_design;

public class PaymentService {
    private PaymentProcessor processor;
    public PaymentService(PaymentProcessor processor) {
        this.processor = processor;
    }
    public void setProcessor(PaymentProcessor processor) {
        this.processor = processor;
    }
    public String chargeCustomer(double amount, String orderId) {
        return processor.charge(amount, orderId);
    }
    public boolean refundCustomer(String transactionId) {
        return processor.refund(transactionId);
    }
}
