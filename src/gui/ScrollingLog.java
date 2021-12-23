/**
 * 
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author will
 *
 */
@SuppressWarnings("serial") // Won't be serializing this object
public final class ScrollingLog extends JPanel {
	private final int MAX_LINES = 100;
	
	// Instance Variables
	private JTextArea text;
	private JScrollPane scroll;
	
	// Constructor
	
	/** Create a new scrolling log of the given visible size.
	 * 
	 * @param rows visible rows
	 * @param columns visible columns
	 */
	public ScrollingLog(int rows, int columns) {
		super(new BorderLayout());
		
		text = new JTextArea(rows, columns);
		text.setEditable(false);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		text.setBackground(new Color(0xFFFFCC));
		
		scroll = new JScrollPane(text);
		add(scroll, BorderLayout.CENTER);
	}
	
	/** Adds a message to the log.
	 * 
	 * @param message  The message to add.
	 */
	public void log(String message) {
		// FIRST, add the new text.
		text.append("\n" + message);
		
		// NEXT, if there's too much text, remove some.
		if (text.getLineCount() > MAX_LINES) {
			try {
				int offset = text.getLineStartOffset(1);
				text.replaceRange(null, 0, offset);
			} catch (Exception e) {
				System.out.println("Oy! I already checked the size!");
				System.exit(1);
			}
		}
		
		// NEXT, scroll to the bottom.
		text.setCaretPosition(text.getDocument().getLength());		
	}

	/** Sets the font for the scrolling log.
	 * @param font
	 * @see javax.swing.JComponent#setFont(java.awt.Font)
	 */
	public final void setLogFont(Font font) {
		text.setFont(font);
	}
}
