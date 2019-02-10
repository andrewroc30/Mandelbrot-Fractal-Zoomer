import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

public class ZoomableImage extends JComponent {
  private float scaleFactor;

  public BufferedImage originalImage;

  public ZoomableImage() {
    this.scaleFactor = 1;
    this.setSize(0, 0);
  }

  public void setImage(BufferedImage image) {
    this.originalImage = image;
    this.setSize(image.getWidth(), image.getHeight());
    //setSize does repainting, no need to call repaint()
    //this.repaint();
  }

  public void setScaleFactor(float scaleFactor) {
    this.scaleFactor = scaleFactor;
    this.repaint();
  }

  @Override
  public void paintComponent(Graphics g) {
    if (this.originalImage != null) {
      Graphics2D g2 = (Graphics2D) g;
      int newW = (int) (originalImage.getWidth() * scaleFactor);
      int newH = (int) (originalImage.getHeight() * scaleFactor);
      this.setPreferredSize(new Dimension(newW, newH));
      this.revalidate();
      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
              //RenderingHints.VALUE_INTERPOLATION_BILINEAR);
              RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
      //RenderingHints.VALUE_INTERPOLATION_BICUBIC);
      g2.drawImage(originalImage, 0, 0, newW, newH, null);
    }
  }
}
