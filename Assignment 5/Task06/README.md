# Task06 - Part B1: File Store and I/O Experiments

- Student: Emad Farooqui
- Register number: 192511019
- Java version: Microsoft OpenJDK 17.0.20

## Compile and run

```powershell
javac -d build Task06/*.java
java -cp build PassStoreDemo Task06/input/passes-damaged.txt
java -cp build PerformanceExperiment experiment-data
```

`PassStore.find()` contains the completed line-by-line search: it skips blank/damaged records and returns the matching valid record. The implementation follows A1.2 directly: existence check, buffered scan, record-level validation, warning, continue, equality test, return.

The damaged P1002 line has the wrong field count, so it is rejected without terminating the scan; P1003 after it is still found. P1004 demonstrates numeric validation and the empty line is ignored.

## Alternatives and complexity

- Scan per lookup: O(n) time and O(1) auxiliary memory; q searches cost O(q x n).
- HashMap: O(n) startup time, O(n) memory and O(1)-average lookup; q searches cost O(n + q).
- Scanning is preferable for rare queries and minimal memory. The map is preferable for repeated queries over a stable snapshot.

Actual measurements and interpretation are stored in `output/` after execution; large generated test files are intentionally excluded from version control and recreated deterministically.
