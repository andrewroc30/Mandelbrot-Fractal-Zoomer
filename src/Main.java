import java.io.File;
import java.util.concurrent.locks.*;

public class Main {

    static StartWindow startWindow = new StartWindow();
    static GIFWindow gifWindow = new GIFWindow();
    static MP4Window mp4Window = new MP4Window();
    static PNGWindow pngWindow = new PNGWindow();
    //static ExploreWindow exploreWindow = new ExploreWindow();
    private static boolean isPaused = true;
    private static boolean isCancelled = false;
    private static boolean isCancelledForce = false;
    static ReentrantLock arrayPushLock = new ReentrantLock();
    static ReentrantLock numThreadsLock = new ReentrantLock();
    static File tempImageDir;
    static File finalOutputDir;
    static String statusType;

    public static void main(String[] args) {
        //uiWindow.showStartUI();
        startWindow.showStartWindow();
    }

    /**
     * Gets whether or not the creation is paused
     * @return boolean of whether we are paused
     */
    static boolean getIsPaused() {
        return isPaused;
    }

    /**
     * Sets whether or not the creation is paused
     * @param set boolean of whether we are paused
     */
    static void setIsPaused(boolean set) {
        isPaused = set;
    }

    /**
     * Gets whether or not the rest of the image creation has been cancelled (after finishing current images)
     * @return boolean of whether the rest of the images have been cancelled
     */
    static boolean getIsCancelled() {
        return isCancelled;
    }

    /**
     * Sets whether or not the rest of the image creation has been cancelled (after finishing current images)
     * @param set boolean of whether the rest of the images have been cancelled
     */
    static void setIsCancelled(boolean set) {
        isCancelled = set;
    }

    /**
     * Gets whether or not the rest of the image creation has been force cancelled (images in creation lost)
     * @return boolean of whether the rest of the images have been force cancelled
     */
    static boolean getIsCancelledForce() {
        return isCancelledForce;
    }

    /**
     * Sets whether or not the rest of the image creation has been force cancelled (images in creation lost)
     * @param set boolean of whether the rest of the images have been force cancelled
     */
    static void setIsCancelledForce(boolean set) {
        isCancelledForce = set;
    }

    /**
     * Updates the status label of the UI window
     * @param status The status to set the status label to
     */
    static void updateStatusLabel(String status) {
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
     * Converts the given frames per second into ms between frames
     * @param fps The frames per second of the GIF
     * @return Integer containing number of milliseconds between frames
     */
    static int fpsToMs(int fps) {
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
    static void setPaths(String outputDirectory) {
        tempImageDir = new File(outputDirectory, "tempImages");
        finalOutputDir = new File(outputDirectory);
    }
}
