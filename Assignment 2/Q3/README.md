# Q3 - hashCode-equals Contract

Test 1 adds the same broken object reference twice: predicted and actual size 1 because HashSet detects the identical stored reference. Test 2 adds two distinct but equal registration objects without hashCode: predicted and actual size 2 in the run because identity hashes place them in different buckets. Test 3 mutates a field used by both equals and hashCode after insertion: `contains` becomes false because lookup searches the new bucket while the object remains in the old bucket.

HashSet computes a hash, chooses a bucket, then calls equals only among candidates in that bucket. Therefore equal objects must have equal hash codes. Identity fields used by either method must be immutable. `Attendance.java` fixes TODO 1 with `regNo.hashCode()` and prints `Unique students: 2`.

A `TreeSet` ordered consistently by registration deduplicates in `O(log n)` per scan and keeps order. A `HashMap<String,Student>` makes the key explicit and gives average `O(1)` scan/update. HashSet is average `O(1)` and compact when only membership is needed. All use `O(n)` space.
