# Q5 - Generic Ranking Component

Separate Comparator objects are chosen because a team can have multiple legitimate orders; embedding one natural order in Comparable would be less reusable. The chain orders points descending, goal difference descending, then name ascending. `Leaderboard<T>` accepts `Comparator<? super T>` and `addAll(List<? extends T>)`, then returns a sorted copy so internal insertion data is unchanged.

The required output matches exactly. Bisons precedes Panthers because their points and goal difference tie, then `"Bisons"` is alphabetically before `"Panthers"`.

The comparator is transitive because it is a lexicographic composition of transitive comparisons; equality occurs only when all three compared keys tie. A subtraction comparator failed on integer overflow (`MAX` incorrectly ranked below `MIN`), proving why `comparingInt` is safer.

One comparator-chain sort is `O(n log n)` and `O(n)` copy space. Three stable sorts are still `O(n log n)` asymptotically but perform three sorting passes and depend on least-significant-first ordering. Comparable also takes one sort but couples one order to Team. The generic comparator design is easiest to reuse for players and colleges.
