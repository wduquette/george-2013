/**
 * 
 */
package app;

import graphics.Sprite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;

import javax.swing.JPanel;

import rpg.Item;

/** An item box displays an item, or nothing.
 * TBD: It might be better to base this on a JLabel using standard Borders.  
 * JLabel does support a mouse listener.
 * @author will
 *
 */
@SuppressWarnings("serial") // Won't be serializing the actions.
public final class ItemBox extends JPanel {
	// GUI Layout Constants
	private static final int BORDER = 2;     	// Width of the box's border
	private static final int MARGIN = 2;     	// Internal margins
	private static final int WIDTH 	=         	// Item Box Width and Height
			Sprite.SIZE + 2*(BORDER + MARGIN);
	private static final int HEIGHT = WIDTH;
	
	// Instance Variables
	private Sprite defaultSprite = null;
	private Item item = null;
	private String defaultToolTipText = "";
	
	// Constructor
	
	/** Creates a particular kind of item box.
	 * 
	 * @param defaultSprite The sprite to display when the box is empty.
	 * @param defaultToolTipText The tool tip text when the box is empty.
	 */
	public ItemBox(Sprite defaultSprite, String defaultToolTipText) {
		// FIRST, save the inputs.
		this.defaultSprite = defaultSprite;
		this.defaultToolTipText = defaultToolTipText;
	
		// NEXT, set the preferred size.
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setMaximumSize(getPreferredSize());
	}
	
	/** Creates an item box with no default sprite or tool tip.
	 */
	public ItemBox() {
		this(null, "");
	}

	// Painting
	
	/** Painting code. */
	public void paintComponent(Graphics g) {
		// FIRST, prepare to paint
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		
		// FIRST, we'll draw the border
		g2d.setColor(Color.DARK_GRAY);
		g2d.fillRect(0, 0, WIDTH, HEIGHT);

		// NEXT, fill in the inset area
		g2d.setColor(new Color(0xF7D7B7));
		g2d.fillRect(BORDER, BORDER, WIDTH - 2*BORDER, HEIGHT - 2*BORDER);
				
		// NEXT, add the icon.
		int iTop = BORDER + MARGIN;
		int iLeft = BORDER + MARGIN;
		
		if (item != null) {
			g2d.drawImage(item.getImage(), iLeft, iTop, this);
		} else if (defaultSprite != null) {
			g2d.drawImage(defaultSprite.image(), iLeft, iTop, this);
		} else {
			// box is empty
		}
			
		// NEXT, sync everything; apparently this is required on Linux.
		Toolkit.getDefaultToolkit().sync();
	}

	/**
	 * @return the item
	 */
	public Item getItem() {
		return this.item;
	}

	/**
	 * @param item the item to set
	 */
	public void setItem(Item item) {
		this.item = item;
		if (item != null)
			this.setToolTipText(item.toString());
		else
			this.setToolTipText(defaultToolTipText);
		repaint();
	}
}
