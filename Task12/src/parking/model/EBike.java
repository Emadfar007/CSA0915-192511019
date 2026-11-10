package parking.model;

import parking.pricing.FlatPricing;

public final class EBike extends Vehicle {
    public EBike(String registrationNumber) {
        super(registrationNumber, new FlatPricing(25));
    }
}
