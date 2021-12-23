/**
 * 
 */
package app;

import graphics.StandardTile;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;

import javax.swing.JRadioButton;

import rpg.Condition;
import rpg.pc.PlayerCharacter;



/** A status box displays information about a player character.
 * TBD: Consider making this a static nested class of StatusBar.
 * @author will
 *
 */
@SuppressWarnings("serial") // Won't be serializing the actions.
final class StatusBox extends JRadioButton {
	// GUI Layout Constants
	private static final int BORDER = 3;     // Width of the box's border
	private static final int MARGIN = 4;     // Internal margins
	private static final int GHEIGHT = 3;    // Gauge height
	private static final int CSIZE = 10;   // Condition Icon size in pixels.
	private static final int WIDTH =         // Status Box Width
			StandardTile.SIZE + 2*(BORDER + MARGIN);
	
	
	// Instance Variables
	private PlayerCharacter pc = null;
	private int nameHeight = 0;
	private int height;
	
	// Constructor
	
	/** Creates a status box for a  player character. */
	StatusBox() {
		// FIRST, give it a non-zero height; we'll fix it on paint.
		height = StandardTile.SIZE;
		setPreferredSize(new Dimension(WIDTH, height));
	}
	
	// Mutators
	
	/** Links a player character with the status box.
	 * 
	 * @param pc The player character.
	 */
	void setPlayerCharacter(PlayerCharacter pc) {
		this.pc = pc;
		repaint();
	}
	
	/** @return the displayed player character. */
	PlayerCharacter getPlayerCharacter() {
		return pc;
	}
	
	/** Sets the box's selection flag.
	 * TBD: Is this override necessary? 
	 * 
	 * @param selected the new flag.
	 */
	public void setSelected(Boolean selected) {
		super.setSelected(selected);
		repaint();  // TBD: Necessary?
	}
	
	// Painting
	
	/** Map-painting code. */
	public void paintComponent(Graphics g) {
		// FIRST, prepare to paint
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		
		// NEXT, determine the height of the font for drawing character names.
		FontMetrics metrics = g2d.getFontMetrics(App.DATA_FONT);

		if (nameHeight == 0) {
			// FIRST, get the height of the name text.
			nameHeight = metrics.getHeight();
			
			// NEXT, compute the total height of the widget.
			height = BORDER +                 // Top border
					 MARGIN +                 // Margin
					 nameHeight +             // PC Name
					 MARGIN +                 // Margin
					 StandardTile.SIZE +      // PC Icon
					 MARGIN +                 // Margin
					 3*GHEIGHT +              // The life/mana/exp gauges
					 MARGIN +                 // Margin
					 CSIZE +                // Condition Icon Height
					 MARGIN +                 // Margin
					 BORDER;                  // Bottom border
			
			setPreferredSize(new Dimension(WIDTH, height));
			setMaximumSize(getPreferredSize());
			revalidate();
		}
		
		if (pc == null) {
			g2d.setColor(Color.BLACK);
			g2d.fillRect(0,  0, WIDTH, height);
			
			// NEXT, sync everything; apparently this is required on Linux.
			Toolkit.getDefaultToolkit().sync();
			
			return;
		}

		if (isSelected())
			g2d.setColor(Color.LIGHT_GRAY);
		else
			g2d.setColor(Color.DARK_GRAY);

		g2d.fillRect(0, 0, WIDTH, height);
		
		// NEXT, fill in the inset area
		g2d.setColor(Color.GRAY);
		g2d.fillRect(BORDER, BORDER, WIDTH - 2*BORDER, height - 2*BORDER);
		
		// NEXT, add the name.
		g2d.setColor(Color.BLACK);
		g2d.setFont(App.DATA_FONT);
		int left = BORDER + MARGIN;
		int top = BORDER + MARGIN;
		g2d.drawString(pc.name(), left, top + metrics.getAscent());
		
		// NEXT, add pc's icon.
		top += nameHeight + MARGIN;
		g2d.drawImage(pc.image(), left, top, this);
		
		// NEXT, if character is dead, mark him so.
		if (!pc.isAlive()) {
			BasicStroke lineStroke = new BasicStroke((float)4.0, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

			g2d.setColor(Color.RED);
			g2d.setStroke(lineStroke);
			g2d.drawLine(left + 2, 
						 top + 2, 
						 left + StandardTile.SIZE - 3, 
						 top + StandardTile.SIZE - 3);
			g2d.drawLine(left + 2, 
						 top + StandardTile.SIZE - 3, 
					 	 left + StandardTile.SIZE - 3, 
					 	 top + 2);
		}

		// NEXT, add the gauges.  First, draw the background
		top += StandardTile.SIZE + MARGIN;
		g2d.setColor(Color.DARK_GRAY);
		g2d.fillRect(left, top, StandardTile.SIZE, 3*GHEIGHT);
		
		// NEXT, draw the bars.
		drawBar(g2d, left, top, Color.GREEN, pc.HP(), pc.maxHP());
		
		top += GHEIGHT;
		drawBar(g2d, left, top, Color.RED, pc.mana(), pc.stats.manaMax());
		
		top += GHEIGHT;
		int num = pc.stats.exp() - pc.stats.expBase(); 
		int den = pc.stats.expNext() - pc.stats.expBase();
		drawBar(g2d, left, top, Color.cyan, num, den);
		
		// NEXT, draw the status conditions.  At present, assume
		// that there are four of them.
		top += GHEIGHT + MARGIN;
		
		int cleft = left;
		
		for (Condition cond : Condition.values()) {
			if (pc.hasCondition(cond)) {
				g2d.drawImage(cond.image(), cleft, top, this);
			}
		
			cleft += CSIZE;
		}
		
	}
	
	private void drawBar(Graphics g, int left, int top, Color color, int num, int den) {
		int lbWidth = (int)Math.ceil(StandardTile.SIZE * ((float)num/den));
		g.setColor(color);
		g.fillRect(left, top, lbWidth, GHEIGHT);
	}
}
