package View;

import Main.Main;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractCreatorWindow extends JFrame implements CreationWindow {

    protected JLabel xLabel;
    protected JTextField xText;
    protected JLabel yLabel;
    protected JTextField yText;
    protected JLabel zoomFactorLabel;
    protected JTextField zoomFactorText;
    protected JLabel numImagesLabel;
    protected JTextField numImagesText;
    protected JLabel iterationsLabel;
    protected JTextField iterationsText;
    protected JLabel initialZoomLabel;
    protected JTextField initialZoomText;
    protected JLabel timeBetweenFramesLabel;
    protected JTextField timeBetweenFramesText;
    protected JLabel dimensionsLabel;
    protected JLabel dimensionsSplitLabel;
    protected JTextField dimensionsTextX;
    protected JTextField dimensionsTextY;
    protected JLabel filePickerLabel;
    protected JTextField filePickerText;
    protected JButton filePickerButton;
    protected JFileChooser filePickerChooser;
    protected JButton playPauseButton;
    protected JButton cancelButton;
    protected JButton cancelButtonForce;
    protected JLabel statusLabel;
    protected JButton createButton;
    protected JPanel progressBarPanel;
    protected GridLayout progressBarGridLayout;
    protected final Map<String, JProgressBar> progressBarMap = new HashMap<>();

    public AbstractCreatorWindow(String title) {
        super(title);
    }

    protected void initializeWindow() {
        setBasicWindowSettings();
        setXCoordinateField();
        setYCoordinateField();
        setZoomFactorField();
        setNumImagesField();
        setIterationsField();
        setInitialZoomField();
        setFramesPerSecondField();
        setDimensionsField();
        setOutputFolderField();
        setPauseResumeButton();
        setCancelButton();
        setForceCancelButton();
        setStatusLabel();
        setProgressField();
    }

    private void setBasicWindowSettings() {
        setSize(500, 700);
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setXCoordinateField() {
        xLabel = new JLabel();
        xLabel.setText("Enter X-Coordinate");
        xLabel.setBounds(50, 10, 500, 100);
        xText = new JTextField();
        xText.setBounds(300, 50, 130, 25);
        xText.setText("0.250004192545193613127858564129342013402481966322603088153880158130118342411377044460335903569109029974830577473040521791862202620804388057367031844851715");
        add(xLabel);
        add(xText);
    }

    private void setYCoordinateField() {
        yLabel = new JLabel();
        yLabel.setText("Enter Y-Coordinate");
        yLabel.setBounds(50, -50, 500, 300);
        yText = new JTextField();
        yText.setBounds(300, 85, 130, 25);
        yText.setText("0.0000000136723440278498956363855799786211940098275946182822890638711641266657225239686535941616043103142296320806428032888628485431058181507295587901452113878999");
        add(yLabel);
        add(yText);
    }

    private void setZoomFactorField() {
        zoomFactorLabel = new JLabel();
        zoomFactorLabel.setText("Enter Zoom Factor");
        zoomFactorLabel.setBounds(50, -10, 500, 300);
        zoomFactorText = new JTextField();
        zoomFactorText.setBounds(300, 125, 130, 25);
        zoomFactorText.setText("1.1");
        add(zoomFactorLabel);
        add(zoomFactorText);
    }

    private void setNumImagesField() {
        numImagesLabel = new JLabel();
        numImagesLabel.setText("Enter Number of Images");
        numImagesLabel.setBounds(50, 25, 500, 300);
        numImagesText = new JTextField();
        numImagesText.setBounds(300, 160, 130, 25);
        numImagesText.setText("200");
        add(numImagesLabel);
        add(numImagesText);
    }

    private void setIterationsField() {
        iterationsLabel = new JLabel();
        iterationsLabel.setText("Enter Iterations");
        iterationsLabel.setBounds(50, 65, 500, 300);
        iterationsText = new JTextField();
        iterationsText.setBounds(300, 200, 130, 25);
        iterationsText.setText("10000");
        add(iterationsLabel);
        add(iterationsText);
    }

    private void setInitialZoomField() {
        initialZoomLabel = new JLabel();
        initialZoomLabel.setText("Enter Initial Zoom");
        initialZoomLabel.setBounds(50, 100, 500, 300);
        initialZoomText = new JTextField();
        initialZoomText.setBounds(300, 235, 130, 25);
        initialZoomText.setText("1");
        add(initialZoomLabel);
        add(initialZoomText);
    }

    private void setFramesPerSecondField() {
        timeBetweenFramesLabel = new JLabel();
        timeBetweenFramesLabel.setText("Enter Frames Per Second");
        timeBetweenFramesLabel.setBounds(50, 135, 500, 300);
        timeBetweenFramesText = new JTextField();
        timeBetweenFramesText.setBounds(300, 270, 130, 25);
        timeBetweenFramesText.setText("30");
        add(timeBetweenFramesLabel);
        add(timeBetweenFramesText);
    }

    private void setDimensionsField() {
        dimensionsLabel = new JLabel();
        dimensionsSplitLabel = new JLabel();
        dimensionsLabel.setText("Dimensions");
        dimensionsSplitLabel.setText("x");
        dimensionsLabel.setBounds(50, 170, 500, 300);
        dimensionsSplitLabel.setBounds(330, 165, 500, 300);
        dimensionsTextX = new JTextField();
        dimensionsTextY = new JTextField();
        dimensionsTextX.setBounds(240, 305, 75, 25);
        dimensionsTextY.setBounds(355, 305, 75, 25);
        dimensionsTextX.setText("1920");
        dimensionsTextY.setText("1080");
        add(dimensionsLabel);
        add(dimensionsSplitLabel);
        add(dimensionsTextX);
        add(dimensionsTextY);
    }

    private void setOutputFolderField() {
        String defaultDirectory = System.getProperty("user.home") + File.separator + "Documents";
        filePickerLabel = new JLabel();
        filePickerLabel.setText("Pick output folder");
        filePickerLabel.setBounds(50, 205, 500, 300);
        filePickerText = new JTextField();
        filePickerText.setBounds(180, 340, 200, 25);
        filePickerText.setText(defaultDirectory);
        filePickerButton = new JButton();
        filePickerButton.setText("Browse");
        filePickerButton.setBounds(380, 340, 80, 25);
        filePickerChooser = new JFileChooser();
        filePickerChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        filePickerChooser.setCurrentDirectory(new File(defaultDirectory));
        filePickerButton.addActionListener(evt -> {
            if (filePickerChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                filePickerText.setText(filePickerChooser.getSelectedFile().getAbsolutePath());
            }
        });
        add(filePickerLabel);
        add(filePickerText);
        add(filePickerButton);
        add(filePickerChooser);
    }

    private void setPauseResumeButton() {
        // Pause/Resume Button
        playPauseButton = new JButton();
        playPauseButton.setText("Pause");
        playPauseButton.setBounds(50, 540, 100, 25);
        playPauseButton.addActionListener(e -> {
            Main.setIsPaused(!Main.getIsPaused());
            if (Main.getIsPaused()) {
                playPauseButton.setText("Resume");
            }
            else {
                playPauseButton.setText("Pause");
            }
            System.out.println("isPaused: " + Main.getIsPaused());
        });
        add(playPauseButton);
    }

    private void setCancelButton() {
        cancelButton = new JButton();
        cancelButton.setText("Cancel");
        cancelButton.setBounds(200, 540, 100, 25);
        cancelButton.addActionListener(e -> {
            Main.setIsCancelled(!Main.getIsCancelled());
            System.out.println("isCancelled: " + Main.getIsCancelled());
        });
        add(cancelButton);
    }

    private void setForceCancelButton() {
        cancelButtonForce = new JButton();
        cancelButtonForce.setText("Force Cancel");
        cancelButtonForce.setBounds(350, 540, 100, 25);
        cancelButtonForce.addActionListener(e -> {
            Main.setIsCancelledForce(!Main.getIsCancelledForce());
            System.out.println("isCancelledForce: " + Main.getIsCancelledForce());
        });
        add(cancelButtonForce);
    }

    private void setProgressField() {
        progressBarPanel = new JPanel();
        Border border = BorderFactory.createLineBorder(Color.black);
        progressBarPanel.setBorder(border);
        progressBarGridLayout = new GridLayout(5, 0);
        progressBarPanel.setLayout(progressBarGridLayout);
        progressBarPanel.setBounds(10, 380, 450, 100);
        add(progressBarPanel);
    }

    private void setStatusLabel() {
        statusLabel = new JLabel();
        statusLabel.setBounds(160, 360, 500, 300);
        add(statusLabel);
    }

    /**
     * Updates the Status Label with the given String
     * @param status The new status to set the Status Label to
     */
    public void updateStatusLabel(String status) {
        System.out.println(status);
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(status);
            repaint();
        });
    }

    /**
     * Validates user input to prevent bad data, puts data on status label to inform user
     *
     * @return Whether all the values in the input can be parsed properly
     */
    protected boolean validateInput() {
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

    @Override
    public void addNewProgressBar(String filename) {
        // Add progress bar to window
        SwingUtilities.invokeLater(() -> {
            JProgressBar progressBar = new JProgressBar();
            progressBar.setMinimum(0);
            progressBar.setMaximum(100);
            progressBarMap.put(filename, progressBar);
            progressBarPanel.add(progressBar);
            setProgressBarGridLayoutDimensions();
        });
    }

    @Override
    public void updateProgress(String filename, int completionPercentage) {
        SwingUtilities.invokeLater(() -> {
            JProgressBar progressBar = progressBarMap.get(filename);
            if (Objects.nonNull(progressBar) && progressBar.getValue() < completionPercentage) {
                progressBar.setValue(completionPercentage);
                progressBar.update(progressBar.getGraphics());
            }
        });
    }

    @Override
    public void removeProgressBar(String filename) {
        // Remove the progress bar from the window
        SwingUtilities.invokeLater(() -> {
            JProgressBar progressBar = progressBarMap.remove(filename);
            progressBarPanel.remove(progressBar);
            setProgressBarGridLayoutDimensions();
        });
    }

    private void setProgressBarGridLayoutDimensions() {
        // Want to have the number of rows be 5 times the number of columns at large thread counts
        // Example: 100 elements = 20 rows of 5 columns
        // Also want a minimum of 5 rows at all times thread count is greater than 4
        int totalElements = progressBarMap.size();
        if (totalElements <= 25) {
            progressBarGridLayout.setRows(5);
        } else {
            progressBarGridLayout.setRows(totalElements / 5);
        }
    }

}
