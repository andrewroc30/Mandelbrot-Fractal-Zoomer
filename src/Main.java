import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.*;

public class Main {

    private static JLabel statusLabel = new JLabel();
    private static JFrame f = new JFrame("Mandelbrot Image Zoomer");
    private static boolean isPaused = true;
    public static ReentrantLock arrayPushLock = new ReentrantLock();
    public static ReentrantLock numThreadsLock = new ReentrantLock();
    public static File tempImageDir;
    public static File finalOutputDir;

    //Good point: (-.74364386269, .13182590271)
    //Good point: (0.001643721971153, 0.822467633298876)
    //Good Point: (-1.74995768370609350360221450607069970727110579726252077930242837820286008082972804887218672784431700831100544507655659531379747541999999995,
    //              0.00000000000000000278793706563379402178294753790944364927085054500163081379043930650189386849765202169477470552201325772332454726999999995)

    public static void main(String[] args) {
        statusLabel.setText("");
        showStartUI();
    }

    /**
     * Gets the status label of the UI
     * @return JLabel of status
     */
    public static JLabel getStatusLabel() {
        return statusLabel;
    }

    /**
     * Gets the UI window
     * @return JFrame of the UI
     */
    public static JFrame getFrame() {
        return f;
    }

    /**
     * Gets whether or not we are paused
     * @return boolean of whether we are paused
     */
    public static boolean getIsPaused() {
        return isPaused;
    }

    /**
     * Sets whether or not we are paused
     * @param set Value to set whether we are paused to
     */
    public static void setIsPaused(boolean set) {
        isPaused = set;
    }

    /**
     * Updates the UI Status Label with the given String
     * @param status The new status to set the UI Status Label to
     */
    public static void updateStatusLabel(String status) {
        System.out.println(status);
        statusLabel.setText(status);
        f.update(f.getGraphics());
    }

    /**
     * Converts the given frames per second into ms between frames
     * @param fps The frames per second of the GIF
     * @return Integer containing number of milliseconds between frames
     */
    private static int fpsToMs(int fps) {
        if (fps == 0) {
            return 10;
        }
        return 1000 / fps;
    }

    /**
     * Sets the file paths for the temporary images and final output
     *
     * @param outputDirectory The user chosen output directory
     */
    private static void setPaths(String outputDirectory) {
        tempImageDir = new File(outputDirectory, "tempImages");
        finalOutputDir = new File(outputDirectory);
    }

    /**
     * Returns all of the elements in the JFrame
     */
    public static Map<String, JComponent> getElements() {
        HashMap<String, JComponent> elements = new HashMap<>();

        JButton gifButton = new JButton("Create GIF");
        gifButton.setBounds(20, 400, 150, 50);
        elements.put("gifButton", gifButton);

        JButton mp4Button = new JButton("Create MP4");
        mp4Button.setBounds(170, 400, 150, 50);
        elements.put("mp4Button", mp4Button);

        JButton imgButton = new JButton("Create PNG");
        imgButton.setBounds(320, 400, 150, 50);
        elements.put("imgButton", imgButton);

        JLabel xLabel = new JLabel();
        xLabel.setText("Enter X-Coordinate");
        xLabel.setBounds(50, 10, 500, 100);
        JTextField xText = new JTextField();
        xText.setBounds(300, 50, 130, 25);
        xText.setText("-.74364386269");
        elements.put("xLabel", xLabel);
        elements.put("xText", xText);

        JLabel yLabel = new JLabel();
        yLabel.setText("Enter Y-Coordinate");
        yLabel.setBounds(50, -50, 500, 300);
        JTextField yText = new JTextField();
        yText.setBounds(300, 85, 130, 25);
        yText.setText(".13182590271");
        elements.put("yLabel", yLabel);
        elements.put("yText", yText);

        JLabel zoomFactorLabel = new JLabel();
        zoomFactorLabel.setText("Enter Zoom Factor");
        zoomFactorLabel.setBounds(50, -10, 500, 300);
        JTextField zoomFactorText = new JTextField();
        zoomFactorText.setBounds(300, 125, 130, 25);
        zoomFactorText.setText("1.5");
        elements.put("zoomFactorLabel", zoomFactorLabel);
        elements.put("zoomFactorText", zoomFactorText);

        JLabel numImagesLabel = new JLabel();
        numImagesLabel.setText("Enter Number of Images");
        numImagesLabel.setBounds(50, 25, 500, 300);
        JTextField numImagesText = new JTextField();
        numImagesText.setBounds(300, 160, 130, 25);
        numImagesText.setText("5");
        elements.put("numImagesLabel", numImagesLabel);
        elements.put("numImagesText", numImagesText);

        JLabel iterationsLabel = new JLabel();
        iterationsLabel.setText("Enter Iterations");
        iterationsLabel.setBounds(50, 65, 500, 300);
        JTextField iterationsText = new JTextField();
        iterationsText.setBounds(300, 200, 130, 25);
        iterationsText.setText("1000");
        elements.put("iterationsLabel", iterationsLabel);
        elements.put("iterationsText", iterationsText);

        JLabel initialZoomLabel = new JLabel();
        initialZoomLabel.setText("Enter Initial Zoom");
        initialZoomLabel.setBounds(50, 100, 500, 300);
        JTextField initialZoomText = new JTextField();
        initialZoomText.setBounds(300, 235, 130, 25);
        initialZoomText.setText("1");
        elements.put("initialZoomLabel", initialZoomLabel);
        elements.put("initialZoomText", initialZoomText);

        JLabel timeBetweenFramesLabel = new JLabel();
        timeBetweenFramesLabel.setText("Enter Frames Per Second");
        timeBetweenFramesLabel.setBounds(50, 135, 500, 300);
        JTextField timeBetweenFramesText = new JTextField();
        timeBetweenFramesText.setBounds(300, 270, 130, 25);
        timeBetweenFramesText.setText("30");
        elements.put("timeBetweenFramesLabel", timeBetweenFramesLabel);
        elements.put("timeBetweenFramesText", timeBetweenFramesText);

        JLabel filePickerLabel = new JLabel();
        filePickerLabel.setText("Pick an output filepath");
        filePickerLabel.setBounds(50, 170, 500, 300);
        JTextField filePickerText = new JTextField();
        filePickerText.setBounds(180, 305, 200, 25);
        JButton filePickerButton = new JButton();
        filePickerButton.setText("Browse");
        filePickerButton.setBounds(380, 305, 80, 25);
        JFileChooser filePickerChooser = new JFileChooser();
        filePickerChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        filePickerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (filePickerChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    filePickerText.setText(filePickerChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });
        elements.put("filePickerLabel", filePickerLabel);
        elements.put("filePickerText", filePickerText);
        elements.put("filePickerButton", filePickerButton);

        statusLabel.setBounds(160, 200, 500, 300);
        elements.put("statusLabel", statusLabel);

        return elements;
    }

    /**
     * Configure the JFrame with all elements
     */
    public static void configureFrame(Map<String, JComponent> elements) {
        for (Map.Entry j : elements.entrySet()) {
            f.add((JComponent)j.getValue());
        }
        f.setSize(500, 500);
        f.setLayout(null);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Shows the UI for the program and handles input
     */
    private static void showStartUI() {
        Map<String, JComponent> elements = getElements();
        configureFrame(elements);

        ((JButton)elements.get("gifButton")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (validateInput(elements)) {
                    try {
                        setPaths(((JTextField)elements.get("filePickerText")).getText());
                        double x = Double.parseDouble(((JTextField)elements.get("xText")).getText());
                        double y = Double.parseDouble(((JTextField)elements.get("yText")).getText());
                        double zoomFactor = Double.parseDouble(((JTextField)elements.get("zoomFactorText")).getText());
                        int numImages = Integer.parseInt(((JTextField)elements.get("numImagesText")).getText());
                        int iterations = Integer.parseInt(((JTextField)elements.get("iterationsText")).getText());
                        double initialZoom = Double.parseDouble(((JTextField)elements.get("initialZoomText")).getText());
                        int timeBetweenFramesMS = fpsToMs(Integer.parseInt(((JTextField)elements.get("timeBetweenFramesText")).getText()));
                        GifController.makeGifWithThreads(numImages, 1920, 1080, iterations, initialZoom, zoomFactor, x, y, 10, timeBetweenFramesMS);
                    } catch (Exception e) {
                        System.out.println("GIF Creation Failed!");
                    }
                }
            }
        });

        ((JButton)elements.get("mp4Button")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (validateInput(elements)) {
                    try {
                        setPaths(((JTextField)elements.get("filePickerText")).getText());
                        double x = Double.parseDouble(((JTextField)elements.get("xText")).getText());
                        double y = Double.parseDouble(((JTextField)elements.get("yText")).getText());
                        double zoomFactor = Double.parseDouble(((JTextField)elements.get("zoomFactorText")).getText());
                        int numImages = Integer.parseInt(((JTextField)elements.get("numImagesText")).getText());
                        int iterations = Integer.parseInt(((JTextField)elements.get("iterationsText")).getText());
                        double initialZoom = Double.parseDouble(((JTextField)elements.get("initialZoomText")).getText());
                        int fps = Integer.parseInt(((JTextField)elements.get("timeBetweenFramesText")).getText());
                        GifController.makeMp4WithThreads(numImages, 1920, 1080, iterations, initialZoom, zoomFactor, x, y, 10, fps);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        for (StackTraceElement trace : e.getStackTrace()) {
                            System.out.println(trace);
                        }
                        System.out.println("GIF Creation Failed!");
                    }
                }
            }
        });

        ((JButton)elements.get("imgButton")).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (validateInput(elements)) {
                    try {
                        double x = Double.parseDouble(((JTextField)elements.get("xText")).getText());
                        double y = Double.parseDouble(((JTextField)elements.get("yText")).getText());
                        int iterations = Integer.parseInt(((JTextField)elements.get("iterationsText")).getText());
                        double initialZoom = Double.parseDouble(((JTextField)elements.get("initialZoomText")).getText());
                        ImageIO.write(ImageController.createZoomedImage(1920, 1080, iterations, initialZoom, x, y),
                                "png", new File(finalOutputDir, "mandelbrot.png"));
                        updateStatusLabel("Image created!");
                    } catch (Exception e) {
                        System.out.println("Image Creation Failed!");
                    }
                }
            }
        });
    }

    /**
     * Validates user input to prevent bad data, puts data on status label to inform user
     *
     * @return Whether or not all of the values in the input can be parsed properly
     */
    public static boolean validateInput(Map<String, JComponent> elements) {
        //Validate x
        try {
            double x = Double.parseDouble(((JTextField)elements.get("xText")).getText());
            if (x < -2 || x > 2) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            updateStatusLabel("X must be a number between -2 and 2");
            return false;
        }
        //Validate y
        try {
            double y = Double.parseDouble(((JTextField)elements.get("yText")).getText());
            if (y < -2 || y > 2) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            updateStatusLabel("Y must be a number between -2 and 2");
            return false;
        }
        //Validate zoomFactor
        try {
            Double.parseDouble(((JTextField)elements.get("zoomFactorText")).getText());
        } catch (NumberFormatException e) {
            updateStatusLabel("Zoom Factor must be a number");
            return false;
        }
        //Validate numImages
        try {
            int numImages = Integer.parseInt(((JTextField)elements.get("numImagesText")).getText());
            if (numImages <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            updateStatusLabel("Number of Images must be a positive integer");
            return false;
        }
        //Validate iterations
        try {
            int iterations = Integer.parseInt(((JTextField)elements.get("iterationsText")).getText());
            if (iterations <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            updateStatusLabel("Iterations must be a positive integer");
            return false;
        }
        //Validate initialZoom
        try {
            double initialZoom = Double.parseDouble(((JTextField)elements.get("initialZoomText")).getText());
            if (initialZoom <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            updateStatusLabel("Initial Zoom must be a positive number");
            return false;
        }
        //Validate fps
        try {
            int fps = Integer.parseInt(((JTextField)elements.get("timeBetweenFramesText")).getText());
            if (fps <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            updateStatusLabel("Frames per Second must be a positive integer");
            return false;
        }
        //TODO: Validate path in filePickerText (needs to exist)

        return true;
    }
}
