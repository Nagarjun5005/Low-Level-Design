package strategy.pricing;

import entity.ParkingTicket;

/**
 * Computes the parking fee as a fixed flat rate.
 *
 * <p>This pricing model charges the same fee regardless of
 * how long the vehicle was parked.</p>
 *
 * <p>Fee calculation rule:</p>
 * <pre>
 *   fee = flatRate  (always, no matter the duration)
 *
 * Examples at ₹30 flat rate:
 *   20 minutes  → ₹30
 *   2 hours     → ₹30
 *   8 hours     → ₹30
 * </pre>
 *
 * <p>Real world use cases:</p>
 * <ul>
 *   <li>Hospital parking — flat rate for any visit</li>
 *   <li>Event parking — fixed charge for the event duration</li>
 *   <li>Short stay zones — same fee for any short visit</li>
 * </ul>
 *
 * <p>Notice how {@link #compute(ParkingTicket)} completely ignores
 * the ticket's entry and exit time — duration is irrelevant here.
 * This is the simplest possible implementation of
 * {@link PricingStrategy}.</p>
 */
public class FixedPricing implements PricingStrategy {

    /**
     * The flat rate charged regardless of parking duration.
     * Example: 30.0 means ₹30 always.
     * Fixed at construction — never changes.
     */
    private final double flatRate;

    /**
     * Creates a fixed pricing strategy with the given flat rate.
     *
     * @param flatRate the fixed fee to charge for any parking duration
     */
    public FixedPricing(double flatRate) {
        this.flatRate = flatRate;
    }

    /**
     * Returns the flat rate as the parking fee.
     *
     * <p>Duration does not matter — entry time and exit time
     * on the ticket are intentionally ignored.</p>
     *
     * @param ticket the ticket issued at entry (duration ignored)
     * @return the flat rate fee
     */
    @Override
    public double compute(ParkingTicket ticket) {
        return flatRate;   // same fee always — duration is irrelevant
    }
}