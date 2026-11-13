# Q6 - Priority, Order and Fairness

A FIFO queue cannot select a later critical patient. The PriorityQueue comparator uses severity ascending, then a monotonically increasing arrival counter. Thus equal-severity patients are FIFO. `poll()` removes the correct next patient in `O(log n)`; admission is `O(log n)`. Printing the heap directly reveals heap-array order, not complete sorted treatment order; a copied queue must be repeatedly polled for display.

The eight-patient hand order `P1 P3 P5 P7 P0 P6 P2 P4` matched exactly. Measurements at 1,000, 5,000 and 20,000 patients remained sub-microsecond per operation after warm-up on the test machine. This is easily adequate for 5,000 daily patients; persistence, concurrency and clinical validation would dominate deployment concerns.

Starvation occurs because an unlimited stream of severities 1-4 can always outrank severity 5. Fair rule: after every 30 waiting minutes, improve effective severity by one level (never above 1), then rebuild/re-evaluate before selection. That bounds waiting at the cost of periodic `O(n)` recalculation and requires the policy to be clinically approved.

A sorted LinkedList admits in `O(n)` and serves in `O(1)`. Five FIFO severity queues give `O(1)` admission and `O(1)` service by checking at most five fixed queues, and are an excellent specialised alternative. PriorityQueue is more general and uses `O(n)` references.
