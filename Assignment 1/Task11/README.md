# Task 11 - Implement, Test and Defend

Compile and run the package tree using the root README commands. `ParkingSystemTest` covers normal two-wheeler/car/EV operation; the last free slot; a full facility; zero minutes; exactly 60 minutes; duplicate registration; unknown type; invalid exit and slot; free monthly-pass parking; EV charging; and a hand total.

Hand calculation: two-wheeler 130 min = 3 x 10 = 30; car 60 min = 1 x 30 = 30; car 0 min = 0; EV 130 min plus 10 kWh = 3 x 30 + 10 x 12 = 210. Total = Rs 270, matching the program.

The design enforces encapsulation, shares state/behaviour through inheritance, uses runtime polymorphism for rates, and separates optional charging with an interface. Its deliberate limitation is that active tickets are stored in a fixed array: lookup is `O(n)`, capacity is fixed, and removal may reorder tickets.

Alternative exit lookup: an array and `ArrayList` both need `O(n)` search; array removal by swap is `O(1)` after search, while preserving-list order requires `O(n)` shifting. A `HashMap<String, ParkingTicket>` has expected `O(1)` search/removal and `O(n)` space but higher per-entry memory and no natural order. Task 12 uses the map because registration is the unique lookup key.
