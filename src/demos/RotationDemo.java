package demos;


import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class RotationDemo {

    static JFrame window;
    static RotationDemoDisplay screen;
    

    public static void main(String[] args) {

        window = new JFrame("Rotation test");
        window.setSize(500, 200);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBackground(Color.BLACK);
        window.setBounds(0, 0, 500, 700);
        

        screen = new RotationDemoDisplay(window);
        Container c = window.getContentPane();
        c.setLayout(new GridLayout());
        c.add("unamed", screen);

        System.out.println("Time to shine!");
        window.setVisible(true); // remember that this call goes last!!!
        
    }
}

class RotationDemoDisplay extends JPanel implements ActionListener {

    static ImageIcon gardieIcon; 
    static BufferedImage gardieImage, gardieRotated90cw, gardieSpinning;
    static Timer fps;
    static final String gardieLocation = "sprites\\gardie.png";
    final static Color freeze = new Color(10, 230, 250, 100); // 0xFF0ae6fa
    final int mask = 0xFF0ae6fa;

    static int counter = 0;
    
   
    public RotationDemoDisplay(Container p) {
        gardieIcon = new ImageIcon(gardieLocation);
        setBackground(Color.LIGHT_GRAY);
        
        try {
            // this is how you can read in from an image file:
            gardieImage = ImageIO.read(new File(gardieLocation));
            gardieRotated90cw = ImageIO.read(new File(gardieLocation));
            gardieSpinning = ImageIO.read(new File(gardieLocation));

            

        } catch (IOException e) {
            e.printStackTrace();
        }

        // convert rotation angle to rad
        double rotation = Math.toRadians(90);

        // get the coords for the center of the image
        double locationX = gardieImage.getWidth() / 2;
        double locationY = gardieImage.getHeight() / 2;

        // create "functions" that rotate images by the specified angle
        AffineTransform tx = AffineTransform.getRotateInstance(rotation, locationX, locationY);
        AffineTransformOp op90cw = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR); // can also try bi-cubic

        // this is how you apply the rotation; rotated image stored in second argument 
        // argument 1 cannot be the same as argument 2 for some reason
        op90cw.filter(gardieImage, gardieRotated90cw);

        
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
        g2.drawImage(gardieRotated90cw, 200, 200, null);
        g.drawImage(gardieIcon.getImage(), 0, 400, freeze, null);

        g.drawImage(gardieSpinning, 200, 400, null);

        
        //System.out.println("drawing!");
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(fps)) {
            repaint();
            //System.out.println("refreshing!");

            // the following code makes gardevoir spin
            counter = (counter+60) % 360;

            // convert rotation angle to rad
            double rotation = Math.toRadians(counter);

            // get the coords for the center of the image
            double locationX = gardieImage.getWidth() / 2;
            double locationY = gardieImage.getHeight() / 2;

            // create "functions" that rotate images by the specified angle
            AffineTransform tx = AffineTransform.getRotateInstance(rotation, locationX, locationY);
            AffineTransformOp op90cw = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR); // can also try bi-cubic

            // this is how you apply the rotation; rotated image stored in second argument 
            // argument 1 cannot be the same as argument 2 for some reason
            op90cw.filter(gardieImage, gardieSpinning);

        }
    }

}
