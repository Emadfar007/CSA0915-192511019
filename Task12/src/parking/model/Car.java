package parking.model;

import parking.pricing.HourlyPricing;

public final class Car extends Vehicle {
    public Car(String registrationNumber) {
        super(registrationNumber, new HourlyPricing(30));
    }
}
