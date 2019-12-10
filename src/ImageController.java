import java.awt.*;
import java.awt.image.BufferedImage;

import static java.lang.Math.floor;
import static java.lang.Math.log;

public class ImageController {

    /**
     * Returns an image of the Mandelbrot set with the given zoom
     * @param width The width in pixels of the image
     * @param height The height in pixels of the image
     * @param max The maximum number of iterations per pixel
     * @param zoom The zoom of the image
     * @param x_coord The x-coordinate of the center of the image
     * @param y_coord The y-coordinate of the center of the image
     * @return BufferedImage of the zoomed in image of the Mandelbrot set
     */
    public static BufferedImage createZoomedImage(int width, int height, int max, double zoom, double x_coord, double y_coord) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int black = 0;
        int[] colors = new int[max];
        for (int i = 0; i < max; i++) {
            colors[i] = Color.HSBtoRGB(i / 256f, 1, i / (i + 8f));
        }

        double x0, y0, x, y, xTemp;

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                x0 = (((col - width / 2) * 4.0 / width) / zoom) + x_coord;
                y0 = (((row - height / 2) * 4.0 / width) / zoom) + y_coord;

                //Cardioid checking
                double lessX = (x0 - 0.25);
                double ySquare = y0 * y0;
                double q = (lessX * lessX) + ySquare;
                if (q * (q + lessX) <= 0.25 * ySquare) {
                    continue;
                }

                x = 0;
                y = 0;
                int iteration = 0;
                while (x * x + y * y < 2 * 2 && iteration < max) {
                    // If we cancel, stop creating the image
                    if (Main.getIsCancelledForce()) {
                        return null;
                    }
                    // Do the actual algorithm...
                    xTemp = x * x - y * y + x0;
                    y = 2 * x * y + y0;
                    x = xTemp;
                    iteration++;
                }
                if (iteration < max) image.setRGB(col, row, colors[iteration]);
                else image.setRGB(col, row, black);
            }
        }
        return image;
    }

    /**
     * Returns an image of the Mandelbrot set with the given zoom, but without banding
     * @param w The width in pixels of the image
     * @param h The height in pixels of the image
     * @param m The maximum number of iterations per pixel
     * @param zoom The zoom of the image
     * @param x_coord The x-coordinate of the center of the image
     * @param y_coord The y-coordinate of the center of the image
     * @return BufferedImage of the zoomed in image of the Mandelbrot set
     */
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

                if (iteration < max) {
                    // sqrt of inner term removed using log simplification rules.
                    double log_zn = log(x * x + y * y) / 2;
                    double nu = log(log_zn / log(2)) / log(2);
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
                    int color1 = colors[(int) floor(iteration)];
                    int color2 = colors[(int) floor(iteration) + 1];
                    // iteration % 1 = fractional part of iteration.
                    int color = Color.HSBtoRGB(0.95f + 10 * linear_interpolate(color1, color2, (float) iteration % 1), 0.6f, 1.0f);
                    image.setRGB(col, row, color);
                } else {
                    image.setRGB(col, row, black);
                }
            }
        }
        return image;
    }

    // TODO: should this return a double? Also stop casting to ints?
    /**
     * Performs linear interpolation on the given values to get a smooth coloring scheme
     * @param c1 Value representing the first color
     * @param c2 Value representing the second color
     * @param i The current iteration
     * @return The linear interpolation of the 2 colors
     */
    public static float linear_interpolate(int c1, int c2, float i) {
        return (1 - i) * c1 + i * c2;
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

        double x0, y0, x, y;
        double[][] memoX = new double[height][width];
        double[][] memoY = new double[height][width];
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
                    // If we cancel, stop creating the image
                    if (Main.getIsCancelledForce()) {
                        return null;
                    }
                    // Do the actual algorithm...
                    x0 = (((col - width / 2) * 4.0 / width) / zoom) + x_coord;
                    y0 = (((row - height / 2) * 4.0 / width) / zoom) + y_coord;
                    x = memoX[row][col];
                    y = memoY[row][col];
                    if (isDone[row][col]) {
                        continue;
                    } else if (x * x + y * y >= 4) {
                        isDone[row][col] = true;
                        finishedIteration[row][col] = iteration;
                    } else {
                        memoX[row][col] = x * x - y * y + x0;
                        memoY[row][col] = 2 * x * y + y0;
                    }
                    // Set the color
                    if (isDone[row][col]) {
                        image.setRGB(col, row, colors[finishedIteration[row][col]]);
                    } else {
                        image.setRGB(col, row, black);
                    }
                }
            }

            System.out.println("Displaying image with iteration " + iteration);
            w.setImageLabel(image);
        }

        return image;
    }
}
