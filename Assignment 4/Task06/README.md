# Task 06 - Campus Event Registration

## Users, screens, data and rules

- Student: enters register number/name, selects event, receives success/error.
- Committee member: filters a paginated table and exports CSV.
- Data: events, capacity/date, registration identity, student identity and timestamp.
- Rules: nine-digit registration; valid name; existing event; no duplicate `(register_number,event_id)`; capacity enforced; database work off EDT.

## Layered class structure

```text
EventRegistrationApp (Swing UI + SwingWorker)
            |
RegistrationService (validation, duplicate policy, pagination)
            |
RegistrationDao interface
       /                    \
MemoryRegistrationDao      JdbcRegistrationDao
       |                    |
 demo/test lists            MySQL + PreparedStatement
```

## Screen/dialog flow

```text
Menu + Tabbed Window
  -> Student Registration tab -> validate -> background service -> Success/Error status
  -> Committee View tab -> filter -> SwingWorker -> JTable -> Export CSV
```

## Register pseudocode

```text
REGISTER(regNo, name, event)
  validate regNo, name and event in service
  IF dao.exists(regNo,event.id): return duplicate error
  TRY dao.insert using prepared statement
  CATCH unique-constraint violation: return duplicate error
  return immutable Registration
```

## Filter pseudocode

```text
ON filter selected
  disable Refresh; show Loading
  SwingWorker.doInBackground:
    dao.findByEvent(eventId, pageSize, page*pageSize)
  SwingWorker.done on EDT:
    replace table model; enable Refresh; show count/error
```

`schema.sql` creates foreign keys, a unique student-event constraint, and `(event_id,registered_at)` index. Prepared statements prevent SQL injection and try-with-resources closes JDBC objects. The demo proves normal, invalid and duplicate cases with the actual service layer.

Loading 100,000 rows is `O(n)` time and memory and estimated 6.4 MB for simplified 64-byte rows (real JTable/JDBC objects cost more). Loading 100 rows uses about 6.4 KB, a 1000x reduction, and returns control sooner. `LIMIT/OFFSET` is simple but deep offsets may scan discarded rows; keyset pagination (`registered_at,id < lastSeen`) is preferable at scale. Duplicate lookup is average `O(1)` in the demo and indexed `O(log n)` in MySQL.
