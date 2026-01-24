package designpattern.creational.task2.solution2;
import lombok.Builder;
/*
In pom.xml, add the following dependencies:

<dependencies>
        <!-- Source: https://mvnrepository.com/artifact/org.projectlombok/lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.42</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>
 */

import java.util.Map;
import java.util.UUID;

@Builder
public class PaymentRequest {
    public final Double amount;
    public final String currency;
    public final UUID paymentMethodId;
    public final Integer customerId;
    public final String description;
    public final String statementDescriptor;
    public final Map<String, String> metedata;
    public final designpattern.creational.task2.solution1.PaymentRequest.CaptureMethod captureMethod;
    public final Boolean isOffSession;

    public static enum CaptureMethod {
        AUTOMATIC, MANUAL
    }
}

class Main {
    public static void main(String[] args) {
        PaymentRequest request = PaymentRequest.builder()
                .amount(100.0)
                .currency("USD")
                .paymentMethodId(UUID.randomUUID())
                .customerId(1)
                .description("Payment for order 12345")
                .build();
    }
}
