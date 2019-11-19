import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.log;

public class GifController {

    public static ArrayList<BufferedImage> images = new ArrayList<>();
    public static ArrayList<ImageWithZoom> unorderedImages = new ArrayList<>();
    public static int numImagesCreated = 0;
    public static int numActiveThreads = 0;
    public static int numImagesToCreate = 0;

    /**
     * Creates a Mandelbrot GIF using the given settings
     * @param numImages The total number of images in the GIF
     * @param width The width in pixels of the GIF
     * @param height The height in pixels of the GIF
     * @param iterations The number if iterations used to determine a single pixel (higher iterations means better image)
     * @param zoom The starting zoom for the GIF
     * @param zoomFactor The factor by which the zoom is increased every image in the GIF
     * @param x The x-coordinate to zoom into
     * @param y The y-coordinate to zoom into
     * @throws Exception
     */
    public static void makeGifWithThreads(int numImages, int width, int height, int iterations, double zoom,
                                          double zoomFactor, double x, double y, int maxThreads) throws Exception {
        numImagesToCreate = numImages;
        long startTime = System.nanoTime();
        ThreadedImageCreator t;
        for (int i = 0; i < numImages; i++) {
            //Wait until there is another space for the thread to be made
            while(numActiveThreads >= maxThreads) {
                Thread.sleep(10);
            }
            t = new ThreadedImageCreator(Integer.toString(width) + "," + Integer.toString(height) + "," +
                    Integer.toString(iterations) + "," + Double.toString(zoom) + "," + Double.toString(x) + "," +
                    Double.toString(y));
            t.start();
            zoom = zoom * zoomFactor;
        }

        while (numImagesCreated < numImages) {
            TimeUnit.MILLISECONDS.sleep(10);
        }

        System.out.println("Starting sorting");
        double smallestZoom;
        int smallestIndex;
        while (images.size() < numImages) {
            smallestZoom = Double.MAX_VALUE;
            smallestIndex = 0;
            for (int i = 0; i < unorderedImages.size(); i++) {
                if (unorderedImages.get(i).getZoom() < smallestZoom) {
                    smallestIndex = i;
                    smallestZoom = unorderedImages.get(i).getZoom();
                }
            }
            images.add(unorderedImages.get(smallestIndex).image);
            unorderedImages.remove(smallestIndex);
        }
        System.out.println("Finished sorting");

        ImageOutputStream output = new FileImageOutputStream(new File("images/mandelbrotThreaded.gif"));
        GifSequenceWriter writer = new GifSequenceWriter(output, images.get(0).getType(), 10, true);
        writer.writeToSequence(images.get(0));
        System.out.println("Images processed: " + 1 + "/" + numImages);

        for (int i = 1; i < numImages; i++) {
            writer.writeToSequence(images.get(i));
            Main.updateStatusLabel("Images processed: " + (i + 1) + "/" + numImages);
        }

        writer.close();
        output.close();

        images = new ArrayList<>();
        unorderedImages = new ArrayList<>();
        numActiveThreads = 0;
        numImagesCreated = 0;

        Main.updateStatusLabel("GIF Created!");

        System.out.println("Time elapsed: " + ((System.nanoTime() - startTime) / 1000000000) + " seconds");
    }

    /**
     * Creates a Mandelbrot GIF using the given settings, but automatically sets iterations
     * @param numImages The total number of images in the GIF
     * @param width The width in pixels of the GIF
     * @param height The height in pixels of the GIF
     * @param zoom The starting zoom for the GIF
     * @param zoomFactor The factor by which the zoom is increased every image in the GIF
     * @param x The x-coordinate to zoom into
     * @param y The y-coordinate to zoom into
     * @throws Exception
     */
    public static void makeGifWithThreadsAutoIterations(int numImages, int width, int height, double zoom,
                                                        double zoomFactor, double x, double y) throws Exception {
        numImagesToCreate = numImages;
        int iterations = (int) Math.ceil(1000 * log(zoom)) + 100;
        long startTime = System.nanoTime();
        ThreadedImageCreator t;
        for (int i = 0; i < numImages; i++) {
            System.out.println(iterations);
            //System.out.println(zoom);
            t = new ThreadedImageCreator(Integer.toString(width) + "," + Integer.toString(height) + "," +
                    Integer.toString(iterations) + "," + Double.toString(zoom) + "," + Double.toString(x) + "," + Double.toString(y));
            iterations = (int) Math.ceil(10000 * Math.pow(log(zoom), 1.1)) + 100;
            t.start();
            zoom = zoom * zoomFactor;
        }

        while (numImagesCreated < numImagesToCreate) {
            TimeUnit.MILLISECONDS.sleep(10);
        }

        System.out.println("Starting sorting");
        double smallestZoom;
        int smallestIndex;
        while (images.size() < numImages) {
            smallestZoom = Double.MAX_VALUE;
            smallestIndex = 0;
            for (int i = 0; i < unorderedImages.size(); i++) {
                if (unorderedImages.get(i).getZoom() < smallestZoom) {
                    smallestIndex = i;
                    smallestZoom = unorderedImages.get(i).getZoom();
                }
            }
            images.add(unorderedImages.get(smallestIndex).image);
            //System.out.println("Image " + images.size() + " has zoom " + unorderedImages.get(smallestIndex).zoom);
            unorderedImages.remove(smallestIndex);
        }
        System.out.println("Finished sorting");

        ImageOutputStream output = new FileImageOutputStream(new File("images/mandelbrotThreaded.gif"));
        GifSequenceWriter writer = new GifSequenceWriter(output, images.get(0).getType(), 5, true);
        writer.writeToSequence(images.get(0));
        System.out.println("Images processed: " + 1 + "/" + numImages);

        for (int i = 1; i < numImages; i++) {
            writer.writeToSequence(images.get(i));
            System.out.println("Images processed: " + (i + 1) + "/" + numImages);
            Main.getStatusLabel().setText("Images processed: " + (i + 1) + "/" + numImages);
            Main.getFrame().update(Main.getFrame().getGraphics());
            //statusLabel.update(statusLabel.getGraphics());
        }

        writer.close();
        output.close();

        images = new ArrayList<>();
        unorderedImages = new ArrayList<>();
        numActiveThreads = 0;
        numImagesCreated = 0;

        Main.getStatusLabel().setText("GIF Created!");
        Main.getFrame().update(Main.getFrame().getGraphics());

        System.out.println((System.nanoTime() - startTime) / 1000000000);
    }

    public static void makeGif(int numImages, int width, int height, int iterations, double initialZoom, double zoomFactor, double x, double y) throws Exception {
        long startTime = System.nanoTime();
        String fileName = "images/mandelbrot.gif";
        double zoom = initialZoom;
        BufferedImage img1 = ImageController.createZoomedImage(width, height, iterations, zoom, x, y);

        ImageOutputStream output = new FileImageOutputStream(new File(fileName));
        GifSequenceWriter writer = new GifSequenceWriter(output, img1.getType(), 10, true);
        writer.writeToSequence(img1);
        /*Main.getStatusLabel().setText("Images processed: " + (1) + "/" + numImages);
        Main.getFrame().update(Main.getFrame().getGraphics());*/
        System.out.println("Images processed: " + (1) + "/" + numImages);

        for (int i = 1; i < numImages; i++) {
            writer.writeToSequence(ImageController.createZoomedImage(width, height, iterations, zoom, x, y));
            zoom = zoom * zoomFactor;
            Main.getStatusLabel().setText("Images processed: " + (i + 1) + "/" + numImages);
            Main.getFrame().update(Main.getFrame().getGraphics());
            System.out.println("Images processed: " + (i + 1) + "/" + numImages);
        }

        writer.close();
        output.close();

        /*Main.getStatusLabel().setText("GIF Created!");
        Main.getFrame().update(Main.getFrame().getGraphics());*/

        System.out.println("Sequential GIF creator: " + ((System.nanoTime() - startTime) / 1000000000));
    }
}
