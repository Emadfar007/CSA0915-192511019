import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public final class MemoryRegistrationDao implements RegistrationDao {
    private final List<EventRecord> events = List.of(
            new EventRecord(1, "Code Sprint", 120),
            new EventRecord(2, "Robotics Expo", 80),
            new EventRecord(3, "Design Challenge", 100));
    private final CopyOnWriteArrayList<Registration> registrations = new CopyOnWriteArrayList<>();
    private final AtomicLong sequence = new AtomicLong(1);

    @Override public List<EventRecord> findEvents() { return events; }
    @Override public boolean exists(String registerNumber, int eventId) {
        return registrations.stream().anyMatch(r -> r.registerNumber().equals(registerNumber) && r.eventId() == eventId);
    }
    @Override public Registration insert(String registerNumber, String studentName, EventRecord event) throws SQLException {
        synchronized (registrations) {
            if (exists(registerNumber, event.id())) throw new SQLException("Duplicate registration");
            long current = registrations.stream().filter(r -> r.eventId() == event.id()).count();
            if (current >= event.capacity()) throw new SQLException("Event capacity reached");
            Registration value = new Registration(sequence.getAndIncrement(), registerNumber, studentName,
                    event.id(), event.name(), LocalDateTime.now());
            registrations.add(value); return value;
        }
    }
    @Override public List<Registration> findByEvent(Integer eventId, int limit, int offset) {
        List<Registration> filtered = registrations.stream().filter(r -> eventId == null || r.eventId() == eventId).toList();
        if (offset >= filtered.size()) return List.of();
        return new ArrayList<>(filtered.subList(offset, Math.min(filtered.size(), offset + limit)));
    }
}
