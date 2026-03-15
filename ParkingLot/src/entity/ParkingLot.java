package entity;

import entity.spot.ParkingSpot;
import entity.vehicle.Vehicle;
import strategy.spot.NearestSpotFinder;
import strategy.spot.SpotFinderStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the parking lot — the central entity of the entire system.
 *
 * <p>This class follows the <b>Singleton Pattern</b> — there is exactly
 * one parking lot in the system. All entry gates, exit gates, and
 * attendants talk to this single instance.</p>
 *
 * <p>Why Singleton? If two instances existed, one gate might think a spot
 * is free while another gate has already assigned it. A single shared
 * instance prevents this inconsistency.</p>
 *
 * <p>The parking lot itself does not decide HOW to find a spot — it
 * delegates that responsibility to {@link SpotFinderStrategy}.
 * This follows the <b>Strategy Pattern</b> — the finding algorithm
 * can be swapped at runtime without changing this class.</p>
 *
 * <pre>
 * Structure:
 *   ParkingLot
 *       └── ParkingFloor (floor 0)
 *               └── TwoWheelerSpot, FourWheelerSpot, HeavyVehicleSpot
 *       └── ParkingFloor (floor 1)
 *               └── TwoWheelerSpot, FourWheelerSpot, HeavyVehicleSpot
 * </pre>
 */
public class ParkingLot {

    /**
     * The single instance of ParkingLot.
     * Volatile ensures visibility across threads.
     */
    private static ParkingLot instance;

    /** All floors in this parking lot. */
    private final List<ParkingFloor> floors;

    /**
     * The strategy used to find an available spot.
     * Default is NearestSpotFinder — scans floor 0 to floor N.
     * Can be swapped at runtime via setSpotFinder().
     */
    private SpotFinderStrategy spotFinder;

    /**
     * Private constructor — prevents direct instantiation.
     * Use getInstance() to get the single shared instance.
     */
    public ParkingLot() {
        this.floors     = new ArrayList<>();
        this.spotFinder = new NearestSpotFinder();
    }

    /**
     * Returns the single instance of ParkingLot.
     * Creates it if it does not exist yet.
     *
     * <p>Synchronized to handle the case where two entry gates
     * call getInstance() simultaneously at startup — ensures
     * only one instance is ever created.</p>
     *
     * @return the single ParkingLot instance
     */
    public static synchronized ParkingLot getInstance() {
        if (instance == null) {
            instance = new ParkingLot();
        }
        return instance;
    }

    /**
     * Adds a floor to this parking lot.
     * Called once during system setup before any vehicles arrive.
     *
     * @param floor the floor to add
     */
    public void addFloor(ParkingFloor floor) {
        floors.add(floor);
    }

    /**
     * Swaps the spot-finding strategy at runtime.
     *
     * <p>Example: switch from NearestSpotFinder to LowestFloorFirstFinder
     * without changing any other class — Strategy Pattern in action.</p>
     *
     * @param strategy the new spot finding strategy to use
     */
    public void setSpotFinder(SpotFinderStrategy strategy) {
        this.spotFinder = strategy;
    }

    /**
     * Finds an available spot for the given vehicle.
     *
     * <p>Delegates entirely to the SpotFinderStrategy —
     * ParkingLot does not know or care how the search works.</p>
     *
     * <p>Returns Optional.empty() if the lot is full for this vehicle type.</p>
     *
     * @param vehicle the vehicle looking for a spot
     * @return Optional containing a free ParkingSpot, or empty if none found
     */
    public Optional<ParkingSpot> findAvailableSpot(Vehicle vehicle) {
        return spotFinder.findSpot(floors, vehicle.getVehicleType());
    }

    /**
     * Returns all floors in this parking lot.
     *
     * @return list of ParkingFloor objects
     */
    public List<ParkingFloor> getFloors() { return floors; }
}