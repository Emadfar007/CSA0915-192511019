# Task 10 - Packages and Access Modifiers

Package layout is `parking.model` for domain objects, `parking.service` for allocation/use-case logic, and `parking.app` for the executable test/application boundary.

Fields are private to preserve invariants. Constructors and operations used across packages are public. `ratePerHour()` is protected because only subclasses supply it. Package-private visibility is used for no external API in the final model. Getters expose immutable values; mutation occurs through validated operations rather than raw setters. Negative duration/energy, blank identity, invalid capacity, duplicate registration, and invalid slots are rejected.

Wrappers arise naturally with collections such as `Map<String, ParkingTicket>` and would arise with `List<Integer>` through autoboxing. `Integer a == b` compares object identity outside the cache range; `a.equals(b)` or unboxing compares values. Unboxing `null` throws `NullPointerException`.

Public fields plus caller-side validation still cost `O(1)` per check, but every one of `m` update locations must repeat it (`O(m)` duplicated code) and one missed caller can corrupt state. Encapsulation performs the same constant-time check once at the controlled boundary.
