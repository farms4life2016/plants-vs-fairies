package demos;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class CartoonizeDemo {

    static JFrame window;
    static CartoonizeDisplay screen;
    

    public static void main(String[] args) {

        final int SCREEN_W = 700, SCREEN_H = 700;

        window = new JFrame("Colour similarity test");
        window.setSize(SCREEN_W, SCREEN_H);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBackground(Color.BLACK);
        window.setBounds(0, 0, SCREEN_W, SCREEN_H);
        

        screen = new CartoonizeDisplay(window);
        Container c = window.getContentPane();
        c.setLayout(new GridLayout());
        c.add("unamed", screen);

        System.out.println("Time to shine!");
        window.setVisible(true); // remember that this call goes last!!!
        
    }
    
}


class CartoonizeDisplay extends JPanel implements ActionListener {

    static BufferedImage gardieImage, gardieCartoonized;
    static Timer fps;
    static final String gardieLocation = "sprites\\blender_gardie.png";
    static final int TOLERANCE = 10;
    
   
    public CartoonizeDisplay(Container p) {
        setBackground(Color.LIGHT_GRAY);

        int pxchanged = 0;
        
        try {
            // this is how you can read in from an image file:
            gardieImage = ImageIO.read(new File(gardieLocation));
            gardieCartoonized = ImageIO.read(new File(gardieLocation));

            pxchanged = cartoonize(gardieCartoonized);
            TraceDemoDisplay.traceImage(gardieCartoonized, null);

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Pixels changed: " + pxchanged);

        fps = new Timer(1000, this);
        fps.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.drawImage(gardieImage, -100, 200, null);
        g2.drawImage(gardieCartoonized, 200, 200, null);
        
        //System.out.println("drawing!");
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(fps)) {
            repaint();
            //System.out.println("refreshing!");
        }
    }

    public int cartoonize(BufferedImage image) {
        // we use a set to store all pixels that will be coloured black
        // Set<Point> traceable = new TreeSet<>();
        final int width = image.getWidth();
        final int height = image.getHeight();
        int pixelsChanged = 0;
        // Graphics2D g = image.createGraphics(); // do we need this?

        // my plan is to loop through every pixel
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                // convert hex colour into rgb triplet 
                Color current = new Color(image.getRGB(x, y), true);

                // for opaque pixels, we need a colour-similarity detector
                if (current.getAlpha() == 255) {

                    if (x < width - 1) {
                        Color right = new Color(image.getRGB(x + 1, y));
                        if (right.getAlpha() == 255 && withinTolerance(current, right)) {
                            image.setRGB(x + 1, y, current.getRGB());
                            pixelsChanged++;
                        }
                    }

                    if (y < height - 1) {
                        Color below = new Color(image.getRGB(x, y + 1));
                        if (below.getAlpha() == 255 && withinTolerance(current, below)) {
                            image.setRGB(x, y + 1, current.getRGB());
                            pixelsChanged++;
                        }
                    }

                }
                // System.out.println(image.getRGB(x, y));
            }
        }

        /*
        try { // write image to output file
            ImageIO.write(image, "png", new File("sprites\\traced_braixen.png"));
        } catch (IOException e) {
            e.printStackTrace();
        } */
        return pixelsChanged;
        
    }

    /**
     * Checks if two colours are similar to each other
     * @param original
     * @param other
     * @return true iff each rgb value satisfies abs(original - tolerance) <= other
     */
    public boolean withinTolerance(Color original, Color other) {
        //System.out.println("does this even run?");
        // assume original and other both have alpha 255
        if (original.getRed() - TOLERANCE <= other.getRed() && other.getRed() <= original.getRed() + TOLERANCE) {
            if (original.getGreen() - TOLERANCE <= other.getGreen() && other.getGreen() <= original.getGreen() + TOLERANCE) {
                if (original.getBlue() - TOLERANCE <= other.getBlue() && other.getBlue() <= original.getBlue() + TOLERANCE) {
                    
                    return true;
                }
            }
        }

        return false;
    }
}

