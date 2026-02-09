package org.gs.hands_on3.before;

import java.util.Scanner;
import java.util.UUID;

public class ECommerceApp {
    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        //products (hard coded)
        String[] productNames = {"Laptop", "Phone", "Headphones", "Book"};
        double[] productPrices = {20000, 15000, 400, 200};

        //Cart
        String[] cartProductNames = new String[100];
        double[] cartProductPrices = new double[100];
        int[] cartQuantities = new int[100];
        int cartItemCount = 0;

        System.out.println("=====================================");
        System.out.println("       WELCOME TO E-SHOP");
        System.out.println("=====================================");

        while (true) {
            //Menu display
            System.out.println("\n----- MAIN MENU -----");
            System.out.println("1. View Products");
            System.out.println("2. Add to Cart");
            System.out.println("3. View Cart");
            System.out.println("4. Checkout");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");

            int choice = 0;
            try {
                choice = scanner.nextInt();
            } catch (Exception e) {
                scanner.nextLine();
                System.out.println("Invalid input!");
                continue;
            }

            if (choice == 1) {/// View Products
                System.out.println("\n----- PRODUCTS -----");
                for (int i = 0; i < productNames.length; i++) {
                    System.out.println((i + 1) + ". " + productNames[i] + " - " + productPrices[i] + " EGP");
                }
                Thread.sleep(2000);

            }
            else if (choice == 2) {/// Add to Cart
                System.out.println("\n----- PRODUCTS -----");
                for (int i = 0; i < productNames.length; i++) {
                    System.out.println((i + 1) + ". " + productNames[i] + " - " + productPrices[i] + " EGP");
                }

                System.out.print("Enter product number: ");
                int productNum = scanner.nextInt();

                if (productNum < 1 || productNum > productNames.length) {
                    System.out.println("Invalid product!");
                    continue;
                }

                System.out.print("Enter quantity: ");
                int quantity = scanner.nextInt();

                if (quantity < 1) {
                    System.out.println("Invalid quantity!");
                    Thread.sleep(1000);
                    continue;
                }

                //Check if product already in cart
                boolean found = false;
                for (int i = 0; i < cartItemCount; i++) {
                    if (cartProductNames[i].equals(productNames[productNum - 1])) {
                        cartQuantities[i] += quantity;
                        found = true;
                        break;
                    }
                }

                //Add it to cart if not founded
                if (!found) {
                    cartProductNames[cartItemCount] = productNames[productNum - 1];
                    cartProductPrices[cartItemCount] = productPrices[productNum - 1];
                    cartQuantities[cartItemCount] = quantity;
                    cartItemCount++;
                }

                System.out.println("Added " + quantity + "x " + productNames[productNum - 1] + " to cart!");
                Thread.sleep(2000);
            }
            else if (choice == 3) {
                // View Cart
                System.out.println("\n----- YOUR CART -----");

                if (cartItemCount == 0) {
                    System.out.println("Cart is empty!");
                } else {
                    double total = 0;
                    for (int i = 0; i < cartItemCount; i++) {
                        double itemTotal = cartProductPrices[i] * cartQuantities[i];
                        System.out.println(cartProductNames[i] + " x" + cartQuantities[i] + " = " + itemTotal + " EGP");
                        total += itemTotal;
                    }
                    System.out.println("---------------------");
                    System.out.println("Total: " + total + " EGP");
                }

                Thread.sleep(2000);
            }
            else if (choice == 4) {
                // Checkout Logic
                if (cartItemCount == 0) {
                    System.out.println("Cart is empty!");
                    continue;
                }

                /// cart display logic again and again
                System.out.println("\n----- YOUR CART -----");
                double subtotal = 0;
                for (int i = 0; i < cartItemCount; i++) {
                    double itemTotal = cartProductPrices[i] * cartQuantities[i];
                    System.out.println(cartProductNames[i] + " x" + cartQuantities[i] + " = " + itemTotal + " EGP");
                    subtotal += itemTotal;
                }
                System.out.println("---------------------");
                System.out.println("Subtotal: " + subtotal + " EGP");

                //Discount logic (We can add another methods also)
                System.out.println("\n----- DISCOUNT -----");
                System.out.println("1. SAVE10 (10% off)");
                System.out.println("2. FLAT50 ($50 off)");
                System.out.println("3. No discount");
                System.out.print("Select discount: ");
                int discountChoice = scanner.nextInt();

                double finalTotal = subtotal;
                String discountApplied = "None";

                if (discountChoice == 1) {
                    finalTotal = subtotal * 0.9;
                    discountApplied = "SAVE10 (10% off)";
                    System.out.println("Applied: 10% discount");
                } else if (discountChoice == 2) {
                    finalTotal = subtotal - 50;
                    if (finalTotal < 0) finalTotal = 0;
                    discountApplied = "FLAT50 ($50 off)";
                    System.out.println("Applied: $50 discount");
                } else {
                    System.out.println("No discount applied");
                }

                System.out.println("Final Total: " + finalTotal + " EGP");

                //Payment logic (we can add another payment method also)
                System.out.println("\n----- PAYMENT -----");
                System.out.println("1. Credit Card");
                System.out.println("2. PayPal");
                System.out.print("Select payment: ");
                int paymentChoice = scanner.nextInt();
                scanner.nextLine();

                String paymentMethod = "";

                //payment successful always for now
                if (paymentChoice == 1) {
                    System.out.print("Enter card number: ");
                    String cardNumber = scanner.nextLine();
                    System.out.print("Enter CVV: ");
                    String cvv = scanner.nextLine();

                    //Payment processing
                    System.out.println("Processing credit card " + cardNumber + "...");
                    System.out.println("Payment of " + finalTotal + " EGP successful!");
                    paymentMethod = "Credit Card";

                } else if (paymentChoice == 2) {
                    System.out.print("Enter PayPal email: ");
                    String email = scanner.nextLine();

                    System.out.println("Processing PayPal payment for " + email + "...");
                    System.out.println("Payment of " + finalTotal + " EGP successful!");
                    paymentMethod = "PayPal";

                } else {
                    System.out.println("Invalid payment method!");
                    continue;
                }

                //Generate order ID
                String orderId = UUID.randomUUID().toString();

                //Notification logic
                System.out.println("\n----- NOTIFICATIONS -----");
                System.out.println("Sending Email to customer@email.com...");
                System.out.println("  Order " + orderId + " confirmed! Total: " + finalTotal + " EGP");
                System.out.println("Sending SMS to +1234567890...");
                System.out.println("  Order " + orderId + " confirmed!");

                //Order summary
                System.out.println("\n===== ORDER CONFIRMED =====");
                System.out.println("Order ID: " + orderId);
                System.out.println("Items:");
                for (int i = 0; i < cartItemCount; i++) {
                    System.out.println("  - " + cartProductNames[i] + " x" + cartQuantities[i]);
                }
                System.out.println("Discount: " + discountApplied);
                System.out.println("Payment: " + paymentMethod);
                System.out.println("Total: " + finalTotal + "EGP");
                System.out.println("Status: Confirmed");
                System.out.println("===========================");

                //Clear cart
                cartItemCount = 0;

                Thread.sleep(5000);
            }
            else if (choice == 5) {
                System.out.println("\nThank you for shopping! Goodbye!");
                Thread.sleep(3000);
            }
            else {
                System.out.println("Invalid choice!");
                Thread.sleep(1000);
                break;
            }
        }

        scanner.close();
    }
}