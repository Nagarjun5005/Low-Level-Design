package entity.spot;

import entity.vehicle.Vehicle;
import enums.SpotType;
import enums.VehicleType;

/**
 * Represents a parking spot reserved for heavy vehicles.
 *
 * <p>Accepts only vehicles of type {@link VehicleType#HEAVY_VEHICLE}.</p>
 *
 * <p>Real world examples of vehicles that fit here:</p>
 * <ul>
 *   <li>Truck</li>
 *   <li>Bus</li>
 *   <li>Tempo</li>
 * </ul>
 *
 * <p>This class only does one thing — defines the fitting rule
 * via {@link #canFit(Vehicle)}. Everything else (assignment,
 * release, locking) is handled by the parent {@link ParkingSpot}.</p>
 *
 * <pre>
 * Example spot ids:
 *   "G-H1"  → Ground floor, heavy vehicle spot 1
 *   "F1-H2" → First floor, heavy vehicle spot 2
 * </pre>
 */
public class HeavyVehicleSpot extends ParkingSpot {

    /**
     * Creates a heavy vehicle spot with the given id.
     * Spot type is fixed as HEAVY_VEHICLE — never changes.
     *
     * @param spotId unique identifier for this spot
     */
    public HeavyVehicleSpot(String spotId) {
        super(spotId, SpotType.HEAVY_VEHICLE);
    }

    /**
     * Returns true only if the vehicle is a heavy vehicle.
     *
     * <p>A bike or car trying to park here will get false —
     * the spot will not be assigned to them.</p>
     *
     * @param vehicle the vehicle trying to park
     * @return true if vehicle is HEAVY_VEHICLE, false otherwise
     */
    @Override
    public boolean canFit(Vehicle vehicle) {
        return vehicle.getVehicleType() == VehicleType.HEAVY_VEHICLE;
    }
}