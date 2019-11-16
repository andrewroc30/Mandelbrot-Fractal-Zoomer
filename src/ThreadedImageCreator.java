public class ThreadedImageCreator implements Runnable {
    private Thread t;
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
    }

    public void run() {
        try {
            GifController.numActiveThreads++;
            GifController.unorderedImages.add(new ImageWithZoom(ImageController.createZoomedImage(this.width, this.height, this.iterations, this.zoom, this.x, this.y), zoom));
            System.out.println("Images created: " + (GifController.numImagesCreated++ + 1) + "/" + GifController.numImagesToCreate);
            GifController.numActiveThreads--;
        } catch (Error e) {
            System.out.println("Thing ran out of space!");
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, inputStr);
            t.start();
        }
    }
}