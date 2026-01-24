package payment_system.good_design;

import payment_system.providor.PayPalSDK;

public class PayPalAdapter implements PaymentProcessor {
    private final PayPalSDK payPalSdk;
    public PayPalAdapter(PayPalSDK payPalSdk) {
        this.payPalSdk = payPalSdk;
    }
    @Override
    public String charge(double amount, String orderId) {
        try {
            return payPalSdk.sendPaymentInUSD(amount, orderId);
        } catch (Exception e) {
            System.err.println("[PayPalAdapter] charge failed: " + e.getMessage());
            return null;
        }
    }
    @Override
    public boolean refund(String transactionId) {
        try {
            return payPalSdk.refundPayment(transactionId);
        } catch (Exception e) {
            System.err.println("[PayPalAdapter] refund failed: " + e.getMessage());
            return false;
        }
    }

}
