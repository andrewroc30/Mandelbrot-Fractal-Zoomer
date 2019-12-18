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
                double xDist = Math.abs(onClickX - onReleaseX);
                double ratio = xDist / (double)parentWindow.dimX;
                double newZoom = parentWindow.zoom * (1 / ratio);
                // Get the new midpoint
                int xPixel = (onClickX + onReleaseX) / 2;
                int yPixel = (onClickY + onReleaseY) / 2;
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
            //Draw dragged box TODO: Make it so that we preserve dimensions http://math.hws.edu/eck/js/mandelbrot/MB.html
            int diffX = onClickX - onDragX;
            int diffY = onClickY - onDragY;
            int dimX = parentWindow.dimX;
            int dimY = parentWindow.dimY;
            double ratio = (double)(dimX) / (double)(dimY);
            if (diffX > diffY) {
                //Top
                g2d.draw(new Line2D.Double(onClickX, onClickY, onDragX, onClickY));
                //Left
                g2d.draw(new Line2D.Double(onClickX, onClickY, onClickX, onDragY));
                //Right
                g2d.draw(new Line2D.Double(onDragX, onClickY, onDragX, onDragY));
                //Bottom
                g2d.draw(new Line2D.Double(onClickX, onDragY, onDragX, onDragY));
            } else if (diffX < diffY) {
                //Top
                g2d.draw(new Line2D.Double(onClickX, onClickY, onDragX, onClickY));
                //Left
                g2d.draw(new Line2D.Double(onClickX, onClickY, onClickX, onDragY));
                //Right
                g2d.draw(new Line2D.Double(onDragX, onClickY, onDragX, onDragY));
                //Bottom
                g2d.draw(new Line2D.Double(onClickX, onDragY, onDragX, onDragY));
            }
        }
        g2d.dispose();
    }
}
