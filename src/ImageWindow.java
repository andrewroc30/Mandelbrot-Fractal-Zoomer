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
        setCloseOperation();
    }

    /**
     * Makes sure to cancel image creation on window close
     */
    public void setCloseOperation() {
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                Main.setIsCancelledForce(true);
            }
        });
    }

    /**
     * Sets the image of this window
     * @param img The image to display in the window
     */
    public void setImageLabel(BufferedImage img) {
        imageLabel.setIcon(new ImageIcon(img));
        revalidate();
        repaint();
    }
}
