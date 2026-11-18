# Task08 - Part B3: JDBC Performance Comparison

- Student: Emad Farooqui
- Register number: 192511019
- Java version: Microsoft OpenJDK 17.0.20
- Database: local MySQL 8.4.5 / InnoDB
- JDBC driver: MySQL Connector/J 9.2.0 (not committed)

## Compile and run

```powershell
mysql -h 127.0.0.1 -P 3307 -u assignment5 -p assignment5_test < Task08/schema.sql
javac -cp path/to/mysql-connector-j.jar -d build Task08/PassLoader.java
java -cp "build;path/to/mysql-connector-j.jar" PassLoader
```

The three supplied completion points in `PassLoader` are implemented as parameter binding, `addBatch()`, and `executeBatch()` every 1,000 rows plus the remainder. No placeholder remains.

## Approach comparison

- `Statement` rebuilds and parses SQL for every generated row and is unsafe for untrusted values.
- reused `PreparedStatement` separates values, supports type-safe binding and can reuse server/client preparation work.
- `PreparedStatement` batch reduces client/server round trips; Connector/J uses `rewriteBatchedStatements=true` for the experiment.
- one transaction avoids a durability flush for each row and gives atomic failure behaviour. Auto-commit commits every insert independently and leaves earlier rows after a later failure.

For a nightly import of 100,000 validated passes, use prepared batches of about 1,000 inside one transaction with a rejected-record report. For one counter-issued pass, use one PreparedStatement in a short transaction; batching would only add latency.

Multi-row INSERT can approach rewritten-batch performance but requires dynamic placeholder construction and packet-size management. `LOAD DATA` is usually best for trusted, clean bulk files and exposes file/privilege/validation concerns. Prepared batches are the balanced Java/JDBC choice because they retain parameter safety, row validation and transaction control.

All strategies are O(n) time for n inserted rows. Batch memory is O(b) for batch size b; single-row Statement/Prepared processing is O(1) application memory. The database stores O(n) rows and maintains indexes in implementation-dependent O(log n) per insert.
