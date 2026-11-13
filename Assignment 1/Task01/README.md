# Task 1 - Understand the Problem and Identify the Objects

## Inputs and outputs

Inputs are registration number, vehicle category, user category/pass status, entry and exit time, floor capacities, requested charging energy, and entry/exit actions. Outputs are allocation or full-facility status, a unique ticket, fee/receipt, released-slot status, occupancy, and total collection.

## Candidate objects and responsibilities

| Noun / class | Why it is needed | Responsibilities / verbs |
|---|---|---|
| `Vehicle` | Represents the identity and common behaviour of every parked vehicle | Validate registration, calculate fee, compare identity |
| `TwoWheeler`, `Car`, `ElectricCar` | Different rates/capabilities need specialised behaviour | Supply rate; EV also calculates charging fee |
| `User` with `Student`, `Staff`, `Visitor` | Eligibility for free parking belongs to the person/account | Report pass validity |
| `ParkingFacility` | Owns the floors and slots | Allocate, release, validate position, report occupancy |
| `ParkingTicket` | Immutable record connecting an entry to its slot | Store unique number, vehicle, user, slot and entry time |
| `ParkingService` | Coordinates a use case spanning several objects | Enter, reject duplicates/full state, exit, bill, total collection |
| `Receipt` | Makes exit result explicit and auditable | Report ticket, registration, duration and amount |

The main verbs become `allocate`, `release`, `enter`, `exit`, `calculateFee`, and `chargingFee`.

## Business rules and assumptions

- A slot holds at most one vehicle; a vehicle has at most one active ticket.
- Ticket numbers are process-wide, increasing, and never reused.
- A free slot is selected by deterministic first-fit, floor then slot.
- A started hour is billed in full; zero minutes costs zero; negative duration/energy is invalid.
- A valid student/staff monthly pass makes the complete parking transaction free.
- An EV charging charge is added only for a fee-paying transaction.
- Registration comparison is case-insensitive at service boundaries and canonicalised to upper case.
- Times are local campus time; an overloaded `LocalTime` method supports a stay crossing midnight.

## Alternative: parallel arrays

With `regNo[]`, `type[]`, and `entryTime[]`, index `i` represents one vehicle. Finding a registration is still a linear scan: `O(n)` time and `O(1)` auxiliary space, the same as scanning an object array. Adding an attribute requires a new `n`-element array, initialising it, and keeping every index synchronised. With objects, a field is added in the relevant class and each array entry remains one coherent reference. Both store `O(n)` data, but parallel arrays add an array reference plus per-array headers and invite mismatched indices; objects add per-object header/reference overhead but provide invariants and extensibility.
