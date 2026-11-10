package parking.model;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public record ParkingTicket(long number, Vehicle vehicle, User user,
                            int floor, int slot, LocalDateTime entryTime) {
    private static final AtomicLong NEXT = new AtomicLong(1);

    public ParkingTicket(Vehicle vehicle, User user, int floor, int slot,
                         LocalDateTime entryTime) {
        this(NEXT.getAndIncrement(), vehicle, user, floor, slot, entryTime);
    }
}
