# Campus Smart-Parking System - Java Assignment 1 (CO1)

## Student details

- Name: **Emad Farooqui**
- Register number: **REPLACE_WITH_REGISTER_NUMBER**
- Java version used: Microsoft OpenJDK 17.0.20
- GitHub repository: **TO_BE_ADDED_AFTER_REPOSITORY_IS_PROVIDED**

This repository-ready submission contains one folder for every task, both completed-code exercises, the complete original system, the adapted Day-4 design, test output, screenshots, UML, pseudocode, and complexity analyses. Replace the two student-detail placeholders before submission.

## Quick verification

Run these commands from the repository root in PowerShell:

```powershell
Set-Location Task03
javac ParkingFacility.java
java ParkingFacility

Set-Location ..\Task08
javac FeeDemo.java
java FeeDemo

Set-Location ..\Task11
New-Item -ItemType Directory -Force build | Out-Null
$sources = (Get-ChildItem src -Recurse -Filter *.java).FullName
javac -d build $sources
java -cp build parking.app.ParkingSystemTest

Set-Location ..\Task12
New-Item -ItemType Directory -Force build | Out-Null
$sources = (Get-ChildItem src -Recurse -Filter *.java).FullName
javac -d build $sources
java -cp build parking.app.AdaptedSystemTest
```

## Task results

| Task | Result |
|---|---|
| 1 | Objects, responsibilities, rules, assumptions, and parallel-array comparison documented |
| 2 | Types, jagged-array representation, casting risk, and alternatives analysed |
| 3 | Allocation and fee pseudocode written; supplied program completed and executed |
| 4 | Constructors, chaining, factories, copy decision, and memory model documented |
| 5 | Static ticket sequence and object lifecycle documented and implemented |
| 6 | Vehicle/user inheritance, UML, `super`, and constructor trace supplied |
| 7 | Overriding, `final`, and Object-method contracts implemented and evaluated |
| 8 | Overloading and runtime polymorphism demonstrated; supplied program executed |
| 9 | Abstract class and interfaces implemented and compared with the reversed design |
| 10 | Packages, access modifiers, validation, wrappers, and autoboxing risks documented |
| 11 | Complete original system implemented and tested, including invalid/boundary cases |
| 12 | Design adapted for e-bikes, delivery vans, one-day passes, and five jagged floors |

See [Assignment_Report.md](Assignment_Report.md) for the full evidence and `evidence/` for captured outputs and screenshots.
