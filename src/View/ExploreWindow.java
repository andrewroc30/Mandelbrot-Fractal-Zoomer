package View;

import Controller.ImageController;
import Utils.ZoomedImage;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

public class ExploreWindow extends JFrame {

    JLabel background;
    BufferedImage image;
    int dimX;
    int dimY;
    Point.Double[][] points;
    double zoom;

    public ExploreWindow() {
        super("Explore the Mandelbrot Fractal");
    }

    public void initWindow() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        dimX = (int)screenSize.getWidth();
        dimY = (int)screenSize.getHeight();
        setSize(dimX, dimY);
        ZoomedImage zi = ImageController.createZoomedImage((int)screenSize.getWidth(), (int)screenSize.getHeight(), 1000, 1, 0, 0);
        //ZoomedImage zi = ImageController.createSmoothZoomedImage((int)screenSize.getWidth(), (int)screenSize.getHeight(), 1000, 1, 0, 0);
        image = zi.image;
        points = zi.points;
        zoom = zi.zoom;
        background = new JLabel();
        background.setIcon(new ImageIcon(image));
        background.setLayout(new BorderLayout());
        this.setContentPane(background);
        add(new LayeredPane(this));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void updateImage(ZoomedImage img) {
        image = img.image;
        points = img.points;
        zoom = img.zoom;
        background = new JLabel();
        background.setIcon(new ImageIcon(image));
        background.setLayout(new BorderLayout());
        this.setContentPane(background);
        add(new LayeredPane(this));
        revalidate();
        repaint();
    }
}


class LayeredPane extends JLayeredPane {

    int onClickX;
    int onClickY;
    int onDragX;
    int onDragY;
    int onReleaseX;
    int onReleaseY;
    int highlightedX;
    int highlightedY;
    boolean dragging;
    ExploreWindow parentWindow;

    public LayeredPane(ExploreWindow explorewindow) {
        parentWindow = explorewindow;
        dragging = false;
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                onClickX = e.getX();
                onClickY = e.getY();
            }
            @Override
            public void mouseDragged(MouseEvent e) {
                onDragX = e.getX();
                onDragY = e.getY();
                dragging = true;
                repaint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                onReleaseX = e.getX();
                onReleaseY = e.getY();
                dragging = false;

                // Get the new zoom
                double xDist = Math.abs(onClickX - highlightedX);
                double ratio = xDist / (double)parentWindow.dimX;
                double newZoom = parentWindow.zoom * (1 / ratio);
                // Get the new midpoint
                int xPixel = (onClickX + highlightedX) / 2;
                int yPixel = (onClickY + highlightedY) / 2;
                Point.Double midPoint = parentWindow.points[yPixel][xPixel];
                // Create the image
                ZoomedImage img = ImageController.createZoomedImage(parentWindow.dimX, parentWindow.dimY, 1000, newZoom, midPoint.x, midPoint.y);
                //ZoomedImage img = ImageController.createSmoothZoomedImage(parentWindow.dimX, parentWindow.dimY, 1000, newZoom, midPoint.x, midPoint.y);
                parentWindow.updateImage(img);
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        if (dragging) {
            // Get the points with the correct ratio
            int diffX = Math.abs(onClickX - onDragX);
            int diffY = Math.abs(onClickY - onDragY);
            int dimX = parentWindow.dimX;
            int dimY = parentWindow.dimY;
            float xRatio = (float)(diffX) / (float)(dimX);
            float yRatio = (float)(diffY) / (float)(dimY);
            if (xRatio > yRatio) {
                highlightedX = onDragX;
                int newDiffY = (int)(dimY * xRatio);
                if (onDragY > onClickY) {
                    highlightedY = onClickY + newDiffY;
                } else {
                    highlightedY = onClickY - newDiffY;
                }
            } else if (xRatio < yRatio) {
                highlightedY = onDragY;
                int newDiffX = (int)(dimX * yRatio);
                if (onDragX > onClickX) {
                    highlightedX = onClickX + newDiffX;
                } else {
                    highlightedX = onClickX - newDiffX;
                }
            } else {
                highlightedX = onDragX;
                highlightedY = onDragY;
            }

            // Draw dragged box
            //Top
            g2d.draw(new Line2D.Double(onClickX, onClickY, highlightedX, onClickY));
            //Left
            g2d.draw(new Line2D.Double(onClickX, onClickY, onClickX, highlightedY));
            //Right
            g2d.draw(new Line2D.Double(highlightedX, onClickY, highlightedX, highlightedY));
            //Bottom
            g2d.draw(new Line2D.Double(onClickX, highlightedY, highlightedX, highlightedY));
        }
        g2d.dispose();
    }
}
