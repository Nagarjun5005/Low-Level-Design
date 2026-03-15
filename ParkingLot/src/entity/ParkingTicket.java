package entity;

import entity.spot.ParkingSpot;
import entity.vehicle.Vehicle;
import enums.TicketStatus;

import java.time.LocalDateTime;

/**
 * Represents the parking ticket issued to a vehicle at entry.
 *
 * <p>A ticket is the contract between the vehicle and the parking lot.
 * It is created at entry and closed at exit.</p>
 *
 * <p>The ticket has two kinds of fields:</p>
 * <ul>
 *   <li><b>Immutable</b> — fixed at entry, never change:
 *       ticketId, vehicle, spot, entryTime</li>
 *   <li><b>Mutable</b> — change when vehicle exits:
 *       exitTime, status</li>
 * </ul>
 *
 * <p>Ticket lifecycle:</p>
 * <pre>
 *   Vehicle arrives
 *       → new ParkingTicket(vehicle, spot)
 *       → status = ACTIVE
 *       → entryTime = now
 *
 *   Vehicle exits
 *       → markPaid()
 *       → exitTime = now
 *       → status = PAID
 * </pre>
 *
 * <p>The ticket is also used by {@link strategy.pricing.PricingStrategy}
 * to compute the fee — it reads entryTime and exitTime to
 * calculate the parking duration.</p>
 */
public class ParkingTicket {

    /**
     * Uniquely identifies this ticket.
     * Generated automatically using system time at creation.
     * Example: "TKT-1718000000000"
     * Never changes after creation.
     */
    private final String ticketId;

    /**
     * The vehicle this ticket belongs to.
     * Fixed at entry — a ticket always belongs to the same vehicle.
     * Never changes after creation.
     */
    private final Vehicle vehicle;

    /**
     * The spot assigned to this vehicle.
     * Fixed at entry — a ticket always points to the same spot.
     * Used at exit to release the spot after payment.
     * Never changes after creation.
     */
    private final ParkingSpot spot;

    /**
     * The date and time when the vehicle entered.
     * Recorded automatically at ticket creation.
     * Used by PricingStrategy to calculate parking duration.
     * Never changes after creation.
     */
    private final LocalDateTime entryTime;

    /**
     * The date and time when the vehicle exited.
     * Null until markPaid() is called at exit.
     * Set automatically when the vehicle leaves.
     */
    private LocalDateTime exitTime;

    /**
     * The current status of this ticket.
     * ACTIVE  — vehicle is currently parked.
     * PAID    — vehicle has exited and payment is done.
     */
    private TicketStatus status;

    /**
     * Creates a new parking ticket for the given vehicle and spot.
     *
     * <p>Called by {@link entity.gate.EntryGate} after a spot
     * is successfully assigned to the vehicle.</p>
     *
     * <p>Entry time is recorded automatically as the current time.
     * Status starts as ACTIVE.</p>
     *
     * @param vehicle the vehicle entering the parking lot
     * @param spot    the spot assigned to this vehicle
     */
    public ParkingTicket(Vehicle vehicle, ParkingSpot spot) {
        this.ticketId  = generateId();
        this.vehicle   = vehicle;
        this.spot      = spot;
        this.entryTime = LocalDateTime.now();
        this.status    = TicketStatus.ACTIVE;
    }

    /**
     * Generates a unique ticket id using the current system time.
     *
     * <p>In a production system, this would use a UUID or a
     * database sequence to guarantee uniqueness across restarts.</p>
     *
     * @return a unique ticket id string
     */
    private String generateId() {
        return "TKT-" + System.currentTimeMillis();
    }

    /**
     * Closes this ticket when the vehicle exits.
     *
     * <p>Two things happen when this is called:</p>
     * <ol>
     *   <li>Exit time is recorded as the current time</li>
     *   <li>Status is changed from ACTIVE to PAID</li>
     * </ol>
     *
     * <p>After this call, {@link strategy.pricing.PricingStrategy}
     * can use both entryTime and exitTime to compute the fee.</p>
     *
     * <p>Called by {@link entity.gate.ExitGate} before fee computation.</p>
     */
    public void markPaid() {
        this.exitTime = LocalDateTime.now();
        this.status   = TicketStatus.PAID;
    }

    /**
     * Returns the unique id of this ticket.
     *
     * @return ticketId string
     */
    public String getTicketId() { return ticketId; }

    /**
     * Returns the vehicle this ticket belongs to.
     *
     * @return Vehicle object
     */
    public Vehicle getVehicle() { return vehicle; }

    /**
     * Returns the spot assigned to this vehicle.
     * Used at exit to release the spot after payment.
     *
     * @return ParkingSpot object
     */
    public ParkingSpot getSpot() { return spot; }

    /**
     * Returns the time the vehicle entered.
     * Used by PricingStrategy to compute duration.
     *
     * @return entry time as LocalDateTime
     */
    public LocalDateTime getEntryTime() { return entryTime; }

    /**
     * Returns the time the vehicle exited.
     * Null if the vehicle is still parked.
     * Set when markPaid() is called.
     *
     * @return exit time as LocalDateTime, or null if still parked
     */
    public LocalDateTime getExitTime() { return exitTime; }

    /**
     * Returns the current status of this ticket.
     *
     * @return TicketStatus — ACTIVE or PAID
     */
    public TicketStatus getStatus() { return status; }
}
