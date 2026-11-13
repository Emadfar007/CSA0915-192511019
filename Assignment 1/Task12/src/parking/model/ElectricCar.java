package parking.model;

import parking.pricing.HourlyPricing;

public final class ElectricCar extends Vehicle implements Chargeable {
    public ElectricCar(String registrationNumber) {
        super(registrationNumber, new HourlyPricing(30));
    }

    @Override
    public double chargingFee(int kWh) {
        if (kWh < 0) throw new IllegalArgumentException("kWh cannot be negative");
        return kWh * 12.0;
    }
}
