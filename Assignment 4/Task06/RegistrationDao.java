import java.sql.SQLException;
import java.util.List;

public interface RegistrationDao {
    List<EventRecord> findEvents() throws SQLException;
    boolean exists(String registerNumber, int eventId) throws SQLException;
    Registration insert(String registerNumber, String studentName, EventRecord event) throws SQLException;
    List<Registration> findByEvent(Integer eventId, int limit, int offset) throws SQLException;
}
