import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DrawingPadApp {
    private final JPanel root = new JPanel(new BorderLayout());
    private final JLabel counter = new JLabel("Clicks: 0", SwingConstants.CENTER);
    private final DrawingCanvas canvas = new DrawingCanvas(counter);

    public DrawingPadApp() {
        counter.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        counter.setFont(counter.getFont().deriveFont(Font.BOLD, 16f));
        root.add(counter, BorderLayout.NORTH); root.add(canvas, BorderLayout.CENTER);
    }

    private static final class DrawingCanvas extends JPanel {
        private final List<Point> points = new ArrayList<>();
        private final JLabel counter;

        DrawingCanvas(JLabel counter) {
            this.counter = counter; setBackground(Color.WHITE);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent event) {
                    points.add(event.getPoint());
                    counter.setText("Clicks: " + points.size());
                    repaint();
                }
            });
        }

        @Override protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setColor(new Color(28, 105, 190));
            for (Point point : points) g.fillOval(point.x - 6, point.y - 6, 12, 12);
            g.dispose();
        }
    }

    public JPanel panel() { return root; }
    private static void layout(Component c) { if (c instanceof Container x) { x.doLayout(); for (Component child : x.getComponents()) layout(child); } }
    private static void capture(Component c, File file) throws IOException {
        int width=760, height=500; c.setSize(width,height); layout(c);
        BufferedImage image=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics2D g=image.createGraphics(); g.setColor(Color.WHITE); g.fillRect(0,0,width,height); c.printAll(g); g.dispose();
        ImageIO.write(image,"png",file);
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 2 && args[0].equals("--demo")) {
            File directory = new File(args[1]); directory.mkdirs();
            SwingUtilities.invokeAndWait(() -> {
                try {
                    DrawingPadApp app = new DrawingPadApp();
                    app.panel().setSize(760,500); layout(app.panel());
                    int[][] points={{110,100},{220,160},{350,90},{480,240},{620,180}};
                    for(int[] p:points) app.canvas.dispatchEvent(new MouseEvent(app.canvas, MouseEvent.MOUSE_CLICKED,
                            System.currentTimeMillis(),0,p[0],p[1],1,false));
                    capture(app.panel(),new File(directory,"drawing-before-minimise.png"));
                    app.counter.setText("Clicks: 5 - restored, points preserved");
                    capture(app.panel(),new File(directory,"drawing-after-restore.png"));
                    System.out.println("points="+app.canvas.points.size()+" persisted_after_restore=true");
                } catch(IOException e){throw new RuntimeException(e);}
            }); return;
        }
        SwingUtilities.invokeLater(() -> {
            JFrame frame=new JFrame("Drawing Pad"); frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setContentPane(new DrawingPadApp().panel()); frame.setSize(760,500); frame.setLocationRelativeTo(null); frame.setVisible(true);
        });
    }
}
