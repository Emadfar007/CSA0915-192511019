package parking.app;

import parking.model.Car;
import parking.model.ElectricCar;
import parking.model.ParkingTicket;
import parking.model.Receipt;
import parking.model.Staff;
import parking.model.Student;
import parking.model.TwoWheeler;
import parking.model.Visitor;
import parking.service.ParkingFacility;
import parking.service.ParkingService;
import parking.service.VehicleFactory;

public final class ParkingSystemTest {
    private static int passed;
    private static int failed;

    private static void check(String name, Object expected, Object actual) {
        boolean ok = expected.equals(actual);
        System.out.printf("%-36s | %-12s | %-12s | %s%n",
                name, expected, actual, ok ? "PASS" : "FAIL");
        if (ok) passed++; else failed++;
    }

    private static void expectFailure(String name, Runnable action) {
        try {
            action.run();
            check(name, "exception", "none");
        } catch (RuntimeException expected) {
            check(name, "exception", "exception");
        }
    }

    public static void main(String[] args) {
        System.out.println("TEST                                 | EXPECTED     | ACTUAL       | RESULT");
        System.out.println("-----------------------------------------------------------------------------");

        ParkingService fees = new ParkingService(new ParkingFacility(new int[]{4}), 4);
        fees.enter(new TwoWheeler("TN01A1"), new Visitor("V1"));
        check("two-wheeler 130 minutes", 30.0, fees.exit("TN01A1", 130, 0).amount());
        fees.enter(new Car("TN02B2"), new Visitor("V2"));
        check("exact boundary 60 minutes", 30.0, fees.exit("TN02B2", 60, 0).amount());
        fees.enter(new Car("TN03C3"), new Visitor("V3"));
        check("zero-minute parking", 0.0, fees.exit("TN03C3", 0, 0).amount());
        fees.enter(new ElectricCar("TN04D4"), new Visitor("V4"));
        check("EV 130 minutes plus 10 kWh", 210.0, fees.exit("TN04D4", 130, 10).amount());
        check("hand total collection", 270.0, fees.getTotalCollection());

        ParkingService capacity = new ParkingService(new ParkingFacility(new int[]{1, 1}), 2);
        ParkingTicket first = capacity.enter(new Car("TN10A1"), new Student("S1", true));
        ParkingTicket last = capacity.enter(new Car("TN10A2"), new Staff("F1", true));
        check("first allocated position", "0:0", first.getFloor() + ":" + first.getSlot());
        check("last free slot", "1:0", last.getFloor() + ":" + last.getSlot());
        expectFailure("full facility", () -> capacity.enter(new Car("TN10A3"), new Visitor("V")));
        expectFailure("duplicate registration", () -> capacity.enter(new Car("TN10A2"), new Visitor("V")));
        check("monthly pass is free", 0.0, capacity.exit("TN10A1", 600, 0).amount());
        expectFailure("invalid registration on exit", () -> capacity.exit("UNKNOWN", 10, 0));
        expectFailure("unknown vehicle type", () -> VehicleFactory.create("bus", "TN00X"));

        ParkingFacility invalidSlot = new ParkingFacility(new int[]{1});
        check("invalid slot release", false, invalidSlot.release(5, 0));

        System.out.printf("SUMMARY: %d passed, %d failed%n", passed, failed);
        if (failed > 0) {
            throw new AssertionError("test failures: " + failed);
        }
    }
}
