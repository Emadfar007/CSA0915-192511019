# Task 2 - Choose the Data Representation

| Value | Java representation | Reason |
|---|---|---|
| floor, slot, minutes, kWh | `int` | Integral, bounded campus values |
| ticket number | `long` | Much larger unique sequence |
| registration number | canonical `String` | Contains letters, digits and formatting |
| entry time | `LocalDateTime` | Date plus time, immutable and type-safe |
| fee/rate | `double` | Required by the brief; production money should use `BigDecimal` or integer paise |
| occupancy/pass flags | `int` / `boolean` | Counter and two-state facts |

Slots use a jagged `Vehicle[][]`: each `slots[floor]` can have a different length, while `slots[f][s]` remains direct `O(1)` access. Input can be read using `Scanner` in an interactive front end; the test driver uses constructor arguments so tests are deterministic.

The wrong expression `minutes / 60 * 30` bills 61 minutes as `1 * 30 = Rs 30`. Casting afterwards, `(double)(minutes / 60)`, cannot recover the discarded fraction. The design uses integer ceiling arithmetic `(minutes + 59) / 60`, so 61 minutes is two started hours and costs Rs 60. Negative minutes are rejected before the addition.

## Alternatives

- Flat one-dimensional array: index `offset[floor] + slot`. Access is `O(1)`, but unequal floors require an `O(F)` offset table or an `O(F)` prefix calculation; capacity is compact but mapping is less readable.
- `ArrayList<Vehicle[]>`: direct floor access and slot access are both `O(1)`, unequal floors work, and floors can be appended. It adds list capacity/boxing-like reference overhead and exposes resize behaviour not needed after construction.
- Jagged array: `O(1)` lookup, `O(total slots)` storage, fixed topology, and the clearest match to a fixed facility. This is selected.
