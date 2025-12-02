# Task: Design a Flexible Payment Request Class

In payment processing systems like Stripe, creating a payment request involves specifying many different details. Your task is to design and implement a `PaymentRequest` class that can handle this complexity in a clean and robust way.

## Scenario

You are building a payment gateway. A `PaymentRequest` object needs to be constructed every time a charge is attempted. These requests have a few mandatory fields but a large number of optional fields that provide additional context for the transaction.

### `PaymentRequest` Fields:

**Required:**
*   `amount`: The transaction amount (e.g., `BigDecimal`).
*   `currency`: The three-letter ISO currency code (e.g., "USD").

**Optional:**
*   `paymentMethodId`: The ID of the payment method to be charged.
*   `customerId`: The ID of the customer making the payment.
*   `description`: An arbitrary string attached to the object.
*   `statementDescriptor`: A short string that appears on the customer's bank statement.
*   `metadata`: A map of custom key-value pairs to store additional information.
*   `captureMethod`: An enum that can be either `AUTOMATIC` (charge immediately) or `MANUAL` (authorize now, capture later).
*   `isOffSession`: A boolean indicating if the payment is being made by the customer in a live session.

## Core Requirements

1.  **Immutability:** Once a `PaymentRequest` object is created, its state must not be changeable. All fields should be final. This is critical for creating reliable and thread-safe financial transaction objects.

2.  **Validity:** It must be impossible to create a `PaymentRequest` object that is in an invalid state (e.g., one that is missing the required `amount` or `currency`).

3.  **Clarity and Readability:** The process of creating a new `PaymentRequest` instance in your client code should be easy to read and understand. It should be immediately clear which parameters are being set for any given request.

## Your Task

1.  Design and implement the `PaymentRequest` class, ensuring it meets all the requirements listed above.
2.  Create any necessary supporting types, such as the `CaptureMethod` enum.
3.  Create a `Main` class to demonstrate how to construct several `PaymentRequest` objects with different combinations of optional parameters. For example:
    *   A simple request with only the required fields.
    *   A request for a returning customer that includes a `customerId` and `paymentMethodId`.
    *   A complex request that includes metadata, a statement descriptor, and is marked for manual capture.
