# B1 Observations

The byte-at-a-time implementation scales linearly but performs one Java/native read per byte. At 50 MiB it took 219,106.580 ms, while the 64 KiB buffered stream took 548.115 ms for the identical 1,379,706-line result: a 399.74x measured speedup. The asymptotic time remains O(n); buffering changes the constant factor by reducing underlying refills to about 801.

For one cold lookup, scanning avoids map construction and can be adequate. At 100,000 records, building the map cost 59.156 ms, but a lookup took only 4.3 microseconds. For 100 deterministic searches, repeated scans took 2,072.836 ms while map lookups took 0.0762 ms after construction. This evidence supports scan-once for rare access and a HashMap/index for repeated access, matching O(q x n) versus O(n + q).
