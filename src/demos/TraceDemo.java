package demos;


import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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

    static BufferedImage gardieImage, gardieTraced, gardieFiltered;
    static Timer fps;
    static final String gardieLocation = "sprites\\blender_braixen.png";
    
   
    public TraceDemoDisplay(Container p) {
        setBackground(Color.LIGHT_GRAY);
        
        try {
            // this is how you can read in from an image file:
            gardieImage = ImageIO.read(new File(gardieLocation));
            gardieTraced = ImageIO.read(new File(gardieLocation));
            gardieFiltered = ImageIO.read(new File(gardieLocation));

            traceImage(gardieTraced);

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

    public void traceImage(BufferedImage image) {
        // we use a set to store all pixels that will be coloured black
        // Set<Point> traceable = new TreeSet<>();
        final int width = image.getWidth();
        final int height = image.getHeight();
        // Graphics2D g = image.createGraphics(); // do we need this?

        // my plan is to loop through every pixel
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color c = new Color(image.getRGB(x, y), true);
                if (0 < c.getAlpha() && c.getAlpha() < 255) {
                    image.setRGB(x, y, 0xFF000000);
                }
                // System.out.println(image.getRGB(x, y));
            }
        }
        try {
            ImageIO.write(image, "png", new File("sprites\\traced_braixen.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
