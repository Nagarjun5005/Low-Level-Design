package entity.gate;

import entity.ParkingLot;
import entity.ParkingTicket;
import entity.spot.ParkingSpot;
import entity.vehicle.Vehicle;

import java.util.Optional;

/**
 * Represents a physical entry gate of the parking lot.
 *
 * <p>This is the starting point of the parking flow. When a vehicle
 * arrives, the entry gate is responsible for:</p>
 * <ol>
 *   <li>Finding a free spot that matches the vehicle type</li>
 *   <li>Atomically assigning that spot to the vehicle</li>
 *   <li>Generating and returning a parking ticket</li>
 * </ol>
 *
 * <p>The parking lot can have multiple entry gates running independently.
 * Each gate talks to the same {@link ParkingLot} singleton — so all gates
 * share the same spot availability data.</p>
 *
 * <pre>
 * Real world flow:
 *   Vehicle arrives at gate
 *       → find free spot     (delegated to SpotFinderStrategy)
 *       → assign the spot    (atomic — no double booking)
 *       → print ticket       (entryTime + spot + vehicle)
 * </pre>
 */
public class EntryGate {

    /** Uniquely identifies this gate. Example: "ENTRY-1", "ENTRY-2" */
    private final String gateId;

    /**
     * Reference to the single parking lot instance.
     * All gates share the same lot — ensures consistent spot availability.
     */
    private final ParkingLot lot;

    /**
     * Creates an entry gate with the given id.
     * Automatically connects to the ParkingLot singleton.
     *
     * @param gateId unique identifier for this gate
     */
    public EntryGate(String gateId) {
        this.gateId = gateId;
        this.lot    = ParkingLot.getInstance();
    }

    /**
     * Handles the entry of a vehicle into the parking lot.
     *
     * <p>Steps performed:</p>
     * <ol>
     *   <li>Ask the lot to find an available spot for this vehicle type</li>
     *   <li>Atomically assign the spot — if another thread grabbed it first, return null</li>
     *   <li>Create a ticket with entry time and spot details</li>
     *   <li>Attach the ticket to the vehicle</li>
     * </ol>
     *
     * <p>Returns null in two cases:</p>
     * <ul>
     *   <li>No spot available — lot is full for this vehicle type</li>
     *   <li>Spot was taken between find and assign — concurrent access</li>
     * </ul>
     *
     * @param vehicle the vehicle trying to enter
     * @return ParkingTicket if successfully parked, null otherwise
     */
    public ParkingTicket enter(Vehicle vehicle) {

        // Step 1: find a free spot matching this vehicle type
        Optional<ParkingSpot> spotOpt = lot.findAvailableSpot(vehicle);

        if (spotOpt.isEmpty()) {
            return null;   // lot is full for this vehicle type
        }

        ParkingSpot spot = spotOpt.get();

        // Step 2: atomically assign the spot
        // tryAssign uses ReentrantLock — if another thread just took it, returns false
        boolean assigned = spot.tryAssign(vehicle);

        if (!assigned) {
            return null;   // spot taken between find and assign — caller can retry
        }

        // Step 3: generate ticket and attach to vehicle
        ParkingTicket ticket = new ParkingTicket(vehicle, spot);
        vehicle.setTicket(ticket);
        return ticket;
    }

    /**
     * Returns the unique id of this entry gate.
     *
     * @return gateId string
     */
    public String getGateId() { return gateId; }
}