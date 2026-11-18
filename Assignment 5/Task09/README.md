# Task09 - Part B4: Migration and Final Engineering Decision

- Student: Emad Farooqui
- Register number: 192511019
- Java version: Microsoft OpenJDK 17.0.20
- Database: local MySQL 8.4.5 / InnoDB

## Compile and run

```powershell
javac -cp path/to/mysql-connector-j.jar -d build Task09/*.java
java -cp build MigrationInputGenerator migration-100000.txt 100000
java -cp "build;path/to/mysql-connector-j.jar" MigrationProgram migration-100000.txt rejected-records.csv
```

The migration validates records one at a time, reports bad lines, executes 1,000-row PreparedStatement batches and commits all valid rows in one transaction. Any JDBC failure rolls the database work back. `DatabaseMetaData` reports the product, version, driver and tables.

## Final engineering decision

Use one shared relational database for the 12-campus, roughly 800,000-pass system. Evidence from Task08 shows 100,000 prepared-batch inserts in about five seconds locally and atomic rollback on failure. Indexed searches, constraints, transactions, concurrent sessions, backups and central reporting address the expanded requirements directly.

Delimited text remains useful as an import/export and audit interchange format, but scanning every query is O(n), concurrent rewriting is unsafe without coordination, and cross-campus consistency is difficult. Java serialization is the least suitable durable system of record: it is Java-specific, version-fragile, opaque to operational tools and unsafe for untrusted streams.

One database provides a single source of truth, central constraints, transactions and cross-campus queries. Separate serialized campus files reduce network dependency but create 12 consistency/backup/versioning problems, prevent safe concurrent global search and require custom merge/conflict logic. If connectivity is unreliable, use local queues/caches that synchronise with the shared database rather than independent authoritative files.

Migration is O(n) time and O(b) application memory for batch size b, plus O(r) rejection-report bytes. Indexed database lookup is generally O(log n), while full reporting scans remain O(n).
