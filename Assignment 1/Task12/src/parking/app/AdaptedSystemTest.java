package parking.app;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import parking.model.DeliveryVan;
import parking.model.EBike;
import parking.model.ElectricCar;
import parking.model.OneDayPass;
import parking.model.Visitor;
import parking.service.ParkingFacility;
import parking.service.ParkingService;

public final class AdaptedSystemTest {
    private static int passed;

    private static void check(String name, double expected, double actual) {
        boolean ok = Double.compare(expected, actual) == 0;
        System.out.printf("%-35s | %8.2f | %8.2f | %s%n", name, expected, actual,
                ok ? "PASS" : "FAIL");
        if (!ok) throw new AssertionError(name);
        passed++;
    }

    public static void main(String[] args) {
        LocalDate day = LocalDate.of(2026, 11, 9);
        ParkingService service = new ParkingService(
                new ParkingFacility(new int[]{2, 3, 1, 4, 2}));
        System.out.println("TEST                                | EXPECTED | ACTUAL   | RESULT");
        System.out.println("------------------------------------------------------------------");

        service.enter(new EBike("TN-E1"), new Visitor("V1"),
                LocalDateTime.of(day, LocalTime.of(8, 0)));
        check("e-bike flat fee", 25.0,
                service.exit("TN-E1", 125, LocalDateTime.of(day, LocalTime.of(10, 5))));

        service.enter(new DeliveryVan("TN-D1"), new Visitor("V2"),
                LocalDateTime.of(day, LocalTime.of(10, 0)));
        check("delivery van peak double rate", 120.0,
                service.exit("TN-D1", 120, LocalDateTime.of(day, LocalTime.of(12, 0))));

        service.enter(new DeliveryVan("TN-D2"), new Visitor("V3"),
                LocalDateTime.of(day, LocalTime.of(18, 0)));
        check("delivery van off-peak car rate", 60.0,
                service.exit("TN-D2", 120, LocalDateTime.of(day, LocalTime.of(20, 0))));

        service.enter(new DeliveryVan("TN-D3"), new Visitor("V5"),
                LocalDateTime.of(day, LocalTime.of(16, 30)));
        check("delivery van crosses 5 PM", 90.0,
                service.exit("TN-D3", 120, LocalDateTime.of(day, LocalTime.of(18, 30))));

        service.enter(new EBike("TN-E2"), new Visitor("V4", new OneDayPass(day)),
                LocalDateTime.of(day, LocalTime.of(9, 0)));
        check("valid visitor one-day pass", 0.0,
                service.exit("TN-E2", 300, LocalDateTime.of(day, LocalTime.of(14, 0))));

        service.enter(new ElectricCar("TN-EV1"), new Visitor("V6"),
                LocalDateTime.of(day, LocalTime.of(10, 0)));
        check("original EV charging retained", 210.0,
                service.exit("TN-EV1", 130, 10, LocalDateTime.of(day, LocalTime.of(12, 10))));

        System.out.println("SUMMARY: " + passed + " passed, 0 failed");
    }
}
