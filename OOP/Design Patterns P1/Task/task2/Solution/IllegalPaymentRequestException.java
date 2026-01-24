package designpattern.creational.task2;

public class IllegalPaymentRequestException extends RuntimeException {
    public IllegalPaymentRequestException(String message) {
        super(message);
    }
}
