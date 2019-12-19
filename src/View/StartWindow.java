package View;

import Main.Main;

import javax.swing.*;

public class StartWindow extends JFrame {
    private JButton gifButton;
    private JButton mp4Button;
    private JButton pngButton;
    private JButton exploreButton;

    public StartWindow() {
        super("Mandelbrot Fractal Zoomer");
    }


    /**
     * Makes this window appear
     */
    public void showStartWindow() {
        this.configureButtons();
        this.setSize(400, 400);
        this.setLayout(null);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Configures all of the buttons for this window
     */
    private void configureButtons() {
        //CREATE ALL OF THE BUTTONS
        this.gifButton = new JButton("Create GIF");
        this.gifButton.setBounds(15, 5, 150, 150);
        this.gifButton.addActionListener(arg0 -> {
            System.out.println("Create a GIF");
            Main.gifWindow.initWindow();
        });

        this.mp4Button = new JButton("Create MP4");
        this.mp4Button.setBounds(215, 5, 150, 150);
        this.mp4Button.addActionListener(arg0 -> {
            System.out.println("Create a MP4");
            Main.mp4Window.initWindow();
        });

        this.pngButton = new JButton("Create PNG");
        this.pngButton.setBounds(15, 205, 150, 150);
        this.pngButton.addActionListener(arg0 -> {
            System.out.println("Create a PNG");
            Main.pngWindow.initWindow();
        });

        this.exploreButton = new JButton("Explore Fractal");
        this.exploreButton.setBounds(215, 205, 150, 150);
        this.exploreButton.addActionListener(arg0 -> {
            System.out.println("Explore!");
            Main.exploreWindow.initWindow();
        });


        //ADD ALL OF THE BUTTONS TO THE FRAME
        this.add(this.gifButton);
        this.add(this.mp4Button);
        this.add(this.pngButton);
        this.add(this.exploreButton);
    }
}
