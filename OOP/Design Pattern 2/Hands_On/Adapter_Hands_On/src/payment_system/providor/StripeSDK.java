package payment_system.providor;

public class StripeSDK {
    public static class Charge {
        public String id;
        Charge(String id) {
            this.id = id;
        }
    }
    public Charge createCharge(int cents, String metadata) {
        String id = "STR-" + System.currentTimeMillis();
        System.out.println("[StripeSDK] Created charge: " + (cents/100.0) + " metadata=" + metadata + " id=" + id);
        return new Charge(id);
    }
    public boolean createRefund(String chargeId) {
        System.out.println("[StripeSDK] Refund created for: " + chargeId);
        return true;
    }
}
