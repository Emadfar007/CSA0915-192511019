# Assignment 2 - Strings and Collections (CO2)

- Student name: **REPLACE_WITH_STUDENT_NAME**
- Register number: **192511019**
- Java: Microsoft OpenJDK 17.0.20
- Repository: https://github.com/Emadfar007/CSA0915-192511019
- Location: `Assignment 2/`

This folder contains the seven required question folders, completed-code programs, investigation/benchmark programs, raw repeated measurements, screenshots, and the assignment report.

## Compile and run

Run from inside `Assignment 2` in PowerShell:

```powershell
foreach ($q in 1..7) {
  Push-Location ("Q" + $q)
  javac *.java
  Pop-Location
}

java -cp Q1 TicketTextBenchmark
java -cp Q2 PlaylistBenchmark
java -cp Q3 Attendance
java -cp Q3 ContractInvestigation
java -cp Q4 PharmacyBenchmark
java -cp Q5 LeaderboardDemo
java -cp Q5 RankingInvestigation
java -cp Q6 Triage
java -cp Q6 TriageInvestigation
java -cp Q7 InventoryRedesignBenchmark
```

## Results summary

| Question | Result |
|---|---|
| Q1 | Three workloads measured; `StringBuilder` recommended per ticket, synchronized `StringBuffer` for a truly shared mutable log |
| Q2 | Three playlist sizes measured; `ArrayList` for read/random-access playlists, `ArrayDeque` for Up Next |
| Q3 | Broken and corrected equality contracts tested; completed output is `Unique students: 2` |
| Q4 | Three maps measured at three sizes; deployment recommendation uses hash lookup plus explicit ordered report views |
| Q5 | Generic leaderboard and comparator chain completed; overflow/tie tests passed |
| Q6 | Triage completed output matched; eight-patient hand order and growth tests passed |
| Q7 | Maintained indexes removed repeated sorting; 3.74x to 12.45x report speedup measured |

Raw evidence is in `raw-data/`; rendered console evidence is in `evidence/`.
