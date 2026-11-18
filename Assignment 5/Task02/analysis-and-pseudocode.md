# A2 Buffered I/O Analysis

## Prediction

Using the binary definition, 50 MiB = `50 x 1,048,576 = 52,428,800` bytes. Calling `read()` once per byte makes 52,428,800 successful Java method calls and one final EOF call. If the underlying stream is unbuffered, these calls can translate into very many native reads. Repeated Java/native transitions, bounds checks and tiny transfers dominate the useful newline comparison.

With a 64 KiB byte array, `52,428,800 / 65,536 = 800` full blocks. A final read detects EOF, so roughly 801 high-level bulk-read invocations replace over 52 million one-byte invocations.

## Algorithm A2.1 - COUNT_LINES_BUFFERED

**Input:** `filePath`, positive `bufferSize`  
**Output:** `lineCount`  
**Precondition:** the file is a byte-oriented log where LF terminates a line.  
**Postcondition:** every LF is counted once; a non-empty final unterminated line is also counted.

```text
ALGORITHM COUNT_LINES_BUFFERED(filePath, bufferSize)
BEGIN
  1. IF FILE_EXISTS(filePath) = FALSE THEN
       RETURN STORE_NOT_FOUND
     END IF
  2. CREATE byteArray[bufferSize]
  3. SET lineCount <- 0
  4. SET bytesRead <- 0
  5. SET sawAnyByte <- FALSE
  6. SET lastByte <- EOF
  7. OPEN filePath FOR BUFFERED_BINARY_READ
  8. WHILE (bytesRead <- READ_BLOCK(filePath, byteArray)) != EOF DO
       8.1 SET sawAnyByte <- TRUE
       8.2 FOR index <- 0 TO bytesRead - 1 DO
             SET lastByte <- byteArray[index]
             IF lastByte = LF THEN
               SET lineCount <- lineCount + 1
             END IF
           END FOR
     END WHILE
  9. CLOSE filePath
 10. IF sawAnyByte = TRUE AND lastByte != LF THEN
       SET lineCount <- lineCount + 1
     END IF
 11. RETURN lineCount
END
```

Time is O(n) for n bytes and auxiliary space is O(b) for buffer size b.

## Why buffering helps

The operating system reads storage in blocks. Buffering amortises the fixed cost of system calls and stream dispatch across thousands of bytes, improves sequential read-ahead and lets the CPU inspect data already in memory. It does not change O(n), but substantially reduces the constant factor.

## When buffering gives little benefit

If the data is already held in a `ByteArrayInputStream`, no device or kernel transition occurs; an additional buffer only copies or adds method layers. It also offers little benefit when a tiny file is read once because startup/open costs dominate.

## Buffered stream versus Files.readAllLines

| Property | Buffered streaming | `Files.readAllLines()` |
|---|---|---|
| Time | O(n) | O(n) plus decoding/object creation |
| Auxiliary memory | O(b) | O(n) characters plus String/List overhead |
| First result | available while reading | only after complete load |
| Very large file | bounded memory | may cause GC pressure or OutOfMemoryError |
| Random reprocessing | requires another pass | lines already available in memory |

For a 50 MiB log that only needs a count, buffered streaming is the correct engineering choice.
