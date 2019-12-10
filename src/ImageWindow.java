import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageWindow extends JFrame {

    JLabel imageLabel;

    public ImageWindow(int dimX, int dimY, BufferedImage image) {
        super("Image Viewer");
        this.imageLabel = new JLabel(new ImageIcon(image));
        setSize(dimX, dimY);
        this.getContentPane().setLayout(new FlowLayout());
        this.getContentPane().add(imageLabel);
        this.setVisible(true);
    }
}
