package parking.model;

import parking.pricing.HourlyPricing;

public final class TwoWheeler extends Vehicle {
    public TwoWheeler(String registrationNumber) {
        super(registrationNumber, new HourlyPricing(10));
    }
}
