# Q4 - Pharmacy Inventory Maps

`HashMap` has average `O(1)` put/get and unordered `O(n)` iteration. `TreeMap` has `O(log n)` put/get and sorted `O(n)` iteration. `LinkedHashMap` has average `O(1)` put/get plus insertion-order `O(n)` iteration, with linked-order overhead.

At 10,000 medicines, measured lookup averages were 50 ns (HashMap), 285 ns (TreeMap), and 85 ns (LinkedHashMap), all below the defined 1 ms interactive threshold. Code-report averages were 5,256,240 ns from HashMap with sorting, 681,380 ns from TreeMap, and 1,057,460 ns from LinkedHashMap with sorting.

Deploy a HashMap for the dominant lookup path and maintain explicit ordered views when reports are frequent (the Q7 redesign). For a simple single-map system, TreeMap naturally serves code reports, while LinkedHashMap naturally serves insertion reports. Hashtable is legacy, synchronizes every operation, forbids nulls, and lacks the modern composition/concurrency choices of HashMap or ConcurrentHashMap.

A sorted array gives `O(log n)` binary search and `O(n)` insertion, produces code order in `O(n)`, and uses compact `O(n)` space. It is attractive for read-only snapshots, not continuously updated stock.
