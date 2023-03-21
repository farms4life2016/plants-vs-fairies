package demos;


import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;
import java.awt.image.ShortLookupTable;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class ColorizeDemo {

    static JFrame window;
    static Display screen;
    

    public static void main(String[] args) {

        window = new JFrame("Image test");
        window.setSize(500, 200);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBackground(Color.BLACK);
        window.setBounds(0, 0, 500, 700);
        

        screen = new Display(window);
        Container c = window.getContentPane();
        c.setLayout(new GridLayout());
        c.add("unamed", screen);

        System.out.println("Time to shine!");
        window.setVisible(true); // remember that this call goes last!!!
        
    }
}

class Display extends JPanel implements ActionListener {

    static ImageIcon gardieIcon; 
    static BufferedImage gardieImage, gardieColored, gardieFiltered;
    static Timer fps;
    static final String gardieLocation = "sprites\\gardie.png";
    final static Color freeze = new Color(10, 230, 250, 100); // 0xFF0ae6fa
    final int mask = 0xFF0ae6fa;
    BufferedImageOp colorizeFilter = 
        createColorizeOp((short)freeze.getRed(), (short)freeze.getGreen(), (short)freeze.getBlue());
    
   
    public Display(Container p) {
        gardieIcon = new ImageIcon(gardieLocation);
        setBackground(Color.LIGHT_GRAY);
        
        try {
            // this is how you can read in from an image file:
            gardieImage = ImageIO.read(new File(gardieLocation));
            gardieColored = ImageIO.read(new File(gardieLocation));
            gardieFiltered = ImageIO.read(new File(gardieLocation));

            // we are essentially drawing a freeze-coloured rectangle on gardieImage
            // the transparent pixels in gardieImage won't be drawn on
            final int w = gardieImage.getWidth();
            final int h = gardieImage.getHeight();
            Graphics2D g = gardieImage.createGraphics();
            g.setComposite(AlphaComposite.SrcAtop);
            g.setColor(freeze);
            g.fillRect(0, 0, w, h);
            g.dispose();
            // ImageIO.write(gardieImage, "png", new File("sprites\\snowy_gardie.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        // rgb(111, 237, 248) 0xFF6fedf8
        gardieColored = createColorImage(gardieColored, mask);
        colorizeFilter.filter(gardieFiltered, gardieFiltered);

        fps = new Timer(1000, this);
        fps.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.drawImage(gardieIcon.getImage(), 0, 0, null);
        g2.drawImage(gardieIcon.getImage(), 200, 0, null);
        g2.drawImage(gardieImage, 0, 200, null);
        g2.drawImage(gardieColored, 200, 200, null);
        g.drawImage(gardieIcon.getImage(), 0, 400, freeze, null);

        g.drawImage(gardieFiltered, 200, 400, null);

        g.setColor(freeze);
        g.fillRect(200, 0, 200, 200);

        
        //System.out.println("drawing!");
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(fps)) {
            repaint();
            //System.out.println("refreshing!");
        }
    }

    private BufferedImage createColorImage(BufferedImage originalImage, int mask) {

        // make a copy of the original image
        BufferedImage colorImage = new BufferedImage(originalImage.getWidth(),
                originalImage.getHeight(), originalImage.getType());

        // loop through every pixel in the image, and apply the given color mask onto each pixel
        for (int x = 0; x < originalImage.getWidth(); x++) {
            for (int y = 0; y < originalImage.getHeight(); y++) {
                int pixel = originalImage.getRGB(x, y) & mask;
                colorImage.setRGB(x, y, pixel);
                
            }
        }

        return colorImage;
    }

    protected LookupOp createColorizeOp(short R1, short G1, short B1) {
        short[] alpha = new short[256];
        short[] red = new short[256];
        short[] green = new short[256];
        short[] blue = new short[256];
    
        for (short i = 0; i < 256; i++) {
            alpha[i] = i;

            // each component gets masked with the respective colour's component 
            red[i] = (short) (i & R1);
            green[i] = (short) (i & G1);
            blue[i] = (short) (i & B1);

            // Y = 0.2126*R + 0.59*G + 0.11*B
        }
    
        short[][] data = new short[][] {
                red, green, blue, alpha
        };
    
        LookupTable lookupTable = new ShortLookupTable(0, data);
        return new LookupOp(lookupTable, null);
    }
    
}