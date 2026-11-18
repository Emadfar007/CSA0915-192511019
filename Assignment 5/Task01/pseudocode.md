# SIMATS Engineering - Standard Algorithmic Pseudocode

## Algorithm A1.1 - ADD_PASS

**Purpose:** Append one valid, non-duplicate bus pass safely.  
**Input:** `filePath`, `newPass`  
**Output:** `SUCCESS` or a typed error  
**Preconditions:** `newPass` contains all seven logical fields.  
**Postconditions:** Exactly one complete line is committed, or the original file is unchanged.

```text
ALGORITHM ADD_PASS(filePath, newPass)
BEGIN
  1. IF VALIDATE_PASS(newPass) = FALSE THEN
       RETURN INVALID_PASS
     END IF
  2. IF FILE_EXISTS(filePath) = TRUE AND FIND_PASS_BY_ID(filePath, newPass.passId) != NOT_FOUND THEN
       RETURN DUPLICATE_ID
     END IF
  3. SET tempPath <- filePath + ".tmp"
  4. OPEN filePath FOR BUFFERED_READ IF IT EXISTS
  5. OPEN tempPath FOR BUFFERED_WRITE WITH UTF-8
  6. COPY every existing validated line to tempPath
  7. SET payload <- ENCODE_FIELDS(newPass)
  8. SET line <- payload + "|" + CRC32(payload)
  9. WRITE line AND NEWLINE TO tempPath
 10. FLUSH tempPath AND FORCE_TO_STABLE_STORAGE(tempPath)
 11. CLOSE all streams
 12. ATOMIC_REPLACE(tempPath, filePath)
 13. RETURN SUCCESS
EXCEPTION I/O_ERROR
 14. CLOSE any open stream
 15. DELETE tempPath IF it is incomplete
 16. RETURN STORE_ERROR
END
```

## Algorithm A1.2 - FIND_PASS_BY_ID

**Input:** `filePath`, `targetId`  
**Output:** matching `Pass`, `NOT_FOUND`, or `STORE_NOT_FOUND`  
**Invariant:** all complete lines before the current line have been checked.

```text
ALGORITHM FIND_PASS_BY_ID(filePath, targetId)
BEGIN
  1. IF FILE_EXISTS(filePath) = FALSE THEN
       RETURN STORE_NOT_FOUND
     END IF
  2. OPEN filePath FOR BUFFERED_READ WITH UTF-8
  3. SET lineNumber <- 0
  4. WHILE HAS_NEXT_LINE(filePath) = TRUE DO
       4.1 SET line <- READ_LINE(filePath)
       4.2 SET lineNumber <- lineNumber + 1
       4.3 IF line IS BLANK OR line STARTS_WITH "#" THEN
             CONTINUE
           END IF
       4.4 SET parseResult <- PARSE_AND_VALIDATE(line)
       4.5 IF parseResult IS INVALID THEN
             LOG(lineNumber, parseResult.reason)
             CONTINUE
           END IF
       4.6 IF parseResult.pass.passId = targetId THEN
             CLOSE filePath
             RETURN parseResult.pass
           END IF
     END WHILE
  5. CLOSE filePath
  6. RETURN NOT_FOUND
END
```

## Algorithm A1.3 - LIST_PASSES_ON_ROUTE

**Input:** `filePath`, `targetRoute`  
**Output:** ordered list `matches`  
**Postcondition:** every valid record on `targetRoute` appears exactly once.

```text
ALGORITHM LIST_PASSES_ON_ROUTE(filePath, targetRoute)
BEGIN
  1. SET matches <- EMPTY_LIST
  2. IF FILE_EXISTS(filePath) = FALSE THEN
       RETURN STORE_NOT_FOUND
     END IF
  3. OPEN filePath FOR BUFFERED_READ WITH UTF-8
  4. FOR EACH line IN filePath DO
       4.1 IF line IS BLANK OR line STARTS_WITH "#" THEN
             CONTINUE
           END IF
       4.2 SET parseResult <- PARSE_AND_VALIDATE(line)
       4.3 IF parseResult IS INVALID THEN
             LOG_CURRENT_LINE(parseResult.reason)
             CONTINUE
           END IF
       4.4 IF parseResult.pass.routeNumber = targetRoute THEN
             APPEND parseResult.pass TO matches
           END IF
     END FOR
  5. CLOSE filePath
  6. RETURN matches
END
```

Both search and route listing take O(n) time and O(1) auxiliary space excluding returned matches; route listing returns O(r) output space.
