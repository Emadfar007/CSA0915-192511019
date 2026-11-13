# Q2 - ArrayList Engineering Choice

At sizes 1,000, 10,000 and 50,000, `ArrayList` delivered roughly constant indexed access (149, 141, 135 ns), whereas `LinkedList` middle access grew (1,147, 10,403, 53,649 ns). ArrayList front churn grew to 13,447 ns at 50,000 because elements shift. LinkedList front churn stayed about 218 ns, but locating the middle dominated its middle edits.

Use ArrayList when appending is common, random access and cache-friendly iteration matter, and positional front/middle changes are uncommon. Stop recommending it when frequent front shifts dominate or when stable node insertion is performed through an already-positioned iterator.

For Up Next, `ArrayDeque` supports both ends in amortised `O(1)` without linked-node overhead and is selected. It does not support efficient indexed access or arbitrary middle insertion.

| Operation | ArrayList | LinkedList | ArrayDeque |
|---|---|---|---|
| add end | amortised O(1) | O(1) | amortised O(1) |
| add/remove front | O(n) | O(1) | amortised O(1) |
| middle by index | O(n) shift | O(n) locate | unsupported/linear workaround |
| get k | O(1) | O(n) | unsupported |
| iterate | O(n), compact | O(n), node chasing | O(n), compact |

A plain array matches ArrayList lookup/iteration but manual resizing and shifting remain `O(n)`. ArrayList stores one reference per capacity slot; LinkedList adds node object and two links per element; ArrayDeque stores references in a circular array.
