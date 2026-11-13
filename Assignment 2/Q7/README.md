# Q7 - Diagnose, Redesign and Validate

## Diagnosis and objective

The Q4 HashMap code-order report copies and sorts all keys every evening. At 10,000 items it took 5,256,240 ns per code report, and repeated reports repeat allocation and `O(n log n)` sorting. The objective is to eliminate report-time sorting, preserve average constant-time lookup, and achieve at least 3x faster repeated code reports at 20,000 medicines.

## Revised design

```text
IndexedInventory
  lookup: HashMap<code, stock>          // fast operational lookup
  codeIndex: TreeMap<code, stock>       // always code ordered
  insertionIndex: LinkedHashMap<code, stock> // always insertion ordered

put(code, stock): update all indexes
lookup(code): use lookup
codeReport(): iterate codeIndex
insertionReport(): iterate insertionIndex
```

This exploits multiple maintained indexes rather than merely replacing one map. It avoids repeated key-list allocation and sorting, while writes update three maps. Prediction: insertions will cost more, report time will become `O(n)`, and speedup will grow with size/report frequency.

## Validation and defence

For 100 code reports, speedup rose from 3.74x at 1,000 items to 6.73x at 5,000 and 12.45x at 20,000, exceeding the objective. At 20,000, original reports used 604,057,400 ns versus 48,513,300 ns redesigned. Insertions increased from 175,052,800 ns to 190,390,700 ns because three indexes are maintained, and memory is approximately three sets of map entries/references.

The redesign is justified when reads/reports dominate and both orders are repeatedly required. The original single HashMap remains preferable for small inventories, infrequent reports, or tight memory budgets.
