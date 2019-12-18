import java.awt.*;
import java.awt.image.BufferedImage;

public class ZoomedImage {

    BufferedImage image;
    Point.Double[][] points;
    double zoom;

    public ZoomedImage(BufferedImage image, Point.Double[][] points, double zoom) {
        this.image = image;
        this.points = points;
        this.zoom = zoom;
    }
}
