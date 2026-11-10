package parking.pricing;

import java.time.LocalTime;

public final class FlatPricing implements PricingRule {
    private final double flatFee;

    public FlatPricing(double flatFee) {
        if (flatFee < 0) throw new IllegalArgumentException("fee cannot be negative");
        this.flatFee = flatFee;
    }

    @Override
    public double calculate(int minutes, LocalTime entryTime) {
        if (minutes < 0) throw new IllegalArgumentException("minutes cannot be negative");
        return minutes == 0 ? 0.0 : flatFee;
    }
}
