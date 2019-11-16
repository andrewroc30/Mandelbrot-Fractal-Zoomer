import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Main {

    public static JLabel statusLabel = new JLabel();
    public static JFrame f = new JFrame("Mandelbrot Image Zoomer");
    public static boolean isPaused = true;

    //Good point: (-.74364386269, .13182590271)
    //Good point: (0.001643721971153, 0.822467633298876)
    //Good Point: (-1.74995768370609350360221450607069970727110579726252077930242837820286008082972804887218672784431700831100544507655659531379747541999999995,
    //              0.00000000000000000278793706563379402178294753790944364927085054500163081379043930650189386849765202169477470552201325772332454726999999995)

    public static void main(String[] args) throws Exception {
        statusLabel.setText("STATUS");
        showStartUI();
    }

    // Shows the UI for the program and handles input
    private static void showStartUI() {
        JButton gif = new JButton("Create GIF");
        gif.setBounds(50, 400, 150, 50);

        JButton img = new JButton("Create PNG");
        img.setBounds(250, 400, 150, 50);

        JLabel xLabel = new JLabel();
        xLabel.setText("Enter X-Coordinate (between -2 and 2)");
        xLabel.setBounds(50, 10, 500, 100);
        JTextField xText = new JTextField();
        xText.setBounds(300, 50, 130, 25);
        xText.setText("-.74364386269");

        JLabel yLabel = new JLabel();
        yLabel.setText("Enter Y-Coordinate (between -2 and 2)");
        yLabel.setBounds(50, -50, 500, 300);
        JTextField yText = new JTextField();
        yText.setBounds(300, 85, 130, 25);
        yText.setText(".13182590271");

        JLabel zoomFactorLabel = new JLabel();
        zoomFactorLabel.setText("Enter Zoom Factor");
        zoomFactorLabel.setBounds(50, -10, 500, 300);
        JTextField zoomFactorText = new JTextField();
        zoomFactorText.setBounds(300, 125, 130, 25);
        zoomFactorText.setText("1.5");

        JLabel numImagesLabel = new JLabel();
        numImagesLabel.setText("Enter Number of Images");
        numImagesLabel.setBounds(50, 25, 500, 300);
        JTextField numImagesText = new JTextField();
        numImagesText.setBounds(300, 160, 130, 25);
        numImagesText.setText("5");

        JLabel iterationsLabel = new JLabel();
        iterationsLabel.setText("Enter Iterations");
        iterationsLabel.setBounds(50, 65, 500, 300);
        JTextField iterationsText = new JTextField();
        iterationsText.setBounds(300, 200, 130, 25);
        iterationsText.setText("1000");

        JLabel initialZoomLabel = new JLabel();
        initialZoomLabel.setText("Enter Initial Zoom");
        initialZoomLabel.setBounds(50, 100, 500, 300);
        JTextField initialZoomText = new JTextField();
        initialZoomText.setBounds(300, 235, 130, 25);
        initialZoomText.setText("1");

        statusLabel.setBounds(160, 200, 500, 300);

        f.add(xLabel);
        f.add(xText);
        f.add(yLabel);
        f.add(yText);
        f.add(zoomFactorLabel);
        f.add(zoomFactorText);
        f.add(numImagesLabel);
        f.add(numImagesText);
        f.add(iterationsLabel);
        f.add(iterationsText);
        f.add(initialZoomLabel);
        f.add(initialZoomText);
        f.add(statusLabel);
        f.add(gif);
        f.add(img);
        f.setSize(500, 500);
        f.setLayout(null);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gif.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                System.out.println("Button Pressed");
                try {
                    double x = Double.parseDouble(xText.getText());
                    double y = Double.parseDouble(yText.getText());
                    double zoomFactor = Double.parseDouble(zoomFactorText.getText());
                    int numImages = Integer.parseInt(numImagesText.getText());
                    int iterations = Integer.parseInt(iterationsText.getText());
                    double initialZoom = Double.parseDouble(initialZoomText.getText());
                    GifController.makeGifWithThreads(numImages, 1920, 1080, iterations, initialZoom, zoomFactor, x, y);
                    //GifController.makeGifWithThreadsAutoIterations(numImages, 1920, 1080, iterations, initialZoom, zoomFactor, x, y);
                } catch (Exception e) {
                    System.out.println("GIF Creation Failed!");
                }
            }
        });

        img.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                System.out.println("Button Pressed");
                try {
                    double x = Double.parseDouble(xText.getText());
                    double y = Double.parseDouble(yText.getText());
                    int iterations = Integer.parseInt(iterationsText.getText());
                    double initialZoom = Double.parseDouble(initialZoomText.getText());
                    ImageIO.write(ImageController.createZoomedImage(1920, 1080, iterations, initialZoom, x, y),
                            "png", new File("images/mandelbrot.png"));
                    statusLabel.setText("Image created!");
                    //f.update(f.getGraphics());
                    f.repaint();
                } catch (Exception e) {
                    System.out.println("Image Creation Failed!");
                }
            }
        });
    }
}