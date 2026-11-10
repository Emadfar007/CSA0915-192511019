package parking.service;

import parking.model.Chargeable;
import parking.model.ParkingTicket;
import parking.model.Receipt;
import parking.model.User;
import parking.model.Vehicle;

public final class ParkingService {
    private final ParkingFacility facility;
    private final ParkingTicket[] activeTickets;
    private int activeCount;
    private double totalCollection;

    public ParkingService(ParkingFacility facility, int maximumActiveTickets) {
        this.facility = facility;
        this.activeTickets = new ParkingTicket[maximumActiveTickets];
    }

    public ParkingTicket enter(Vehicle vehicle, User user) {
        if (findIndex(vehicle.getRegistrationNumber()) >= 0) {
            throw new IllegalArgumentException("duplicate registration number");
        }
        if (activeCount == activeTickets.length) {
            throw new IllegalStateException("ticket storage is full");
        }
        int[] position = facility.allocate(vehicle);
        if (position == null) {
            throw new IllegalStateException("parking facility is full");
        }
        ParkingTicket ticket = new ParkingTicket(vehicle, user, position[0], position[1]);
        activeTickets[activeCount++] = ticket;
        return ticket;
    }

    public Receipt exit(String registrationNumber, int minutes, int kWh) {
        int index = findIndex(registrationNumber);
        if (index < 0) {
            throw new IllegalArgumentException("no active ticket for " + registrationNumber);
        }
        ParkingTicket ticket = activeTickets[index];
        if (!facility.release(ticket.getFloor(), ticket.getSlot())) {
            throw new IllegalStateException("ticket refers to an invalid or free slot");
        }
        double amount = ticket.getUser().hasValidMonthlyPass()
                ? 0.0 : ticket.getVehicle().calculateFee(minutes);
        if (amount > 0.0 && ticket.getVehicle() instanceof Chargeable chargeable) {
            amount += chargeable.chargingFee(kWh);
        }
        totalCollection += amount;
        activeTickets[index] = activeTickets[activeCount - 1];
        activeTickets[--activeCount] = null;
        return new Receipt(ticket.getTicketNumber(), registrationNumber, minutes, amount);
    }

    private int findIndex(String registrationNumber) {
        for (int i = 0; i < activeCount; i++) {
            if (activeTickets[i].getVehicle().getRegistrationNumber()
                    .equalsIgnoreCase(registrationNumber)) {
                return i;
            }
        }
        return -1;
    }

    public double getTotalCollection() {
        return totalCollection;
    }
}
