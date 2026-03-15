package entity.spot;

import entity.vehicle.Vehicle;
import enums.SpotType;
import enums.VehicleType;

/**
 * Represents a parking spot reserved for four-wheelers.
 *
 * <p>Accepts only vehicles of type {@link VehicleType#FOUR_WHEELER}.</p>
 *
 * <p>Real world examples of vehicles that fit here:</p>
 * <ul>
 *   <li>Car</li>
 *   <li>SUV</li>
 *   <li>Van</li>
 * </ul>
 *
 * <p>This class only does one thing — defines the fitting rule
 * via {@link #canFit(Vehicle)}. Everything else (assignment,
 * release, locking) is handled by the parent {@link ParkingSpot}.</p>
 *
 * <pre>
 * Example spot ids:
 *   "G-C1"  → Ground floor, four-wheeler spot 1
 *   "F1-C2" → First floor, four-wheeler spot 2
 * </pre>
 */
public class FourWheelerSpot extends ParkingSpot {

    /**
     * Creates a four-wheeler spot with the given id.
     * Spot type is fixed as FOUR_WHEELER — never changes.
     *
     * @param spotId unique identifier for this spot
     */
    public FourWheelerSpot(String spotId) {
        super(spotId, SpotType.FOUR_WHEELER);
    }

    /**
     * Returns true only if the vehicle is a four-wheeler.
     *
     * <p>A bike or truck trying to park here will get false —
     * the spot will not be assigned to them.</p>
     *
     * @param vehicle the vehicle trying to park
     * @return true if vehicle is FOUR_WHEELER, false otherwise
     */
    @Override
    public boolean canFit(Vehicle vehicle) {
        return vehicle.getVehicleType() == VehicleType.FOUR_WHEELER;
    }
}