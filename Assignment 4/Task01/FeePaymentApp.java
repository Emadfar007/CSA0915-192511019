import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FeePaymentApp {
    private final FeeService service;
    private final JPanel root = new JPanel(new BorderLayout(12, 12));
    private final JTextField registerField = new JTextField(16);
    private final JTextField amountField = new JTextField(16);
    private final JComboBox<String> methodBox = new JComboBox<>(new String[]{"UPI", "Card", "Cash"});
    private final JLabel statusLabel = new JLabel("Enter payment details");
    private final JButton payButton = new JButton("Pay Fee");
    private final JButton receiptButton = new JButton("Receipt");
    private PaymentReceipt lastReceipt;

    public FeePaymentApp(FeeService service) {
        this.service = service;
        buildUi();
        payButton.addActionListener(this::handlePayment);
        receiptButton.addActionListener(e -> showReceipt());
        registerField.addActionListener(this::handlePayment);
        amountField.addActionListener(this::handlePayment);
    }

    private void buildUi() {
        root.setBorder(new EmptyBorder(18, 18, 18, 18));
        JLabel title = new JLabel("Student Fee Payment", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(BorderFactory.createTitledBorder("Payment details"));
        form.add(new JLabel("Register number:")); form.add(registerField);
        form.add(new JLabel("Amount (Rs):")); form.add(amountField);
        form.add(new JLabel("Method:")); form.add(methodBox);
        root.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        receiptButton.setEnabled(false);
        actions.add(statusLabel); actions.add(receiptButton); actions.add(payButton);
        root.add(actions, BorderLayout.SOUTH);
    }

    private void handlePayment(ActionEvent ignored) {
        try {
            lastReceipt = service.process(registerField.getText(), amountField.getText(),
                    String.valueOf(methodBox.getSelectedItem()));
            statusLabel.setForeground(new Color(0, 120, 50));
            statusLabel.setText("Paid: " + lastReceipt.receiptNumber());
            receiptButton.setEnabled(true);
        } catch (IllegalArgumentException ex) {
            lastReceipt = null;
            receiptButton.setEnabled(false);
            statusLabel.setForeground(Color.RED.darker());
            statusLabel.setText(ex.getMessage());
        }
    }

    private void showReceipt() {
        if (lastReceipt == null) return;
        JOptionPane.showMessageDialog(root, lastReceipt.printableText(), "Receipt",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public JPanel panel() { return root; }

    private static void screenshot(Component component, File file, int width, int height) throws IOException {
        component.setSize(width, height); component.doLayout();
        layoutTree(component);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.WHITE); graphics.fillRect(0, 0, width, height);
        component.printAll(graphics); graphics.dispose();
        ImageIO.write(image, "png", file);
    }

    private static void layoutTree(Component component) {
        if (component instanceof Container container) {
            container.doLayout();
            for (Component child : container.getComponents()) layoutTree(child);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 2 && args[0].equals("--demo")) {
            SwingUtilities.invokeAndWait(() -> {
                try {
                    FeePaymentApp app = new FeePaymentApp(new FeeService());
                    app.registerField.setText("192511019");
                    app.amountField.setText("12500.00");
                    app.methodBox.setSelectedItem("UPI");
                    app.payButton.doClick();
                    screenshot(app.panel(), new File(args[1]), 760, 330);
                    System.out.println("status=" + app.statusLabel.getText());
                } catch (IOException e) { throw new RuntimeException(e); }
            });
            return;
        }
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Fee Payment");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setContentPane(new FeePaymentApp(new FeeService()).panel());
            frame.pack(); frame.setLocationRelativeTo(null); frame.setVisible(true);
        });
    }
}

final class FeeService {
    PaymentReceipt process(String registerNumber, String amountText, String method) {
        String register = registerNumber == null ? "" : registerNumber.trim();
        if (!register.matches("\\d{9}")) throw new IllegalArgumentException("Register number must contain 9 digits");
        BigDecimal amount;
        try { amount = new BigDecimal(amountText.trim()).setScale(2, RoundingMode.HALF_UP); }
        catch (Exception ex) { throw new IllegalArgumentException("Enter a valid amount"); }
        if (amount.signum() <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (!(method.equals("UPI") || method.equals("Card") || method.equals("Cash")))
            throw new IllegalArgumentException("Unsupported payment method");
        String receipt = "FEE-" + register + "-" + System.currentTimeMillis();
        return new PaymentReceipt(receipt, register, amount, method, LocalDateTime.now());
    }
}

record PaymentReceipt(String receiptNumber, String registerNumber, BigDecimal amount,
                      String method, LocalDateTime paidAt) {
    String printableText() {
        return "Receipt: " + receiptNumber + "\nRegister: " + registerNumber
                + "\nAmount: Rs " + amount + "\nMethod: " + method + "\nPaid: "
                + paidAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
    }
}
