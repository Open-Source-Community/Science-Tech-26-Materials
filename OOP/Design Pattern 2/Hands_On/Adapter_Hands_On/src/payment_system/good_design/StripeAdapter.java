package payment_system.good_design;

import payment_system.providor.StripeSDK;

public class StripeAdapter implements PaymentProcessor{
    private final StripeSDK stripe;

    public StripeAdapter(StripeSDK stripe) {
        this.stripe = stripe;
    }

    @Override
    public String charge(double amount, String orderId) {
        try {
            int cents = (int) Math.round(amount * 100); // conversion moved here
            StripeSDK.Charge ch = stripe.createCharge(cents, "order:" + orderId);
            return ch != null ? ch.id : null;
        } catch (Exception e) {
            System.err.println("[StripeAdapter] charge failed: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean refund(String transactionId) {
        try {
            return stripe.createRefund(transactionId);
        } catch (Exception e) {
            System.err.println("[StripeAdapter] refund failed: " + e.getMessage());
            return false;
        }
    }
}
