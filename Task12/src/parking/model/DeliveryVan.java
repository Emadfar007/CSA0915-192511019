package parking.model;

import parking.pricing.DeliveryVanPricing;

public final class DeliveryVan extends Vehicle {
    public DeliveryVan(String registrationNumber) {
        super(registrationNumber, new DeliveryVanPricing(30));
    }
}
