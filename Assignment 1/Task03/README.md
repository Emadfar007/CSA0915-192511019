# Task 3 - Design the Control Logic

## Allocation pseudocode

```text
ALGORITHM Allocate(vehicle)
INPUT: a validated vehicle
OUTPUT: [floor, slot] or NULL
FOR floor <- 0 TO numberOfFloors - 1 DO
    FOR slot <- 0 TO slotsOnFloor(floor) - 1 DO
        IF slots[floor][slot] = NULL THEN
            slots[floor][slot] <- vehicle
            RETURN [floor, slot]
        END IF
    END FOR
END FOR
RETURN NULL
END ALGORITHM
```

`return` stops both loops immediately. A `continue` would be useful to skip incompatible reserved slots; `break` would only leave the inner loop and therefore needs an outer result check. If no slot is found, the service reports that the facility is full.

## Fee pseudocode

```text
ALGORITHM ComputeFee(vehicle, user, minutes, kWh)
INPUT: vehicle, user, non-negative minutes and energy
OUTPUT: fee
IF minutes < 0 OR kWh < 0 THEN RAISE invalid input
IF user has valid monthly pass THEN RETURN 0
hours <- (minutes + 59) DIV 60
fee <- hours * vehicle.ratePerHour()
IF vehicle supports charging THEN fee <- fee + vehicle.chargingFee(kWh)
RETURN fee
END ALGORITHM
```

The completed supplied program is `ParkingFacility.java`; captured output matches all six expected lines.

## Stack/count alternative

The scan performs `allocate` in worst-case `O(F*S)` and `release` in `O(1)`, with no index structure beyond the slots. Keeping a free-count per floor plus a stack of free slot numbers makes both operations `O(1)` amortised if a nonempty-floor stack is also maintained. Extra space is `O(F*S + F)` for the slot stacks/counts; the original extra indexing space is `O(1)`. Stack order may not preserve first-fit unless slots are pushed in reverse order.
