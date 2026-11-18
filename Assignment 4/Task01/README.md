# Task 01 - GUI Design Review

## Diagnosis

| Problem | Impact |
|---|---|
| One shared listener branches on button text | Fragile when labels/localisation change; violates command separation |
| `==` for String comparison | Compares references, so intended actions may not run |
| Business/database logic in the handler | UI becomes untestable and tightly coupled |
| No validation before parsing | Bad values cause exceptions or invalid payments |
| Database call on the EDT | Window freezes during I/O |
| `Thread.sleep()` in handler | Blocks painting and input |
| SQL built by concatenation | SQL injection and quoting errors |
| Connections/statements not closed | Resource leaks and connection exhaustion |
| Generic catch or no feedback | User cannot correct the problem; defects are hidden |
| Receipt button has no independent action/state | It may print nothing or a stale receipt |

## Corrected structure

```text
FeePaymentApp (Swing UI)
  -> FeeService.process(validated strings)
       -> PaymentReceipt (immutable result)
```

The UI only reads controls and displays results. `FeeService` validates registration, amount and method and returns an immutable receipt. Separate listeners call named methods, so button labels are irrelevant. Validation failures become clear status messages. A production DAO would use prepared statements, try-with-resources and background work for I/O. This split permits service tests without Swing and keeps the EDT responsive. (74 words)

Separate listeners are clearer for unrelated commands. A shared listener is acceptable only when it delegates immediately by source. `Action` is best when the same command is used by a button, menu and keyboard shortcut because name, enabled state and behavior stay synchronized.

Run the deterministic evidence mode:

```powershell
java -Djava.awt.headless=true -cp ../build/Task01 FeePaymentApp --demo output/fee-payment-success.png
```
