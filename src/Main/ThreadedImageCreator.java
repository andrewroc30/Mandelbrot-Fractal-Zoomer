package Main;

import Controller.*;
import Utils.ZoomedImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ThreadedImageCreator implements Runnable {

    private Thread t;           /* This Thread */
    private File outputFile;    /* The File to save the created image to */
    private String inputStr;    /* The fields in a comma delimited String */
    private int width;          /* The width of the image to create */
    private int height;         /* The height of the image to create */
    private int iterations;     /* The number of iterations to use in the image */
    private double zoom;        /* The zoom of the image to create */
    private double x;           /* The x-coordinate of the center of the image to create */
    private double y;           /* The y-coordinate of the center of the image to create */

    /**
     * Constructor which sets most fields from the comma delimited String input
     * @param input     The comma delimited String which contains values to set fields to
     */
    public ThreadedImageCreator(String input) {
        this.inputStr = input;
        String[] inputs = input.split(",");
        this.width = Integer.parseInt(inputs[0]);
        this.height = Integer.parseInt(inputs[1]);
        this.iterations = Integer.parseInt(inputs[2]);
        this.zoom = Double.parseDouble(inputs[3]);
        this.x = Double.parseDouble(inputs[4]);
        this.y = Double.parseDouble(inputs[5]);
        this.outputFile = new File(Main.tempImageDir, inputs[6]);
    }

    /**
     * Runs the work for this thread, which is to create and save an image
     */
    public void run() {
        try {
            ZoomedImage zi = ImageController.createZoomedImage(this.width, this.height, this.iterations, this.zoom, this.x, this.y);
            //ZoomedImage zi = ImageController.createSmoothZoomedImage(this.width, this.height, this.iterations, this.zoom, this.x, this.y);
            if (zi == null) {
                System.out.println("Cancelling thread with zoom " + this.zoom);
                return;
            }

            BufferedImage image = zi.image;
            try {
                Main.arrayPushLock.lock();
                ImageIO.write(image, "png", outputFile);
                GifController.numImagesCreated++;
                Main.updateStatusLabel("Images created: " + (GifController.numImagesCreated) + "/" + GifController.numImagesToCreate);
            } catch (Exception e) {
                System.out.println("THREAD FAILED");
            } finally {
                Main.arrayPushLock.unlock();
                changeNumThreads(false);
            }
        } catch (Error e) {
            System.out.println("Couldn't create image!");
        }
    }

    /**
     * Either increments or decrements numActiveThreads in a thread safe manner
     * @param increment     If true then increment, decrement otherwise
     */
    public static void changeNumThreads(boolean increment) {
        Main.numThreadsLock.lock();
        try {
            if(increment) {
                GifController.numActiveThreads++;
            } else {
                GifController.numActiveThreads--;
            }
        } finally {
            Main.numThreadsLock.unlock();
        }
    }

    /**
     * Initializes this Thread
     */
    public void start() {
        if (t == null) {
            t = new Thread(this, inputStr);
            t.start();
        }
    }
}
