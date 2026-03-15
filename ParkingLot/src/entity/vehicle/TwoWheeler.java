package entity.vehicle;

import enums.VehicleType;

/**
 * Represents a two-wheeler vehicle in the parking lot.
 *
 * <p>Real world examples:</p>
 * <ul>
 *   <li>Motorcycle</li>
 *   <li>Scooter</li>
 *   <li>Bike</li>
 * </ul>
 *
 * <p>This class only does one thing — tells the system it is a
 * TWO_WHEELER by passing the correct type to the parent constructor.
 * Everything else (licence plate, ticket) is handled by {@link Vehicle}.</p>
 *
 * <p>A {@link TwoWheeler} can only be assigned to a
 * {@link entity.spot.TwoWheelerSpot} — enforced by
 * {@link entity.spot.TwoWheelerSpot#canFit(Vehicle)}.</p>
 */
public class TwoWheeler extends Vehicle {

    /**
     * Creates a two-wheeler with the given licence plate.
     * Vehicle type is fixed as TWO_WHEELER — never changes.
     *
     * @param licensePlate unique identifier of this vehicle
     */
    public TwoWheeler(String licensePlate) {
        super(licensePlate, VehicleType.TWO_WHEELER);
    }
}