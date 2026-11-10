package parking.model;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

public abstract class Vehicle {
    private final String registrationNumber;

    protected Vehicle(String registrationNumber) {
        if (registrationNumber == null || registrationNumber.isBlank()) {
            throw new IllegalArgumentException("registration number is required");
        }
        this.registrationNumber = registrationNumber.trim().toUpperCase();
    }

    public final String getRegistrationNumber() {
        return registrationNumber;
    }

    protected abstract double ratePerHour();

    public double calculateFee(int minutes) {
        if (minutes < 0) {
            throw new IllegalArgumentException("minutes cannot be negative");
        }
        int startedHours = (minutes + 59) / 60;
        return startedHours * ratePerHour();
    }

    public final double calculateFee(LocalTime in, LocalTime out) {
        long minutes = Duration.between(in, out).toMinutes();
        if (minutes < 0) {
            minutes += 24 * 60;
        }
        return calculateFee(Math.toIntExact(minutes));
    }

    @Override
    public final boolean equals(Object other) {
        return other instanceof Vehicle vehicle
                && registrationNumber.equals(vehicle.registrationNumber);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(registrationNumber);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + registrationNumber + "]";
    }
}
