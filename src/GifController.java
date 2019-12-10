import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.log;

public class GifController {

    public static volatile int numImagesCreated = 0;
    public static volatile int numActiveThreads = 0;
    public static volatile int numImagesToCreate = 0;

    /**
     * Creates the images using threads
     *
     * @param width      The width in pixels of the GIF
     * @param height     The height in pixels of the GIF
     * @param iterations The number if iterations used to determine a single pixel (higher iterations means better image)
     * @param zoom       The starting zoom for the GIF
     * @param zoomFactor The factor by which the zoom is increased every image in the GIF
     * @param x          The x-coordinate to zoom into
     * @param y          The y-coordinate to zoom into
     * @param maxThreads The maximum number of threads that can be active at once
     * @throws Exception
     */
    public static void createImagesThreaded(int width, int height, int iterations, double zoom,
                                            double zoomFactor, double x, double y, int maxThreads) throws Exception {
        int lastImageIndex = 0;
        ThreadedImageCreator t;
        for (int i = 0; i < numImagesToCreate; i++) {
            //Wait until there is another space for the thread to be made
            while (numActiveThreads >= maxThreads || Main.getIsPaused() && !Main.getIsCancelled() && !Main.getIsCancelledForce()) {
                Thread.sleep(10);
            }
            // Stop creating images if cancelled
            if (Main.getIsCancelled()) {
                lastImageIndex = i;
                break;
            }
            if (Main.getIsCancelledForce()) {
                break;
            }
            // Kick off a thread to create the zoomed image
            t = new ThreadedImageCreator(width + "," + height + "," + iterations + "," + zoom + "," + x + "," + y);
            t.start();
            zoom = zoom * zoomFactor;
            ThreadedImageCreator.changeNumThreads(true);
        }
        while (numImagesCreated < numImagesToCreate) {
            if (Main.getIsCancelled() && numImagesCreated == lastImageIndex) {
                break;
            }
            if (Main.getIsCancelledForce()) {
                break;
            }
            TimeUnit.MILLISECONDS.sleep(10);
        }
    }

    /**
     * Cleans up loose ends so another GIF can be created
     */
    public static void cleanup() {
        numActiveThreads = 0;
        numImagesCreated = 0;

        File dir = Main.tempImageDir;
        if (dir.listFiles() != null) {
            for (File file : dir.listFiles()) {
                if (!file.isDirectory()) {
                    file.delete();
                }
            }
        }
        dir.delete();
    }

    /**
     * Converts the number to a proper format for file naming convention
     *
     * @param count The number which needs to be converted
     * @return The converted number as a String wiht proper number of leading 0's
     */
    public static String convertNumToStr(int count) {
        int maxStrLen = String.valueOf(numImagesToCreate).length();
        int curStrLen = String.valueOf(count).length();
        int diff = maxStrLen - curStrLen;
        String answer = "";
        for (int i = 0; i < diff; i++) {
            answer = answer + "0";
        }
        answer = answer + count;
        return answer;
    }

    /**
     * Converts all of the filenames of the images
     *
     * @return ArrayList containing all of the Files
     */
    public static ArrayList<File> convertImageNames() {
        //GET ALL IMAGE NAMES
        ArrayList<String> imgLst = new ArrayList<>();
        String fName;
        File dir = Main.tempImageDir;
        for (File file : dir.listFiles()) {
            if (!file.isDirectory()) {
                fName = file.getName();
                imgLst.add(fName.substring(0, fName.length() - 4));
            }
        }
        //SORT THE IMAGE NAMES BY THEIR ZOOM
        ArrayList<Double> doubles = new ArrayList<>();
        for (String s : imgLst) {
            doubles.add(Double.valueOf(s));
        }
        Collections.sort(doubles);
        //RENAME THE IMAGES BY THEIR SORTED VALUES
        ArrayList<File> newFiles = new ArrayList<>();
        int count = 1;
        for (double d : doubles) {
            File oldFile = new File(Main.tempImageDir, d + ".png");
            File newFile = new File(Main.tempImageDir, convertNumToStr(count) + ".png");
            try {
                Files.move(oldFile.toPath(), newFile.toPath());
                newFiles.add(newFile);
            } catch (IOException e) {
                System.out.println("RENAME FAILED");
            }
            count++;
        }
        return newFiles;
    }

    /**
     * Writes the created images into a GIF
     *
     * @param timeBetweenFramesMS Time between frames in the GIF
     * @param files               ArrayList of Files to write to GIF
     * @throws Exception
     */
    public static void writeToGif(int timeBetweenFramesMS, ArrayList<File> files) throws Exception {
        //CONVERT TO BUFFEREDIMAGES
        ArrayList<BufferedImage> imgs = new ArrayList<>();
        for (File f : files) {
            imgs.add(ImageIO.read(f));
        }
        //WRITE TO GIF
        ImageOutputStream output = new FileImageOutputStream(new File(Main.finalOutputDir, "mandelbrotThreaded.gif"));
        GifSequenceWriter writer = new GifSequenceWriter(output, imgs.get(0).getType(), timeBetweenFramesMS, true);
        for (int i = 0; i < files.size(); i++) {
            writer.writeToSequence(imgs.get(i));
            Main.updateStatusLabel("Images processed: " + (i + 1) + "/" + files.size());
        }
        writer.close();
        output.close();
    }

    /**
     * Writes all of the created images to an MP4 using ffmpeg
     *
     * @param fps The frames per second of the video
     */
    public static void writeToMp4(int fps, int numImages) {
        try {
            int numZeroes = String.valueOf(numImages).length();
            Main.updateStatusLabel("Creating MP4");
            ProcessBuilder builder = new ProcessBuilder("ffmpeg", "-y", "-framerate", String.valueOf(fps), "-i", "%0" + numZeroes + "d.png", "-c:v", "libx264", "-pix_fmt", "yuv420p", new File(Main.finalOutputDir, "out.mp4").getAbsolutePath());
            System.out.println(builder.command());
            builder.directory(Main.tempImageDir);
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);
            Process createVideo = builder.start();
            createVideo.waitFor();
        } catch (Exception e) {
            System.out.println("Shell script failed!  Make sure you have ffmpeg installed and usable on the command line!");
            e.printStackTrace();
        }
    }

    /**
     * Creates a Mandelbrot GIF with threads
     *
     * @param numImages           The total number of images in the GIF
     * @param width               The width in pixels of the GIF
     * @param height              The height in pixels of the GIF
     * @param iterations          The number of iterations used to determine a single pixel (higher iterations means better image)
     * @param zoom                The starting zoom for the GIF
     * @param zoomFactor          The factor by which the zoom is increased every image in the GIF
     * @param x                   The x-coordinate to zoom into
     * @param y                   The y-coordinate to zoom into
     * @param maxThreads          The maximum number of threads that can be active at once
     * @param timeBetweenFramesMS The number of milliseconds between frames
     * @throws Exception
     */
    public static void makeGifWithThreads(int numImages, int width, int height, int iterations, double zoom,
                                          double zoomFactor, double x, double y, int maxThreads, int timeBetweenFramesMS) throws Exception {
        long startTime = System.nanoTime();
        numImagesToCreate = numImages;
        // CLEANUP
        cleanup();
        // CREATE TEMP IMAGES FOLDER
        Main.tempImageDir.mkdir();
        // CREATE THE IMAGES, EACH THREAD MAKES A DIFFERENT IMAGE
        createImagesThreaded(width, height, iterations, zoom, zoomFactor, x, y, maxThreads);
        // WRITE IMAGES TO GIF
        writeToGif(timeBetweenFramesMS, convertImageNames());
        // CLEANUP
        cleanup();
        Main.updateStatusLabel("GIF Created!");
        System.out.println("Time elapsed: " + ((System.nanoTime() - startTime) / 1000000000) + " seconds");
    }

    /**
     * Creates a Mandelbrot MP4 with threads
     *
     * @param numImages  The number of frames in the video
     * @param width      The width in pixels of the video
     * @param height     The height in pixels of the video
     * @param iterations The number of iterations used to determine a single pixel (higher iterations means better image)
     * @param zoom       The starting zoom
     * @param zoomFactor The factor by which the zoom is increased every image in the video
     * @param x          The x-coordinate of the center point
     * @param y          The y-coordinate of the center point
     * @param maxThreads The maximum number of threads that can be active at once
     * @param fps        The frames per second of the video
     * @throws Exception
     */
    public static void makeMp4WithThreads(int numImages, int width, int height, int iterations, double zoom,
                                          double zoomFactor, double x, double y, int maxThreads, int fps) throws Exception {
        long startTime = System.nanoTime();
        numImagesToCreate = numImages;
        // CLEANUP
        cleanup();
        // CREATE TEMP IMAGES FOLDER
        Main.tempImageDir.mkdir();
        // CREATE THE IMAGES, EACH THREAD MAKES A DIFFERENT IMAGE
        createImagesThreaded(width, height, iterations, zoom, zoomFactor, x, y, maxThreads);
        // WRITE IMAGES TO GIF
        convertImageNames();
        writeToMp4(fps, numImages);
        // CLEANUP
        cleanup();
        Main.updateStatusLabel("MP4 Created!");
        System.out.println("Time elapsed: " + ((System.nanoTime() - startTime) / 1000000000) + " seconds");
    }

    /**
     * Creates a Mandelbrot GIF with threads, but automatically sets iterations
     *
     * @param numImages           The total number of images in the GIF
     * @param width               The width in pixels of the GIF
     * @param height              The height in pixels of the GIF
     * @param zoom                The starting zoom for the GIF
     * @param zoomFactor          The factor by which the zoom is increased every image in the GIF
     * @param x                   The x-coordinate to zoom into
     * @param y                   The y-coordinate to zoom into
     * @param timeBetweenFramesMS The number of milliseconds between frames
     * @throws Exception
     */
    public static void makeGifWithThreadsAutoIterations(int numImages, int width, int height, double zoom,
                                                        double zoomFactor, double x, double y, int timeBetweenFramesMS) throws Exception {
        long startTime = System.nanoTime();
        numImagesToCreate = numImages;
        // CLEANUP
        cleanup();
        // CREATE TEMP IMAGES FOLDER
        Main.tempImageDir.mkdir();

        // CREATE THE IMAGES, EACH THREAD MAKES A DIFFERENT IMAGE
        int iterations = (int) Math.ceil(1000 * log(zoom)) + 100;
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

        // WRITE IMAGES TO VIDEO
        writeToGif(timeBetweenFramesMS, convertImageNames());
        // CLEANUP
        cleanup();
        Main.updateStatusLabel("GIF Created!");
        System.out.println((System.nanoTime() - startTime) / 1000000000);
    }
}
