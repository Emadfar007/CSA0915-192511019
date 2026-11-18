# A5 Correctness and Safety

## Algorithm A5.1 - TRANSFER_WALLET_FUNDS

**Purpose:** transfer 500.00 rupees (50,000 paise) without loss or creation.  
**Input:** `hostelWalletId`, `canteenWalletId`, `amountPaise = 50000`  
**Output:** `SUCCESS`, `INSUFFICIENT_FUNDS`, or `TRANSFER_FAILED`  
**Invariant:** total value across both wallets is unchanged after commit or rollback.

```text
ALGORITHM TRANSFER_WALLET_FUNDS(hostelWalletId, canteenWalletId, amountPaise)
BEGIN
  1. OPEN connection
  2. SET connection.autoCommit <- FALSE
  3. SET connection.isolation <- SERIALIZABLE
  4. TRY
       4.1 SELECT balance_paise FROM wallet
           WHERE wallet_id = hostelWalletId FOR UPDATE
       4.2 IF source row is missing OR balance_paise < amountPaise THEN
             ROLLBACK connection
             RETURN INSUFFICIENT_FUNDS
           END IF
       4.3 UPDATE wallet
           SET balance_paise = balance_paise - amountPaise
           WHERE wallet_id = hostelWalletId
       4.4 VERIFY exactly one source row was updated
       4.5 UPDATE wallet
           SET balance_paise = balance_paise + amountPaise
           WHERE wallet_id = canteenWalletId
       4.6 VERIFY exactly one destination row was updated
       4.7 INSERT immutable transfer audit record
       4.8 COMMIT connection
       4.9 RETURN SUCCESS
     CATCH any SQLException
       4.10 ROLLBACK connection
       4.11 RETURN TRANSFER_FAILED
     FINALLY
       4.12 RESTORE autoCommit AND CLOSE connection
     END TRY
END
```

The work is O(1) SQL statements. Indexed wallet lookup is O(log n) in the general B-tree model, and auxiliary application memory is O(1).

## Unsafe login analysis

Unsafe construction such as:

```text
"SELECT id FROM users WHERE username='" + user + "' AND password='" + password + "'"
```

treats user input as SQL syntax. Crafted input can close the quoted value and add a condition that is always true, possibly commenting out the remainder. The database then evaluates attacker-controlled logic rather than comparing literal credentials. Task07 demonstrates this only against the isolated assignment database.

## Algorithm A5.2 - CHECK_LOGIN_PREPARED

```text
ALGORITHM CHECK_LOGIN_PREPARED(username, passwordChars)
BEGIN
  1. IF username IS BLANK OR passwordChars IS EMPTY THEN
       RETURN REJECTED
     END IF
  2. SET sql <- "SELECT password_hash, password_salt FROM app_user WHERE username = ?"
  3. OPEN connection AND PREPARE sql
  4. BIND username AS A STRING TO parameter 1
  5. EXECUTE_QUERY AS resultSet
  6. IF resultSet HAS NO ROW THEN
       CLOSE resources
       RETURN REJECTED
     END IF
  7. SET candidateHash <- SLOW_PASSWORD_HASH(passwordChars, storedSalt)
  8. SET accepted <- CONSTANT_TIME_EQUALS(candidateHash, storedHash)
  9. CLEAR passwordChars AND candidateHash FROM MUTABLE MEMORY
 10. CLOSE resources
 11. RETURN accepted
END
```

Parameters are sent separately from SQL structure, so metacharacters stay data. Passwords should be slow-hashed and never stored as plaintext; the Part B injection experiment uses obvious test-only values solely to isolate query behaviour.

## One transaction versus two auto-commit updates

With one transaction, both debiting and crediting commit together, and any failure rolls both back. With two auto-commit statements, the debit may commit before the credit fails, losing money; reversing the order can create money. A transaction also provides a clear isolation boundary for concurrent transfers. Therefore two independent auto-commit updates are unacceptable for a financial invariant.
