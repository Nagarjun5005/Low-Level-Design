package entity.vehicle;

import enums.VehicleType;

/**
 * Represents a heavy vehicle in the parking lot.
 *
 * <p>Real world examples:</p>
 * <ul>
 *   <li>Truck</li>
 *   <li>Bus</li>
 *   <li>Tempo</li>
 * </ul>
 *
 * <p>This class only does one thing — tells the system it is a
 * HEAVY_VEHICLE by passing the correct type to the parent constructor.
 * Everything else (licence plate, ticket) is handled by {@link Vehicle}.</p>
 *
 * <p>A {@link HeavyVehicles} can only be assigned to a
 * {@link entity.spot.HeavyVehicleSpot} — enforced by
 * {@link entity.spot.HeavyVehicleSpot#canFit(Vehicle)}.</p>
 */
public class HeavyVehicles extends Vehicle {

    /**
     * Creates a heavy vehicle with the given licence plate.
     * Vehicle type is fixed as HEAVY_VEHICLE — never changes.
     *
     * @param licencePlate unique identifier of this vehicle
     */
    public HeavyVehicles(String licencePlate) {
        super(licencePlate, VehicleType.HEAVY_VEHICLE);
    }
}