package entity.vehicle;

import enums.VehicleType;

/**
 * Represents a four-wheeler vehicle in the parking lot.
 *
 * <p>Real world examples:</p>
 * <ul>
 *   <li>Car</li>
 *   <li>SUV</li>
 *   <li>Van</li>
 * </ul>
 *
 * <p>This class only does one thing — tells the system it is a
 * FOUR_WHEELER by passing the correct type to the parent constructor.
 * Everything else (licence plate, ticket) is handled by {@link Vehicle}.</p>
 *
 * <p>A {@link FourWheeler} can only be assigned to a
 * {@link entity.spot.FourWheelerSpot} — enforced by
 * {@link entity.spot.FourWheelerSpot#canFit(Vehicle)}.</p>
 */
public class FourWheeler extends Vehicle {

    /**
     * Creates a four-wheeler with the given licence plate.
     * Vehicle type is fixed as FOUR_WHEELER — never changes.
     *
     * @param licencePlate unique identifier of this vehicle
     */
    public FourWheeler(String licencePlate) {
        super(licencePlate, VehicleType.FOUR_WHEELER);
    }
}