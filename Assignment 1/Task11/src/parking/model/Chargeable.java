package parking.model;

public interface Chargeable {
    double chargingFee(int kWh);

    static void requireNonNegative(int kWh) {
        if (kWh < 0) {
            throw new IllegalArgumentException("kWh cannot be negative");
        }
    }
}
