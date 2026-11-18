# A3 Generic DAO Design

## Common and variable steps

StudentDB, CourseDB and MarksDB all obtain a connection, prepare SQL, bind values, execute, traverse results, map rows, close resources and translate SQL errors. They differ in table/column names, SQL text, key type, value bindings, validation and row-to-object mapping.

## Generic contract

```text
INTERFACE Dao<T, K>
  insert(entity: T) RETURNS T
  findById(key: K) RETURNS OPTIONAL<T>
  findAll() RETURNS LIST<T>
  update(entity: T) RETURNS BOOLEAN
  delete(key: K) RETURNS BOOLEAN
END INTERFACE
```

`StudentDao IMPLEMENTS Dao<Student, String>` because the register number is the stable primary key.

## Object-to-table mapping

| Student field | `student` column | SQL type | Constraint |
|---|---|---|---|
| `registerNumber` | `register_number` | `CHAR(9)` | PRIMARY KEY |
| `name` | `name` | `VARCHAR(100)` | NOT NULL |
| `email` | `email` | `VARCHAR(160)` | NOT NULL UNIQUE |
| `departmentCode` | `department_code` | `VARCHAR(12)` | NOT NULL, FK candidate |
| `admissionYear` | `admission_year` | `SMALLINT` | NOT NULL, checked range |

## Algorithm A3.1 - STUDENT_DAO_FIND_BY_ID

**Input:** `registerNumber`  
**Output:** optional Student  
**Postcondition:** resources are closed whether the lookup succeeds or fails.

```text
ALGORITHM STUDENT_DAO_FIND_BY_ID(registerNumber)
BEGIN
  1. VALIDATE registerNumber AS nine digits
  2. SET sql <- "SELECT register_number, name, email, department_code, admission_year
                  FROM student WHERE register_number = ?"
  3. OPEN connection FROM dataSource
  4. PREPARE sql AS statement
  5. BIND registerNumber TO parameter 1
  6. EXECUTE_QUERY statement AS resultSet
  7. IF resultSet HAS_NEXT_ROW THEN
       SET student <- MAP_CURRENT_ROW_TO_STUDENT(resultSet)
       CLOSE resultSet, statement, connection
       RETURN OPTIONAL_OF(student)
     ELSE
       CLOSE resultSet, statement, connection
       RETURN EMPTY_OPTIONAL
     END IF
EXCEPTION SQLException error
  8. CLOSE all opened resources
  9. THROW DataAccessException(error)
END
```

## Algorithm A3.2 - STUDENT_DAO_INSERT

```text
ALGORITHM STUDENT_DAO_INSERT(student)
BEGIN
  1. VALIDATE all student fields
  2. SET sql <- "INSERT INTO student
                  (register_number, name, email, department_code, admission_year)
                  VALUES (?, ?, ?, ?, ?)"
  3. OPEN connection AND PREPARE sql
  4. BIND student fields TO parameters 1 THROUGH 5
  5. SET affectedRows <- EXECUTE_UPDATE
  6. CLOSE statement AND connection
  7. IF affectedRows != 1 THEN
       THROW DataAccessException("Exactly one row was expected")
     END IF
  8. RETURN student
END
```

## Pattern comparison and complexity

A DAO keeps persistence details out of GUI/service classes, supports dependency injection and test doubles, centralises resource/error policy, and makes database migrations localised. Direct SQL can be shorter for a one-off script, but it couples presentation/business logic to schema details, duplicates security/resource code and is harder to test. The generic interface should not force unlike entities into identical SQL; entity-specific queries can extend the base contract.

Primary-key lookup is O(log n) with a B-tree index (or implementation-dependent near O(1) for a hash index); `findAll` is O(n) time and O(n) returned space.
