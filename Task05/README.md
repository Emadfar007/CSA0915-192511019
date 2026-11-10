# Task 5 - Static Members and Object Life Cycle

`ParkingTicket.NEXT_NUMBER` is static because uniqueness belongs to the ticket class/application, not one ticket. `AtomicLong` makes increments safe if two gates create tickets concurrently. Occupancy is an instance field of each facility because north and south facilities need independent counts. A static validation/helper method is appropriate when no object state is required.

Objects are passed and returned as copied references. On exit, the ticket is removed from the active array and its array cell is set to `null`. When no service, receipt workflow, or caller retains that ticket, it becomes eligible for garbage collection; eligibility does not guarantee immediate collection.

If the counter were an instance field of `ParkingFacility`, two facilities could both issue ticket 1, breaking campus-wide uniqueness. That design uses `O(k)` counter words for `k` facilities. A single static/central `AtomicLong` uses `O(1)` counter space for `n` tickets. If uniqueness is required only within a facility, an instance counter is correct and the compound identity should be `(facilityId, ticketNumber)`.
