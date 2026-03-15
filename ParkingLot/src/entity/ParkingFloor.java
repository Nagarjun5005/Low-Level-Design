package entity;

import entity.spot.ParkingSpot;
import enums.SpotType;
import enums.VehicleType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents one level / floor of the parking lot.
 *
 * <p>A parking lot has multiple floors. Each floor independently
 * manages its own collection of parking spots.</p>
 *
 * <p>The floor's only job is to:</p>
 * <ul>
 *   <li>Hold a list of spots</li>
 *   <li>Report which spots are free for a given vehicle type</li>
 * </ul>
 *
 * <p>The floor does NOT decide which spot to assign — it just
 * reports availability. The {@link strategy.spot.SpotFinderStrategy}
 * makes the final decision.</p>
 *
 * <pre>
 * ParkingFloor (floor 0)
 *     └── TwoWheelerSpot   "G-T1"  free=true
 *     └── TwoWheelerSpot   "G-T2"  free=false
 *     └── FourWheelerSpot  "G-C1"  free=true
 *     └── HeavyVehicleSpot "G-H1"  free=true
 * </pre>
 */
public class ParkingFloor {

    /** The level number of this floor. Ground floor = 0. */
    private final int floorNumber;

    /** All parking spots on this floor. */
    private final List<ParkingSpot> spots;

    /**
     * Creates a new floor with the given level number.
     *
     * @param floorNumber the level of this floor (0 = ground, 1 = first, etc.)
     */
    public ParkingFloor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.spots       = new ArrayList<>();
    }

    /**
     * Adds a parking spot to this floor.
     * Called once during system setup before any vehicles arrive.
     *
     * @param spot the spot to add to this floor
     */
    public void addSpot(ParkingSpot spot) {
        spots.add(spot);
    }

    /**
     * Returns all free spots on this floor that can accept the given vehicle type.
     *
     * <p>Two filters are applied:</p>
     * <ol>
     *   <li>Spot must be free</li>
     *   <li>Spot type must match the vehicle type</li>
     * </ol>
     *
     * <p>Uses {@link #toSpotType(VehicleType)} to map vehicle type to spot type
     * explicitly — avoids fragile string comparison between two different enums.</p>
     *
     * @param vehicleType the type of vehicle looking for a spot
     * @return list of free spots matching the vehicle type, empty list if none
     */
    public List<ParkingSpot> getAvailableSpots(VehicleType vehicleType) {
        SpotType required = toSpotType(vehicleType);
        return spots.stream()
                .filter(ParkingSpot::isFree)
                .filter(s -> s.getSpotType() == required)  // enum equality — not string
                .collect(Collectors.toList());
    }

    /**
     * Maps a VehicleType to its corresponding SpotType.
     *
     * <p>Why explicit mapping instead of string comparison?</p>
     * <ul>
     *   <li>VehicleType and SpotType are two different enums</li>
     *   <li>String comparison silently breaks if an enum value is renamed</li>
     *   <li>This mapping fails fast with a clear error if a new vehicle type
     *       is added but no spot mapping is defined for it</li>
     * </ul>
     *
     * @param vehicleType the vehicle type to map
     * @return the corresponding SpotType
     * @throws IllegalArgumentException if no mapping exists for the given type
     */
    private SpotType toSpotType(VehicleType vehicleType) {
        switch (vehicleType) {
            case TWO_WHEELER:   return SpotType.TWO_WHEELER;
            case FOUR_WHEELER:  return SpotType.FOUR_WHEELER;
            case HEAVY_VEHICLE: return SpotType.HEAVY_VEHICLE;
            default: throw new IllegalArgumentException(
                    "No spot mapping for vehicle type: " + vehicleType);
        }
    }

    /**
     * Returns the level number of this floor.
     *
     * @return floor number (0 = ground floor)
     */
    public int getFloorNumber() { return floorNumber; }

    /**
     * Returns all spots on this floor regardless of availability.
     *
     * @return list of all ParkingSpot objects on this floor
     */
    public List<ParkingSpot> getSpots() { return spots; }
}