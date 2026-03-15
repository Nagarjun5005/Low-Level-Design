package strategy.pricing;

import entity.ParkingTicket;

import java.time.Duration;

/**
 * Computes the parking fee based on how long the vehicle was parked.
 *
 * <p>This is the most common pricing model — the longer you stay,
 * the more you pay.</p>
 *
 * <p>Fee calculation rule:</p>
 * <pre>
 *   duration  = exit time - entry time  (in minutes)
 *   hours     = ceil(duration / 60)     (round UP to next full hour)
 *   hours     = max(hours, 1)           (minimum charge = 1 hour)
 *   fee       = hours × ratePerHour
 *
 * Examples at ₹50/hour:
 *   20 minutes  → ceil(20/60) = 1 hour  → ₹50
 *   60 minutes  → ceil(60/60) = 1 hour  → ₹50
 *   61 minutes  → ceil(61/60) = 2 hours → ₹100
 *   90 minutes  → ceil(90/60) = 2 hours → ₹100
 * </pre>
 *
 * <p>Why round UP?</p>
 * <p>Most real parking lots charge for the full hour even if you
 * use only part of it — 61 minutes is billed as 2 hours.</p>
 *
 * <p>Why minimum 1 hour?</p>
 * <p>Prevents a zero fee if entry and exit times are recorded
 * in the same minute.</p>
 */
public class HourlyPricing implements PricingStrategy {

    /**
     * The charge per hour in the local currency.
     * Example: 50.0 means ₹50 per hour.
     * Fixed at construction — never changes.
     */
    private final double ratePerHour;

    /**
     * Creates an hourly pricing strategy with the given rate.
     *
     * @param ratePerHour the charge per hour (e.g. 50.0 for ₹50/hour)
     */
    public HourlyPricing(double ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    /**
     * Computes the fee based on parking duration.
     *
     * <p>Steps:</p>
     * <ol>
     *   <li>Calculate total minutes between entry and exit</li>
     *   <li>Convert to hours — rounded UP to next full hour</li>
     *   <li>Apply minimum charge of 1 hour</li>
     *   <li>Multiply by rate per hour</li>
     * </ol>
     *
     * @param ticket the ticket containing entry and exit time
     * @return the total fee to be paid
     */
    @Override
    public double compute(ParkingTicket ticket) {

        // total minutes the vehicle was parked
        long minutes = Duration.between(
                ticket.getEntryTime(),
                ticket.getExitTime()
        ).toMinutes();

        // round up to next full hour — 61 minutes = 2 hours
        long hours = (long) Math.ceil(minutes / 60.0);

        // minimum 1 hour — prevents zero fee for same-minute entry/exit
        hours = Math.max(hours, 1);

        return hours * ratePerHour;
    }
}