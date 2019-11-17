//public class DeprecatedMethods {
//    public static void testUnlimitedThreads() throws Exception {
//        long startTime = System.nanoTime();
//        ThreadedImageCreator t;
//        double zoom = 1;
//        for (int i = 0; i < 50; i++) {
//            t = new ThreadedImageCreator(Double.toString(zoom));
//            t.start();
//            zoom = zoom * 1.1;
//        }
//        while (numActiveThreads > 0) {
//            TimeUnit.MILLISECONDS.sleep(10);
//        }
//        System.out.println("unlimited threads: " + ((System.nanoTime() - startTime) / 1000000000));
//    }
//
//    public static void testLimitedThreads() throws Exception {
//        int availableCores = Runtime.getRuntime().availableProcessors();
//        long startTime = System.nanoTime();
//        ThreadedImageCreator t;
//        double zoom = 1;
//        for (int i = 0; i < 50; i++) {
//            if (numActiveThreads > availableCores) {
//                i--;
//            } else {
//                t = new ThreadedImageCreator(Double.toString(zoom));
//                t.start();
//                zoom = zoom * 1.1;
//            }
//            System.out.println(numActiveThreads);
//        }
//        while (numActiveThreads > 0) {
//            TimeUnit.MILLISECONDS.sleep(10);
//        }
//        System.out.println("limited threads: " + ((System.nanoTime() - startTime) / 1000000000));
//    }
//
//    //This is slower than the current makeGIF
//    public static void makeGif2(int numImages, int width, int height, int iterations, double initialZoom, double zoomFactor, double x, double y) throws Exception {
//        long startTime = System.nanoTime();
//        BufferedImage[] images = new BufferedImage[numImages];
//        double zoom = initialZoom;
//        String fileName = "images/mandelbrot.gif";
//        for (int i = 0; i < numImages; i++) {
//            images[i] = ImageController.createZoomedImage(width, height, iterations, zoom, x, y);
//            zoom = zoom * zoomFactor;
//            statusLabel.setText("Images created: " + (i + 1) + "/" + numImages);
//            f.update(f.getGraphics());
//            System.out.println("Images created: " + (i + 1) + "/" + numImages);
//        }
//
//        ImageOutputStream output = new FileImageOutputStream(new File(fileName));
//        GifSequenceWriter writer = new GifSequenceWriter(output, images[0].getType(), 10, true);
//
//        int i = 1;
//        for (BufferedImage img : images) {
//            writer.writeToSequence(img);
//            statusLabel.setText("Images processed: " + i + "/" + numImages);
//            f.update(f.getGraphics());
//            System.out.println("Images processed: " + i + "/" + numImages);
//            i++;
//        }
//
//        writer.close();
//        output.close();
//
//        System.out.println("Sequential GIF creator: " + ((System.nanoTime() - startTime) / 1000000000));
//    }
//
//    public static void makeImages() throws Exception {
//        long zoom = 10;
//        int numImages = 10;
//        String fileName;
//        for (int i = 0; i < numImages; i++) {
//            fileName = "images/mandelbrot" + i + ".jpg";
//            writeToFile(ImageController.createZoomedImage(1920, 1080, 1000, zoom, 0.001643721971153, 0.822467633298876), fileName);
//            zoom = zoom * 2;
//        }
//    }
//
//    public static void playFrames(ZoomableImage zi) throws Exception {
//        long zoom = 10;
//        while (true) {
//            TimeUnit.NANOSECONDS.sleep(1); //just so that loop works, doesn't work without something here
//            if (!isPaused) {
//                zi.setImage(ImageController.createZoomedImage(1920, 1080, 1000, zoom, 0.001643721971153, 0.822467633298876));
//                zoom = zoom * 2;
//            }
//        }
//    }
//
//
//    public static void writeToFile(BufferedImage img, String pathName) throws Exception {
//        ImageIO.write(img, "jpg", new File(pathName));
//    }
//}
