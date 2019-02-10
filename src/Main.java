import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;

import static java.lang.Math.floor;
import static java.lang.Math.log;

//TODO: Get rid of ZoomableImage, that is no longer necessary (blurs image WAY too much to be viable)

//TODO: Change up the algorithm to be smooth instead of normal (check wiki for pseudo)
//TODO: Change coloring to use histogram coloring?? (need better color scheme as well)

//TODO: Option to use higher d (multibrot sets with higher exponents)

//TODO: Possibly increase iterations as we zoom in further?  Could make initial images lighter

public class Main {

  public static JLabel statusLabel = new JLabel();
  public static JFrame f = new JFrame("Mandelbrot Image Zoomer");
  public static boolean isPaused = true;
  public static ArrayList<BufferedImage> images = new ArrayList<>();
  public static ArrayList<ImageWithZoom> unorderedImages = new ArrayList<>();
  public static int numImagesCreated = 0;
  public static int numActiveThreads = 0;
  public static int numImagesToCreate = 0;

  //Good point: (-.74364386269, .13182590271)
  //Good point: (0.001643721971153, 0.822467633298876)

  public static void main(String[] args) throws Exception{

    //showStart();

    //BufferedImage image = createZoomedImage(1920, 1080, 100, 1L, 0.001643721971153, 0.822467633298876);
    //ZoomableImage zi = writeToZoomableImage(image);
    //playFrames(zi);

    //makeImages();

    statusLabel.setText("STATUS");
    showStartUI();

    //makeGif(30, 1920, 1080, 1000, 1, 1.5, -.74364386269, .13182590271);
    //makeGifWithThreads(100, 1920, 1080, 1000, 1, 1.01, -.74364386269, .13182590271);
    //System.out.println(Runtime.getRuntime().availableProcessors());

    //testUnlimitedThreads();
    //testLimitedThreads();
  }

  public static BufferedImage createZoomedImage(int w, int h, int m, double zoom, double x_coord, double y_coord) {
    int width = w, height = h, max = m;
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    int black = 0;
    int[] colors = new int[max];
    for (int i = 0; i < max; i++) {
      colors[i] = Color.HSBtoRGB(i / 256f, 1, i / (i + 8f));
    }

    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        double x0 = (((col - width / 2) * 4.0 / width) / zoom) + x_coord;
        double y0 = (((row - height / 2) * 4.0 / width) / zoom) + y_coord;
        double x = 0, y = 0;
        int iteration = 0;
        while (x * x + y * y < 2 * 2 && iteration < max) {
          double xTemp = x * x - y * y + x0;
          y = 2 * x * y + y0;
          x = xTemp;
          iteration++;
        }
        if (iteration < max) image.setRGB(col, row, colors[iteration]);
        else image.setRGB(col, row, black);
      }
    }
    //System.out.println("Image created!");
    return image;
  }

  //TODO: this algorithm is bugged, need to figure out what is happening
  public static BufferedImage createSmoothZoomedImage(int w, int h, int m, double zoom, double x_coord, double y_coord) {
    int width = w, height = h, max = m;
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    int black = 0;
    int[] colors = new int[max];
    for (int i = 0; i < max; i++) {
      colors[i] = Color.HSBtoRGB(i / 256f, 1, i / (i + 8f));
    }

    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) {
        double x0 = (((col - width / 2) * 4.0 / width) / zoom) + x_coord;
        double y0 = (((row - height / 2) * 4.0 / width) / zoom) + y_coord;
        double x = 0, y = 0;
        double iteration = 0;
        while (x * x + y * y <= (1 << 16) && iteration < max) {
          double xTemp = x * x - y * y + x0;
          y = 2 * x * y + y0;
          x = xTemp;
          iteration++;
        }

        if ( iteration < max ) {
          // sqrt of inner term removed using log simplification rules.
          double log_zn = log( x*x + y*y ) / 2;
          double nu = log( log_zn / log(2) ) / log(2);
          // Rearranging the potential function.
          // Dividing log_zn by log(2) instead of log(N = 1<<8)
          // because we want the entire palette to range from the
          // center to radius 2, NOT our bailout radius.
          iteration = iteration + 1 - nu;
        }

        /*
        For the coloring we must have a cyclic scale of colors (constructed mathematically, for instance) and containing H colors numbered from 0 to H − 1 (H = 500, for instance).
        We multiply the real number v(z) by a fixed real number determining the density of the colors in the picture, take the integral part of
        this number modulo H, and use it to look up the corresponding color in the color table.
         */

        if (iteration < max) {
          int color1 = colors[(int)floor(iteration)];
          int color2 = colors[(int)floor(iteration) + 1];
          // iteration % 1 = fractional part of iteration.
          int color = Color.HSBtoRGB(0.95f + 10 * linear_interpolate(color1, color2, (float)iteration % 1), 0.6f, 1.0f);
          image.setRGB(col, row, color);
        }
        else {
          image.setRGB(col, row, black);
        }
      }
    }
    return image;
  }

  //TODO: should this return a double? Also stop casting to ints?
  public static float linear_interpolate(int c1, int c2, float i) {
    return (1 - i) * c1 + i * c2;
  }

  //TODO: Update status in JFrame
  public static void makeGifWithThreads(int numImages, int width, int height, int iterations, double zoom,
                                        double zoomFactor, double x, double y) throws Exception {
    numImagesToCreate = numImages;
    long startTime = System.nanoTime();
    ThreadedImageCreator t;
    for (int i = 0; i < numImages; i++) {
      t = new ThreadedImageCreator(Integer.toString(width) + "," + Integer.toString(height) + "," +
              Integer.toString(iterations) + "," + Double.toString(zoom) + "," + Double.toString(x) + "," + Double.toString(y));
      t.start();
      zoom = zoom * zoomFactor;
    }

    while(numImagesCreated < numImages) {
      TimeUnit.MILLISECONDS.sleep(10);
    }

    System.out.println("Starting sorting");
    double smallestZoom;
    int smallestIndex;
    while(images.size() < numImages) {
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
    GifSequenceWriter writer = new GifSequenceWriter(output, images.get(0).getType(), 10, true);
    writer.writeToSequence(images.get(0));
    System.out.println("Images processed: " + 1 + "/" + numImages);

    for(int i = 1; i < numImages; i++) {
      writer.writeToSequence(images.get(i));
      System.out.println("Images processed: " + (i + 1) + "/" + numImages);
      statusLabel.setText("Images processed: " + (i + 1) + "/" + numImages);
      f.update(f.getGraphics());
      //statusLabel.repaint();
      //statusLabel.update(statusLabel.getGraphics());
    }

    writer.close();
    output.close();

    images = new ArrayList<>();
    unorderedImages = new ArrayList<>();
    numActiveThreads = 0;
    numImagesCreated = 0;

    statusLabel.setText("GIF Created!");
    f.update(f.getGraphics());

    System.out.println((System.nanoTime() - startTime) / 1000000000);
  }

  //TODO: Add images per second
  private static void showStartUI() {
    JButton gif = new JButton("Create GIF");
    gif.setBounds(50,400,150, 50);

    JButton img = new JButton("Create PNG");
    img.setBounds(250,400,150, 50);

    JLabel xLabel = new JLabel();
    xLabel.setText("Enter X-Coordinate (between -2 and 2)");
    xLabel.setBounds(50, 10, 500, 100);
    JTextField xText= new JTextField();
    xText.setBounds(300, 50, 130, 25);
    xText.setText("-.74364386269");

    JLabel yLabel = new JLabel();
    yLabel.setText("Enter Y-Coordinate (between -2 and 2)");
    yLabel.setBounds(50, -50, 500, 300);
    JTextField yText= new JTextField();
    yText.setBounds(300, 85, 130, 25);
    yText.setText(".13182590271");

    JLabel zoomFactorLabel = new JLabel();
    zoomFactorLabel.setText("Enter Zoom Factor");
    zoomFactorLabel.setBounds(50, -10, 500, 300);
    JTextField zoomFactorText= new JTextField();
    zoomFactorText.setBounds(300, 125, 130, 25);
    zoomFactorText.setText("1.5");

    JLabel numImagesLabel = new JLabel();
    numImagesLabel.setText("Enter Number of Images");
    numImagesLabel.setBounds(50, 25, 500, 300);
    JTextField numImagesText= new JTextField();
    numImagesText.setBounds(300, 160, 130, 25);
    numImagesText.setText("5");

    JLabel iterationsLabel = new JLabel();
    iterationsLabel.setText("Enter Iterations");
    iterationsLabel.setBounds(50, 65, 500, 300);
    JTextField iterationsText= new JTextField();
    iterationsText.setBounds(300, 200, 130, 25);
    iterationsText.setText("1000");

    JLabel initialZoomLabel = new JLabel();
    initialZoomLabel.setText("Enter Initial Zoom");
    initialZoomLabel.setBounds(50, 100, 500, 300);
    JTextField initialZoomText= new JTextField();
    initialZoomText.setBounds(300, 235, 130, 25);
    initialZoomText.setText("1");

    statusLabel.setBounds(160, 200, 500, 300);

    f.add(xLabel);
    f.add(xText);
    f.add(yLabel);
    f.add(yText);
    f.add(zoomFactorLabel);
    f.add(zoomFactorText);
    f.add(numImagesLabel);
    f.add(numImagesText);
    f.add(iterationsLabel);
    f.add(iterationsText);
    f.add(initialZoomLabel);
    f.add(initialZoomText);
    f.add(statusLabel);
    f.add(gif);
    f.add(img);
    f.setSize(500,500);
    f.setLayout(null);
    f.setVisible(true);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    gif.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        System.out.println("Button Pressed");
        try {
          double x = Double.parseDouble(xText.getText());
          double y = Double.parseDouble(yText.getText());
          double zoomFactor = Double.parseDouble(zoomFactorText.getText());
          int numImages = Integer.parseInt(numImagesText.getText());
          int iterations = Integer.parseInt(iterationsText.getText());
          double initialZoom = Double.parseDouble(initialZoomText.getText());
          makeGifWithThreads(numImages, 1920, 1080, iterations, initialZoom, zoomFactor, x, y);
        } catch (Exception e) {
          System.out.println("GIF Creation Failed!");
        }
      }
    });

    img.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        System.out.println("Button Pressed");
        try {
          double x = Double.parseDouble(xText.getText());
          double y = Double.parseDouble(yText.getText());
          int iterations = Integer.parseInt(iterationsText.getText());
          double initialZoom = Double.parseDouble(initialZoomText.getText());
          ImageIO.write(createZoomedImage(1920, 1080, iterations, initialZoom, x, y),
                  "png", new File("images/mandelbrot.png"));
          statusLabel.setText("Image created!");
          //f.update(f.getGraphics());
          f.repaint();
        } catch (Exception e) {
          System.out.println("Image Creation Failed!");
        }
      }
    });
  }

  /*

  public static void makeGif(int numImages, int width, int height, int iterations, double initialZoom, double zoomFactor, double x, double y) throws Exception {
    long startTime = System.nanoTime();
    String fileName = "images/mandelbrot.gif";
    double zoom = initialZoom;
    BufferedImage img1 = createZoomedImage(width, height, iterations, zoom, x, y);

    ImageOutputStream output = new FileImageOutputStream(new File(fileName));
    GifSequenceWriter writer = new GifSequenceWriter(output, img1.getType(), 10, true);
    writer.writeToSequence(img1);
    statusLabel.setText("Images processed: " + (1) + "/" + numImages);
    f.update(f.getGraphics());
    System.out.println("Images processed: " + (1) + "/" + numImages);

    for(int i = 1; i < numImages; i++) {
      writer.writeToSequence(createZoomedImage(width, height, iterations, zoom, x, y));
      zoom = zoom * zoomFactor;
      statusLabel.setText("Images processed: " + (i + 1) + "/" + numImages);
      f.update(f.getGraphics());
      System.out.println("Images processed: " + (i + 1) + "/" + numImages);
    }

    writer.close();
    output.close();

    statusLabel.setText("GIF Created!");
    f.update(f.getGraphics());

    System.out.println("Sequential GIF creator: " + ((System.nanoTime() - startTime) / 1000000000));
  }

  public static void testUnlimitedThreads() throws Exception {
    long startTime = System.nanoTime();
    ThreadedImageCreator t;
    double zoom = 1;
    for (int i = 0; i < 50; i++) {
      t = new ThreadedImageCreator(Double.toString(zoom));
      t.start();
      zoom = zoom * 1.1;
    }
    while (numActiveThreads > 0) {
      TimeUnit.MILLISECONDS.sleep(10);
    }
    System.out.println("unlimited threads: " + ((System.nanoTime() - startTime) / 1000000000));
  }

  public static void testLimitedThreads() throws Exception {
    int availableCores = Runtime.getRuntime().availableProcessors();
    long startTime = System.nanoTime();
    ThreadedImageCreator t;
    double zoom = 1;
    for (int i = 0; i < 50; i++) {
      if (numActiveThreads > availableCores) {
        i--;
      }
      else {
        t = new ThreadedImageCreator(Double.toString(zoom));
        t.start();
        zoom = zoom * 1.1;
      }
      System.out.println(numActiveThreads);
    }
    while (numActiveThreads > 0) {
      TimeUnit.MILLISECONDS.sleep(10);
    }
    System.out.println("limited threads: " + ((System.nanoTime() - startTime) / 1000000000));
  }

  //This is slower than the current makeGIF
  public static void makeGif2(int numImages, int width, int height, int iterations, double initialZoom, double zoomFactor, double x, double y) throws Exception {
    long startTime = System.nanoTime();
    BufferedImage[] images = new BufferedImage[numImages];
    double zoom = initialZoom;
    String fileName = "images/mandelbrot.gif";
    for(int i = 0; i < numImages; i++) {
      images[i] = createZoomedImage(width, height, iterations, zoom, x, y);
      zoom = zoom * zoomFactor;
      statusLabel.setText("Images created: " + (i + 1) + "/" + numImages);
      f.update(f.getGraphics());
      System.out.println("Images created: " + (i + 1) + "/" + numImages);
    }

    ImageOutputStream output = new FileImageOutputStream(new File(fileName));
    GifSequenceWriter writer = new GifSequenceWriter(output, images[0].getType(), 10, true);

    int i = 1;
    for (BufferedImage img : images) {
      writer.writeToSequence(img);
      statusLabel.setText("Images processed: " + i + "/" + numImages);
      f.update(f.getGraphics());
      System.out.println("Images processed: " + i + "/" + numImages);
      i++;
    }

    writer.close();
    output.close();

    System.out.println("Sequential GIF creator: " + ((System.nanoTime() - startTime) / 1000000000));
  }

  public static void makeImages() throws Exception{
    long zoom = 10;
    int numImages = 10;
    String fileName;
    for(int i = 0; i < numImages; i++) {
      fileName = "images/mandelbrot" + i + ".jpg";
      writeToFile(createZoomedImage(1920, 1080, 1000, zoom, 0.001643721971153, 0.822467633298876), fileName);
      zoom = zoom * 2;
    }
  }

  public static void playFrames(ZoomableImage zi) throws Exception{
    long zoom = 10;
    while(true) {
      TimeUnit.NANOSECONDS.sleep(1); //just so that loop works, doesn't work without something here
      if (!isPaused) {
        zi.setImage(createZoomedImage(1920, 1080, 1000, zoom, 0.001643721971153, 0.822467633298876));
        zoom = zoom * 2;
      }
    }
  }


  public static void writeToFile(BufferedImage img, String pathName) throws Exception{
    ImageIO.write(img, "jpg", new File(pathName));
  }


  public static void writeToJFrame(BufferedImage image) {
    JFrame frame = new JFrame();
    frame.getContentPane().setLayout(new FlowLayout());
    frame.getContentPane().add(new JLabel(new ImageIcon(image)));
    frame.pack();
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }


  public static ZoomableImage writeToZoomableImage(BufferedImage image) {
    JFrame frame = new JFrame();
    frame.getContentPane().setLayout(new FlowLayout());

    ZoomableImage comp = new ZoomableImage();
    comp.setImage(image);
    comp.setScaleFactor((float)1.1);
    frame.getContentPane().add(comp);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(1920, 1080);

    JPanel gui = new JPanel();
    PlayPauseButton playPause = new PlayPauseButton("Play");
    gui.add(playPause.b);
    frame.getContentPane().add(gui);

    JSplitPane splitPane = new JSplitPane();
    splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    splitPane.setDividerLocation(40);
    splitPane.setTopComponent(gui);
    splitPane.setBottomComponent(comp);
    frame.getContentPane().add(splitPane);

    return comp;
  }


  public static void slowZoom(ZoomableImage image) throws Exception{
    BufferedImage curImg;
    for (int i = 0; i < 100000; i++) {
      TimeUnit.MILLISECONDS.sleep(1);
      curImg = image.originalImage.getSubimage(20, 20, 1900, 1060);
      curImg = convertImage(curImg.getScaledInstance(1920, 1080, Image.SCALE_SMOOTH));
      image.setImage(curImg);
      System.out.println("bigger!");
    }
  }

  public static BufferedImage convertImage(Image img) {
    if (img instanceof BufferedImage) {
      return (BufferedImage) img;
    }

    // Create a buffered image with transparency
    BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

    // Draw the image on to the buffered image
    Graphics2D bGr = bimage.createGraphics();
    bGr.drawImage(img, 0, 0, null);
    bGr.dispose();

    // Return the buffered image
    return bimage;
  }



  public static void showStart() {
    JFrame f=new JFrame("Mandelbrot Image Magnifier");

    JButton b=new JButton("Submit");
    b.setBounds(830,600,140, 40);

    JLabel label = new JLabel();
    label.setText("Enter X-Coordinate (between 0 and 2)");
    label.setBounds(10, 10, 500, 100);

    JLabel label1 = new JLabel();
    label1.setBounds(10, 110, 200, 100);
    JTextField textfield= new JTextField();
    textfield.setBounds(110, 50, 130, 30);

    JLabel label3 = new JLabel();
    label3.setText("Enter Y-Coordinate (between 0 and 2)");
    label3.setBounds(10, 10, 500, 300);

    JLabel label4 = new JLabel();
    label4.setBounds(10, 110, 200, 300);
    JTextField textfield2= new JTextField();
    textfield2.setBounds(110, 350, 130, 30);

    f.add(label1);
    f.add(textfield);
    f.add(label);
    f.add(label3);
    f.add(label4);
    f.add(textfield2);
    f.add(b);
    f.setSize(1920,1080);
    f.setLayout(null);
    f.setVisible(true);
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    b.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        BufferedImage image = createZoomedImage(1920, 1080, 1000, 1L, 0.001643721971153, 0.822467633298876);
        ZoomableImage zi = writeToZoomableImage(image);
        f.dispose();
        //clean this up, also doesn't work for some reason???
        //try {
        //  playFrames(zi);
        //} catch (Exception e) {}
      }
    });
  }
  */
}