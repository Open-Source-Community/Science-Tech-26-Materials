package designpattern.creational.task2;

import java.util.Currency;
import java.util.Map;
import java.util.UUID;


public class PaymentRequest {
    public final Double amount;
    public final String currency;
    public final UUID paymentMethodId;
    public final Integer customerId;
    public final String description;
    public final String statementDescriptor;
    public final Map<String, String> metedata;
    public final CaptureMethod captureMethod;
    public final Boolean isOffSession;

    public static enum CaptureMethod{
        AUTOMATIC,
        MANUAL
    }

    /// We restrict the creation process to the Builder only
    private PaymentRequest(Builder builder) {
        this.amount = builder.amount;
        this.currency = builder.currency;
        this.paymentMethodId = builder.paymentMethodId;
        this.customerId = builder.customerId;
        this.description = builder.description;
        this.statementDescriptor = builder.statementDescriptor;
        this.metedata = builder.metedata;
        this.captureMethod = builder.captureMethod;
        this.isOffSession = builder.isOffSession;
    }

    public static class Builder {
        private Double amount;
        private String currency;
        private UUID paymentMethodId;
        private Integer customerId;
        private String description;
        private String statementDescriptor;
        private Map<String, String> metedata;
        private CaptureMethod captureMethod = CaptureMethod.AUTOMATIC;
        private Boolean isOffSession = false;

        public Builder setAmount(Double amount) {
            this.amount = amount;
            return this;
        }

        public Builder setCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder setPaymentMethodId(UUID paymentMethodId) {
            this.paymentMethodId = paymentMethodId;
            return this;
        }

        public Builder setCustomerId(Integer customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setStatementDescriptor(String statementDescriptor) {
            this.statementDescriptor = statementDescriptor;
            return this;
        }

        public Builder setMetedata(Map<String, String> metedata) {
            this.metedata = metedata;
            return this;
        }

        public Builder setCaptureMethod(CaptureMethod captureMethod) {
            this.captureMethod = captureMethod;
            return this;
        }

        public Builder setIsOffSession(Boolean isOffSession) {
            this.isOffSession = isOffSession;
            return this;
        }

        private static boolean isValidCurrency(String currency) {
            return true;
        }

        public PaymentRequest build() {
            /// We must assert that amount and currency are set and amount shouldn't be negative and also currency should be valid
            if(amount == null || currency == null || amount < 0 || !isValidCurrency(currency)) {
                throw new IllegalPaymentRequestException("Amount and currency must be set");
            }

            return new PaymentRequest(this);
        }
    }


}
