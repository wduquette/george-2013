/**
 * 
 */
package app;

import graphics.Effects;
import graphics.StandardTile;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import map.Cell;
import rpg.Combatant;
import rpg.Entity;
import rpg.Feature;
import rpg.Item;
import rpg.Mobile;
import rpg.Posture;
import rpg.Region;
import rpg.feature.ItemStack;
import rpg.monster.Monster;

/** A widget for displaying a dungeon map.  If the
 * current mover is a player character, the map scrolls to show
 * him.
 * 
 * @author will
 */
@SuppressWarnings("serial") // Won't be serializing the actions.
final class MapPanel extends JPanel {
	// Constants
	/** How close the player is allowed to be to the edge of the
	 * map window during normal mode.
	 */
	private static final int NORMAL_BOUND = 9;
	
	/** How close the player is allowed to be to the edge of the 
	 * map window during combat mode.
	 */
	private static final int COMBAT_BOUND = 8;
	
	// Instance Variables
	private Board board;
	private Region region = null;
	private int visRows;     // viewport height in rows
	private int visCols;     // viewport columns in rows
	private int winWidth;    // viewport width in pixels
	private int winHeight;   // viewport height in pixels
	private int rTop = 0;    // Top row of visible area of map
	private int cLeft = 0;   // Left column of visible area of map.
	
	// Constructor
	
	/** Builds a MapPanel to display the map for any given region.
	 * 
	 * TBD: For now, the visible number of rows and columns must match the map.
	 * 
	 * @param board The game board.
	 * @param visRows The number of map rows to display
	 * @param visCols The number of map columns to display
	 * @param region The initial region.
	 */
	 MapPanel(Board board, int visRows, int visCols, Region region) {
		super();
		
		// FIRST, save the inputs.
		this.board = board;
		this.region = region;
		this.visRows = visRows;
		this.visCols = visCols;
		winWidth = this.visCols * StandardTile.SIZE;
		winHeight = this.visRows * StandardTile.SIZE;
		
		// NEXT, set the preferred panel size.
		setPreferredSize(new Dimension(winWidth, winHeight));
		
		// NEXT, make it double buffered, so that updates are clean.
		setDoubleBuffered(true);
		
		// NEXT, define a dummy tool tip, to turn tool tips on.
		// We override getToolTipText() to provide the real text.
		setToolTipText("Dummy");
	}
	
	/** Sets the region to display. 
	 * 
	 * @param region the new region.
	 */
	void setRegion(Region region) {
		if (region != this.region) {
			this.region = region;
			rTop = 0;
			cLeft = 0;
		}
	}
	
	/** Map-painting code. */
	public void paintComponent(Graphics g) {
		// FIRST, prepare to paint
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		
		// NEXT, if there's no region yet, we're done.
		if (region == null)
			return;
		
		// NEXT, if the current upper left is no longer on the map, reset to 0.
		if (rTop >= region.rows())
			rTop = 0;
		
		if (cLeft >= region.cols())
			cLeft = 0;
		
		// NEXT, if the current mover is a player character, make sure
		// that it's visible.
		Mobile pc = board.getMover();
		
		if (pc != null && board.party.contains(pc) && pc.place() != null) {
			see(pc.place());
		}
		
		// NEXT, the black background
		g2d.setColor(Color.BLACK);
		g2d.fillRect(0, 0, winWidth, winHeight);

		// NEXT, figure out what we can see.
		int rBottom = Math.min(region.rows(), rTop + visRows) - 1;
		int cRight = Math.min(region.cols(), cLeft + visCols) - 1;
		
		for (int r = rTop; r <= rBottom; r++) {
			for (int c = cLeft; c <= cRight; c++) {
				Cell rc = new Cell(r,c);
				if (region.inLOS(rc)) {
					region.markSeen(rc);
				}
			}
		}

		// NEXT, draw the region.
		
		for (int r = rTop; r <= rBottom; r++) {
			for (int c = cLeft; c <= cRight; c++) {
				Cell rc = new Cell(r,c);
				if (region.hasBeenSeen(rc)) {
					g2d.drawImage(region.terrainAt(rc).image(), c2x(c), r2y(r), this);
				}
			}
		}
	
		// NEXT, draw the Features on squares that have been seen.
		for (Feature f : region.getFeatures()) {
			if (region.hasBeenSeen(f.place()))
				drawEntity(g2d, f);
		}
		
		// NEXT, Draw the Monsters.
		for (Monster m : region.getMonsters()) {
			if (region.inLOS(m.place())) {
				if (m.isHostile())
					drawMobile(g2d, m, Color.RED);
				else
					drawMobile(g2d, m, Color.CYAN);
			}
		}
		
		// NEXT, Draw the NPCs.
		for (Mobile m : region.getNPCs()) {
			if (region.inLOS(m.place()))
				drawMobile(g2d, m, Color.CYAN);
		}

		// NEXT, draw the party
		for (Mobile m : board.party.members())
			drawMobile(g2d, m, Color.GREEN);
		
		// NEXT, draw the party leader mover, so as to make sure he's
		// on top of any stack.
		if (board.party.leader() != null) {
			drawMobile(g2d, board.party.leader(), Color.GREEN);
		}
		
		// NEXT, draw the attack animation sprite, if any.
		if (board.getEffect() != null) {
			drawEntity(g2d, board.getEffect());
		}
		
		// NEXT, sync everything; apparently this is required on Linux.
		Toolkit.getDefaultToolkit().sync();
	}
	
	/** Scrolls the visible map so that the required cell
	 * is visible.
	 * @param cell The cell to see.
	 */
	private void see(Cell cell) {
		int bound;
		
		if (board.inCombat())
			bound = COMBAT_BOUND;
		else
			bound = NORMAL_BOUND;
		
		int vrows = Math.min(visRows, region.rows());
		int vcols = Math.min(visCols, region.cols());
		
		
		int rNew = rTop;
		int cNew = cLeft;
		
		if (cell.row < rTop + bound) {
			rNew = Math.max(cell.row - bound, 0);
		} else if (cell.row >= rTop + vrows - bound) {
			rNew = Math.min(cell.row - vrows + bound, region.rows() - vrows);
		}

		if (cell.col < cLeft + bound) {
			cNew = Math.max(cell.col - bound, 0);
		} else if (cell.col >= cLeft + vcols - bound) {
			cNew = Math.min(cell.col - vcols + bound, region.cols() - vcols);
		}
		
		rTop = rNew;
		cLeft = cNew;
		
		assert rTop >= 0 && rTop + vrows <= region.rows();
		assert cLeft >= 0 && cLeft + vcols <= region.cols();
	}
	
	
	/** Draw the feature if it's visible.  If it's an ItemStack, draw
	 * all items in the stack, one on top of another. */
	private void drawEntity(Graphics2D g2d, Entity e) {
		if (!e.isVisible())
			return;
		
		int x = c2x(e.place().col);
		int y = r2y(e.place().row);
		
		if (e instanceof ItemStack) {
			ItemStack stack = (ItemStack)e;
			for (Item item : stack.getItems()) {
				g2d.drawImage(item.getImage(), x, y, this);
			}
		} else {
			g2d.drawImage(e.image(), x, y, this);
		}
	}
	
	/** Draw the mobile if it's visible.  In COMBAT mode, draw the 
	 * life bar.
	 * 
	 * @param g2d The graphics context
	 * @param m The mobile to draw
	 * @param barColor The color of the life bar
	 */
	private void drawMobile(Graphics2D g2d, Mobile m, Color barColor) {
		// FIRST, skip invisible entities.
		if (!m.isVisible())
			return;
			
		// NEXT, draw the entity's image.
		int left = c2x(m.place().col);
		int top = r2y(m.place().row);
		g2d.drawImage(m.image(), left, top, this);
		
		// NEXT, if it's a monster and it's asleep, draw Zzz's
		if (m instanceof Monster) {
			Monster mon = (Monster)m;
			
			if (mon.getPosture() == Posture.SLEEPING) {
				g2d.drawImage(Effects.ZZZ.image(), left, top, this);
			}
		}
		
		top += StandardTile.SIZE + 1;
		
		// NEXT, in combat mode draw the life bar.
		if (board.inCombat()) {
			int hp = 1;
			int hpMax = 1;
			
			if (m instanceof Combatant) {
				Combatant c = (Combatant)m;
				hp = c.HP();
				hpMax = c.maxHP();
			}
			
			int lbLeft = left + 2;
			int lbWidth = StandardTile.SIZE - 4;
			int lifeWidth = (int)Math.ceil(lbWidth * ((float)hp/hpMax));

			g2d.setColor(Color.DARK_GRAY);
			g2d.fillRect(lbLeft, top, lbWidth, 2);
			
			g2d.setColor(barColor);
			g2d.fillRect(lbLeft, top, lifeWidth, 2);
			
			// Skip life bar
			top += 3;
		}
		
		// NEXT, if it's a PC waiting for input, draw a box around it.
		if (m.equals(board.getMover()) && board.isWaiting()) {
			int bLeft = c2x(m.place().col) - 3;
			int bTop = r2y(m.place().row) - 3;
			int bWid = StandardTile.SIZE + 6;
			int bHt = StandardTile.SIZE + 6;
			
			if (board.inCombat()) {
				// Allow for life bar
				bHt += 3;
			}
			
			g2d.setColor(Color.GREEN);
			g2d.drawRect(bLeft, bTop, bWid, bHt);
		}
	}
	
	/** Set the tool tip text to the entity on the map. */
	@Override
	public String getToolTipText(MouseEvent e) {
		Cell cell = xy2cell(e.getX(), e.getY());
		
		if (cell != null)
			return board.getCellToolTipText(cell);
		else
			return "";
	}
	
	/** Converts x,y mouse coordinates to a map cell.
	 * 
	 * @param x The mouse x coordinate
	 * @param y The mouse y coordinate
	 * @return The cell, or null if there's no matching cell.
	 */
	Cell xy2cell(int x, int y) {
      	int col = cLeft + x / StandardTile.SIZE;
      	int row = rTop + y / StandardTile.SIZE;
      	
      	if (col < region.cols() && row < region.rows())
      		return new Cell(row, col);
      	else 
      		return null;
	}
	
	/** Convert a row coordinate to a y pixel coordinate.
	 * 
	 * @param r The row
	 * @return The y pixel coordinate.
	 */
	private int r2y(int r) {
		return (r - rTop)*StandardTile.SIZE;
	}

	/** Convert a column coordinate to an x pixel coordinate.
	 * 
	 * @param c The column
	 * @return The x pixel coordinate.
	 */
	private int c2x(int c) {
		return (c - cLeft)*StandardTile.SIZE;
	}
}