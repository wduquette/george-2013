/**
 * 
 */
package app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JFrame;

/** This is the main module, and the main window; but all it really does
 * is create an initialize a Board, which then carries the load.
 * @author will
 *
 */ 
@SuppressWarnings("serial") // Won't be serializing the actions.
public final class App extends JFrame {
	// Application Constants
	
	/** Font to use for major titles. */
	public static final Font BIG_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 14);

	/** Font to use for log. */
	public static final Font LOG_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);

	/** Font to use for displaying data. */
	public static final Font DATA_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 10);

	/** The background color for cards in the card stack. */
	public static final Color BACKGROUND = new Color(0x8A4C0F);
	

	// Constructor
	
	/** Starts the application. */
	private App() {
		Board board = Board.get();
		board.start();
		add(board, BorderLayout.CENTER);
		setTitle("George's Saga");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
		
	}
	
	/** Main routine
	 * @param args Unused
	 */
	public static void main(String[] args) {
		//Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}
	
	private static void createAndShowGUI() {
		new App();
	}

}
