package parking.service;

import parking.model.Car;
import parking.model.ElectricCar;
import parking.model.TwoWheeler;
import parking.model.Vehicle;

public final class VehicleFactory {
    private VehicleFactory() { }

    public static Vehicle create(String type, String registrationNumber) {
        return switch (type.toLowerCase()) {
            case "two-wheeler", "twowheeler" -> new TwoWheeler(registrationNumber);
            case "car" -> new Car(registrationNumber);
            case "electric-car", "electriccar", "ev" -> new ElectricCar(registrationNumber);
            default -> throw new IllegalArgumentException("unknown vehicle type: " + type);
        };
    }
}
