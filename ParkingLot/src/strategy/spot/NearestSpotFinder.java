package strategy.spot;

import entity.ParkingFloor;
import entity.spot.ParkingSpot;
import enums.VehicleType;

import java.util.List;
import java.util.Optional;

/**
 * Finds the nearest available spot by scanning floors in order.
 *
 * <p>This is the default implementation of {@link SpotFinderStrategy}.</p>
 *
 * <p>Search rule:</p>
 * <pre>
 *   Start at Floor 0
 *       → check each spot on this floor
 *       → if a free spot matching the vehicle type is found → return it
 *       → if nothing found on this floor → move to Floor 1
 *       → repeat until all floors are exhausted
 *       → if nothing found anywhere → return empty
 * </pre>
 *
 * <p>This gives the vehicle the spot closest to the ground floor —
 * hence the name "nearest".</p>
 *
 * <p>To change the search behaviour, create a new implementation
 * of {@link SpotFinderStrategy} and inject it into {@link entity.ParkingLot}
 * via setSpotFinder() — this class never needs to change.</p>
 */
public class NearestSpotFinder implements SpotFinderStrategy {

    /**
     * Scans floors from index 0 upward and returns the first
     * available spot that matches the given vehicle type.
     *
     * <p>Delegates availability check to
     * {@link entity.ParkingFloor#getAvailableSpots(VehicleType)}
     * — this class only decides the ORDER of search, not the filtering.</p>
     *
     * @param floors      all floors in the parking lot, in order (0 to N)
     * @param vehicleType the type of vehicle looking for a spot
     * @return Optional containing the first free matching spot,
     *         or Optional.empty() if the lot is full
     */
    @Override
    public Optional<ParkingSpot> findSpot(List<ParkingFloor> floors,
                                          VehicleType vehicleType) {
        for (ParkingFloor floor : floors) {

            // ask this floor for all free spots matching the vehicle type
            List<ParkingSpot> available = floor.getAvailableSpots(vehicleType);

            if (!available.isEmpty()) {
                return Optional.of(available.getFirst()); // nearest = first found
            }
        }

        return Optional.empty(); // no spot found on any floor — lot is full
    }
}