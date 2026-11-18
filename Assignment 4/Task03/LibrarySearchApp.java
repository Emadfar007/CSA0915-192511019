import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LibrarySearchApp {
    private final JPanel root = new JPanel(new BorderLayout(10, 10));
    private final JTextField searchField = new JTextField(24);
    private final JComboBox<String> categoryBox = new JComboBox<>(new String[]{"All", "Title", "Author", "Subject"});
    private final JButton searchButton = new JButton("Search");
    private final JLabel status = new JLabel("Ready - 0 results");
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"Title", "Author", "Availability"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    public LibrarySearchApp() {
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel title = new JLabel("Campus Library Search", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        root.add(title, BorderLayout.NORTH);

        JPanel centre = new JPanel(new BorderLayout(8, 8));
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchRow.add(new JLabel("Find:")); searchRow.add(searchField);
        searchRow.add(new JLabel("Category:")); searchRow.add(categoryBox); searchRow.add(searchButton);
        centre.add(searchRow, BorderLayout.NORTH);
        JTable results = new JTable(model); results.setFillsViewportHeight(true);
        centre.add(new JScrollPane(results), BorderLayout.CENTER);
        root.add(centre, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.add(new JButton("Prev")); buttons.add(new JButton("Next")); buttons.add(new JButton("Reserve"));
        south.add(buttons, BorderLayout.NORTH); south.add(status, BorderLayout.SOUTH);
        root.add(south, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> search());
        searchField.addActionListener(e -> search());
    }

    private void search() {
        model.setRowCount(0);
        model.addRow(new Object[]{"Effective Java", "Joshua Bloch", "Available"});
        model.addRow(new Object[]{"Java: The Complete Reference", "Herbert Schildt", "Issued"});
        model.addRow(new Object[]{"Head First Java", "Kathy Sierra", "Available"});
        status.setText("3 results for '" + searchField.getText().trim() + "' in " + categoryBox.getSelectedItem());
    }

    public JPanel panel() { return root; }
    private static void layout(Component c) { if (c instanceof Container x) { x.doLayout(); for (Component child : x.getComponents()) layout(child); } }
    private static void capture(Component c, File file, int width, int height) throws IOException {
        c.setSize(width, height); layout(c);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics(); g.setColor(Color.WHITE); g.fillRect(0,0,width,height); c.printAll(g); g.dispose();
        ImageIO.write(image, "png", file);
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 2 && args[0].equals("--demo")) {
            File directory = new File(args[1]); directory.mkdirs();
            SwingUtilities.invokeAndWait(() -> {
                try {
                    LibrarySearchApp app = new LibrarySearchApp();
                    app.searchField.setText("Java"); app.searchButton.doClick();
                    capture(app.panel(), new File(directory, "library-small.png"), 640, 420);
                    capture(app.panel(), new File(directory, "library-medium.png"), 900, 560);
                    capture(app.panel(), new File(directory, "library-large.png"), 1200, 720);
                    System.out.println("screenshots=3 status=" + app.status.getText());
                } catch (IOException e) { throw new RuntimeException(e); }
            }); return;
        }
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Library Search"); frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setContentPane(new LibrarySearchApp().panel()); frame.setSize(900,560); frame.setLocationRelativeTo(null); frame.setVisible(true);
        });
    }
}
