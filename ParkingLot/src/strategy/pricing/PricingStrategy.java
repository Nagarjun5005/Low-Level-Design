package strategy.pricing;

import entity.ParkingTicket;

/**
 * Defines the contract for computing the parking fee.
 *
 * <p>This interface follows the <b>Strategy Pattern</b> — the algorithm
 * for computing the fee is separated from the exit gate itself.
 * Different implementations can define different pricing rules.</p>
 *
 * <p>Why a separate strategy?</p>
 * <ul>
 *   <li>A mall charges hourly — longer you stay, more you pay</li>
 *   <li>A hospital charges a flat rate — same fee regardless of duration</li>
 *   <li>An airport charges differently at night vs day</li>
 * </ul>
 *
 * <p>The rule can change — but {@link entity.gate.ExitGate} never needs
 * to change. You simply swap the strategy.</p>
 *
 * <pre>
 * PricingStrategy
 *       │
 *       ├── HourlyPricing    (fee = duration × ratePerHour)
 *       └── FixedPricing     (fee = flatRate always)
 * </pre>
 *
 * @see HourlyPricing
 * @see FixedPricing
 */
public interface PricingStrategy {

    /**
     * Computes the parking fee for the given ticket.
     *
     * <p>The ticket contains all the information needed
     * to calculate the fee:</p>
     * <ul>
     *   <li>Entry time — when the vehicle arrived</li>
     *   <li>Exit time  — when the vehicle left</li>
     * </ul>
     *
     * <p>Each implementation uses this information differently —
     * HourlyPricing uses the duration, FixedPricing ignores it.</p>
     *
     * @param ticket the ticket issued at entry, closed at exit
     * @return the fee to be paid as a double
     */
    double compute(ParkingTicket ticket);
}