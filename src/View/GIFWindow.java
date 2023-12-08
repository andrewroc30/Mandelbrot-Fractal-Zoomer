package View;

import Controller.GifController;
import Main.Main;

import javax.swing.*;

public class GIFWindow extends AbstractCreatorWindow {

    public GIFWindow() {
        super("Create a GIF");
    }

    /**
     * Initializes this window with all its components
     */
    public void initWindow() {
        super.initializeWindow();

        // GIF Button
        this.createButton = new JButton();
        this.createButton.setText("Create GIF");
        this.createButton.setBounds(170, 600, 150, 50);
        this.createButton.addActionListener(e -> {
            System.out.println("Create a GIF");
            if (validateInput()) {
                try {
                    Main.statusType = "gif";
                    Main.setPaths(this.filePickerText.getText());
                    CreationWindow creationWindow = this;
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
                            GifController.makeGifWithThreads(creationWindow, numImages, dimX, dimY, iterations, initialZoom, zoomFactor, x, y, maxThreads, timeBetweenFramesMS);
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
        this.add(this.createButton);
    }
}
