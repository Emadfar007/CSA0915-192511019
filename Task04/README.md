# Task 4 - Classes, Objects and Constructors

`Vehicle` has registration identity and fee operations; subclasses supply rate/capability. `ParkingTicket` owns the immutable entry snapshot. `ParkingFacility` owns slot state, while `ParkingService` coordinates entry and exit.

The ticket has an overloaded constructor: the four-argument form delegates with `this(..., LocalDateTime.now())` to the five-argument form. Subclass constructors call `super(registrationNumber)`, establishing the superclass invariant first. No default constructor is exposed because a vehicle without a registration or a ticket without its linked objects is invalid. A copy constructor is unnecessary: a duplicate receipt should reference the same immutable ticket data and ticket number rather than create a second logical ticket.

Local reference variables and parameters live in stack frames; constructed objects/arrays live on the heap, and reference fields connect them. Java passes object references by value.

Setter-after-default construction has `O(1)` time but permits temporarily invalid objects and forgotten calls. Constructor chaining and a static factory are also `O(1)` per object; creating `n` tickets is `O(n)` time and `O(n)` heap space in every approach. A factory adds no per-object space and is useful when choosing a subtype or caching, while constructors remain simplest for direct creation.
