package payment_system.good_design;

import payment_system.providor.PayPalSDK;
import payment_system.providor.StripeSDK;

public class PaymentProcessorFactory {
    public static PaymentProcessor create(String provider) {
        if ("paypal".equalsIgnoreCase(provider)) {
            return new PayPalAdapter(new PayPalSDK());
        } else if ("stripe".equalsIgnoreCase(provider)) {
            return new StripeAdapter(new StripeSDK());
        } else {
            throw new IllegalArgumentException("Unknown provider: " + provider);
        }
    }
}
