package entity.vehicle;

import entity.ParkingTicket;
import enums.VehicleType;

/**
 * Represents a vehicle in the parking lot system.
 *
 * <p>This class is abstract because a bare "vehicle" has no real-world meaning.
 * You always park a specific type — a bike, a car, or a truck.
 * Subclasses must represent a concrete vehicle type.</p>
 *
 * <p>Every vehicle has two things that never change after arrival:</p>
 * <ul>
 *   <li>Licence plate  — uniquely identifies the vehicle</li>
 *   <li>Vehicle type   — determines which spot it can be assigned to</li>
 * </ul>
 *
 * <p>The ticket is assigned at entry and cleared at exit.</p>
 *
 * <pre>
 * Real world:
 *   Vehicle arrives → registered with plate + type → ticket assigned
 *
 * Subclasses:
 *   TwoWheeler    (bike, scooter, motorcycle)
 *   FourWheeler   (car, SUV, van)
 *   HeavyVehicle  (truck, bus, tempo)
 * </pre>
 */
public abstract class Vehicle {

    /** Uniquely identifies the vehicle. Never changes after creation. */
    private final String licencePlate;

    /**
     * The category of this vehicle.
     * Determines which spot type it can be parked in.
     * Never changes after creation.
     */
    private final VehicleType vehicleType;

    /**
     * The ticket assigned to this vehicle at entry.
     * Null until the vehicle is parked.
     * Cleared when the vehicle exits.
     */
    private ParkingTicket ticket;

    /**
     * Creates a vehicle with the given licence plate and type.
     * Constructor is package-accessible — only subclasses call this via super().
     *
     * @param licencePlate unique identifier of the vehicle
     * @param vehicleType  category of the vehicle (TWO_WHEELER, FOUR_WHEELER, HEAVY_VEHICLE)
     */
    public Vehicle(String licencePlate, VehicleType vehicleType) {
        this.licencePlate = licencePlate;
        this.vehicleType  = vehicleType;
    }

    /**
     * Returns the licence plate of this vehicle.
     *
     * @return licence plate string
     */
    public String getLicencePlate() {
        return licencePlate;
    }

    /**
     * Returns the type of this vehicle.
     * Used by SpotFinderStrategy to find a matching spot.
     *
     * @return VehicleType enum value
     */
    public VehicleType getVehicleType() {
        return vehicleType;
    }

    /**
     * Returns the parking ticket assigned to this vehicle.
     * Returns null if the vehicle has not been parked yet.
     *
     * @return ParkingTicket or null
     */
    public ParkingTicket getTicket() {
        return ticket;
    }

    /**
     * Assigns a parking ticket to this vehicle.
     * Called by EntryGate after a spot is successfully assigned.
     *
     * @param ticket the ticket generated at entry
     */
    public void setTicket(ParkingTicket ticket) {
        this.ticket = ticket;
    }
}