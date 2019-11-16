/*public class ThreadedImageCreator implements Runnable{
    private Thread t;
    private String zoomStr;
    private double zoom;

    ThreadedImageCreator (String zoom) {
        this.zoomStr = zoom;
        this.zoom = Double.valueOf(zoom);
    }

    public void run() {
        Main.numActiveThreads++;
        //System.out.println("creating image with zoom " + zoom);
        Main.unorderedImages.add(new ImageWithZoom(Main.createZoomedImage(1920, 1080, 10000, zoom, -.74364386269, .13182590271), zoom));
        //System.out.println("Images created: " + (Main.numImagesCreated++ + 1) + " with zoom " + zoom);
        System.out.println("Images created: " + (Main.numImagesCreated++ + 1));
        Main.numActiveThreads--;
    }

    public void start () {
        if (t == null) {
            t = new Thread(this, zoomStr);
            t.start();
        }
    }
}
*/

public class ThreadedImageCreator implements Runnable{
    private Thread t;
    private String inputStr;

    private int width;
    private int height;
    private int iterations;
    private double zoom;
    private double x;
    private double y;

    ThreadedImageCreator (String input) {
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
            Main.numActiveThreads++;
            Main.unorderedImages.add(new ImageWithZoom(Main.createZoomedImage(this.width, this.height, this.iterations, this.zoom, this.x, this.y), zoom));
            System.out.println("Images created: " + (Main.numImagesCreated++ + 1) + "/" + Main.numImagesToCreate);
            /*Main.statusLabel.setText("Images created: " + (Main.numImagesCreated) + "/" + Main.numImagesToCreate);
            Main.f.update(Main.f.getGraphics());*/
            //Main.f.repaint();
            Main.numActiveThreads--;
        }
        catch (Error e) {
            System.out.println("Thing ran out of space!");
        }
    }

    public void start () {
        if (t == null) {
            t = new Thread(this, inputStr);
            t.start();
        }
    }
}