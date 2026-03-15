import entity.gate.EntryGate;
import entity.gate.ExitGate;
import entity.spot.FourWheelerSpot;
import entity.spot.HeavyVehicleSpot;
import entity.spot.TwoWheelerSpot;
import entity.vehicle.FourWheeler;
import entity.vehicle.HeavyVehicles;
import entity.vehicle.TwoWheeler;
import entity.*;
import entity.vehicle.Vehicle;
import enums.PaymentType;
import strategy.pricing.FixedPricing;
import strategy.pricing.HourlyPricing;

/**
 * Entry point of the Parking Lot system.
 *
 * <p>This class demonstrates the complete end-to-end flow of the
 * parking lot — from building the lot, to vehicles arriving,
 * parking, and exiting with payment.</p>
 *
 * <p>Flow demonstrated:</p>
 * <pre>
 *   1. Build the lot
 *      → create floors
 *      → add spots of each type to each floor
 *      → add floors to the lot
 *
 *   2. Create gates
 *      → two entry gates  (vehicles arrive at either)
 *      → two exit gates   (one hourly pricing, one flat rate)
 *
 *   3. Vehicles arrive
 *      → each vehicle enters through a gate
 *      → system finds a free spot matching vehicle type
 *      → spot is assigned and ticket is generated
 *
 *   4. Vehicles exit
 *      → each vehicle exits through a gate with its ticket
 *      → fee is computed based on gate's pricing strategy
 *      → payment is processed
 *      → spot is freed for the next vehicle
 * </pre>
 */
public class ParkingLotDemo {

    public static void main(String[] args) {

        // ── Step 1: Build the lot ─────────────────────────────
        // ParkingLot is a singleton — only one instance exists
        ParkingLot lot = ParkingLot.getInstance();

        // Ground floor — floor number 0
        ParkingFloor ground = new ParkingFloor(0);
        ground.addSpot(new TwoWheelerSpot("G-T1"));    // bike spot 1
        ground.addSpot(new TwoWheelerSpot("G-T2"));    // bike spot 2
        ground.addSpot(new FourWheelerSpot("G-C1"));   // car spot 1
        ground.addSpot(new FourWheelerSpot("G-C2"));   // car spot 2
        ground.addSpot(new HeavyVehicleSpot("G-H1"));  // truck spot 1

        // First floor — floor number 1
        ParkingFloor first = new ParkingFloor(1);
        first.addSpot(new TwoWheelerSpot("F1-T1"));    // bike spot 1
        first.addSpot(new FourWheelerSpot("F1-C1"));   // car spot 1
        first.addSpot(new HeavyVehicleSpot("F1-H1"));  // truck spot 1

        lot.addFloor(ground);
        lot.addFloor(first);

        // ── Step 2: Create gates ──────────────────────────────
        // Two entry gates — vehicles can arrive at either
        EntryGate entry1 = new EntryGate("ENTRY-1");
        EntryGate entry2 = new EntryGate("ENTRY-2");

        // Two exit gates — each with a different pricing strategy
        // EXIT-1 charges hourly  → ₹50 per hour
        // EXIT-2 charges flat    → ₹30 always
        ExitGate exitHourly = new ExitGate("EXIT-1", new HourlyPricing(50.0));
        ExitGate exitFlat   = new ExitGate("EXIT-2", new FixedPricing(30.0));

        // ── Step 3: Vehicles arrive ───────────────────────────
        // Three vehicles of different types arrive
        Vehicle bike  = new TwoWheeler("KA-01-EF-1111");
        Vehicle car   = new FourWheeler("KA-02-AB-2222");
        Vehicle truck = new HeavyVehicles("KA-03-CD-3333");

        // Each vehicle enters through a gate
        // System finds a matching free spot and returns a ticket
        // Ticket is null if lot is full for that vehicle type
        ParkingTicket t1 = entry1.enter(bike);   // assigned G-T1
        ParkingTicket t2 = entry2.enter(car);    // assigned G-C1
        ParkingTicket t3 = entry1.enter(truck);  // assigned G-H1

        // ── Step 4: Vehicles exit ─────────────────────────────
        // Each vehicle exits through a gate with its ticket
        // Null check ensures we only process successfully parked vehicles
        if (t1 != null) exitHourly.exit(t1, PaymentType.UPI);          // bike  → hourly → UPI
        if (t2 != null) exitFlat.exit(t2, PaymentType.CASH);           // car   → flat   → cash
        if (t3 != null) exitHourly.exit(t3, PaymentType.CREDIT_CARD);  // truck → hourly → card
    }
}