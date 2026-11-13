# Task 7 - Override and Specialise Behaviour

Subclasses override `ratePerHour()` because the hourly rate differs; `ElectricCar` implements `chargingFee()`. `@Override` lets the compiler verify the same signature and a compatible return/access level. An overriding method cannot reduce visibility.

Registration and ticket number are `final` fields because identity must not change. Identity getters/equality are final in the complete system so subclasses cannot break collection behaviour. Leaf classes are final when extension has no designed contract.

`toString()` reports subtype and registration. `equals()` and `hashCode()` use canonical registration so equal registrations have equal hashes. This intentionally makes a `Car("TN02B2")` equal to an `ElectricCar("TN02B2")`: registration is the parked physical vehicle identity. If category must be part of identity, both methods must include `getClass()`; this system rejects simultaneous duplicate registrations, so registration-only identity is appropriate.

A single switch on a type field is `O(1)` for a Java switch (or up to `O(k)` as an if-chain), but every new type changes the central method and possibly callers. Overriding remains `O(1)` dynamic dispatch and normally adds a subtype without changing existing fee code.
