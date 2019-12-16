import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageWindow extends JFrame {

    JLabel imageLabel;
    //JLabel iterationsLabel;

    public ImageWindow(int dimX, int dimY, BufferedImage image) {
        super("Image Viewer");
        this.imageLabel = new JLabel(new ImageIcon(image));
        //this.iterationsLabel = new JLabel();
        //this.iterationsLabel.setBounds(50, 50, 50, 50);
        setSize(dimX, dimY);
        this.getContentPane().setLayout(new FlowLayout());
        this.getContentPane().add(imageLabel);
        //this.getContentPane().add(iterationsLabel);
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
    public void setImageLabel(BufferedImage img, int iterations) {
        imageLabel.setIcon(new ImageIcon(img));
        //iterationsLabel.setText(String.valueOf(iterations));
        revalidate();
        repaint();
    }
}
