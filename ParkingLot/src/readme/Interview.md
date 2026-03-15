# Parking Lot LLD — Interview Questions & Answers

---

## Basic Level

---

### Q1. Can you explain what you have designed?

I designed a multi-level parking lot system.
A vehicle arrives at an entry gate, the system finds a free spot matching
the vehicle type, generates a ticket, and the vehicle parks.
When the vehicle exits, the fee is calculated based on a pricing strategy,
payment is made, and the spot is freed for the next vehicle.

---

### Q2. Why is Vehicle an abstract class?

Because a bare "vehicle" has no real-world meaning.
You always park a specific type — a bike, a car, or a truck.
Making it abstract prevents direct instantiation and enables polymorphism —
every gate and strategy works with `Vehicle` regardless of the actual subtype.

```java
// NOT allowed
new Vehicle("KA-01-1234");

// ONLY this is allowed
new TwoWheeler("KA-01-1234");
new FourWheeler("KA-02-5678");
new HeavyVehicle("KA-03-9999");
```

---

### Q3. Why is ParkingSpot abstract?

Same reason as `Vehicle`.
A spot always belongs to a specific category — bike spot, car spot, truck spot.
Each subtype defines its own `canFit()` rule.
The parent cannot generalise this because the fitting rule is different
for every spot type.

---

### Q4. What is canFit() and why is it abstract?

`canFit()` answers one question — can this vehicle park in this spot?

It is abstract because each spot type has a different answer:
- `TwoWheelerSpot`   → accepts only bikes
- `FourWheelerSpot`  → accepts only cars
- `HeavyVehicleSpot` → accepts only trucks

Making it abstract forces every subclass to define its own rule.
The parent cannot provide a default because there is no general rule.

```java
// TwoWheelerSpot defines its own rule
@Override
public boolean canFit(Vehicle vehicle) {
    return vehicle.getVehicleType() == VehicleType.TWO_WHEELER;
}
```

---

### Q5. What enums have you used and why?

| Enum | Values |
|---|---|
| `VehicleType` | TWO_WHEELER, FOUR_WHEELER, HEAVY_VEHICLE |
| `SpotType` | TWO_WHEELER, FOUR_WHEELER, HEAVY_VEHICLE |
| `TicketStatus` | ACTIVE, PAID |
| `PaymentType` | CASH, CREDIT_CARD, UPI |
| `PaymentStatus` | PENDING, COMPLETED, FAILED |

Enums prevent invalid values, make switch statements exhaustive,
and make the code self-documenting.
`VehicleType.TWO_WHEELER` is clearer than a string `"bike"`
which could be misspelled and the compiler would never catch it.

---

## Intermediate Level

---

### Q6. Why did you use the Strategy Pattern for spot finding?

Because the rule for finding a spot can change.
- Today → nearest spot first
- Tomorrow → lowest floor first
- Next week → nearest to exit

If the logic were hardcoded inside `ParkingLot`, I would have to modify
the lot every time the rule changes.

With Strategy Pattern, I write a new class implementing `SpotFinderStrategy`
and inject it — `ParkingLot` never changes.
This follows the **Open/Closed Principle** — open for extension, closed for modification.

```
SpotFinderStrategy  (interface)
      │
      ├── NearestSpotFinder       ← current
      └── LowestFloorFirstFinder  ← add tomorrow, no other class changes
```

---

### Q7. Why did you use the Strategy Pattern for pricing?

Because different parking lots charge differently:
- Mall → hourly rate
- Hospital → flat rate
- Airport → different rate at night

The exit gate should not know HOW the fee is calculated.
It just calls `pricingStrategy.compute(ticket)` and gets the fee back.
Swapping the strategy changes the pricing model without touching the gate.

```
PricingStrategy  (interface)
      │
      ├── HourlyPricing   ← fee = hours × rate
      └── FixedPricing    ← fee = flat rate always
```

---

### Q8. Why is ParkingLot a Singleton?

Because there is exactly one parking lot in the system.
All entry gates and exit gates must share the same spot availability data.

If two instances existed:
- Gate 1 might think spot G-T1 is free
- Gate 2 has already assigned it
- Two vehicles get the same spot → conflict

Singleton guarantees all gates always talk to the same shared instance.

```java
public static synchronized ParkingLot getInstance() {
    if (instance == null) {
        instance = new ParkingLot();
    }
    return instance;
}
```

---

### Q9. How does the entry flow work end to end?

```
Vehicle arrives at EntryGate
        ↓
EntryGate calls ParkingLot.findAvailableSpot(vehicle)
        ↓
ParkingLot delegates to SpotFinderStrategy.findSpot(floors, vehicleType)
        ↓
Strategy scans floors in order → returns first free matching spot
        ↓
EntryGate calls spot.tryAssign(vehicle)  ← atomic
        ↓
ParkingTicket created → vehicle, spot, entryTime
        ↓
Ticket returned to caller
```

---

### Q10. How does the exit flow work end to end?

```
Vehicle arrives at ExitGate with ticket
        ↓
Validate ticket → reject if already PAID
        ↓
ticket.markPaid()  ← records exitTime
        ↓
pricingStrategy.compute(ticket)  ← calculates fee
        ↓
Payment created and processed
        ↓
spot.release()  ← spot is free for next vehicle
```

---

### Q11. Why are ticketId, vehicle, spot, and entryTime final in ParkingTicket?

Because these facts are fixed at the moment of entry and should never change:
- A ticket always belongs to the same vehicle
- A ticket always points to the same spot
- Entry time never changes

Making them `final` enforces immutability at compile time.
Only `exitTime` and `status` are mutable because they change when the vehicle exits.

```java
private final String         ticketId;    // never changes
private final Vehicle        vehicle;     // never changes
private final ParkingSpot    spot;        // never changes
private final LocalDateTime  entryTime;   // never changes

private       LocalDateTime  exitTime;    // set at exit
private       TicketStatus   status;      // ACTIVE → PAID
```

---

## Advanced Level

---

### Q12. How did you handle the concurrency problem — no double booking?

Each `ParkingSpot` has its own `ReentrantLock`.

When `tryAssign()` is called:
1. Acquire the lock
2. Check if the spot is free and can fit the vehicle
3. Assign atomically
4. Release the lock

If two entry gates try to assign the same spot simultaneously,
only one succeeds — the other gets `false` and can retry.

```java
public boolean tryAssign(Vehicle vehicle) {
    lock.lock();                          // guaranteed acquire
    try {
        if (!isFree || !canFit(vehicle))
            return false;
        this.parkedVehicle = vehicle;
        this.isFree        = false;
        return true;
    } finally {
        lock.unlock();                    // always released
    }
}
```

---

### Q13. Why per-spot lock and not a single lock on the whole lot?

A single lock on the lot would mean:
- Assigning spot A blocks ALL other spots from being assigned simultaneously
- Only one vehicle can enter at a time across all gates → bottleneck

With per-spot locks:
- Spot A and spot B can be assigned simultaneously by two different gates
- One vehicle's assignment never blocks another spot

This satisfies the requirement:
> *"For one vehicle we should not block the whole parking lot"*

---

### Q14. Why lock.lock() and not lock.tryLock() in tryAssign()?

`tryLock()` returns `false` immediately if the lock is held by another thread.
In a single-threaded demo this caused a bug — the lock appeared to fail
even when no other thread was holding it.

`lock.lock()` guarantees the lock is always acquired — it waits if necessary.

```java
// WRONG — tryLock() can return false even in single thread
if (!lock.tryLock()) return false;

// CORRECT — always acquires, never skips
lock.lock();
```

`tryLock()` is appropriate only when you want to skip a busy spot
and immediately try the next one in a concurrent retry loop.

---

### Q15. How would you extend this system to support EV charging spots?

1. Add `EV_VEHICLE` to `VehicleType` enum
2. Add `EV_CHARGING` to `SpotType` enum
3. Create `ElectricVehicle extends Vehicle`
4. Create `EVChargingSpot extends ParkingSpot` with its own `canFit()` rule
5. Add mapping in `ParkingFloor.toSpotType()`

No other class needs to change.
This is the **Open/Closed Principle** in action —
open for extension, closed for modification.

```java
// Only this method needs one new line
private SpotType toSpotType(VehicleType vehicleType) {
    switch (vehicleType) {
        case TWO_WHEELER:   return SpotType.TWO_WHEELER;
        case FOUR_WHEELER:  return SpotType.FOUR_WHEELER;
        case HEAVY_VEHICLE: return SpotType.HEAVY_VEHICLE;
        case EV_VEHICLE:    return SpotType.EV_CHARGING;  // ← add this
    }
}
```

---

### Q16. How would you add a display board showing real-time availability?

Add a `DisplayBoard` class to `ParkingFloor`.
Every time `tryAssign()` or `release()` is called,
the floor updates the board with the current free count per spot type.

For a more decoupled solution — use the **Observer Pattern**:
- Spot notifies registered observers when its status changes
- `DisplayBoard` is one such observer
- Tomorrow you can add SMS alerts, app notifications as new observers
  without changing any existing class

```
ParkingSpot  →  notifies  →  DisplayBoard
                          →  SMSAlert        (add tomorrow)
                          →  AppNotification (add tomorrow)
```

---

### Q17. How would you make this production ready?

| What | How |
|---|---|
| Unique ticket ids | Replace `System.currentTimeMillis()` with UUID |
| Persistence | Add `TicketRepository`, `PaymentRepository` with DB |
| Real payment | Integrate Razorpay / PayTM / Stripe in `Payment.process()` |
| Dependency injection | Use Spring — no manual Singleton needed |
| API layer | REST endpoints for entry, exit, availability |
| Real-time availability | Redis cache for spot counts, invalidate on assign/release |
| Exception handling | Custom exceptions — `LotFullException`, `InvalidTicketException` |
| Logging | Add SLF4J logging at every gate action |

---

## One-line Summary — Design Patterns Used

```
Singleton       →  "One lot, shared by all gates"
Strategy        →  "Swap the rule without changing the caller"
Template Method →  "Parent defines the contract, subclass fills the rule"
Abstract class  →  "You can never park a generic vehicle"
ReentrantLock   →  "One spot, one vehicle, no exceptions"
```

---

## Quick Revision — The Complete Flow

```
ENTRY                                    EXIT
─────────────────────────────────────────────────────────
Vehicle arrives                          Vehicle hands ticket
        ↓                                        ↓
EntryGate.enter(vehicle)                 ExitGate.exit(ticket)
        ↓                                        ↓
findAvailableSpot(vehicle)               ticket.markPaid()
        ↓                                        ↓
SpotFinderStrategy.findSpot()            pricingStrategy.compute(ticket)
        ↓                                        ↓
spot.tryAssign(vehicle)                  payment.process()
        ↓                                        ↓
new ParkingTicket(vehicle, spot)         spot.release()
        ↓                                        ↓
return ticket                            return payment
```