package parking.model;

import java.time.LocalTime;
import java.util.Objects;
import parking.pricing.PricingRule;

public abstract class Vehicle {
    private final String registrationNumber;
    private final PricingRule pricingRule;

    protected Vehicle(String registrationNumber, PricingRule pricingRule) {
        if (registrationNumber == null || registrationNumber.isBlank()) {
            throw new IllegalArgumentException("registration number is required");
        }
        this.registrationNumber = registrationNumber.trim().toUpperCase();
        this.pricingRule = Objects.requireNonNull(pricingRule);
    }

    public final String getRegistrationNumber() { return registrationNumber; }

    public final double calculateFee(int minutes, LocalTime entryTime) {
        return pricingRule.calculate(minutes, entryTime);
    }

    @Override
    public final boolean equals(Object other) {
        return other instanceof Vehicle vehicle
                && registrationNumber.equals(vehicle.registrationNumber);
    }

    @Override
    public final int hashCode() { return registrationNumber.hashCode(); }
}
