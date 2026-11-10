# Task 12 - Re-evaluate and Adapt the Design

## Impact and evaluation

New requirements add e-bikes with a flat fee, time-dependent delivery-van pricing, visitor one-day passes, and five unequal floors. Vehicle creation, fee policy, visitor eligibility, facility construction, and tests are affected. The jagged `Vehicle[][]` already supports unequal floors because each row has its own length; only the capacity input changes.

The original subtype override design remains good for identity and capabilities, but complex pricing would spread time/rate logic across vehicle classes. It is modified, not discarded: `Vehicle` keeps identity while holding a `PricingRule`. `HourlyPricing`, `FlatPricing`, and `DeliveryVanPricing` isolate pricing. `Pass` and `OneDayPass` isolate eligibility. Active tickets change from an array to a `HashMap` for expected `O(1)` exit lookup.

## Change count and defence

Existing concepts changed: `Vehicle` gained a pricing strategy, simple vehicle constructors select strategies, `User` gained an optional `Pass`, and `ParkingService` uses a map and entry time. `ParkingFacility` required no algorithmic change and accepts `{2,3,1,4,2}` unchanged. New code is added for `EBike`, `DeliveryVan`, three pricing policies, `Pass`, `OneDayPass`, and `MonthlyPass`. EV charging and student/staff users remain supported.

A further simple vehicle type requires one small subclass plus selection of an existing rule; a genuinely new tariff requires one `PricingRule` implementation. Existing allocation, ticket and exit orchestration remain unchanged. The trade-off is one extra strategy reference/object and more classes, in exchange for localised rules and testability. Fee calls remain `O(1)`; hash ticket lookup is expected `O(1)` with `O(n)` storage.

Tests prove: e-bike flat fee Rs 25, peak delivery van 120 minutes Rs 120, off-peak van 120 minutes Rs 60, a van crossing 5 PM correctly splits to Rs 90, a valid one-day visitor pass is Rs 0, and the original EV case remains Rs 210. Therefore **MODIFY** is the evidence-supported decision.
