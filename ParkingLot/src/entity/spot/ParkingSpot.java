package entity.spot;

import entity.vehicle.Vehicle;
import enums.SpotType;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a single parking spot in the parking lot.
 *
 * <p>This class is abstract because a bare "spot" has no real-world meaning.
 * You always have a specific type — a bike spot, a car spot, or a truck spot.
 * Each subclass defines its own rule for which vehicle can park in it.</p>
 *
 * <p>Every spot has three responsibilities:</p>
 * <ol>
 *   <li>Know what type of vehicle it accepts — {@link #canFit(Vehicle)}</li>
 *   <li>Atomically assign itself to a vehicle  — {@link #tryAssign(Vehicle)}</li>
 *   <li>Release itself when the vehicle exits  — {@link #release()}</li>
 * </ol>
 *
 * <p><b>Concurrency:</b> Each spot has its own {@link ReentrantLock}.
 * This satisfies two key requirements:</p>
 * <ul>
 *   <li>No double booking — only one vehicle can claim a spot at a time</li>
 *   <li>No lot-wide blocking — assigning spot A never blocks spot B</li>
 * </ul>
 *
 * <pre>
 * Subclasses:
 *   TwoWheelerSpot    → accepts TWO_WHEELER only
 *   FourWheelerSpot   → accepts FOUR_WHEELER only
 *   HeavyVehicleSpot  → accepts HEAVY_VEHICLE only
 * </pre>
 */
public abstract class ParkingSpot {

    /** Uniquely identifies this spot. Example: "G-T1", "F1-C2" */
    private final String spotId;

    /**
     * The type of this spot.
     * Determines which vehicle category can park here.
     * Never changes after creation.
     */
    private final SpotType spotType;

    /**
     * Whether this spot is currently free.
     * true  = available for assignment.
     * false = already occupied by a vehicle.
     */
    private boolean isFree;

    /**
     * The vehicle currently parked in this spot.
     * Null when the spot is free.
     */
    private Vehicle parkedVehicle;

    /**
     * Per-spot lock for thread-safe assignment and release.
     *
     * <p>Each spot has its OWN lock — so assigning one spot
     * never blocks another spot from being assigned simultaneously.
     * This is the key to avoiding lot-wide blocking.</p>
     */
    private final ReentrantLock lock;

    /**
     * Creates a parking spot with the given id and type.
     * Spot starts as free with no vehicle assigned.
     *
     * @param spotId   unique identifier for this spot
     * @param spotType the type of vehicle this spot accepts
     */
    public ParkingSpot(String spotId, SpotType spotType) {
        this.spotId   = spotId;
        this.spotType = spotType;
        this.isFree   = true;
        this.lock     = new ReentrantLock();
    }

    /**
     * Defines whether the given vehicle can park in this spot.
     *
     * <p>This method is abstract because the fitting rule is different
     * for every spot type — a bike spot rejects a car, a car spot
     * rejects a truck. Each subclass owns its own rule.</p>
     *
     * <p>This follows the <b>Template Method Pattern</b> — the parent
     * declares the contract, subclasses provide the implementation.</p>
     *
     * @param vehicle the vehicle to check
     * @return true if this spot can accept the vehicle, false otherwise
     */
    public abstract boolean canFit(Vehicle vehicle);

    /**
     * Atomically assigns this spot to the given vehicle.
     *
     * <p>Uses {@link ReentrantLock#lock()} to guarantee the assignment
     * is thread-safe. If two entry gates try to assign the same spot
     * simultaneously, only one will succeed — the other gets false.</p>
     *
     * <p>Returns false if:</p>
     * <ul>
     *   <li>The spot is already occupied</li>
     *   <li>The vehicle type does not fit this spot</li>
     * </ul>
     *
     * @param vehicle the vehicle to assign to this spot
     * @return true if successfully assigned, false otherwise
     */
    public boolean tryAssign(Vehicle vehicle) {
        lock.lock();                     // guaranteed acquire — never skips
        try {
            if (!isFree || !canFit(vehicle)) {
                return false;            // occupied or wrong vehicle type
            }
            this.parkedVehicle = vehicle;
            this.isFree        = false;
            return true;
        } finally {
            lock.unlock();               // always released — even if exception occurs
        }
    }

    /**
     * Releases this spot after the vehicle has exited and paid.
     *
     * <p>Clears the parked vehicle and marks the spot as free.
     * Uses lock to ensure the release is thread-safe.</p>
     *
     * <p>Called by {@link entity.gate.ExitGate} after payment is confirmed.</p>
     */
    public void release() {
        lock.lock();
        try {
            this.parkedVehicle = null;
            this.isFree        = true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the unique id of this spot.
     *
     * @return spotId string
     */
    public String getSpotId() { return spotId; }

    /**
     * Returns the type of this spot.
     * Used by ParkingFloor to filter spots by vehicle type.
     *
     * @return SpotType enum value
     */
    public SpotType getSpotType() { return spotType; }

    /**
     * Returns whether this spot is currently free.
     *
     * @return true if free, false if occupied
     */
    public boolean isFree() { return isFree; }

    /**
     * Manually sets the free status of this spot.
     * Prefer using tryAssign() and release() over this setter
     * as they are thread-safe.
     *
     * @param free true to mark as free, false to mark as occupied
     */
    public void setFree(boolean free) { isFree = free; }

    /**
     * Returns the vehicle currently parked in this spot.
     *
     * @return parked Vehicle, or null if the spot is free
     */
    public Vehicle getParkedVehicle() { return parkedVehicle; }

    /**
     * Manually sets the parked vehicle.
     * Prefer using tryAssign() over this setter
     * as it is thread-safe and validates canFit().
     *
     * @param parkedVehicle the vehicle to set
     */
    public void setParkedVehicle(Vehicle parkedVehicle) {
        this.parkedVehicle = parkedVehicle;
    }

    /**
     * Returns the ReentrantLock for this spot.
     * Exposed for testing purposes only — use tryAssign() and release()
     * for all normal operations.
     *
     * @return the spot's ReentrantLock
     */
    public ReentrantLock getLock() { return lock; }
}