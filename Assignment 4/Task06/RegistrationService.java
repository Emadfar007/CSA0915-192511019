import java.sql.SQLException;
import java.util.List;

public final class RegistrationService {
    private final RegistrationDao dao;
    public RegistrationService(RegistrationDao dao) { this.dao=dao; }
    public List<EventRecord> events() throws SQLException { return dao.findEvents(); }
    public Registration register(String registerNumber,String name,EventRecord event) throws SQLException {
        String reg=registerNumber==null?"":registerNumber.trim(); String student=name==null?"":name.trim();
        if(!reg.matches("\\d{9}"))throw new IllegalArgumentException("Register number must contain 9 digits");
        if(student.length()<2 || !student.matches("[A-Za-z .'-]+"))throw new IllegalArgumentException("Enter a valid student name");
        if(event==null)throw new IllegalArgumentException("Select an event");
        if(dao.exists(reg,event.id()))throw new IllegalStateException("Student is already registered for this event");
        try{return dao.insert(reg,student,event);}catch(SQLException ex){
            if("23000".equals(ex.getSQLState()))throw new IllegalStateException("Student is already registered for this event"); throw ex;
        }
    }
    public List<Registration> registrations(Integer eventId,int page,int pageSize)throws SQLException{
        if(page<0||pageSize<1||pageSize>500)throw new IllegalArgumentException("Invalid page request");
        return dao.findByEvent(eventId,pageSize,page*pageSize);
    }
}
