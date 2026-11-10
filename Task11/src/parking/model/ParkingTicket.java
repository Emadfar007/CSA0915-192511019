package parking.model;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public final class ParkingTicket {
    private static final AtomicLong NEXT_NUMBER = new AtomicLong(1);

    private final long ticketNumber;
    private final Vehicle vehicle;
    private final User user;
    private final int floor;
    private final int slot;
    private final LocalDateTime entryTime;

    public ParkingTicket(Vehicle vehicle, User user, int floor, int slot) {
        this(vehicle, user, floor, slot, LocalDateTime.now());
    }

    public ParkingTicket(Vehicle vehicle, User user, int floor, int slot,
                         LocalDateTime entryTime) {
        this.ticketNumber = NEXT_NUMBER.getAndIncrement();
        this.vehicle = vehicle;
        this.user = user;
        this.floor = floor;
        this.slot = slot;
        this.entryTime = entryTime;
    }

    public long getTicketNumber() { return ticketNumber; }
    public Vehicle getVehicle() { return vehicle; }
    public User getUser() { return user; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public LocalDateTime getEntryTime() { return entryTime; }
}
