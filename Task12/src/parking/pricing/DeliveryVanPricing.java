package parking.pricing;

import java.time.LocalTime;

public final class DeliveryVanPricing implements PricingRule {
    private static final LocalTime PEAK_START = LocalTime.of(9, 0);
    private static final LocalTime PEAK_END = LocalTime.of(17, 0);
    private final double carRate;

    public DeliveryVanPricing(double carRate) {
        this.carRate = carRate;
    }

    @Override
    public double calculate(int minutes, LocalTime entryTime) {
        if (minutes < 0) throw new IllegalArgumentException("minutes cannot be negative");
        int startedHours = (minutes + 59) / 60;
        double total = 0.0;
        for (int hour = 0; hour < startedHours; hour++) {
            LocalTime billableHourStart = entryTime.plusHours(hour);
            boolean peak = !billableHourStart.isBefore(PEAK_START)
                    && billableHourStart.isBefore(PEAK_END);
            total += peak ? carRate * 2 : carRate;
        }
        return total;
    }
}
