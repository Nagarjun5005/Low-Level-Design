package entity.spot;

import entity.vehicle.Vehicle;
import enums.SpotType;
import enums.VehicleType;

/**
 * Represents a parking spot reserved for two-wheelers.
 *
 * <p>Accepts only vehicles of type {@link VehicleType#TWO_WHEELER}.</p>
 *
 * <p>Real world examples of vehicles that fit here:</p>
 * <ul>
 *   <li>Motorcycle</li>
 *   <li>Scooter</li>
 *   <li>Bike</li>
 * </ul>
 *
 * <p>This class only does one thing — defines the fitting rule
 * via {@link #canFit(Vehicle)}. Everything else (assignment,
 * release, locking) is handled by the parent {@link ParkingSpot}.</p>
 *
 * <pre>
 * Example spot ids:
 *   "G-T1"  → Ground floor, two-wheeler spot 1
 *   "F1-T2" → First floor, two-wheeler spot 2
 * </pre>
 */
public class TwoWheelerSpot extends ParkingSpot {

    /**
     * Creates a two-wheeler spot with the given id.
     * Spot type is fixed as TWO_WHEELER — never changes.
     *
     * @param spotId unique identifier for this spot
     */
    public TwoWheelerSpot(String spotId) {
        super(spotId, SpotType.TWO_WHEELER);
    }

    /**
     * Returns true only if the vehicle is a two-wheeler.
     *
     * <p>A car or truck trying to park here will get false —
     * the spot will not be assigned to them.</p>
     *
     * @param vehicle the vehicle trying to park
     * @return true if vehicle is TWO_WHEELER, false otherwise
     */
    @Override
    public boolean canFit(Vehicle vehicle) {
        return vehicle.getVehicleType() == VehicleType.TWO_WHEELER;
    }
}