# Assignment 5 - Data Persistence Engineering

- Student: **Emad Farooqui**
- Register number: **192511019**
- Java: **Microsoft OpenJDK 17.0.20**
- Database used for experiments: **MySQL 8.4.5 / InnoDB**
- JDBC driver: **MySQL Connector/J 9.2.0**
- Repository: https://github.com/Emadfar007/CSA0915-192511019
- Folder: `Assignment 5/`

## Task map

| Folder | Faculty task | Deliverable |
|---|---|---|
| Task01 | A1 | text/binary record layouts, three SIMATS algorithms, failure handling |
| Task02 | A2 | buffered-I/O prediction, redesign, limitation and complexity |
| Task03 | A3 | generic DAO, StudentDao algorithms and object/table mapping |
| Task04 | A4 | serialization graph, transient fields, versioning and alternatives |
| Task05 | A5 | transaction pseudocode, SQL-injection analysis and PreparedStatement design |
| Task06 | B1 | file store, damaged-data tests and required size experiments |
| Task07 | B2 | unsafe/prepared login and adversarial pass data |
| Task08 | B3 | Statement/Prepared/Batch performance, metadata and rollback |
| Task09 | B4 | validated batch migration, metadata and final engineering decision |

Part A contains design and SIMATS Engineering algorithmic pseudocode—not Java substitutes. Part B contains executable Java, SQL, inputs, raw outputs, measurements, explanations and genuine screenshots rendered from those outputs.

## Compile

From `Assignment 5` in PowerShell:

```powershell
New-Item -ItemType Directory -Force build | Out-Null
javac -encoding UTF-8 -d build Task06/*.java
javac -encoding UTF-8 -cp path/to/mysql-connector-j.jar -d build Task07/*.java
javac -encoding UTF-8 -cp path/to/mysql-connector-j.jar -d build Task08/*.java
javac -encoding UTF-8 -cp path/to/mysql-connector-j.jar -d build Task09/*.java
```

## Execute

```powershell
java -cp build PassStoreDemo Task06/input/passes-damaged.txt
java -cp build PerformanceExperiment experiment-data

$env:A5_DB_URL = "jdbc:mysql://127.0.0.1:3307/assignment5_test?rewriteBatchedStatements=true"
$env:A5_DB_USER = "assignment5"
$env:A5_DB_PASSWORD = "your-local-test-password"

java -cp "build;path/to/mysql-connector-j.jar" LoginSecurityDemo
java -cp build AdversarialPassDemo Task07/input/adversarial-passes.txt
java -cp "build;path/to/mysql-connector-j.jar" PassLoader
java -cp build MigrationInputGenerator migration-100000.txt 100000
java -cp "build;path/to/mysql-connector-j.jar" MigrationProgram migration-100000.txt rejected-records.csv
```

Execute `Task07/schema.sql`, `Task08/schema.sql` and `Task09/schema.sql` as required before standalone database runs. Database credentials are read from environment variables; third-party connector binaries and local database files are intentionally not committed.

## Measured results

- 50 MiB line count: 219,106.580 ms byte-at-a-time versus 548.115 ms buffered (399.74x speedup), identical 1,379,706 lines.
- 100 lookups over 100,000 passes: 2,072.836 ms repeated scans versus 0.0762 ms HashMap lookups after a 59.156 ms build.
- 100,000 JDBC inserts: 38,550.114 ms Statement, 34,881.468 ms PreparedStatement and 4,959.373 ms 1,000-row prepared batch.
- 10,000 prepared inserts: 15,463.552 ms auto-commit versus 3,957.834 ms one transaction.
- Forced invalid amount: transaction rolled back; zero rows remained.
- Migration: 100,000 accepted and three rejected records; database row count verified as 100,000.

See `Assignment_5_Report.pdf` for the complete layouts, pseudocode, diagrams, experimental method, screenshots, interpretations, complexity analysis and final engineering decision.
