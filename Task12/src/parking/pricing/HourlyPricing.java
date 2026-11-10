package parking.pricing;

import java.time.LocalTime;

public final class HourlyPricing implements PricingRule {
    private final double hourlyRate;

    public HourlyPricing(double hourlyRate) {
        if (hourlyRate < 0) throw new IllegalArgumentException("rate cannot be negative");
        this.hourlyRate = hourlyRate;
    }

    @Override
    public double calculate(int minutes, LocalTime entryTime) {
        if (minutes < 0) throw new IllegalArgumentException("minutes cannot be negative");
        return ((minutes + 59) / 60) * hourlyRate;
    }
}
