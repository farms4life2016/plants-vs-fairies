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
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class TraceDemo {

    static JFrame window;
    static TraceDemoDisplay screen;
    

    public static void main(String[] args) {

        final int SCREEN_W = 700, SCREEN_H = 700;

        window = new JFrame("Trace test");
        window.setSize(SCREEN_W, SCREEN_H);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBackground(Color.BLACK);
        window.setBounds(0, 0, SCREEN_W, SCREEN_H);
        

        screen = new TraceDemoDisplay(window);
        Container c = window.getContentPane();
        c.setLayout(new GridLayout());
        c.add("unamed", screen);

        System.out.println("Time to shine!");
        window.setVisible(true); // remember that this call goes last!!!
        
    }
    
}

class TraceDemoDisplay extends JPanel implements ActionListener {

    static BufferedImage gardieImage, gardieTraced;
    static Timer fps;
    static final String gardieLocation = "sprites\\blender_braixen.png";
    
   
    public TraceDemoDisplay(Container p) {
        setBackground(Color.LIGHT_GRAY);
        
        try {
            // this is how you can read in from an image file:
            gardieImage = ImageIO.read(new File(gardieLocation));
            gardieTraced = ImageIO.read(new File(gardieLocation));

            traceImage(gardieImage, null);
            traceImage(gardieTraced, null);

            doubleTrace(gardieTraced, null);

        } catch (IOException e) {
            e.printStackTrace();
        }

        fps = new Timer(1000, this);
        fps.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.drawImage(gardieImage, -100, 200, null);
        g2.drawImage(gardieTraced, 200, 200, null);
        
        //System.out.println("drawing!");
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(fps)) {
            repaint();
            //System.out.println("refreshing!");
        }
    }

    public static void traceImage(BufferedImage image) {
        traceImage(image, "sprites\\traced_braixen.png");
    }

    /**
     * traces/outlines a given image by mutating all semi-transparent pixels to black
     * @param image
     * @param filename if not null, the traced image will be sent to the specified output file
     */
    public static int traceImage(BufferedImage image, String filename) {
        
        final int WIDTH = image.getWidth();
        final int HEIGHT = image.getHeight();
        int pxChanged = 0;

        // my plan is to loop through every pixel
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                Color c = new Color(image.getRGB(x, y), true);
                if (0 < c.getAlpha() && c.getAlpha() < 255) {
                    image.setRGB(x, y, Color.BLACK.getRGB());
                    pxChanged++;
                }
                // System.out.println(image.getRGB(x, y));
            }
        }

        // if filename is not null, write image to output file
        if (filename != null) {
            try {
                ImageIO.write(image, "png", new File("sprites\\traced_braixen.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return pxChanged;
        
    }

    /**
     * actually tries to trace an image
     * @param image
     * @param name
     * @return
     */
    public static int doubleTrace(BufferedImage image, String name) {

        // we use a set to store all pixels that will be coloured black
        Set<Point> traceable = new TreeSet<>();
        final int WIDTH = image.getWidth();
        final int HEIGHT = image.getHeight();
        int pxChanged = 0;

        // my plan is to loop through every pixel
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {

                // get the current pixel's colour
                Color c = new Color(image.getRGB(x, y), true);
                if (c.equals(Color.BLACK)) {
                    
                    // check surrounding pixels for transparent pixels
                    if (0 < x) { 
                        Color adj = new Color(image.getRGB(x - 1, y), true);
                        if (adj.getAlpha() == 0)
                            traceable.add(new Point(x - 1, y)); // left
                    }
                    if (0 < y) {
                        Color adj = new Color(image.getRGB(x, y - 1), true);
                        if (adj.getAlpha() == 0)
                            traceable.add(new Point(x, y - 1)); // up
                    }
                    if (x < WIDTH - 1) {
                        Color adj = new Color(image.getRGB(x + 1, y), true);
                        if (adj.getAlpha() == 0)
                            traceable.add(new Point(x + 1, y)); // right
                    }
                    if (y < HEIGHT - 1) {
                        Color adj = new Color(image.getRGB(x, y + 1), true);
                        if (adj.getAlpha() == 0)
                            traceable.add(new Point(x, y + 1)); // down
                    }

                }
                // System.out.println(image.getRGB(x, y));
            }
        }

        // for each point in the set, mutate the corresponding pixel to black
        for (Point p : traceable) {
            image.setRGB(p.x, p.y, Color.BLACK.getRGB());
            pxChanged++;
        }

        // if filename is not null, write image to output file
        if (name != null) {
            try {
                ImageIO.write(image, "png", new File("sprites\\traced_braixen.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return pxChanged;
    }
}

class Point implements Comparator<Point>, Comparable<Point> {

    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    
    @Override
    public int compare(Point o1, Point o2) {
        int compareX = Integer.compare(o1.x, o2.x);
        if (compareX != 0) {
            return compareX;
        } else {
            return Integer.compare(o1.y, o2.y);
        }
        
    }


    @Override
    public int compareTo(Point o) {
        return compare(this, o);
    }
}
