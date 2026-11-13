# Q1 - Building Text Under Load

Prediction: repeated `String +` in a loop creates a new immutable result after each append and repeatedly copies the growing prefix, so its copied-character work approaches `O(n^2)`. `StringBuilder` and `StringBuffer` use a growable array and are `O(n)` amortised; Buffer pays synchronization overhead.

Three workloads were tested: one 10-field ticket, 1,000 tickets x 50 fields, and 200 wide tickets x 200 fields. Five timed retained-result runs followed two warm-ups. The wide workload measured 13,147,440 ns for String, 2,593,200 ns for Builder and 3,616,780 ns for Buffer. Estimated intermediate objects were 120,000 versus 400.

For per-ticket text, use a thread-confined `StringBuilder`. For a genuinely shared append-only log, use `StringBuffer` or, preferably in production, a logging framework/queue with one writer. Synchronizing a Builder externally is valid but easier to misuse.

`String.join`/`StringJoiner` are also linear when fields already exist. `String.format` is readable but parsing/formatting adds overhead. Repeated `+` is acceptable only for tiny fixed expressions that `javac` can combine, not an expanding loop.
