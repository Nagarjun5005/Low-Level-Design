# Parking Lot — Low Level Design

---

## Problem Statement

Design a Parking Lot management system for a multi-level parking building.
The system should handle:

* Vehicles of different types (two-wheeler, four-wheeler, heavy vehicle) arriving at multiple entry gates
* Automatically finding and assigning the right spot based on vehicle type
* Generating a parking ticket with entry time and spot details
* Computing the parking fee at exit using different pricing models (hourly or flat rate)
* Accepting payment and freeing the spot for the next vehicle
* Ensuring no two vehicles are assigned the same spot at the same time

## How to Think About This Problem

Before writing any code, ask three questions:

```
1. WHAT are the things?   (nouns  → classes)
2. WHAT do they do?       (verbs  → methods)
3. HOW do they connect?   (relationships → associations)
```

---

## What We See in a Real Parking Lot

Walk into any mall parking lot. What do you see?

```
- The parking lot itself
- Multiple floors / levels
- Parking spots on each floor
- Vehicles coming in
- A ticket in your hand
- Entry gate where you came in
- Exit gate where you pay and leave
- A payment machine at the exit
```

Each of these becomes a **class**.

---

## The Flow — Plain English First

### Entry Side

```
1. Vehicle arrives with a number plate and vehicle type
                        ↓
2. Search floor by floor for a free spot that fits the vehicle type
                        ↓
3. Assign that spot to the vehicle
                        ↓
4. Print a ticket with spot number and entry time
                        ↓
5. Vehicle parks
```

### Exit Side

```
1. Vehicle hands ticket at exit gate
                        ↓
2. Note the exit time
                        ↓
3. Calculate fee  (exit time - entry time)
                        ↓
4. Take payment  (cash / card / UPI)
                        ↓
5. Free the spot for the next vehicle
```

---

## Plain English → Classes

| Plain English                        | Java Class / Concept                              |
|--------------------------------------|---------------------------------------------------|
| Vehicle with number and type         | `abstract class Vehicle`                          |
| Bike / Car / Truck                   | `TwoWheeler` `FourWheeler` `HeavyVehicle`         |
| Spot with id, type, free or not      | `abstract class ParkingSpot`                      |
| Bike spot / Car spot / Truck spot    | `TwoWheelerSpot` `FourWheelerSpot` `HeavyVehicleSpot` |
| Floor that holds spots               | `ParkingFloor`                                    |
| Lot that holds floors                | `ParkingLot`                                      |
| Rule for searching a free spot       | `SpotFinderStrategy`                              |
| Search floor by floor                | `NearestSpotFinder`                               |
| Ticket with entry time and spot      | `ParkingTicket`                                   |
| Gate where vehicle enters            | `EntryGate`                                       |
| Gate where vehicle exits and pays    | `ExitGate`                                        |
| Rule for calculating fee             | `PricingStrategy`                                 |
| Hourly fee / Flat fee                | `HourlyPricing` `FixedPricing`                    |
| Payment with amount and type         | `Payment`                                         |

---

## Why Some Classes Are Abstract

> "Can you ever park a generic *vehicle* without it being a bike, car or truck?"
> **No.**

> "Can a spot exist without being a bike spot, car spot or truck spot?"
> **No.**

That is why both `Vehicle` and `ParkingSpot` are **abstract** — they can never
be created directly. You must always create a specific type.

```
// NOT allowed
new Vehicle("KA-01-1234");
new ParkingSpot("G-T1");

// ONLY this is allowed
new TwoWheeler("KA-01-1234");
new TwoWheelerSpot("G-T1");
```

---

## Why Two Things Use Strategy Pattern

> "Does every parking lot search for spots the same way?"
> **No** — one lot searches nearest first, another searches lowest floor first.

> "Does every parking lot charge the same way?"
> **No** — mall charges hourly, hospital charges flat rate.

So both the search rule and the pricing rule are **pluggable**:

```
SpotFinderStrategy                    PricingStrategy
        │                                   │
        ├── NearestSpotFinder               ├── HourlyPricing
        └── LowestFloorFirstFinder          └── FixedPricing
```

Swap the strategy → behaviour changes. No other class needs to change.

---

## UML Class Diagram

```
                    ┌──────────────────────────┐
                    │       ParkingLot          │
                    │       «singleton»         │
                    │──────────────────────────│
                    │ - floors : List<Floor>    │
                    │ - spotFinder : Strategy   │
                    │──────────────────────────│
                    │ + getInstance()           │
                    │ + findAvailableSpot(v)    │
                    └────────────┬─────────────┘
                                 │ has many
                    ┌────────────▼─────────────┐
                    │       ParkingFloor        │
                    │──────────────────────────│
                    │ - floorNumber : int       │
                    │ - spots : List<Spot>      │
                    │──────────────────────────│
                    │ + getAvailableSpots(type) │
                    └────────────┬─────────────┘
                                 │ has many
                    ┌────────────▼─────────────┐
                    │       ParkingSpot         │
                    │       «abstract»          │
                    │──────────────────────────│
                    │ - spotId : String         │
                    │ - spotType : SpotType     │
                    │ - isFree : boolean        │
                    │ - lock : ReentrantLock    │
                    │──────────────────────────│
                    │ + canFit(v)  «abstract»  │
                    │ + tryAssign(v) : boolean  │
                    │ + release()               │
                    └────┬──────────┬──────────┘
              ┌──────────┘          └──────────┐
   ┌──────────▼───────┐        ┌───────────────▼──────┐
   │ TwoWheelerSpot   │        │  FourWheelerSpot      │
   │──────────────────│        │─────────────────────  │
   │ canFit():        │        │ canFit():             │
   │ TWO_WHEELER only │        │ FOUR_WHEELER only     │
   └──────────────────┘        └───────────────────────┘
              ┌─────────────────────────┐
              │   HeavyVehicleSpot      │
              │─────────────────────────│
              │ canFit():               │
              │ HEAVY_VEHICLE only      │
              └─────────────────────────┘


   ┌──────────────────┐        ┌──────────────────────────┐
   │   Vehicle        │        │   SpotFinderStrategy      │
   │   «abstract»     │        │   «interface»             │
   │──────────────────│        │──────────────────────────│
   │ - licensePlate   │        │ + findSpot(floors, type)  │
   │ - vehicleType    │        └─────────────┬────────────┘
   │ - ticket         │                      │ implements
   └────────┬─────────┘        ┌─────────────▼────────────┐
            │ extends          │   NearestSpotFinder       │
   ┌────────▼─────────┐        │   (floor 0 → floor N)    │
   │  TwoWheeler      │        └──────────────────────────┘
   │  FourWheeler     │
   │  HeavyVehicle    │        ┌──────────────────────────┐
   └──────────────────┘        │   PricingStrategy         │
                               │   «interface»             │
                               │──────────────────────────│
                               │ + compute(ticket)         │
                               └─────────────┬────────────┘
                                             │ implements
                               ┌─────────────▼────────────┐
                               │  HourlyPricing            │
                               │  FixedPricing             │
                               └──────────────────────────┘


   ┌──────────────────┐        ┌──────────────────────────┐
   │   EntryGate      │        │   ExitGate                │
   │──────────────────│        │──────────────────────────│
   │ - gateId         │        │ - gateId                  │
   │ - lot            │        │ - pricing : Strategy      │
   │──────────────────│        │──────────────────────────│
   │ + enter(v)       │        │ + exit(ticket)            │
   └────────┬─────────┘        └─────────────┬────────────┘
            │ creates                         │ creates
   ┌────────▼─────────┐        ┌─────────────▼────────────┐
   │  ParkingTicket   │        │   Payment                 │
   │──────────────────│        │──────────────────────────│
   │ - ticketId       │        │ - amount                  │
   │ - vehicle        │        │ - paymentType             │
   │ - spot           │        │ - status                  │
   │ - entryTime      │        │──────────────────────────│
   │ - exitTime       │        │ + process()               │
   │ - status         │        └──────────────────────────┘
   └──────────────────┘
```

---

## Design Patterns Used

| Pattern         | Where                 | Why                                               |
|-----------------|-----------------------|---------------------------------------------------|
| Singleton       | `ParkingLot`          | Only one lot exists — all gates share it          |
| Strategy        | `SpotFinderStrategy`  | Search rule can change per parking lot            |
| Strategy        | `PricingStrategy`     | Pricing rule can change per exit gate             |
| Template Method | `ParkingSpot`         | `canFit()` rule is different per spot type        |

---

## Package Structure

```
parking-lot/
│
├── enums/
│   ├── VehicleType.java        TWO_WHEELER, FOUR_WHEELER, HEAVY_VEHICLE
│   ├── SpotType.java           TWO_WHEELER, FOUR_WHEELER, HEAVY_VEHICLE
│   ├── TicketStatus.java       ACTIVE, PAID
│   ├── PaymentType.java        CASH, CREDIT_CARD, UPI
│   └── PaymentStatus.java      PENDING, COMPLETED, FAILED
│
├── vehicle/
│   ├── Vehicle.java            abstract — licensePlate, vehicleType
│   ├── TwoWheeler.java         bike, scooter, motorcycle
│   ├── FourWheeler.java        car, SUV, van
│   └── HeavyVehicle.java       truck, bus, tempo
│
├── spot/
│   ├── ParkingSpot.java        abstract — canFit(), tryAssign(), release()
│   ├── TwoWheelerSpot.java     canFit() → TWO_WHEELER only
│   ├── FourWheelerSpot.java    canFit() → FOUR_WHEELER only
│   └── HeavyVehicleSpot.java   canFit() → HEAVY_VEHICLE only
│
├── strategy/
│   ├── SpotFinderStrategy.java    interface — findSpot(floors, type)
│   ├── NearestSpotFinder.java     scans floor 0 → N, first free match
│   ├── PricingStrategy.java       interface — compute(ticket)
│   ├── HourlyPricing.java         fee = duration × ratePerHour
│   └── FixedPricing.java          fee = flatRate always
│
├── entity/
│   ├── ParkingFloor.java       holds List<ParkingSpot>
│   ├── ParkingLot.java         singleton — owns floors + spotFinder
│   ├── ParkingTicket.java      entryTime, spot, vehicle — final fields
│   └── Payment.java            amount, paymentType, status
│
└── gate/
    ├── EntryGate.java          enter(vehicle) → ParkingTicket
    └── ExitGate.java           exit(ticket, paymentType) → Payment
```

---

## How to Extend

| Want to add               | What to do                                                     |
|---------------------------|----------------------------------------------------------------|
| New vehicle type (EV)     | Add `EV` to `VehicleType`, create `ElectricVehicle`           |
| New spot type (EV charge) | Add to `SpotType`, create `EVChargingSpot`                    |
| New pricing (weekend)     | Create `WeekendPricing implements PricingStrategy`            |
| New search rule           | Create `LowestFloorFirstFinder implements SpotFinderStrategy` |
| Display board             | Add `DisplayBoard` to `ParkingFloor`, update on assign/release|