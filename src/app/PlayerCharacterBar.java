/**
 * 
 */
package app;

import graphics.Buttons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import rpg.pc.PlayerCharacter;

/** This widget displays a player character's icon, status gauge, and
 * individual stats in a ribbon across a larger panel.
 * TBD: The stats panel is too tall relative to the icon and gauge.
 * Make the gauge bigger?  Do something about it.
 * @author will
 */
public class PlayerCharacterBar extends JPanel {
	private static final long serialVersionUID = 1L;

	// Components
	private JLabel nameLabel;
	private JLabel iconLabel;
	private StatsGauge gauge;
	private StatsPanel statsPanel;

	// Constructor
	
	/** Builds the panel. 
	 * NNS
	 * IGS
	 * 
	 * N: Name
	 * S: Stats Panel
	 * I: Icon
	 * G: Gauge
	 */
	PlayerCharacterBar() {
		super(new GridBagLayout());
		setBackground(App.BACKGROUND);
		
		// NEXT, create components.
		GridBagConstraints gbc;
		
		// PC Name
		nameLabel = new JLabel("tbd");
		// nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
		nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		
		add(nameLabel, gbc);

		// PC Icon
		iconLabel = new JLabel(Buttons.UNKNOWN.icon());
		// iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		add(iconLabel, gbc);
		
		// PC Stats Gauge
		gauge = new StatsGauge();

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		add(gauge,gbc);

		// Stats Panel
		statsPanel = new StatsPanel();

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 2;
		add(statsPanel,gbc);
	}

	
	// Setters/Getters
		
	/** Refresh the display for the party leader.. */
	void refresh() {
		PlayerCharacter pc = Board.get().party.leader();
		
		nameLabel.setText(pc.name() + ": " + 
				  "Level " + pc.stats.level() + " " +
				  pc.stats.myclass());

		iconLabel.setIcon(new ImageIcon(pc.image()));
		gauge.refresh();
		statsPanel.refresh();

		// NEXT, repainting is necessary (I think); revalidation might not be.
		revalidate();  // TBD: Needed?
	}
	
	
	//-------------------------------------------------------------------------
	// StatsGauge

	/** A stats gauge shows the PC's remaining hit points and mana,
	 * and also how much experience the PC has toward his next level.
	 * @author will
	 *
	 */
	private final static class StatsGauge extends JPanel {
		private static final long serialVersionUID = 1L;

		// GUI Layout Constants	
		private final static int GAUGE_WIDTH = 200;
		private final static int GAUGE_BORDER = 2;
		private final static int MARGIN = 2;
		private final static int INSET = 8;
		
		// Instance Variables
		private PlayerCharacter pc = null;
		private int textHeight = 0;
		private int textWidth = 0;
		private int textAscent = 0;
		private int height;
		private int width;
		
		// Constructor
		
		/** Creates a status box for a  player character. */
		StatsGauge() {
			super();
			setBackground(InventoryPanel.BACKGROUND);
		}
		
		// Mutators
		
		/** Refreshes the data in the panel. */
		void refresh() {
			this.pc = Board.get().party.leader();
			repaint();
		}
		
		// Painting
		
		/** Map-painting code. */
		public void paintComponent(Graphics g) {
			// FIRST, prepare to paint
			super.paintComponent(g);
			
			// NEXT, get the font and font metrics, if we haven't already.
			if (textHeight == 0) {
				// FIRST, determine the height and width of a string.
				FontMetrics metrics = g.getFontMetrics(App.DATA_FONT);
				textHeight = metrics.getHeight();
				textWidth  = metrics.stringWidth("0000/0000 MMMM");
				textAscent = metrics.getAscent();
				
				// NEXT, determine the height and width of the widget.
				width = 2*MARGIN + GAUGE_WIDTH + 2*INSET + textWidth;
				height = 4*MARGIN + 3*textHeight + 2*GAUGE_BORDER;
				
				setPreferredSize(new Dimension(width, height));
				setMaximumSize(getPreferredSize());
				revalidate();
			}
			
			// NEXT, if we've no PC we're done.
			if (pc == null) {
				return;
			}
			
			// NEXT, draw the data
			int top = MARGIN;
			int left = MARGIN + INSET;
			
			// First, the border
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(left, top, GAUGE_WIDTH + 2*GAUGE_BORDER, 
						3*textHeight + 2*MARGIN + 2*GAUGE_BORDER);
			
			// Next, the HP bar
			top += GAUGE_BORDER;
			left += GAUGE_BORDER;
			
			drawBar(g, left, top, Color.GREEN, pc.HP(), pc.maxHP(), "HP");
					
			// Next, the mana bar
			top += textHeight + MARGIN;
			drawBar(g, left, top, Color.RED, pc.mana(), pc.stats.manaMax(), "Mana");

			// Next, the exp bar
			top += textHeight + MARGIN;
			
			int num = pc.stats.exp() - pc.stats.expBase(); 
			int den = pc.stats.expNext() - pc.stats.expBase();
			
			drawBar(g, left, top, Color.CYAN, num, den, "Exp");
		}
		
		/** Draw the bar at the specified place.
		 * 
		 * @param g     Graphics context
		 * @param left  x coordinate of upper left corner
		 * @param top	y coordinate of upper left corner
		 * @param color Color
		 * @param num   Numerator of fraction to draw
		 * @param den   Denominator of fraction to draw
		 * @param label Label text
		 */
		private void drawBar(Graphics g, int left, int top, Color color, int num, int den, String label) {
			// FIRST, draw the under-bar, for contrast
			g.setColor(Color.DARK_GRAY);
			g.fillRect(left, top, GAUGE_WIDTH, textHeight);
			
			// NEXT, draw the bar itself
			int lbWidth = (int)Math.ceil(GAUGE_WIDTH * ((float)num/den));
			g.setColor(color);
			g.fillRect(left, top, lbWidth, textHeight);

			// Next, draw the text.
			int ttop = top + textAscent;
			int tleft = left + GAUGE_WIDTH + INSET;
			String text = String.format("%d/%d %s", num, den, label);
			g.setFont(App.DATA_FONT);
			g.setColor(Color.BLACK);
			g.drawString(text, tleft, ttop);
		}
	}

	//------------------------------------------------------------------------
	// StatsPanel
	
	/** A stats panel shows the PC's stats
	 * @author will
	 */
	private final static class StatsPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		// Instance Variables
		private JPanel grid;
		private JLabel str;
		private JLabel dex;
		private JLabel wis;
		private JLabel con;
		private JLabel ac;
		private JLabel spd;
		private JLabel mp;
		private JLabel ap;
		
		// Constructor
		
		/** Creates a stats panel for a  player character. */
		public StatsPanel() {
			// FIRST, set up the panel
			super();
			setLayout(new GridBagLayout());
			setBackground(InventoryPanel.BACKGROUND);
			
			
			// NEXT, add the first data grid.
			grid = new JPanel(new GridLayout(0,2));
			grid.setOpaque(false);
			
			str = addVar("STR:");
			dex = addVar("DEX:");
			wis = addVar("WIS:");
			con = addVar("CON:");

			addGrid(grid, 1);
			
			// NEXT, add the second data grid.
			grid = new JPanel(new GridLayout(0,2));
			grid.setOpaque(false);
			
			ac = addVar("AC:");
			spd = addVar("Speed:");
			mp  = addVar("Movement:");
			ap  = addVar("Attacks:");
			
			addGrid(grid, 2);
						
			// NEXT, add a spacer.
			JLabel spacer = new JLabel(" ");
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 4;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.ipadx = 0;
			gbc.ipady = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weightx = 1;
			
			add(spacer, gbc);
		}
		
		private void addGrid(JPanel grid, int col) {
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = col;
			gbc.gridy = 0;
			gbc.gridwidth = 1;
			gbc.gridheight = 1;
			gbc.insets = new Insets(0, 4, 4, 4);
	 		gbc.anchor = GridBagConstraints.LINE_START;
			add(grid, gbc);
		}

		/** Add a new label/variable pair to the grid.
		 * 
		 * @param text  The label text
		 * @return The new variable JLabel
		 */
		private JLabel addVar(String text) {
			grid.add(new JLabel(text));

			JLabel var = new JLabel("     ");
			grid.add(var);
			return var;
		}
		
		// Mutators
				
		/** Refreshes the display for the current PC. */
		void refresh() {
			PlayerCharacter pc = Board.get().party.leader();
			
			str.setText(String.format(" %2d", pc.stats.str()));
			dex.setText(String.format(" %2d", pc.stats.dex()));
			wis.setText(String.format(" %2d", pc.stats.wis()));
			con.setText(String.format(" %2d", pc.stats.con()));
			
			ac.setText(String.format(" %2d", pc.stats.ac()));
			spd.setText(String.format(" %4.1f", pc.stats.spd()));
			mp.setText(String.format(" %2d", pc.stats.mp()));
			ap.setText(String.format(" %2d", pc.stats.ap()));
		}
		
	}

}
