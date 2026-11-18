import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AppointmentDeskApp {
    private final Map<String, List<String>> doctorsByDepartment = Map.of(
            "Cardiology", List.of("Dr. Anita Rao", "Dr. Vivek Shah"),
            "Dermatology", List.of("Dr. Maya Nair", "Dr. Imran Khan"),
            "Orthopaedics", List.of("Dr. Rohan Das", "Dr. Leena Paul"));
    private final JPanel root = new JPanel(new BorderLayout(10, 10));
    private final JTextField patientField = new JTextField(18);
    private final JTextField dateField = new JTextField(LocalDate.now().plusDays(1).toString(), 18);
    private final JComboBox<String> departmentBox = new JComboBox<>(doctorsByDepartment.keySet().stream().sorted().toArray(String[]::new));
    private final JComboBox<String> doctorBox = new JComboBox<>();
    private final JLabel status = new JLabel("Ready");
    private final JButton bookButton = new JButton("Book Appointment");

    public AppointmentDeskApp() {
        buildUi();
        departmentBox.addActionListener(e -> refreshDoctors());
        bookButton.addActionListener(e -> book());
        refreshDoctors();
    }

    private void buildUi() {
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        JLabel title = new JLabel("Hospital Outpatient Appointment Desk", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 21f));
        root.add(title, BorderLayout.NORTH);
        JPanel form = new JPanel(new GridLayout(4, 2, 9, 9));
        form.add(new JLabel("Patient name:")); form.add(patientField);
        form.add(new JLabel("Department:")); form.add(departmentBox);
        form.add(new JLabel("Doctor:")); form.add(doctorBox);
        form.add(new JLabel("Date (YYYY-MM-DD):")); form.add(dateField);
        root.add(form, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(status); bottom.add(bookButton); root.add(bottom, BorderLayout.SOUTH);
    }

    private void refreshDoctors() {
        doctorBox.removeAllItems();
        List<String> doctors = doctorsByDepartment.get(String.valueOf(departmentBox.getSelectedItem()));
        if (doctors != null) doctors.forEach(doctorBox::addItem);
        status.setText(doctors == null ? "No doctors" : doctors.size() + " doctors loaded from Map");
    }

    private void book() {
        String patient = patientField.getText().trim();
        if (patient.length() < 2) { fail("Enter a valid patient name"); return; }
        LocalDate date;
        try { date = LocalDate.parse(dateField.getText().trim()); }
        catch (Exception ex) { fail("Use date format YYYY-MM-DD"); return; }
        if (date.isBefore(LocalDate.now())) { fail("Appointment date cannot be in the past"); return; }
        status.setForeground(new Color(0, 115, 45));
        status.setText("Booked " + patient + " with " + doctorBox.getSelectedItem());
    }

    private void fail(String message) { status.setForeground(Color.RED.darker()); status.setText(message); }
    public JPanel panel() { return root; }

    private static void capture(Component c, File file) throws IOException {
        c.setSize(820, 360); layout(c);
        BufferedImage image = new BufferedImage(820, 360, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics(); g.setColor(Color.WHITE); g.fillRect(0,0,820,360); c.printAll(g); g.dispose();
        ImageIO.write(image, "png", file);
    }
    private static void layout(Component c) { if (c instanceof Container x) { x.doLayout(); for (Component child : x.getComponents()) layout(child); } }

    public static void main(String[] args) throws Exception {
        if (args.length == 2 && args[0].equals("--demo")) {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    AppointmentDeskApp app = new AppointmentDeskApp();
                    app.patientField.setText("Aarav Menon");
                    app.departmentBox.setSelectedItem("Cardiology");
                    app.dateField.setText(LocalDate.now().plusDays(2).toString());
                    app.bookButton.doClick(); capture(app.panel(), new File(args[1]));
                    System.out.println("status=" + app.status.getText());
                } catch (IOException e) { throw new RuntimeException(e); }
            }); return;
        }
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Appointment Desk");
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override public void windowClosing(WindowEvent e) { frame.dispose(); }
            });
            frame.setContentPane(new AppointmentDeskApp().panel()); frame.pack();
            frame.setLocationRelativeTo(null); frame.setVisible(true);
        });
    }
}
