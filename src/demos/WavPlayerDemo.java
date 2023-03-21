package demos;

/**
 * Programmer Name: Yucen Xie
 * Course: ICS3U7
 * Teacher: Ms. Strelkovska
 * Last modified: Jan 14 2020
 * Program Name: Tchaikovsky Player
 * Description: Plays Tchaikovsky's 1812 Overture, or a file specified in the constructor
 * 				
 */

//import libraries
import java.io.*;
import java.util.Scanner;

import javax.sound.sampled.*;

public class WavPlayerDemo {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int input = 0;

        TchaikovskyPlayer wavplayer = new TchaikovskyPlayer("Grasswalkers.wav");
        TchaikovskyPlayer w2 = new TchaikovskyPlayer("PvZ Cerebrawl (Hextuple Mix).wav");

        // note: on the last repeat, end point will be ignored and audio will play until the last frame
        // note: on the first play, start point will be ignored and audio will play from frame #0
        final int start = 1000_000;
        wavplayer.setEndPoints(start, start + 90000);
        wavplayer.play(69);

        while (true) {
            System.out.println("Commands: ");
            System.out.println("0 : terminate the program");
            System.out.println("1 : pause the playback");
            System.out.println("2 : resume the playback");
            System.out.println("3 : print stuff");
            System.out.println("4 : switch tracks");
            input = sc.nextInt();

            if (input == 0) {
                break;
            } else if (input == 1) {
                wavplayer.pause();
            } else if (input == 2) {
                wavplayer.resume();
            } else if (input == 3) {
                wavplayer.printDebug();
            } else if (input == 4) {

                wavplayer.pause();
                w2.play(1);

            }
        }

        sc.close();
        w2.safeExit();
        wavplayer.safeExit(); // MUST CLOSE ALL RESOURCES !!!
    }

}

/**
 * @author admin
 *
 */
class TchaikovskyPlayer {
	
	//variables
	private static final String PIECE_NAME = ".\\Tchaikovsky - 1812 Overture, Op. 49.wav";
	
	private Clip myClip;
	private AudioInputStream myAudioInputStream;
	
	
	//constructor
	/**
	 * Makes an object that can play the specified file
	 * @param fileName The location of the sound file. Must be in .wav format
	 */
	public TchaikovskyPlayer(String fileName) {
		
		//unsuspicious way to retrieve music file 
        try {
			
			//gets a channel to play music on 
        	myClip = AudioSystem.getClip(); 
			
			//gets a file to play from (and puts it on said channel)
            myAudioInputStream = AudioSystem.getAudioInputStream(new File(fileName)); 
            
			//opens the clip with all the playback info stored in it, ready for playback. 
            myClip.open(myAudioInputStream);
            
		//check for errors
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            
        } //end try-catch
		
	} //end constructor I
	
	/**
	 * Makes an object that can play Tchaikovsky
	 */
	public TchaikovskyPlayer() {
		this(PIECE_NAME);
		
	} //end constructor II
	
	
	//methods
	
	/**
	 * Plays the music.
	 * @param repeats The number of times the music will play. Set repeat as -1 for an infinite loop (of cannon fire)
	 */
	public void play(int repeats) {
		
		 //play it
		myClip.loop(repeats);
			
		//begin reading file 
		myClip.start();
		
	} //end play
	
	/**
	 * Plays Tchaikovsky's 1812 Overture, set repeat as -1 for an infinite loop
	 * @param repeats The number of times the music will play. Set repeat as -1 for an infinite loop (of cannon fire)
	 * @param start The starting point in frames that the song should initially play at. The following iterations will start from the beginning.
	 */
	public void play(int repeats, int start) {
		
		myClip.setFramePosition(start);
		
		//play it
		myClip.loop(repeats);
			
		//begin reading file 
		myClip.start();

	} //end play

    public void pause() {
        myClip.stop();
    }

    public void resume() {
        myClip.start();
    }

    public void setEndPoints(int start, int end) {
        myClip.setLoopPoints(start, end);

        // with this line of code, playback will start at the correct position (instead of from the beginning)
        myClip.setFramePosition(start);
    }

    public void safeExit() {
        myClip.stop();
        myClip.close();

    }

    public void printDebug() {
        // frames = audacity samples.
        System.out.println("frame len: " + myClip.getFrameLength());
        System.out.println("frame posn: " + myClip.getLongFramePosition());
        System.out.println("sound level: " + myClip.getLevel());
        System.out.println("running: " + myClip.isRunning());
        System.out.println("formate: " + myClip.getFormat());
        System.out.println(myClip);
    }
	
} //end class


// some code from stack exchange 
class ClipLooper extends Thread 
{
    private Clip c;
    private int firstFrame, lastFrame, numLoops, currentLoop = 1;

    public ClipLooper(Clip c, int ff, int lf, int nl) throws Exception
    {
        if( ff < 0 || lf < ff || c.getFrameLength() < lf)
            throw new Exception("you screwed up!! \n");
        this.c = c;
        firstFrame = ff;
        lastFrame = lf;
        numLoops = nl;
    }

    @Override
    public void run()
    {
        while(currentLoop <= numLoops || numLoops <= 0)
        {
            c.setFramePosition(firstFrame);

            c.start();
            c.loop(-1);

            while(c.getFramePosition() < lastFrame-220)
            {}

            c.stop();
            ++currentLoop;
        }
    }
}
