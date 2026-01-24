package payment_system.providor;

public class PayPalSDK {
    public String sendPaymentInUSD(double dollars, String invoice) {
        String txId = "PP-" + System.currentTimeMillis();
        System.out.println("[LegacyPayPalSDK] Sent $" + dollars + " invoice=" + invoice + " tx=" + txId);
        return txId;
    }

    public boolean refundPayment(String invoiceOrTx) {
        System.out.println("[LegacyPayPalSDK] Refund for: " + invoiceOrTx);
        return true;
    }
}
