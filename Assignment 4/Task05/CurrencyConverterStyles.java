import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Map;

public class CurrencyConverterStyles {
    private static final Map<String, Double> INR_PER_UNIT = Map.of(
            "INR", 1.0, "USD", 83.25, "EUR", 90.40, "GBP", 105.70);

    private abstract static class ConverterPanel extends JPanel {
        final JTextField amount = new JTextField(14);
        final JComboBox<String> from = new JComboBox<>(new String[]{"INR", "USD", "EUR", "GBP"});
        final JComboBox<String> to = new JComboBox<>(new String[]{"USD", "INR", "EUR", "GBP"});
        final JButton convert = new JButton("Convert");
        final JLabel result = new JLabel("Enter an amount", SwingConstants.CENTER);

        ConverterPanel(String title) {
            super(new BorderLayout(10, 10)); setBorder(new EmptyBorder(16,16,16,16));
            JLabel heading = new JLabel(title, SwingConstants.CENTER);
            heading.setFont(heading.getFont().deriveFont(Font.BOLD, 21f)); add(heading, BorderLayout.NORTH);
            JPanel form = new JPanel(new GridLayout(3,2,8,8));
            form.add(new JLabel("Amount:")); form.add(amount);
            form.add(new JLabel("From:")); form.add(from);
            form.add(new JLabel("To:")); form.add(to); add(form, BorderLayout.CENTER);
            JPanel bottom = new JPanel(new BorderLayout(8,8)); bottom.add(convert, BorderLayout.EAST);
            bottom.add(result, BorderLayout.CENTER); add(bottom, BorderLayout.SOUTH);
        }

        void convertAmount(boolean showDialog) {
            String message;
            try {
                double input = Double.parseDouble(amount.getText().trim());
                if (!Double.isFinite(input) || input < 0) throw new NumberFormatException();
                double inr = input * INR_PER_UNIT.get(String.valueOf(from.getSelectedItem()));
                double output = inr / INR_PER_UNIT.get(String.valueOf(to.getSelectedItem()));
                message = new DecimalFormat("0.00").format(input) + " " + from.getSelectedItem()
                        + " = " + new DecimalFormat("0.00").format(output) + " " + to.getSelectedItem();
                result.setForeground(new Color(0, 115, 45));
            } catch (NumberFormatException ex) {
                message = "Invalid amount - enter a non-negative number";
                result.setForeground(Color.RED.darker());
            }
            result.setText(message);
            if (showDialog) JOptionPane.showMessageDialog(this, message, "Conversion", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Style 1: the panel itself implements ActionListener.
    private static final class ImplementingPanel extends ConverterPanel implements ActionListener {
        ImplementingPanel() { super("Currency Converter - implements ActionListener"); convert.addActionListener(this); amount.addActionListener(this); }
        @Override public void actionPerformed(ActionEvent e) { convertAmount(e.getSource() == amount); }
    }

    // Style 2: reusable named inner listener.
    private static final class NamedInnerPanel extends ConverterPanel {
        NamedInnerPanel() { super("Currency Converter - named inner class"); Handler h=new Handler(); convert.addActionListener(h); amount.addActionListener(h); }
        private final class Handler implements ActionListener {
            @Override public void actionPerformed(ActionEvent e) { convertAmount(e.getSource() == amount); }
        }
    }

    // Style 3: anonymous inner classes.
    private static final class AnonymousPanel extends ConverterPanel {
        AnonymousPanel() {
            super("Currency Converter - anonymous listener");
            convert.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) { convertAmount(false); }});
            amount.addActionListener(new ActionListener() { @Override public void actionPerformed(ActionEvent e) { convertAmount(true); }});
        }
    }

    // Style 4: lambda expressions (supplied lambda version completed).
    private static final class LambdaPanel extends ConverterPanel {
        LambdaPanel() { super("Currency Converter - lambda listener");
            convert.addActionListener(e -> convertAmount(false));
            amount.addActionListener(e -> convertAmount(true));
        }
    }

    private static void layout(Component c) { if(c instanceof Container x){x.doLayout(); for(Component child:x.getComponents()) layout(child);} }
    private static void capture(Component c, File file) throws IOException {
        int w=760,h=360; c.setSize(w,h); layout(c); BufferedImage image=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
        Graphics2D g=image.createGraphics(); g.setColor(Color.WHITE); g.fillRect(0,0,w,h); c.printAll(g); g.dispose(); ImageIO.write(image,"png",file);
    }

    private static void showLive(ConverterPanel panel) {
        JFrame frame=new JFrame("Currency Converter"); frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter(){@Override public void windowClosing(WindowEvent e){frame.dispose();}});
        frame.setContentPane(panel); frame.pack(); frame.setLocationRelativeTo(null); frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        if(args.length==2 && args[0].equals("--demo")){
            File dir=new File(args[1]); dir.mkdirs(); SwingUtilities.invokeAndWait(()->{
                try{
                    // Instantiate all four to verify their handler registrations.
                    ConverterPanel[] styles={new ImplementingPanel(),new NamedInnerPanel(),new AnonymousPanel(),new LambdaPanel()};
                    LambdaPanel lambda=(LambdaPanel)styles[3]; lambda.amount.setText("100"); lambda.from.setSelectedItem("USD"); lambda.to.setSelectedItem("INR");
                    lambda.convert.doClick(); capture(lambda,new File(dir,"converter-valid.png")); String valid=lambda.result.getText();
                    lambda.amount.setText("abc"); lambda.convert.doClick(); capture(lambda,new File(dir,"converter-invalid.png"));
                    System.out.println("styles="+styles.length+" valid='"+valid+"' invalid='"+lambda.result.getText()+"'");
                }catch(IOException e){throw new RuntimeException(e);}
            }); return;
        }
        SwingUtilities.invokeLater(()->showLive(new LambdaPanel()));
    }
}
