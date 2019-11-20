import javax.imageio.ImageIO;
import java.io.File;

public class ThreadedImageCreator implements Runnable {
    private Thread t;
    private File outputFile;
    private String inputStr;
    private int width;
    private int height;
    private int iterations;
    private double zoom;
    private double x;
    private double y;

    ThreadedImageCreator(String input) {
        this.inputStr = input;
        String[] inputs = input.split(",");
        this.width = Integer.valueOf(inputs[0]);
        this.height = Integer.valueOf(inputs[1]);
        this.iterations = Integer.valueOf(inputs[2]);
        this.zoom = Double.valueOf(inputs[3]);
        this.x = Double.valueOf(inputs[4]);
        this.y = Double.valueOf(inputs[5]);
        this.outputFile = new File("" + this.zoom);

    }

    public void run() {
        try {
            changeNumThreads(true);
            ImageWithZoom image = new ImageWithZoom(ImageController.createZoomedImage(this.width, this.height, this.iterations, this.zoom, this.x, this.y), zoom);
            try {
                Main.arrayPushLock.lock();
                GifController.unorderedImages.add(image);
                //ImageIO.write(image.getImage(), "jpg", outputFile);
                GifController.numImagesCreated++;
                Main.updateStatusLabel("Images created: " + (GifController.numImagesCreated) + "/" + GifController.numImagesToCreate);
            } catch (Exception e) {
                System.out.println("THREAD FAILED");
            } finally {
                Main.arrayPushLock.unlock();
                changeNumThreads(false);
            }
        } catch (Error e) {
            System.out.println("Ran out of space!  Files of this size not yet supported");
        }
    }

    public void changeNumThreads(boolean increment) {
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

    public void start() {
        if (t == null) {
            t = new Thread(this, inputStr);
            t.start();
        }
    }
}
