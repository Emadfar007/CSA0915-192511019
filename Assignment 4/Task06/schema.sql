CREATE DATABASE IF NOT EXISTS campus_events;
USE campus_events;

CREATE TABLE events (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL UNIQUE,
    capacity INT NOT NULL CHECK (capacity > 0),
    event_date DATE NOT NULL
);

CREATE TABLE registrations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    register_number CHAR(9) NOT NULL,
    student_name VARCHAR(120) NOT NULL,
    event_id INT NOT NULL,
    registered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_registration_event FOREIGN KEY (event_id) REFERENCES events(id),
    CONSTRAINT uq_student_event UNIQUE (register_number, event_id),
    INDEX idx_event_registered (event_id, registered_at)
);

INSERT INTO events(name, capacity, event_date) VALUES
('Code Sprint', 120, '2026-12-05'),
('Robotics Expo', 80, '2026-12-06'),
('Design Challenge', 100, '2026-12-07');

-- Paginated committee view (bind event id, page size and offset):
SELECT r.register_number, r.student_name, e.name, r.registered_at
FROM registrations r
JOIN events e ON e.id = r.event_id
WHERE e.id = ?
ORDER BY r.registered_at DESC
LIMIT ? OFFSET ?;
