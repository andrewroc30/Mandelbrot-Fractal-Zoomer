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
        this.width = Integer.parseInt(inputs[0]);
        this.height = Integer.parseInt(inputs[1]);
        this.iterations = Integer.parseInt(inputs[2]);
        this.zoom = Double.parseDouble(inputs[3]);
        this.x = Double.parseDouble(inputs[4]);
        this.y = Double.parseDouble(inputs[5]);
        this.outputFile = new File(Main.tempImageDirPath + this.zoom + ".png");
    }

    public void run() {
        try {
            changeNumThreads(true);
            ImageWithZoom image = new ImageWithZoom(ImageController.createZoomedImage(this.width, this.height, this.iterations, this.zoom, this.x, this.y), zoom);
            try {
                Main.arrayPushLock.lock();
                ImageIO.write(image.getImage(), "png", outputFile);
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
