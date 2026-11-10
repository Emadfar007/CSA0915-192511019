package parking.model;

public class Car extends Vehicle {
    public Car(String registrationNumber) {
        super(registrationNumber);
    }

    @Override
    protected double ratePerHour() {
        return 30.0;
    }
}
