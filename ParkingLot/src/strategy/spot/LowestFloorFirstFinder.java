package strategy.spot;

import entity.ParkingFloor;
import entity.spot.ParkingSpot;
import enums.VehicleType;

import java.util.List;
import java.util.Optional;

public class LowestFloorFirstFinder implements SpotFinderStrategy{
    @Override
    public Optional<ParkingSpot> findSpot(List<ParkingFloor> floors, VehicleType vehicleType) {
        return Optional.empty();
    }
}
