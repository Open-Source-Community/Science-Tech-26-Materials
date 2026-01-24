package payment_system.bad_design;

import payment_system.providor.PayPalSDK;
import payment_system.providor.StripeSDK;

public class PaymentService {
    private final PayPalSDK payPal = new PayPalSDK();
    private final StripeSDK stripe = new StripeSDK();

    public String chargeCustomer(String provider, double amount, String orderId) {
        if ("paypal".equalsIgnoreCase(provider)) {
            return payPal.sendPaymentInUSD(amount, orderId);
        } else if ("stripe".equalsIgnoreCase(provider)) {
            int cents = (int) Math.round(amount * 100);
            StripeSDK.Charge ch = stripe.createCharge(cents, "order:" + orderId);
            return ch != null ? ch.id : null;
        } else {
            throw new IllegalArgumentException("Unknown provider: " + provider);
        }
    }
    public boolean refundCustomer(String provider, String transactionId) {
        if ("paypal".equalsIgnoreCase(provider)) {
            return payPal.refundPayment(transactionId);
        } else if ("stripe".equalsIgnoreCase(provider)) {
            return stripe.createRefund(transactionId);
        } else {
            throw new IllegalArgumentException("Unknown provider: " + provider);
        }
    }
}
