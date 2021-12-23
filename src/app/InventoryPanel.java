/**
 * 
 */
package app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import rpg.Item;
import rpg.Slot;
import rpg.pc.PlayerCharacter;

/** A widget for displaying player character inventory and stats.
 * 
 * @author will
 */
@SuppressWarnings("serial") // Won't be serializing the actions.
final class InventoryPanel extends JPanel implements ActionListener {
	// GUI Constants
	private static final int MARGIN = 10;
	
	/** The background color for the Inventory Panel. */
	static final Color BACKGROUND = new Color(0x8A4C0F);
	
	// Other Constants
	
	// Instance Variables
	private Board board;
	private PlayerCharacter pc = null;
	
	// Components
	private PlayerCharacterBar pcbar;
	private InventoryBar ibar;
	private JLabel descriptionLabel;
	private MoneyLabel moneyLabel;
	
	// Constructor
	
	/** Builds an inventory panel.  The layout is as shown:
	 * 
	 * <code><pre>
	 * PP
	 * II
	 * DD
	 * MB
	 * </pre></code>
	 * 
	 * where
	 * P is the PC bar
	 * I is the inventory bar
	 * D is the description area
	 * M is the money label
	 * B is the back button.
	 * 
	 * @param board The game board.
	 */
	
	InventoryPanel(Board board) {
		super(new GridBagLayout());
		setDoubleBuffered(true);
		setBackground(BACKGROUND);
		setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
		
		// FIRST, save the inputs.
		this.board = board;
		
		// NEXT, create the components
		GridBagConstraints gbc;

		// PC Bar
		pcbar = new PlayerCharacterBar();

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		add(pcbar, gbc);

		// Inventory Bar (equipment and inventory slots)
		ibar = new InventoryBar(board);
		ibar.addActionListener(this);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
 		gbc.anchor = GridBagConstraints.LINE_START;
		add(ibar, gbc);
		
		// Description
		descriptionLabel = new JLabel("<html></html>");
		descriptionLabel.setBackground(BACKGROUND);
		descriptionLabel.setBorder(BorderFactory.createTitledBorder("Description"));
		descriptionLabel.setVerticalAlignment(JLabel.TOP);
		descriptionLabel.setHorizontalAlignment(JLabel.LEFT);
		descriptionLabel.setPreferredSize(new Dimension(200,200));
		descriptionLabel.setMinimumSize(descriptionLabel.getPreferredSize());
	
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 3;
		gbc.gridheight = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.PAGE_START;

 		add(descriptionLabel, gbc);
 		
 		// Money Label
		moneyLabel = new MoneyLabel();
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.LINE_START;
		
		add(moneyLabel, gbc);
		
		// Back Button
		JButton back = new JButton("Back");
		back.setActionCommand("back");
		back.addActionListener(this);

		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.LAST_LINE_END;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.weightx = 1;

		add(back, gbc);
	}
	
	// Setters/Getters
	
	/** Displays the given text in the description box.
	 * 
	 * @param text The text to display
	 */
	private void setDescription(String text) {
		descriptionLabel.setText("<html>" + text + "</html>");
	}
	
	/** Refresh the display for the current party leader. */
	public void refresh() {
		// FIRST, get the current party leader.
		pc = board.party.leader();
		
		// FIRST, refresh the components
		pcbar.refresh();
		ibar.refresh();
		moneyLabel.refresh();
		
		// NEXT, repainting is necessary (I think); revalidation might not be.
		revalidate();  // TBD: Needed?
	}
	
	//-------------------------------------------------------------------------
	// Button Handling
	
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "back":
			board.setCard(Board.MAP);
			break;
			
		case "change":
			// Nothing to do except refresh, which happens below.
			break;
			
		case "select":  // User selected an item on the ibar
			setItemDescription(ibar.selectedItem());
			break;

		default:
			board.println("Error, unsupported action on InventoryPanel: " + 
					e.getActionCommand());
			break;
		}
		
		// Ask the board to repaint itself; some actions will change things.
		refresh();
		board.repaint();
	}
	
	
	private void setItemDescription(Item item) {
    	// FIRST, if there's no item, clear the text.
    	if (item == null) {
    		setDescription("");
    		return;
    	}
    	
    	// NEXT, get the initial description.
    	String text = "<b>" + item + ":</b>  " + item.getDescription();
    	
    	// NEXT, if it is equippable but not equipped it requires additional handling.
    	if (item.isEquippable() && !pc.equipment.isEquipped(item)) {
    		Slot slot = item.slot();
    		
    		if (!item.canBeEquippedBy(pc)) {
    			text = text + "  <b>Cannot be equipped by this character.</b>";
    		} else if (pc.equipment.get(slot) != null) {
    			text = text + "<br><br>\n" +
    					"<b>You have equipped:</b><br><br>\n" +
    					pc.equipment.get(slot).getDescription();
    		}
    	}
    	
    	setDescription(text);
	}
		
	//-------------------------------------------------------------------------
	// Inner Classes

	// TBD
}