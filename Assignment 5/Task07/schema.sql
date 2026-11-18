CREATE DATABASE IF NOT EXISTS assignment5_test CHARACTER SET utf8mb4;
USE assignment5_test;

DROP TABLE IF EXISTS app_user;
CREATE TABLE app_user (
    username VARCHAR(80) PRIMARY KEY,
    password_text VARCHAR(120) NOT NULL
) ENGINE=InnoDB;

-- Plaintext is used only to isolate the SQL-injection experiment.
-- Production authentication must store a salted slow password hash.
INSERT INTO app_user(username, password_text)
VALUES ('emad', 'correct-test-password');
