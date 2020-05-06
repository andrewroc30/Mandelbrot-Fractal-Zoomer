package Utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ZoomedImage {

    public BufferedImage image;
    public Point.Double[][] points;
    public double zoom;

    /**
     * Constructor for an image
     * @param image     The image itself
     * @param points    The coordinates for each pixel in the zoomed image
     * @param zoom      The zoom factor of the image
     */
    public ZoomedImage(BufferedImage image, Point.Double[][] points, double zoom) {
        this.image = image;
        this.points = points;
        this.zoom = zoom;
    }
}
