/**
 * 
 */
package app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import map.Cell;
import map.TerrainTile;
import rpg.Feature;
import rpg.Mobile;
import rpg.Region;
import rpg.feature.Door;
import rpg.feature.Exit;
import rpg.pc.PlayerCharacter;

/** A widget for displaying the low-resolution overview map
 * 
 * @author will
 */
@SuppressWarnings("serial") // Won't be serializing the actions.
final class BigMapPanel extends JPanel implements ActionListener {
	// GUI Constants
	
	/** The background color for the panel. */
	static final Color BACKGROUND = Color.BLACK;
	static final Color PC = Color.GREEN;
	static final Color WALKABLE = Color.GRAY;
	static final Color WALL = Color.DARK_GRAY;
	static final Color DOOR = new Color(0x8A4C0F);

	// Other Constants
		
	// Instance Variables
	private Board board;
	private int height;
	private int width;
	private Region region = null;
		
	// Constructor
	
	/** Creates the big map panel
	 * 
	 * @param board The game board.
	 * @param width The width of the panel, in pixels.
	 * @param height The height of the panel, in pixels.
	 */
	
	BigMapPanel(Board board, int width, int height) {
		super(new GridBagLayout());
		
		// FIRST, save the inputs.
		this.board = board;
		this.height = height;
		this.width = width;
		
		setPreferredSize(new Dimension(width, height));
		
		setBackground(BACKGROUND);

		createBackButton();
	}
	

	/** Create the Back button. */
	private void createBackButton() {
		JButton back = new JButton("Back");
		back.setActionCommand("back");
		back.addActionListener(this);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LAST_LINE_END;
		gbc.weighty = 1;
		gbc.weightx = 1;

		add(back, gbc);
	}
	
	// Utilities
	
	/** Sets the big map to display a particular region. 
	 * 
	 * @param region The region.
	 */
	public void setRegion(Region region) {
		this.region = region;
		repaint();
	}
	
	//-------------------------------------------------------------------------
	// Event Handling
	
	
	/** Handles button presses. */
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "back":
			board.setCard(Board.MAP);
			break;

		default:
			board.println("Error, unknown action on BigMapPanel: " + e.getActionCommand());
			break;
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
		
		// NEXT, the black background
		g2d.setColor(BACKGROUND);
		g2d.fillRect(0, 0, width, height);
		
		// NEXT, determine the tile size: the biggest we can do so that
		// the entire map fits on the display.
		int size = Math.min(width / region.cols(), height / region.rows());
		
		board.println("BigMap tile size=" + size);
		
		// NEXT, draw the tiles, light gray for walkable, blue for water,
		// dark gray for not walkable.
		for (int r = 0; r < region.rows(); r++) {
			for (int c = 0; c < region.cols(); c++) {
				// FIRST, ignore cells that have not been seen.
				if (!region.hasBeenSeen(r,c))
					continue;
				
				// NEXT, pick the color.
				Color color;
				
				TerrainTile tile = region.terrainAt(new Cell(r,c));
				
				if (tile.isWalkable()) {
					color = WALKABLE;
				} else if (tile.isFlyable()) {
					// Assume it's water; we don't have any pits yet.
					color = Color.BLUE;
				} else {
					// It's neither walkable nor flyable; assume
					// it's impassable.
					color = WALL;
				}
				
				g2d.setColor(color);
				g2d.fillRect(c*size, r*size, size, size);
			}
		}

		// NEXT, draw (some of) the known features.
		// TBD: The region should determine what features are of interest
		// and how they are drawn.  We should probably do some tool tips 
		// as well.
		for (Feature f : region.getFeatures()) {
			if (!region.hasBeenSeen(f.place()))
				continue;
			
			Color color = null;
			
			if (f instanceof Exit) {
				color = Color.YELLOW;
			} else if (f instanceof Door) {
				Door d = (Door)f;
				
				if (!d.isOpen()) {
					color = DOOR;
				}
			} else if (f.isWalkable()) {
				color = WALKABLE;
			}
			
			if (color != null) {
				g2d.setColor(color);
				g2d.fillRect(f.place().col*size, f.place().row*size, size, size);
			}
		}
		
		// NEXT, draw the NPCs. 
		for (Mobile m : region.getNPCs()) {
			if (!region.hasBeenSeen(m.place()))
				continue;

			g2d.setColor(Color.CYAN);
			g2d.fillRect(m.place().col*size, m.place().row*size, size, size);
		}
		
		// NEXT, draw visible monsters.
		PlayerCharacter leader = board.party.leader();
		
		for (Mobile m : region.getMonsters()) {
			if (!region.hasBeenSeen(m.place()))
				continue;
			
			if (!leader.canSee(m))
				continue;
			
			g2d.setColor(Color.RED);
			g2d.fillRect(m.place().col*size, m.place().row*size, size, size);
		}

		// NEXT, draw the party members.
		for (PlayerCharacter pc : board.party.members()) {
			g2d.setColor(PC);
			g2d.fillRect(pc.place().col*size, pc.place().row*size, size, size);
		}
	}
}