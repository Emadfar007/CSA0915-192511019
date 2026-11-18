# Task01 - Part A1: Design a File Format

- Student: Emad Farooqui
- Register number: 192511019
- Java version: Microsoft OpenJDK 17.0.20

This folder contains the two required record layouts, SIMATS-style pseudocode for add/find/list operations, failure handling and the comparison with fixed-length `RandomAccessFile` records. Part A is intentionally design and pseudocode only.

Files:

- `record-layouts.md` - delimited text and `DataOutputStream` binary formats.
- `pseudocode.md` - three required algorithms.
- `input/sample-pass-records.txt` - example logical records.

Result: the design rejects damaged records, distinguishes missing files from empty stores, and protects replacement writes with a temporary file plus atomic rename.
