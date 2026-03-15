package entity.gate;

import entity.ParkingLot;
import entity.ParkingTicket;
import enums.PaymentType;
import enums.TicketStatus;
import payment.Payment;
import strategy.pricing.PricingStrategy;

/**
 * Represents a physical exit gate of the parking lot.
 *
 * <p>This is the end point of the parking flow. When a vehicle
 * is ready to leave, the exit gate is responsible for:</p>
 * <ol>
 *   <li>Validating the ticket — ensures it has not already been paid</li>
 *   <li>Recording the exit time on the ticket</li>
 *   <li>Computing the parking fee using the injected pricing strategy</li>
 *   <li>Processing the payment</li>
 *   <li>Releasing the spot — makes it free for the next vehicle</li>
 * </ol>
 *
 * <p>The parking lot can have multiple exit gates. Each gate can have
 * a different pricing strategy — one gate charges hourly, another
 * charges a flat rate. This follows the <b>Strategy Pattern</b>.</p>
 *
 * <pre>
 * Real world flow:
 *   Vehicle hands ticket at exit gate
 *       → validate ticket        (not already paid)
 *       → record exit time       (ticket.markPaid())
 *       → compute fee            (delegated to PricingStrategy)
 *       → process payment        (cash / card / UPI)
 *       → release spot           (free for next vehicle)
 * </pre>
 */
public class ExitGate {

    /** Uniquely identifies this gate. Example: "EXIT-1", "EXIT-2" */
    private final String gateId;

    /**
     * The pricing strategy used to compute the parking fee.
     * Injected at construction time — different gates can have
     * different pricing strategies.
     *
     * <pre>
     * EXIT-1 → HourlyPricing  (₹50 per hour)
     * EXIT-2 → FixedPricing   (₹30 flat rate)
     * </pre>
     */
    private final PricingStrategy pricingStrategy;

    /**
     * Reference to the single parking lot instance.
     * Used to access spot and floor information if needed.
     */
    private final ParkingLot lot;

    /**
     * Creates an exit gate with the given id and pricing strategy.
     * Automatically connects to the ParkingLot singleton.
     *
     * @param gateId          unique identifier for this gate
     * @param pricingStrategy the strategy to use for fee computation
     */
    public ExitGate(String gateId, PricingStrategy pricingStrategy) {
        this.gateId          = gateId;
        this.pricingStrategy = pricingStrategy;
        this.lot             = ParkingLot.getInstance();
    }

    /**
     * Handles the exit of a vehicle from the parking lot.
     *
     * <p>Steps performed in order:</p>
     * <ol>
     *   <li>Validate ticket — return null if already paid</li>
     *   <li>Mark ticket as paid — records exit time internally</li>
     *   <li>Compute fee — delegated to the injected PricingStrategy</li>
     *   <li>Process payment — cash, card, or UPI</li>
     *   <li>Release spot — marks it free for the next vehicle</li>
     * </ol>
     *
     * <p>Why is spot released AFTER payment?</p>
     * <p>If the spot were released before payment, the vehicle could
     * drive away without paying. Spot is only freed once payment
     * is confirmed.</p>
     *
     * @param ticket      the ticket issued to this vehicle at entry
     * @param paymentType the mode of payment (CASH, CREDIT_CARD, UPI)
     * @return Payment object if successful, null if ticket already paid
     */
    public Payment exit(ParkingTicket ticket, PaymentType paymentType) {

        // Step 1: validate — reject already paid tickets
        if (ticket.getStatus() == TicketStatus.PAID) {
            System.out.println("  ✘ Ticket already paid");
            return null;
        }

        // Step 2: record exit time on the ticket
        ticket.markPaid();

        // Step 3: compute fee — ExitGate does not know HOW, just asks
        double fee = pricingStrategy.compute(ticket);
        System.out.printf("  Fee: ₹%.2f  (strategy: %s)%n",
                fee, pricingStrategy.getClass().getSimpleName());

        // Step 4: process payment against the computed fee
        Payment payment = new Payment(fee, paymentType);
        boolean success = payment.process();

        // Step 5: release spot only after payment is confirmed
        ticket.getSpot().release();
        System.out.printf("  ✔ Spot %s is now free%n",
                ticket.getSpot().getSpotId());

        return payment;
    }

    /**
     * Returns the unique id of this exit gate.
     *
     * @return gateId string
     */
    public String getGateId() { return gateId; }
}