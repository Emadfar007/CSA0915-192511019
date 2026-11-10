package parking.model;

public final class ElectricCar extends Car implements Chargeable {
    public ElectricCar(String registrationNumber) {
        super(registrationNumber);
    }

    @Override
    public double chargingFee(int kWh) {
        Chargeable.requireNonNegative(kWh);
        return kWh * 12.0;
    }
}
