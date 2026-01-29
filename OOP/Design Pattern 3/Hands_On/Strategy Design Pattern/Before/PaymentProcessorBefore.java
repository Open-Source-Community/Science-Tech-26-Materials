package Strategy;

public class PaymentProcessorBefore {

    private PaymentType paymentType;

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public void processPayment(double amount) {

        if (paymentType == PaymentType.CREDIT_CARD) {
            System.out.println("Processing credit card payment of amount " + amount);

        } else if (paymentType == PaymentType.DEBIT_CARD) {
            System.out.println("Processing debit card payment of amount " + amount);

        } else if (paymentType == PaymentType.PAYPAL) {
            System.out.println("Processing PayPal payment of amount " + amount);

        } else {
            throw new IllegalArgumentException("Invalid payment type");
        }
    }



    public static void main(String[] args) {

        PaymentProcessorBefore processor = new PaymentProcessorBefore();

        processor.setPaymentType(PaymentType.CREDIT_CARD);
        processor.processPayment(100.0);

        processor.setPaymentType(PaymentType.DEBIT_CARD);
        processor.processPayment(200.0);

        processor.setPaymentType(PaymentType.PAYPAL);
        processor.processPayment(300.0);
    }




}

 enum PaymentType {
    CREDIT_CARD,
    DEBIT_CARD,
    PAYPAL
}
