package org.designprinciple.part1.handson;



class CheckoutServiceBefore {

    public void processOrder(double amount, String paymentMethod){
        // Some Order Logic
        System.out.println("Order confirmed...");

        boolean isPaid = false;

        if (paymentMethod.equals("CREDIT_CARD")) {
            // Credit card logic
            isPaid = true;
            System.out.println("Processing credit card payment: " + amount);
        } else if (paymentMethod.equals("PAYPAL")) {
            // PayPal logic
            isPaid = true;
            System.out.println("Processing PayPal payment: " + amount);
        } else if (paymentMethod.equals("FawryPay")) {
            // FawryPay logic
            isPaid = true;
            System.out.println("Processing FawryPay payment: " + amount);
        }

        if(isPaid){
            //make some logic
           System.out.println("Payment Successful");
        }else{
            //make some logic
            System.out.println("Payment Failed");
        }

        //change statues in db
    }
}


public class BeforeEx {
    public static void main(String[] args) {
        String input = "CREDIT_CARD";
        CheckoutServiceBefore checkoutService = new CheckoutServiceBefore();
        checkoutService.processOrder(100, input);
    }
}
