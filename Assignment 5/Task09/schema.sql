CREATE DATABASE IF NOT EXISTS assignment5_test CHARACTER SET utf8mb4;
USE assignment5_test;

CREATE TABLE IF NOT EXISTS bus_pass_migrated (
    pass_id VARCHAR(16) PRIMARY KEY,
    register_number CHAR(9) NOT NULL,
    passenger_name VARCHAR(100) NOT NULL,
    route_number VARCHAR(12) NOT NULL,
    boarding_stop VARCHAR(120) NOT NULL,
    validity_date DATE NOT NULL,
    amount_paid DECIMAL(10,2) NOT NULL CHECK (amount_paid >= 0),
    INDEX idx_migrated_route(route_number),
    INDEX idx_migrated_register(register_number)
) ENGINE=InnoDB;
