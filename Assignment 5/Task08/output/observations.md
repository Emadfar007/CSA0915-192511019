# B3 Observations and Decision

Prepared batching was fastest at every tested size. At 100,000 rows, Statement took 38,550.114 ms, reused PreparedStatement took 34,881.468 ms, and 1,000-row prepared batches took 4,959.373 ms. The batch was 7.77x faster than Statement and 7.03x faster than individual prepared execution because it greatly reduced round trips.

At 10,000 rows, auto-commit required 15,463.552 ms versus 3,957.834 ms for one transaction, a 3.91x improvement. More importantly, the injected negative amount at row 5,001 violated the CHECK constraint, caused rollback, and left zero rows. Independent auto-commit inserts would have left the earlier rows committed.

Engineering decision: use PreparedStatement batches of 1,000 inside one transaction for the nightly import, with validation before binding and a failure report. Use one PreparedStatement in a short transaction for a single counter-issued pass.
