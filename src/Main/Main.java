package Main;

import View.*;

import java.io.File;
import java.util.concurrent.locks.*;

public class Main {

    public static StartWindow startWindow = new StartWindow();
    public static GIFWindow gifWindow = new GIFWindow();
    public static MP4Window mp4Window = new MP4Window();
    public static PNGWindow pngWindow = new PNGWindow();
    public static ExploreWindow exploreWindow = new ExploreWindow();
    private static boolean isPaused = true;
    private static boolean isCancelled = false;
    private static boolean isCancelledForce = false;
    public static ReentrantLock arrayPushLock = new ReentrantLock();
    public static ReentrantLock numThreadsLock = new ReentrantLock();
    public static File tempImageDir;
    public static File finalOutputDir;
    public static String statusType;

    public static void main(String[] args) {
        startWindow.showStartWindow();
    }

    /**
     * Gets whether or not the creation is paused
     * @return boolean of whether we are paused
     */
    public static boolean getIsPaused() {
        return isPaused;
    }

    /**
     * Sets whether or not the creation is paused
     * @param set boolean of whether we are paused
     */
    public static void setIsPaused(boolean set) {
        isPaused = set;
    }

    /**
     * Gets whether or not the rest of the image creation has been cancelled (after finishing current images)
     * @return boolean of whether the rest of the images have been cancelled
     */
    public static boolean getIsCancelled() {
        return isCancelled;
    }

    /**
     * Sets whether or not the rest of the image creation has been cancelled (after finishing current images)
     * @param set boolean of whether the rest of the images have been cancelled
     */
    public static void setIsCancelled(boolean set) {
        isCancelled = set;
    }

    /**
     * Gets whether or not the rest of the image creation has been force cancelled (images in creation lost)
     * @return boolean of whether the rest of the images have been force cancelled
     */
    public static boolean getIsCancelledForce() {
        return isCancelledForce;
    }

    /**
     * Sets whether or not the rest of the image creation has been force cancelled (images in creation lost)
     * @param set boolean of whether the rest of the images have been force cancelled
     */
    public static void setIsCancelledForce(boolean set) {
        isCancelledForce = set;
    }

    /**
     * Updates the status label of the UI window
     * @param status The status to set the status label to
     */
    public static void updateStatusLabel(String status) {
        if (statusType.equals("gif")) {
            gifWindow.updateStatusLabel(status);
        } else if (statusType.equals("mp4")) {
            mp4Window.updateStatusLabel(status);
        } else if (statusType.equals("png")) {
            pngWindow.updateStatusLabel(status);
        } else if (statusType.equals("explore")) {
            //exploreWindow.updateStatusLabel(status);
        }
    }

    /**
     * Sets the file paths for the temporary images and final output
     *
     * @param outputDirectory The user chosen output directory
     */
    public static void setPaths(String outputDirectory) {
        tempImageDir = new File(outputDirectory, "tempImages");
        finalOutputDir = new File(outputDirectory);
    }
}
