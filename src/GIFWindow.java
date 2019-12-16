import javax.swing.*;
import java.io.File;

public class GIFWindow extends JFrame {
    private JLabel xLabel;
    private JTextField xText;

    private JLabel yLabel;
    private JTextField yText;

    private JLabel zoomFactorLabel;
    private JTextField zoomFactorText;

    private JLabel numImagesLabel;
    private JTextField numImagesText;

    private JLabel iterationsLabel;
    private JTextField iterationsText;

    private JLabel initialZoomLabel;
    private JTextField initialZoomText;

    private JLabel timeBetweenFramesLabel;
    private JTextField timeBetweenFramesText;

    private JLabel dimensionsLabel;
    private JLabel dimensionsSplitLabel;
    private JTextField dimensionsTextX;
    private JTextField dimensionsTextY;

    private JLabel filePickerLabel;
    private JTextField filePickerText;
    private JButton filePickerButton;
    private JFileChooser filePickerChooser;

    private JButton playPauseButton;
    private JButton cancelButton;
    private JButton cancelButtonForce;

    private JLabel statusLabel;

    private JButton gifButton;

    public GIFWindow() {
        super("Create a GIF");
    }

    /**
     * Updates the Status Label with the given String
     * @param status The new status to set the Status Label to
     */
    void updateStatusLabel(String status) {
        System.out.println(status);
        SwingUtilities.invokeLater(() -> {
            this.statusLabel.setText(status);
            repaint();
        });
    }


    /**
     * Initializes this window with all its components
     */
    public void initWindow() {
        // Basic Window Settings
        this.setSize(500, 700);
        this.setLayout(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // X-Coordinate Field
        this.xLabel = new JLabel();
        this.xLabel.setText("Enter X-Coordinate");
        this.xLabel.setBounds(50, 10, 500, 100);
        this.xText = new JTextField();
        this.xText.setBounds(300, 50, 130, 25);
        this.xText.setText("0.250004192545193613127858564129342013402481966322603088153880158130118342411377044460335903569109029974830577473040521791862202620804388057367031844851715");
        this.add(this.xLabel);
        this.add(this.xText);

        // Y-Coordinate Field
        this.yLabel = new JLabel();
        this.yLabel.setText("Enter Y-Coordinate");
        this.yLabel.setBounds(50, -50, 500, 300);
        this.yText = new JTextField();
        this.yText.setBounds(300, 85, 130, 25);
        this.yText.setText("0.0000000136723440278498956363855799786211940098275946182822890638711641266657225239686535941616043103142296320806428032888628485431058181507295587901452113878999");
        this.add(this.yLabel);
        this.add(this.yText);

        // Zoom Factor Field
        this.zoomFactorLabel = new JLabel();
        this.zoomFactorLabel.setText("Enter Zoom Factor");
        this.zoomFactorLabel.setBounds(50, -10, 500, 300);
        this.zoomFactorText = new JTextField();
        this.zoomFactorText.setBounds(300, 125, 130, 25);
        this.zoomFactorText.setText("1.1");
        this.add(this.zoomFactorLabel);
        this.add(this.zoomFactorText);

        // Number of Images Field
        this.numImagesLabel = new JLabel();
        this.numImagesLabel.setText("Enter Number of Images");
        this.numImagesLabel.setBounds(50, 25, 500, 300);
        this.numImagesText = new JTextField();
        this.numImagesText.setBounds(300, 160, 130, 25);
        this.numImagesText.setText("200");
        this.add(this.numImagesLabel);
        this.add(this.numImagesText);

        // Iterations Field
        this.iterationsLabel = new JLabel();
        this.iterationsLabel.setText("Enter Iterations");
        this.iterationsLabel.setBounds(50, 65, 500, 300);
        this.iterationsText = new JTextField();
        this.iterationsText.setBounds(300, 200, 130, 25);
        this.iterationsText.setText("10000");
        this.add(this.iterationsLabel);
        this.add(this.iterationsText);

        // Initial Zoom Field
        this.initialZoomLabel = new JLabel();
        this.initialZoomLabel.setText("Enter Initial Zoom");
        this.initialZoomLabel.setBounds(50, 100, 500, 300);
        this.initialZoomText = new JTextField();
        this.initialZoomText.setBounds(300, 235, 130, 25);
        this.initialZoomText.setText("1");
        this.add(this.initialZoomLabel);
        this.add(this.initialZoomText);

        // Frames per Second Field
        this.timeBetweenFramesLabel = new JLabel();
        this.timeBetweenFramesLabel.setText("Enter Frames Per Second");
        this.timeBetweenFramesLabel.setBounds(50, 135, 500, 300);
        this.timeBetweenFramesText = new JTextField();
        this.timeBetweenFramesText.setBounds(300, 270, 130, 25);
        this.timeBetweenFramesText.setText("30");
        this.add(this.timeBetweenFramesLabel);
        this.add(this.timeBetweenFramesText);

        // Dimensions Field
        this.dimensionsLabel = new JLabel();
        this.dimensionsSplitLabel = new JLabel();
        this.dimensionsLabel.setText("Dimensions");
        this.dimensionsSplitLabel.setText("x");
        this.dimensionsLabel.setBounds(50, 170, 500, 300);
        this.dimensionsSplitLabel.setBounds(330, 165, 500, 300);
        this.dimensionsTextX = new JTextField();
        this.dimensionsTextY = new JTextField();
        this.dimensionsTextX.setBounds(240, 305, 75, 25);
        this.dimensionsTextY.setBounds(355, 305, 75, 25);
        this.dimensionsTextX.setText("1920");
        this.dimensionsTextY.setText("1080");
        this.add(this.dimensionsLabel);
        this.add(this.dimensionsSplitLabel);
        this.add(this.dimensionsTextX);
        this.add(this.dimensionsTextY);

        // Output Folder Field
        this.filePickerLabel = new JLabel();
        this.filePickerLabel.setText("Pick output folder");
        this.filePickerLabel.setBounds(50, 205, 500, 300);
        this.filePickerText = new JTextField();
        this.filePickerText.setBounds(180, 340, 200, 25);
        this.filePickerButton = new JButton();
        this.filePickerButton.setText("Browse");
        this.filePickerButton.setBounds(380, 340, 80, 25);
        this.filePickerChooser = new JFileChooser();
        this.filePickerChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        this.filePickerButton.addActionListener(evt -> {
            if (this.filePickerChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                this.filePickerText.setText(this.filePickerChooser.getSelectedFile().getAbsolutePath());
            }
        });
        this.add(this.filePickerLabel);
        this.add(this.filePickerText);
        this.add(this.filePickerButton);
        this.add(this.filePickerChooser);

        // Pause/Resume Button
        this.playPauseButton = new JButton();
        this.playPauseButton.setText("Pause");
        this.playPauseButton.setBounds(50, 540, 100, 25);
        this.playPauseButton.addActionListener(e -> {
            Main.setIsPaused(!Main.getIsPaused());
            if (Main.getIsPaused()) {
                this.playPauseButton.setText("Resume");
            }
            else {
                this.playPauseButton.setText("Pause");
            }
            System.out.println("isPaused: " + Main.getIsPaused());
        });
        this.add(this.playPauseButton);

        // Cancel Button
        this.cancelButton = new JButton();
        this.cancelButton.setText("Cancel");
        this.cancelButton.setBounds(200, 540, 100, 25);
        this.cancelButton.addActionListener(e -> {
            Main.setIsCancelled(!Main.getIsCancelled());
            System.out.println("isCancelled: " + Main.getIsCancelled());
        });
        this.add(this.cancelButton);

        // Force Cancel Button
        this.cancelButtonForce = new JButton();
        this.cancelButtonForce.setText("Force Cancel");
        this.cancelButtonForce.setBounds(350, 540, 100, 25);
        this.cancelButtonForce.addActionListener(e -> {
            Main.setIsCancelledForce(!Main.getIsCancelledForce());
            System.out.println("isCancelledForce: " + Main.getIsCancelledForce());
        });
        this.add(cancelButtonForce);

        // Status Label
        this.statusLabel = new JLabel();
        this.statusLabel.setBounds(160, 360, 500, 300);
        this.add(this.statusLabel);

        // GIF Button
        this.gifButton = new JButton();
        this.gifButton.setText("Create GIF");
        this.gifButton.setBounds(170, 600, 150, 50);
        this.gifButton.addActionListener(e -> {
            System.out.println("Create a GIF");
            if (validateInput()) {
                try {
                    Main.statusType = "gif";
                    Main.setPaths(this.filePickerText.getText());
                    double x = Double.parseDouble(this.xText.getText());
                    double y = Double.parseDouble(this.yText.getText());
                    double zoomFactor = Double.parseDouble(this.zoomFactorText.getText());
                    int numImages = Integer.parseInt(this.numImagesText.getText());
                    int iterations = Integer.parseInt(this.iterationsText.getText());
                    double initialZoom = Double.parseDouble(this.initialZoomText.getText());
                    int timeBetweenFramesMS = Main.fpsToMs(Integer.parseInt(this.timeBetweenFramesText.getText()));
                    int dimX = Integer.parseInt(this.dimensionsTextX.getText());
                    int dimY = Integer.parseInt(this.dimensionsTextY.getText());
                    int maxThreads = Runtime.getRuntime().availableProcessors() - 2;
                    System.out.println("Using " + maxThreads + " threads");
                    SwingWorker worker = new SwingWorker() {
                        @Override
                        protected Boolean doInBackground() throws Exception {
                            Main.setIsPaused(false);
                            Main.setIsCancelled(false);
                            Main.setIsCancelledForce(false);
                            playPauseButton.setText("Pause");
                            GifController.makeGifWithThreads(numImages, dimX, dimY, iterations, initialZoom, zoomFactor, x, y, maxThreads, timeBetweenFramesMS);
                            Main.setIsPaused(true);
                            Main.setIsCancelled(false);
                            Main.setIsCancelledForce(false);
                            playPauseButton.setText("Pause");
                            return true;
                        }
                    };
                    worker.execute();
                } catch (Exception ex) {
                    System.out.println("GIF Creation Failed!");
                }
            }
        });
        this.add(this.gifButton);
    }

    /**
     * Validates user input to prevent bad data, puts data on status label to inform user
     *
     * @return Whether or not all of the values in the input can be parsed properly
     */
    private boolean validateInput() {
        //Check if we are already doing something
        if (!Main.getIsPaused()) {
            updateStatusLabel("Please wait for operation to finish");
            return false;
        }
        //Validate x
        try {
            double x = Double.parseDouble(xText.getText());
            if (x < -2 || x > 2) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            updateStatusLabel("X must be a number between -2 and 2");
            return false;
        }
        //Validate y
        try {
            double y = Double.parseDouble(yText.getText());
            if (y < -2 || y > 2) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            updateStatusLabel("Y must be a number between -2 and 2");
            return false;
        }
        //Validate zoomFactor
        try {
            Double.parseDouble(zoomFactorText.getText());
        } catch (NumberFormatException e) {
            updateStatusLabel("Zoom Factor must be a number");
            return false;
        }
        //Validate numImages
        try {
            int numImages = Integer.parseInt(numImagesText.getText());
            if (numImages <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            updateStatusLabel("Number of Images must be a positive integer");
            return false;
        }
        //Validate iterations
        try {
            int iterations = Integer.parseInt(iterationsText.getText());
            if (iterations <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            updateStatusLabel("Iterations must be a positive integer");
            return false;
        }
        //Validate initialZoom
        try {
            double initialZoom = Double.parseDouble(initialZoomText.getText());
            if (initialZoom <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            updateStatusLabel("Initial Zoom must be a positive number");
            return false;
        }
        //Validate fps
        try {
            int fps = Integer.parseInt(timeBetweenFramesText.getText());
            if (fps <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            updateStatusLabel("Frames per Second must be a positive integer");
            return false;
        }
        //Validate dimensions
        try {
            int dimX = Integer.parseInt(dimensionsTextX.getText());
            int dimY = Integer.parseInt(dimensionsTextY.getText());
            if (dimX <= 0 || dimY <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            updateStatusLabel("Dimensions must be positive integers");
            return false;
        }
        //Validate path in filePickerText
        File f = new File(filePickerText.getText());
        if (!(f.exists() && f.isDirectory())) {
            updateStatusLabel("Not a valid output folder!");
            return false;
        }

        return true;
    }
}
