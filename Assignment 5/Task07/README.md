# Task07 - Part B2: Adversarial Input

- Student: Emad Farooqui
- Register number: 192511019
- Java version: Microsoft OpenJDK 17.0.20
- Test database: local MySQL 8.4.5, database `assignment5_test`

## Compile and run

```powershell
javac -cp path/to/mysql-connector-j.jar -d build Task07/*.java
java -cp "build;path/to/mysql-connector-j.jar" LoginSecurityDemo
java -cp build AdversarialPassDemo Task07/input/adversarial-passes.txt
```

The test-only unsafe query concatenates input and is deliberately vulnerable. A crafted username changes its boolean expression and accepts an invalid login. The PreparedStatement sends the exact same text as a parameter and rejects it. This demonstrates the security property against the isolated local database only; production systems must additionally use salted password hashing.

The damaged-pass cases reveal three missing validations in a naive parser: structural field count, numeric-domain validation and explicit blank-line policy. Rejecting each record locally lets a batch continue without accepting corrupt data.

Both login methods execute one indexed lookup in the intended case. Application work is O(1); the database primary-key lookup is generally O(log n). PreparedStatement adds negligible application memory while preserving query structure and enabling plan reuse.
