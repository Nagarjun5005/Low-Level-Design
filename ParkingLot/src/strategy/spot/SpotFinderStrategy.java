package strategy.spot;

import entity.ParkingFloor;
import entity.spot.ParkingSpot;
import enums.VehicleType;

import java.util.List;
import java.util.Optional;

/**
 * Defines the contract for finding an available parking spot.
 *
 * <p>This interface follows the <b>Strategy Pattern</b> — the algorithm
 * for finding a spot is separated from the parking lot itself.
 * Different implementations can define different search rules.</p>
 *
 * <p>Why a separate strategy?</p>
 * <ul>
 *   <li>One parking lot searches nearest spot first</li>
 *   <li>Another searches lowest floor first</li>
 *   <li>Another searches nearest to the exit</li>
 * </ul>
 *
 * <p>The rule can change — but {@link entity.ParkingLot} never needs
 * to change. You simply swap the strategy.</p>
 *
 * <pre>
 * SpotFinderStrategy
 *       │
 *       ├── NearestSpotFinder       (default — floor 0 to floor N)
 *       └── LowestFloorFirstFinder  (can be added tomorrow)
 * </pre>
 *
 * @see NearestSpotFinder
 */
public interface SpotFinderStrategy {

  /**
   * Finds the first available parking spot for the given vehicle type.
   *
   * <p>Searches across all floors and returns the first free spot
   * that can fit the given vehicle type.</p>
   *
   * <p>Returns {@link Optional#empty()} if:</p>
   * <ul>
   *   <li>The lot is full</li>
   *   <li>No spot of the required type is available</li>
   * </ul>
   *
   * @param floors      all floors in the parking lot
   * @param vehicleType the type of vehicle looking for a spot
   * @return Optional containing a free ParkingSpot, or empty if none found
   */
  Optional<ParkingSpot> findSpot(List<ParkingFloor> floors, VehicleType vehicleType);
}