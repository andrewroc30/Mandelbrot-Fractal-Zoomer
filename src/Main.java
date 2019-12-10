import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.*;

public class Main {

    private static volatile JLabel statusLabel;
    private static JFrame f = new JFrame("Mandelbrot Image Zoomer");
    private static boolean isPaused = true;
    private static boolean isCancelled = false;
    private static boolean isCancelledForce = false;
    public static ReentrantLock arrayPushLock = new ReentrantLock();
    public static ReentrantLock numThreadsLock = new ReentrantLock();
    public static File tempImageDir;
    public static File finalOutputDir;

    public static void main(String[] args) {
        showStartUI();
    }

    /**
     * Gets whether or not the creation is paused
     * @return boolean of whether we are paused
     */
    public static boolean getIsPaused() {
        return isPaused;
    }

    /**
     * Gets whether or not the rest of the image creation has been cancelled (after finishing current images)
     * @return boolean of whether the rest of the images have been cancelled
     */
    public static boolean getIsCancelled() {
        return isCancelled;
    }

    /**
     * Gets whether or not the rest of the image creation has been force cancelled (images in creation lost)
     * @return boolean of whether the rest of the images have been force cancelled
     */
    public static boolean getIsCancelledForce() {
        return isCancelledForce;
    }

    /**
     * Updates the UI Status Label with the given String
     * @param status The new status to set the UI Status Label to
     */
    public static void updateStatusLabel(String status) {
        System.out.println(status);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                statusLabel.setText(status);
                f.update(f.getGraphics());
            }
        });
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

        // Create GIF Button
        JButton gifButton = new JButton("Create GIF");
        gifButton.setBounds(20, 600, 150, 50);
        elements.put("gifButton", gifButton);

        // Create MP4 Button
        JButton mp4Button = new JButton("Create MP4");
        mp4Button.setBounds(170, 600, 150, 50);
        elements.put("mp4Button", mp4Button);

        // Create PNG Button
        JButton imgButton = new JButton("Create PNG");
        imgButton.setBounds(320, 600, 150, 50);
        elements.put("imgButton", imgButton);

        // X-Coordinate Field
        JLabel xLabel = new JLabel();
        xLabel.setText("Enter X-Coordinate");
        xLabel.setBounds(50, 10, 500, 100);
        JTextField xText = new JTextField();
        xText.setBounds(300, 50, 130, 25);
        xText.setText("0.250004192545193613127858564129342013402481966322603088153880158130118342411377044460335903569109029974830577473040521791862202620804388057367031844851715");
        elements.put("xLabel", xLabel);
        elements.put("xText", xText);

        // Y-Coordinate Field
        JLabel yLabel = new JLabel();
        yLabel.setText("Enter Y-Coordinate");
        yLabel.setBounds(50, -50, 500, 300);
        JTextField yText = new JTextField();
        yText.setBounds(300, 85, 130, 25);
        yText.setText("0.0000000136723440278498956363855799786211940098275946182822890638711641266657225239686535941616043103142296320806428032888628485431058181507295587901452113878999");
        elements.put("yLabel", yLabel);
        elements.put("yText", yText);

        // Zoom Factor Field
        JLabel zoomFactorLabel = new JLabel();
        zoomFactorLabel.setText("Enter Zoom Factor");
        zoomFactorLabel.setBounds(50, -10, 500, 300);
        JTextField zoomFactorText = new JTextField();
        zoomFactorText.setBounds(300, 125, 130, 25);
        zoomFactorText.setText("1.1");
        elements.put("zoomFactorLabel", zoomFactorLabel);
        elements.put("zoomFactorText", zoomFactorText);

        // Number of Images Field
        JLabel numImagesLabel = new JLabel();
        numImagesLabel.setText("Enter Number of Images");
        numImagesLabel.setBounds(50, 25, 500, 300);
        JTextField numImagesText = new JTextField();
        numImagesText.setBounds(300, 160, 130, 25);
        numImagesText.setText("200");
        elements.put("numImagesLabel", numImagesLabel);
        elements.put("numImagesText", numImagesText);

        // Iterations Field
        JLabel iterationsLabel = new JLabel();
        iterationsLabel.setText("Enter Iterations");
        iterationsLabel.setBounds(50, 65, 500, 300);
        JTextField iterationsText = new JTextField();
        iterationsText.setBounds(300, 200, 130, 25);
        iterationsText.setText("10000");
        elements.put("iterationsLabel", iterationsLabel);
        elements.put("iterationsText", iterationsText);

        // Initial Zoom Field
        JLabel initialZoomLabel = new JLabel();
        initialZoomLabel.setText("Enter Initial Zoom");
        initialZoomLabel.setBounds(50, 100, 500, 300);
        JTextField initialZoomText = new JTextField();
        initialZoomText.setBounds(300, 235, 130, 25);
        initialZoomText.setText("1");
        elements.put("initialZoomLabel", initialZoomLabel);
        elements.put("initialZoomText", initialZoomText);

        // Frames per Second Field
        JLabel timeBetweenFramesLabel = new JLabel();
        timeBetweenFramesLabel.setText("Enter Frames Per Second");
        timeBetweenFramesLabel.setBounds(50, 135, 500, 300);
        JTextField timeBetweenFramesText = new JTextField();
        timeBetweenFramesText.setBounds(300, 270, 130, 25);
        timeBetweenFramesText.setText("30");
        elements.put("timeBetweenFramesLabel", timeBetweenFramesLabel);
        elements.put("timeBetweenFramesText", timeBetweenFramesText);

        // Dimensions Field
        JLabel dimensionsLabel = new JLabel();
        JLabel dimensionsSplitLabel = new JLabel();
        dimensionsLabel.setText("Dimensions");
        dimensionsSplitLabel.setText("x");
        dimensionsLabel.setBounds(50, 170, 500, 300);
        dimensionsSplitLabel.setBounds(330, 165, 500, 300);
        JTextField dimensionsTextX = new JTextField();
        JTextField dimensionsTextY = new JTextField();
        dimensionsTextX.setBounds(240, 305, 75, 25);
        dimensionsTextY.setBounds(355, 305, 75, 25);
        dimensionsTextX.setText("1920");
        dimensionsTextY.setText("1080");
        elements.put("dimensionsLabel", dimensionsLabel);
        elements.put("dimensionsSplitLabel", dimensionsSplitLabel);
        elements.put("dimensionsTextX", dimensionsTextX);
        elements.put("dimensionsTextY", dimensionsTextY);

        // Output Folder Field
        JLabel filePickerLabel = new JLabel();
        filePickerLabel.setText("Pick output folder");
        filePickerLabel.setBounds(50, 205, 500, 300);
        JTextField filePickerText = new JTextField();
        filePickerText.setBounds(180, 340, 200, 25);
        JButton filePickerButton = new JButton();
        filePickerButton.setText("Browse");
        filePickerButton.setBounds(380, 340, 80, 25);
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

        // Pause/Resume Button
        JButton playPauseButton = new JButton();
        playPauseButton.setText("Pause");
        playPauseButton.setBounds(50, 540, 100, 25);
        playPauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isPaused = !isPaused;
                if (isPaused) {
                    playPauseButton.setText("Resume");
                }
                else {
                    playPauseButton.setText("Pause");
                }
                System.out.println("isPaused: " + isPaused);
            }
        });
        elements.put("playPauseButton", playPauseButton);

        // Cancel Button
        JButton cancelButton = new JButton();
        cancelButton.setText("Cancel");
        cancelButton.setBounds(200, 540, 100, 25);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isCancelled = !isCancelled;
                System.out.println("isCancelled: " + isCancelled);
            }
        });
        elements.put("cancelButton", cancelButton);

        // Force Cancel Button
        JButton cancelButtonForce = new JButton();
        cancelButtonForce.setText("Force Cancel");
        cancelButtonForce.setBounds(350, 540, 100, 25);
        cancelButtonForce.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isCancelledForce = !isCancelledForce;
                System.out.println("isCancelledForce: " + isCancelledForce);
            }
        });
        elements.put("cancelButtonForce", cancelButtonForce);

        // Status Label
        statusLabel = new JLabel();
        statusLabel.setBounds(160, 360, 500, 300);
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
        f.setSize(500, 700);
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
                        int dimX = Integer.parseInt(((JTextField)elements.get("dimensionsTextX")).getText());
                        int dimY = Integer.parseInt(((JTextField)elements.get("dimensionsTextY")).getText());
                        int maxThreads = Runtime.getRuntime().availableProcessors() - 2;
                        System.out.println("Using " + maxThreads + " threads");
                        SwingWorker worker = new SwingWorker() {
                            @Override
                            protected Boolean doInBackground() throws Exception {
                                isPaused = false;
                                isCancelled = false;
                                ((JButton)elements.get("playPauseButton")).setText("Pause");
                                GifController.makeGifWithThreads(numImages, dimX, dimY, iterations, initialZoom, zoomFactor, x, y, maxThreads, timeBetweenFramesMS);
                                isPaused = true;
                                isCancelled = false;
                                ((JButton)elements.get("playPauseButton")).setText("Pause");
                                return true;
                            }
                        };
                        worker.execute();
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
                        int dimX = Integer.parseInt(((JTextField)elements.get("dimensionsTextX")).getText());
                        int dimY = Integer.parseInt(((JTextField)elements.get("dimensionsTextY")).getText());
                        int maxThreads = Runtime.getRuntime().availableProcessors() - 2;
                        System.out.println("Using " + maxThreads + " threads");
                        SwingWorker worker = new SwingWorker() {
                            @Override
                            protected Boolean doInBackground() throws Exception {
                                isPaused = false;
                                isCancelled = false;
                                isCancelledForce = false;
                                ((JButton)elements.get("playPauseButton")).setText("Pause");
                                GifController.makeMp4WithThreads(numImages, dimX, dimY, iterations, initialZoom, zoomFactor, x, y, maxThreads, fps);
                                isPaused = true;
                                isCancelled = false;
                                isCancelledForce = false;
                                ((JButton)elements.get("playPauseButton")).setText("Pause");
                                return true;
                            }
                        };
                        worker.execute();
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
                        setPaths(((JTextField)elements.get("filePickerText")).getText());
                        double x = Double.parseDouble(((JTextField)elements.get("xText")).getText());
                        double y = Double.parseDouble(((JTextField)elements.get("yText")).getText());
                        int iterations = Integer.parseInt(((JTextField)elements.get("iterationsText")).getText());
                        double initialZoom = Double.parseDouble(((JTextField)elements.get("initialZoomText")).getText());
                        int dimX = Integer.parseInt(((JTextField)elements.get("dimensionsTextX")).getText());
                        int dimY = Integer.parseInt(((JTextField)elements.get("dimensionsTextY")).getText());
                        isPaused = false;
                        BufferedImage image = ImageController.createZoomedImage(dimX, dimY, iterations, initialZoom, x, y);
                        if (image != null) {
                            ImageIO.write(image, "png", new File(finalOutputDir, "mandelbrot.png"));
                            updateStatusLabel("Image created!");
                        } else {
                            updateStatusLabel("Image cancelled!");
                        }
                        isPaused = true;
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
        //Check if we are already doing something
        if (!isPaused) {
            updateStatusLabel("Please wait for operation to finish");
            return false;
        }
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
        //Validate dimensions
        try {
            int dimX = Integer.parseInt(((JTextField)elements.get("dimensionsTextX")).getText());
            int dimY = Integer.parseInt(((JTextField)elements.get("dimensionsTextY")).getText());
            if (dimX <= 0 || dimY <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            updateStatusLabel("Dimensions must be positive integers");
            return false;
        }
        //Validate path in filePickerText
        File f = new File(((JTextField)elements.get("filePickerText")).getText());
        if (!(f.exists() && f.isDirectory())) {
            updateStatusLabel("Not a valid output folder!");
            return false;
        }

        return true;
    }
}
