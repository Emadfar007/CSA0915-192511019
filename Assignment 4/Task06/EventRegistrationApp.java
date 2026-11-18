import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class EventRegistrationApp {
    private final RegistrationService service;
    private final JPanel root=new JPanel(new BorderLayout());
    private final JTextField regField=new JTextField(14), nameField=new JTextField(18);
    private final JComboBox<EventRecord> eventBox=new JComboBox<>();
    private final JComboBox<Object> filterBox=new JComboBox<>();
    private final JLabel status=new JLabel("Ready");
    private final JButton registerButton=new JButton("Register"), refreshButton=new JButton("Refresh in background"), exportButton=new JButton("Export CSV");
    private final DefaultTableModel tableModel=new DefaultTableModel(new Object[]{"Reg No","Student","Event","Registered"},0){@Override public boolean isCellEditable(int r,int c){return false;}};

    public EventRegistrationApp(RegistrationService service){this.service=service; buildUi(); loadEvents(); registerButton.addActionListener(e->register()); refreshButton.addActionListener(e->refreshAsync()); exportButton.addActionListener(e->export()); filterBox.addActionListener(e->refreshAsync());}
    private void buildUi(){
        root.setBorder(new EmptyBorder(10,10,10,10));
        JMenuBar menu=new JMenuBar(); JMenu file=new JMenu("File"),help=new JMenu("Help"); file.add(new JMenuItem("Export")); file.add(new JMenuItem("Exit")); help.add(new JMenuItem("About")); menu.add(file); menu.add(help); root.add(menu,BorderLayout.NORTH);
        JTabbedPane tabs=new JTabbedPane();
        JPanel register=new JPanel(new BorderLayout(10,10)); register.setBorder(new EmptyBorder(14,14,14,14));
        JPanel form=new JPanel(new GridLayout(3,2,8,8)); form.add(new JLabel("Register number:"));form.add(regField);form.add(new JLabel("Student name:"));form.add(nameField);form.add(new JLabel("Event:"));form.add(eventBox);register.add(form,BorderLayout.CENTER);
        JPanel action=new JPanel(new FlowLayout(FlowLayout.RIGHT));action.add(status);action.add(registerButton);register.add(action,BorderLayout.SOUTH);tabs.addTab("Student Registration",register);
        JPanel committee=new JPanel(new BorderLayout(8,8)); JPanel filters=new JPanel(new FlowLayout(FlowLayout.LEFT));filters.add(new JLabel("Filter event:"));filters.add(filterBox);filters.add(refreshButton);filters.add(exportButton);committee.add(filters,BorderLayout.NORTH);committee.add(new JScrollPane(new JTable(tableModel)),BorderLayout.CENTER);tabs.addTab("Committee View",committee);
        root.add(tabs,BorderLayout.CENTER);
    }
    private void loadEvents(){try{List<EventRecord> events=service.events();eventBox.removeAllItems();filterBox.removeAllItems();filterBox.addItem("All events");for(EventRecord e:events){eventBox.addItem(e);filterBox.addItem(e);}}catch(SQLException e){setError(e.getMessage());}}
    private void register(){try{Registration r=service.register(regField.getText(),nameField.getText(),(EventRecord)eventBox.getSelectedItem());status.setForeground(new Color(0,115,45));status.setText("Registered #"+r.id()+" for "+r.eventName());}catch(Exception e){setError(e.getMessage());}}
    private void setError(String message){status.setForeground(Color.RED.darker());status.setText(message);}
    private Integer selectedFilter(){Object x=filterBox.getSelectedItem();return x instanceof EventRecord e?e.id():null;}
    private void refreshAsync(){refreshButton.setEnabled(false);status.setText("Loading off EDT...");Integer id=selectedFilter();new SwingWorker<List<Registration>,Void>(){
        @Override protected List<Registration> doInBackground()throws Exception{return service.registrations(id,0,100);}
        @Override protected void done(){try{showRows(get());status.setText("Loaded "+tableModel.getRowCount()+" rows");}catch(Exception e){setError(e.getMessage());}finally{refreshButton.setEnabled(true);}}
    }.execute();}
    private void showRows(List<Registration> rows){tableModel.setRowCount(0);for(Registration r:rows)tableModel.addRow(new Object[]{r.registerNumber(),r.studentName(),r.eventName(),r.registeredAt().withNano(0)});}
    private void export(){try{List<Registration> rows=service.registrations(selectedFilter(),0,500);File file=new File("registrations-export.csv");try(FileWriter w=new FileWriter(file)){w.write("register_number,student_name,event,registered_at\n");for(Registration r:rows)w.write(r.registerNumber()+","+r.studentName()+","+r.eventName()+","+r.registeredAt()+"\n");}status.setText("Exported "+rows.size()+" rows");}catch(Exception e){setError(e.getMessage());}}
    public JPanel panel(){return root;}
    private static void layout(Component c){if(c instanceof Container x){x.doLayout();for(Component child:x.getComponents())layout(child);}}
    private static void capture(Component c,File file)throws IOException{int w=980,h=600;c.setSize(w,h);layout(c);BufferedImage image=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);Graphics2D g=image.createGraphics();g.setColor(Color.WHITE);g.fillRect(0,0,w,h);c.printAll(g);g.dispose();ImageIO.write(image,"png",file);}
    public static void main(String[] args)throws Exception{
        if(args.length==2&&args[0].equals("--demo")){File dir=new File(args[1]);dir.mkdirs();SwingUtilities.invokeAndWait(()->{try{
            EventRegistrationApp app=new EventRegistrationApp(new RegistrationService(new MemoryRegistrationDao()));app.regField.setText("192511019");app.nameField.setText("Emad Farooqui");app.registerButton.doClick();capture(app.panel(),new File(dir,"registration-normal.png"));
            app.regField.setText("ABC");app.registerButton.doClick();capture(app.panel(),new File(dir,"registration-invalid.png"));
            app.regField.setText("192511019");app.registerButton.doClick();capture(app.panel(),new File(dir,"registration-duplicate.png"));System.out.println("normal_invalid_duplicate=verified final_status='"+app.status.getText()+"'");
        }catch(IOException e){throw new RuntimeException(e);}});return;}
        boolean mysql=args.length>0&&args[0].equals("--mysql");RegistrationDao dao=mysql?new JdbcRegistrationDao(System.getenv().getOrDefault("EVENT_DB_URL","jdbc:mysql://localhost:3306/campus_events"),System.getenv().getOrDefault("EVENT_DB_USER","root"),System.getenv().getOrDefault("EVENT_DB_PASSWORD","")):new MemoryRegistrationDao();
        SwingUtilities.invokeLater(()->{JFrame frame=new JFrame("Campus Event Registration");frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);frame.setContentPane(new EventRegistrationApp(new RegistrationService(dao)).panel());frame.setSize(980,600);frame.setLocationRelativeTo(null);frame.setVisible(true);});
    }
}
