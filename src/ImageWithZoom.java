import java.awt.image.BufferedImage;

public class ImageWithZoom {
    public double zoom;
    public BufferedImage image;

    public ImageWithZoom(BufferedImage image, double zoom) {
        this.zoom = zoom;
        this.image = image;
    }

    public BufferedImage getImage() {
        return this.image;
    }

    public double getZoom() {
        return this.zoom;
    }
}
