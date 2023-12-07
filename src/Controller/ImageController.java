package Controller;

import Main.Main;
import Utils.ZoomedImage;
import View.CreationWindow;
import View.ImageWindow;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static java.lang.Math.floor;
import static java.lang.Math.log;

public class ImageController {

    /**
     * Returns an image of the Mandelbrot set with the given zoom
     * @param creationWindow The window to update with status
     * @param width The width in pixels of the image
     * @param height The height in pixels of the image
     * @param max The maximum number of iterations per pixel
     * @param zoom The zoom of the image
     * @param x_coord The x-coordinate of the center of the image
     * @param y_coord The y-coordinate of the center of the image
     * @return BufferedImage of the zoomed in image of the Mandelbrot set
     */
    public static ZoomedImage createZoomedImage(CreationWindow creationWindow, String filename, int width, int height, int max, double zoom, double x_coord, double y_coord) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Point.Double[][] points = new Point.Double[height][width];

        int rowProgressPercentage = 0;
        int black = 0;
        int[] colors = new int[max];
        for (int i = 0; i < max; i++) {
            colors[i] = Color.HSBtoRGB(i / 256f, 1, i / (i + 8f));
        }

        double x0, y0;

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                x0 = (((col - width / 2.0) * 4.0 / width) / zoom) + x_coord;
                y0 = (((row - height / 2.0) * 4.0 / width) / zoom) + y_coord;
                points[row][col] = new Point.Double(x0, y0);

                //Cardioid checking
                double lessX = (x0 - 0.25);
                double ySquare = y0 * y0;
                double q = (lessX * lessX) + ySquare;
                if (q * (q + lessX) <= 0.25 * ySquare) {
                    continue;
                }

                int escapeIteration = getIterations(x0, y0, max);
                if (escapeIteration == -1) {
                    return null;
                }

                if (escapeIteration < max) image.setRGB(col, row, colors[escapeIteration]);
                else image.setRGB(col, row, black);
            }
            int rowCompletionPercentage = (int)(row*100 / (float)height);
            if (Objects.nonNull(creationWindow) && rowCompletionPercentage > rowProgressPercentage) {
                rowProgressPercentage = rowCompletionPercentage;
                creationWindow.updateProgress(filename, rowCompletionPercentage);
            }
        }
        return new ZoomedImage(image, points, zoom);
    }

    /**
     * Returns the number of iterations it took for the given point to escape
     * @param x0 x-coordinate of given point
     * @param y0 y-coordinate of given point
     * @param max maximum number of iterations
     * @return The number of iterations it took to escape, or -1 if the image is cancelled
     */
    private static int getIterations(double x0, double y0, int max) {
        double x = 0, y = 0, xSquare = 0, ySquare = 0, xTemp;
        int iteration = 0;

        while (xSquare + ySquare < 4 && iteration < max) {
            // If we cancel, stop creating the image
            if (Main.getIsCancelledForce()) {
                return -1;
            }
            // Do the actual algorithm...
            xSquare = x * x;
            ySquare = y * y;
            xTemp = xSquare - ySquare + x0;
            y = 2 * x * y + y0;
            x = xTemp;
            iteration++;
        }

        return iteration;
    }

    /**
     * Returns an image of the Mandelbrot set with the given zoom, but without banding
     * @param width The width in pixels of the image
     * @param height The height in pixels of the image
     * @param max The maximum number of iterations per pixel
     * @param zoom The zoom of the image
     * @param x_coord The x-coordinate of the center of the image
     * @param y_coord The y-coordinate of the center of the image
     * @return BufferedImage of the zoomed in image of the Mandelbrot set
     */
    public static ZoomedImage createSmoothZoomedImage(int width, int height, int max, double zoom, double x_coord, double y_coord) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Point.Double[][] points = new Point.Double[height][width];

        int black = 0;
        int[] colors = new int[max + 1];
        for (int i = 0; i <= max; i++) {
            colors[i] = Color.HSBtoRGB(i / 256f, 1, i / (i + 8f));
        }

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                double x0 = (((col - width / 2.0) * 4.0 / width) / zoom) + x_coord;
                double y0 = (((row - height / 2.0) * 4.0 / width) / zoom) + y_coord;
                points[row][col] = new Point.Double(x0, y0);

                // Get the iterations for this point
                double x = 0, y = 0, xSquare = 0, ySquare = 0, iteration = 0, xTemp;
                while (xSquare + ySquare < 4 && iteration < max) {
                    // If we cancel, stop creating the image
                    if (Main.getIsCancelledForce()) {
                        return null;
                    }
                    // Do the actual algorithm...
                    xSquare = x * x;
                    ySquare = y * y;
                    xTemp = xSquare - ySquare + x0;
                    y = 2 * x * y + y0;
                    x = xTemp;
                    iteration++;
                }

                // Smooth the point
                if (iteration < max) {
                    double log_zn = log(xSquare + ySquare) / 2;
                    double nu = log(log_zn / log(2)) / log(2);
                    iteration = iteration + 1 - nu;
                    RGB color1 = intToRGB(colors[(int) floor(iteration)]);
                    RGB color2 = intToRGB(colors[(int) floor(iteration) + 1]);
                    RGB interpolation = new RGB(
                            (int)linear_interpolate((float)color1.red, (float)color2.red, (float)(iteration % 1)),
                            (int)linear_interpolate((float)color1.green, (float)color2.green, (float)(iteration % 1)),
                            (int)linear_interpolate((float)color1.blue, (float)color2.blue, (float)(iteration % 1)));
                    image.setRGB(col, row, RGBtoInt(interpolation.red, interpolation.green, interpolation.blue));
                } else {
                    image.setRGB(col, row, black);
                }
            }
        }
        return new ZoomedImage(image, points, zoom);
    }

    /**
     * Performs linear interpolation on the given values to get a smooth coloring scheme
     * @param c1 Value representing the first color
     * @param c2 Value representing the second color
     * @param i The current iteration
     * @return The linear interpolation of the 2 colors
     */
    private static float linear_interpolate(float c1, float c2, float i) {
        return (1 - i) * c1 + i * c2;
    }

    /**
     * Converts integer to an RGB object
     * @param value The given integer rgb value
     * @return An RGB object containing the rgb values as integers 0-255
     */
    public static RGB intToRGB(int value)
    {
        var red =   ( value >>  0 ) & 255;
        var green = ( value >>  8 ) & 255;
        var blue =  ( value >> 16 ) & 255;
        return new RGB(red, green, blue);
    }

    /**
     * Converts an rgb from separate integers 0-255 to an rgb in integer form
     * @param r The red value
     * @param g The green value
     * @param b The blue value
     * @return An integer representing the rgb value
     */
    public static int RGBtoInt(int r, int g, int b)
    {
        return ( r << 0 ) | ( g << 8 ) | ( b << 16 );
    }


    /**
     * Returns an image of the Mandelbrot set with the given zoom, displaying image every iteration
     * @param width The width in pixels of the image
     * @param height The height in pixels of the image
     * @param max The maximum number of iterations per pixel
     * @param zoom The zoom of the image
     * @param x_coord The x-coordinate of the center of the image
     * @param y_coord The y-coordinate of the center of the image
     * @return BufferedImage of the fully zoomed in image of the Mandelbrot set
     */
    public static BufferedImage createZoomedImageProgression(int width, int height, int max, double zoom, double x_coord, double y_coord, ImageWindow w) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int black = 0;
        int[] colors = new int[max];
        for (int i = 0; i < max; i++) {
            colors[i] = Color.HSBtoRGB(i / 256f, 1, i / (i + 8f));
        }

        double x, y;
        double[][] memoX = new double[height][width];
        double[][] memoY = new double[height][width];
        double[][] memoX0 = new double[height][width];
        double[][] memoY0 = new double[height][width];
        boolean[][] isDone = new boolean[height][width];
        for (int r = 0; r < isDone.length; r++) {
            for (int c = 0; c < isDone[r].length; c++) {
                isDone[r][c] = false;
            }
        }
        int[][] finishedIteration = new int[height][width];

        for (int iteration = 0; iteration < max; iteration++) {
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    // Do the actual algorithm...
                    if (isDone[row][col]) {
                        continue;
                    } else if (iteration == 0) {
                        memoX0[row][col] = (((col - width / 2) * 4.0 / width) / zoom) + x_coord;
                        memoY0[row][col] = (((row - height / 2) * 4.0 / width) / zoom) + y_coord;
                    }

                    //Cardioid checking
                    double lessX = (memoX0[row][col] - 0.25);
                    double ySquare = memoY0[row][col] * memoY0[row][col];
                    double q = (lessX * lessX) + ySquare;
                    if (q * (q + lessX) <= 0.25 * ySquare) {
                        continue;
                    }

                    x = memoX[row][col];
                    y = memoY[row][col];
                    if (x * x + y * y >= 4) {
                        isDone[row][col] = true;
                        finishedIteration[row][col] = iteration;
                    } else {
                        memoX[row][col] = x * x - y * y + memoX0[row][col];
                        memoY[row][col] = 2 * x * y + memoY0[row][col];
                    }

                    // Set the color
                    if (isDone[row][col]) {
                        image.setRGB(col, row, colors[finishedIteration[row][col]]);
                    } else {
                        image.setRGB(col, row, black);
                    }
                }
            }
            // If we cancel, stop iterating
            if (Main.getIsCancelledForce()) {
                return image;
            }
            w.setImageLabel(image, iteration);
            Main.updateStatusLabel("Iterations: " + iteration);
        }

        return image;
    }


    /**
     * A class for representing an rgb as 3 separate integers 0-255
     */
    private static class RGB {
        int red;
        int green;
        int blue;

        public RGB(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }
}
