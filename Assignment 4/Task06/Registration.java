import java.time.LocalDateTime;

public record Registration(long id, String registerNumber, String studentName,
                           int eventId, String eventName, LocalDateTime registeredAt) {
}
