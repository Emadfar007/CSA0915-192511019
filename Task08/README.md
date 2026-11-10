# Task 8 - Apply Polymorphism

Compile-time polymorphism is demonstrated by `calculateFee(int minutes)` and `calculateFee(LocalTime in, LocalTime out)`; the compiler selects the overload from argument types. Runtime polymorphism appears in a `Vehicle[]` containing `TwoWheeler`, `Car`, and `ElectricCar` references (safe upcasting). A call on a declared `Vehicle` uses the actual object's overridden `ratePerHour()`.

Trace: declared type `Vehicle` -> actual object `ElectricCar` -> inherited `Vehicle.calculateFee(130)` -> virtual call to `Car.ratePerHour()` -> 3 hours x Rs 30 -> Rs 90; the `Chargeable` capability adds 10 x Rs 12 -> Rs 210.

The supplied `FeeDemo.java` is completed and its output matches the expected result. Its final `true` is intentional because equality uses registration identity, not category.

An `instanceof` chain may perform up to `k` tests per call (`O(k)`) and grows wherever behaviour is selected. Dynamic dispatch is `O(1)` and keeps subtype behaviour near the subtype. Capability checks remain suitable for optional orthogonal behaviour such as charging.
