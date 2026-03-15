package payment;

import enums.PaymentStatus;
import enums.PaymentType;

/**
 * Represents a payment made by a vehicle at the exit gate.
 *
 * <p>A payment is created after the parking fee is computed.
 * It holds the amount, the mode of payment, and the current status.</p>
 *
 * <p>Payment has its own lifecycle:</p>
 * <pre>
 *   PENDING → process() → COMPLETED
 *                       → FAILED  (if payment gateway rejects)
 * </pre>
 *
 * <p>Why is Payment a separate class and not just a number?</p>
 * <ul>
 *   <li>Payment has its own state — PENDING, COMPLETED, FAILED</li>
 *   <li>Payment has a type — cash, card, UPI</li>
 *   <li>Tomorrow you can add receipt generation, refund logic,
 *       or payment gateway integration — all inside this class
 *       without touching ExitGate</li>
 * </ul>
 *
 * <pre>
 * Real world flow:
 *   Fee computed → Payment created (PENDING)
 *               → process() called
 *               → Payment status becomes COMPLETED
 *               → Spot is released
 * </pre>
 */
public class Payment {

    /**
     * The parking fee to be paid.
     * Computed by PricingStrategy and fixed at creation.
     * Never changes after the Payment object is created.
     */
    private final double amount;

    /**
     * The mode of payment chosen by the vehicle owner.
     * Fixed at creation — cannot be changed after payment is initiated.
     */
    private final PaymentType paymentType;

    /**
     * The current status of this payment.
     * Starts as PENDING and moves to COMPLETED or FAILED
     * after process() is called.
     */
    private PaymentStatus status;

    /**
     * Creates a new payment with the given amount and payment type.
     * Status starts as PENDING until process() is called.
     *
     * @param amount      the fee to be paid
     * @param paymentType the mode of payment (CASH, CREDIT_CARD, UPI)
     */
    public Payment(double amount, PaymentType paymentType) {
        this.amount      = amount;
        this.paymentType = paymentType;
        this.status      = PaymentStatus.PENDING;
    }

    /**
     * Processes this payment.
     *
     * <p>In a real system, this method would call an external
     * payment gateway (Razorpay, PayTM, Stripe) and handle
     * success or failure responses.</p>
     *
     * <p>For now, it simulates a successful payment by setting
     * status to COMPLETED.</p>
     *
     * @return true if payment succeeded, false if it failed
     */
    public boolean process() {
        // TODO: integrate real payment gateway here
        this.status = PaymentStatus.COMPLETED;
        return true;
    }

    /**
     * Returns the amount to be paid.
     *
     * @return fee amount as double
     */
    public double getAmount() { return amount; }

    /**
     * Returns the mode of payment.
     *
     * @return PaymentType enum value
     */
    public PaymentType getPaymentType() { return paymentType; }

    /**
     * Returns the current status of this payment.
     *
     * @return PaymentStatus — PENDING, COMPLETED, or FAILED
     */
    public PaymentStatus getStatus() { return status; }
}