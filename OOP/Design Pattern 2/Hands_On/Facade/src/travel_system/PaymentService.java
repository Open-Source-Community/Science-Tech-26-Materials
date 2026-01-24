package travel_system;

public class PaymentService {
    public String charge(String customerId, double amount) {
        System.out.println("[Payment] Charging " + customerId + " amount=" + amount);
        return "TX-" + System.currentTimeMillis();
    }
    public void refund(String txId) {
        System.out.println("[Payment] Refunding tx " + txId);
    }
}
