import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class JdbcRegistrationDao implements RegistrationDao {
    private final String url, user, password;
    public JdbcRegistrationDao(String url, String user, String password) {
        this.url = url; this.user = user; this.password = password;
    }
    private Connection connection() throws SQLException { return DriverManager.getConnection(url, user, password); }

    @Override public List<EventRecord> findEvents() throws SQLException {
        String sql = "SELECT id, name, capacity FROM events ORDER BY name";
        try (Connection c=connection(); PreparedStatement p=c.prepareStatement(sql); ResultSet r=p.executeQuery()) {
            List<EventRecord> values=new ArrayList<>(); while(r.next()) values.add(new EventRecord(r.getInt(1),r.getString(2),r.getInt(3))); return values;
        }
    }
    @Override public boolean exists(String registerNumber, int eventId) throws SQLException {
        String sql="SELECT 1 FROM registrations WHERE register_number=? AND event_id=? LIMIT 1";
        try(Connection c=connection(); PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,registerNumber); p.setInt(2,eventId); try(ResultSet r=p.executeQuery()){return r.next();}
        }
    }
    @Override public Registration insert(String registerNumber,String studentName,EventRecord event) throws SQLException {
        String sql="INSERT INTO registrations(register_number,student_name,event_id) VALUES(?,?,?)";
        try(Connection c=connection(); PreparedStatement p=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            p.setString(1,registerNumber); p.setString(2,studentName); p.setInt(3,event.id()); p.executeUpdate();
            try(ResultSet keys=p.getGeneratedKeys()){keys.next(); return new Registration(keys.getLong(1),registerNumber,studentName,event.id(),event.name(),java.time.LocalDateTime.now());}
        }
    }
    @Override public List<Registration> findByEvent(Integer eventId,int limit,int offset) throws SQLException {
        String sql="SELECT r.id,r.register_number,r.student_name,e.id,e.name,r.registered_at FROM registrations r JOIN events e ON e.id=r.event_id "
                +(eventId==null?"":"WHERE e.id=? ")+"ORDER BY r.registered_at DESC LIMIT ? OFFSET ?";
        try(Connection c=connection(); PreparedStatement p=c.prepareStatement(sql)){
            int i=1; if(eventId!=null)p.setInt(i++,eventId); p.setInt(i++,limit); p.setInt(i,offset);
            try(ResultSet r=p.executeQuery()){List<Registration> values=new ArrayList<>(); while(r.next())values.add(new Registration(r.getLong(1),r.getString(2),r.getString(3),r.getInt(4),r.getString(5),r.getTimestamp(6).toLocalDateTime())); return values;}
        }
    }
}
