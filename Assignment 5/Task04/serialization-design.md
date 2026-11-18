# A4 Serialization Design

## Object graph

```text
Hostel
  -> List<Room>
       -> List<Resident>
            -> name
            -> registerNumber
            -> password (transient)
            -> phoneNumber (version 2)
            -> Connection (transient)
```

Every object reachable from Hostel is traversed once and written, provided each non-transient object is serializable. Shared references are recorded by handles, so two rooms referring to the same object do not produce two independent objects. A non-serializable reachable field causes `NotSerializableException` unless it is transient or custom-written.

## Transient decisions

- `password`: transient because serialised plaintext/credential material is a security disclosure. Store a salted password hash in a credential store, not inside the graph.
- live JDBC `Connection`: transient because a connection represents process/socket/session state, is generally not serializable and is invalid after restart. Reconnect through a DataSource after deserialization.
- derived values such as occupancy count may also be transient and recomputed.
- name and register number remain persistent domain data. Phone number may persist if policy permits and privacy controls exist.

## Version evolution

Declare an explicit `private static final long serialVersionUID = 1L`. Adding a compatible non-final `phoneNumber` while keeping the same UID allows old streams to deserialize; the new field receives its Java default (`null`). A migration/validation step can request or populate it. Incompatible structural changes should increment the UID and deliberately reject old streams or use custom `readObject` migration. Without an explicit UID, compiler-generated values can change after seemingly small class edits, causing `InvalidClassException`.

## Algorithm A4.1 - RESTORE_HOSTEL_GRAPH

```text
ALGORITHM RESTORE_HOSTEL_GRAPH(filePath, dataSource)
BEGIN
  1. OPEN filePath FOR BUFFERED_OBJECT_INPUT
  2. READ rootObject
  3. VERIFY rootObject IS Hostel
  4. SET hostel <- rootObject
  5. FOR EACH room IN hostel.rooms DO
       5.1 FOR EACH resident IN room.residents DO
             SET resident.connection <- NULL
             VALIDATE resident.registerNumber
             IF resident.phoneNumber IS NULL THEN
               MARK resident AS REQUIRING_PROFILE_UPDATE
             END IF
           END FOR
     END FOR
  6. CLOSE input
  7. ATTACH dataSource TO hostel services, NOT to Resident objects
  8. RETURN hostel
EXCEPTION InvalidClassException OR StreamCorruptedException
  9. CLOSE input
 10. RETURN INCOMPATIBLE_OR_DAMAGED_FILE
END
```

Deserializing v objects and references requires O(v) time and O(v) reconstructed memory.

## Serialization versus CSV and JSON

| Property | Java serialization | CSV | JSON |
|---|---|---|---|
| Object graph/references | automatic | manual/flat | nested but shared identity manual |
| Human-readable | no | yes | yes |
| Language portability | poor | high | high |
| Version evolution | fragile/UID-based | parser policy | tolerant field-based parsers |
| Security | unsafe for untrusted streams | parser validation required | parser validation required |
| Type fidelity | Java-specific | limited | moderate |

For durable interoperable data, JSON with an explicit schema/version is usually preferable. Native serialization is acceptable only for trusted, short-lived Java-only snapshots with controlled classes.
