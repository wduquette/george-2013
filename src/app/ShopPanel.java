/**
 * 
 */
package app;

import graphics.Buttons;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import rpg.Item;
import rpg.Shopkeeper;
import rpg.Slot;
import rpg.pc.PlayerCharacter;

/** A widget for interacting with a shopkeeper.
 * 
 * @author will
 */
@SuppressWarnings("serial") // Won't be serializing the actions.
final class ShopPanel extends JPanel implements ActionListener {
	// Constants
	private static final int MARGIN = 10;
	
	/** The background color for the Panel. 
	 * TBD: This should be defined as a constant by Board. */
	static final Color BACKGROUND = new Color(0x8A4C0F);
	
	// Other Constants
	
	// Instance Variables
	private Board board;
	private Shopkeeper keeper = null;
	
	// Components
	private JLabel shopNameLabel;
	private JLabel shopIconLabel;
	private JLabel dialogLabel;
	
	private MouseListener stockListener;
	private JPanel stockPanel;
	private List<ItemBox> stock;
	
	private JLabel descriptionLabel;
	private PlayerCharacterBar pcbar;
	private InventoryBar ibar;
	private MoneyLabel moneyLabel;
	
	// Transient Items
	
	/** Item selected in the stockPanel */
	private Item chosenItem = null;
	
	
	// Constructor
	
	/** Builds an inventory panel.  The layout is as shown:
	 * 
	 * <code><pre>
	 * 0: SN.SN.SN.SN
	 * 1: SI.SD.SD.SD
	 * 2: ST.ST.ST.ST
	 * 3: ID.ID.ID.ID
	 * 4: PB.PB.PB.PB
	 * 5: IB.IB.IB.IB
	 * 6: ML.ML.ML.BB
	 * </pre></code>
	 * 
	 * where
	 * SN is the shop name
	 * SI is the shopkeeper icon
	 * SD is the shopkeeper dialog
	 * ST is the shopkeeper stock
	 * ID is the item description
	 * PB is the player character bar
	 * IB is the PC inventory bar
	 * ML is the money label
	 * BB is the back button.
	 * 
	 * @param board The game board.
	 */
	
	ShopPanel(Board board) {
		super(new GridBagLayout());
		setDoubleBuffered(true);
		setBackground(BACKGROUND);
		setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
		
		// FIRST, save the inputs.
		this.board = board;
		
		// NEXT, create the components
		GridBagConstraints gbc;
		
		// R0: Shop Name
		shopNameLabel = new JLabel("tbd");
		shopNameLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
		shopNameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 4;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		
		add(shopNameLabel, gbc);
		
		// R1,C0: Shopkeeper's icon
		shopIconLabel = new JLabel(Buttons.UNKNOWN.icon());
		shopIconLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));

		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		
		add(shopIconLabel, gbc);
		
		// R1C1: Shopkeeper's dialog
		dialogLabel = new JLabel("<html></html>");
		dialogLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
		dialogLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
		dialogLabel.setVerticalAlignment(JLabel.TOP);
		dialogLabel.setHorizontalAlignment(JLabel.LEFT);
		dialogLabel.setPreferredSize(new Dimension(200,50));
		dialogLabel.setMinimumSize(dialogLabel.getPreferredSize());

		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 3;
		gbc.gridheight = 1;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		add(dialogLabel, gbc);
		
		// R2: Shopkeeper's stock
		stockListener = new StockListener();
		stock = new ArrayList<>();
		stockPanel = new JPanel(new GridLayout(0, 16));
		stockPanel.setBackground(App.BACKGROUND);
		stockPanel.setBorder(BorderFactory.createTitledBorder("Items for sale"));
		
		for (int i = 0; i < Shopkeeper.MAX_STOCK; i++) {
			addStockSlot(i);
		}
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.gridwidth = 4;
		gbc.gridheight = 1;
 		gbc.anchor = GridBagConstraints.LINE_START;
 		//gbc.fill = GridBagConstraints.HORIZONTAL;
		add(stockPanel, gbc);
		
		// R3: Item Description
		descriptionLabel = new JLabel("<html></html>");
		descriptionLabel.setBackground(BACKGROUND);
		descriptionLabel.setBorder(BorderFactory.createTitledBorder("Item Description"));
		descriptionLabel.setVerticalAlignment(JLabel.TOP);
		descriptionLabel.setHorizontalAlignment(JLabel.LEFT);
		descriptionLabel.setPreferredSize(new Dimension(200,200));
		descriptionLabel.setMinimumSize(descriptionLabel.getPreferredSize());
	
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.gridwidth = 4;
		gbc.gridheight = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.PAGE_START;

 		add(descriptionLabel, gbc);
		
		
		// R4: PC Bar
		pcbar = new PlayerCharacterBar();
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 4;
		gbc.anchor = GridBagConstraints.LINE_START;
		
		add(pcbar, gbc);

		// R5: PC Inventory Bar
		ibar = new InventoryBar(board);
		ibar.addActionListener(this);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 4;
		gbc.anchor = GridBagConstraints.LINE_START;
		
		add(ibar, gbc);
		
		
		// R6C0: Money Label
		moneyLabel = new MoneyLabel();
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.gridwidth=3;
		gbc.anchor = GridBagConstraints.LINE_START;
		
		add(moneyLabel, gbc);
		
		// R6C3: Back button
		JButton back = new JButton("Back");
		back.setActionCommand("back");
		back.addActionListener(this);

		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.gridy = 6;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.weightx = 1;

		add(back, gbc);
	}
	
	private void addStockSlot(int i) {
		ItemBox box = new ItemBox();
		stock.add(box);
		stockPanel.add(box);
		box.addMouseListener(stockListener);
	}
	
	// Setters/Getters
	
	
	/** Sets the shopkeeper to display. 
	 * 
	 * @param keeper the shopkeeper to display.
	 */
	void setShopkeeper(Shopkeeper keeper) {
		this.keeper = keeper;
		setDialog(keeper.getGreeting());

		refresh();
	}
	
	/** Refresh the display for the party leader and the given shopkeeper. */
	public void refresh() {
		// FIRST, identify the shopkeeper visually.
		shopNameLabel.setText(keeper.getShopName());
		shopIconLabel.setIcon(new ImageIcon(keeper.image()));
		pcbar.refresh();
		ibar.setShopkeeper(keeper);
		ibar.refresh();
		moneyLabel.refresh();
		
		for (int i = 0; i < keeper.getStock().size(); i++) {
			stock.get(i).setItem(keeper.getStock().get(i));
		}

		repaint();
	}
	
	/** Displays the given text in the keeper's dialog area.
	 * 
	 * @param text  The text to display.
	 */
	private void setDialog(String text) {
		dialogLabel.setText("<html>" + text + "</html>");
		keeper.log(text);
	}
	
	/** Displays the given text in the description box.
	 * 
	 * @param text The text to display
	 */
	private void setDescription(String text) {
		descriptionLabel.setText("<html>" + text + "</html>");
	}
	
	//-------------------------------------------------------------------------
	// Button Handling
	
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "back":
			board.setCard(Board.MAP);
			break;

		case "buy":   // User wants to buy the selected item.
			pcBuysItem(chosenItem);
			break;

		case "change":
			// Nothing to do except refresh, which happens below.
			break;
			
		case "select":  // User selected an item on the ibar
			setPCItemDescription(ibar.selectedItem());
			break;
			
		case "sell":   // User wants to sell the selected item.
			pcSellsItem(ibar.selectedItem());
			break;

		default:
			board.println("Error, unknown action on ShopPanel: " + 
					e.getActionCommand());
			break;
		}
		
		// Ask the panel and the board to repaint themselves; some actions will 
		// change things.
		refresh();
		board.repaint();
	}
	
	/** The PC buys the item from the shopkeeper.  It's assumed that the
	 * PC has the money.  The PC gains the item.
	 * @param item The item to sell.
	 */
	private void pcBuysItem(Item item) {
		PlayerCharacter pc = board.party.leader();
		
		if (pc.inventory.isFull()) {
			setDialog("You've got no room for that.");
			return;
		}
				
		int price = keeper.sellFor(item);
		pc.inventory.add(item);
		board.party.pay(price);
		pc.log("buys the " + item + " for " + price +
				" gold piece" + ((price == 1) ? "." : "s."));

		setDialog(keeper.getDialogOnSelling());
		
		// NOTE: The shopkeeper will replenish his stock on refresh().
	}
	
	/** The PC sells the item to the shopkeeper.  It's assumed that the
	 * shopkeeper is willing to buy it.  The PC loses the item, and the
	 * party gains the sale price.
	 * @param item The item to sell.
	 */
	private void pcSellsItem(Item item) {
		PlayerCharacter pc = board.party.leader();
		
		int price = keeper.buyFor(item);
		pc.inventory.remove(item);
		board.party.earn(price);
		keeper.log("buys the " + item + " for " + price +
				" gold piece" + ((price == 1) ? "." : "s."));

		setDialog(keeper.getDialogOnBuying());
	}
	
	private void setPCItemDescription(Item item) {
    	// FIRST, if there's no item, clear the text.
    	if (item == null) {
    		setDescription("");
    		return;
    	}
    	
    	// NEXT, get the initial description.
    	StringBuilder text = new StringBuilder();
    	text.append("<b>" + item + ":</b>  " + item.getDescription());
    	
    	// NEXT, if it is equippable but not equipped it requires additional handling.
    	PlayerCharacter pc = board.party.leader();
    	
    	if (item.isEquippable() && !pc.equipment.isEquipped(item)) {
    		Slot slot = item.slot();
    		
    		if (!item.canBeEquippedBy(pc)) {
    			text.append(" <b>Cannot be equipped by this character.</b>");
    		} else if (pc.equipment.get(slot) != null) {
    			text.append("<br><br>\n" +
    					"<b>You have equipped:</b><br><br>\n" +
    					pc.equipment.get(slot).getDescription());
    		}
    	}
    	
    	int price = keeper.buyFor(item);
    	
    	if (price > 0) {
    		text.append("<br><br>" + keeper.name());
    		text.append(" will buy this from you for " + price + " gold ");
    		text.append((price == 1) ? "piece." : "pieces.");
    	} else {
    		text.append("<br><br>" + keeper.name());
    		text.append(" has no interest in this item.");
    	}
    	
    	setDescription(text.toString());
	}
	
	private void displayStockDescription(Item item) {
    	// FIRST, if there's no item, clear the text.
    	if (item == null) {
    		setDescription("");
    		return;
    	}
    	
    	// NEXT, get the initial description.
    	StringBuilder text = new StringBuilder();
    	text.append("<b>" + item + ":</b>  " + item.getDescription());
    	
    	// NEXT, if it is equippable but not equipped it requires additional handling.
    	PlayerCharacter pc = board.party.leader();
    	
    	int price = keeper.sellFor(item);
    	
    	text.append("<br><br>" + keeper.name());
    	text.append(" will sell this to you for " + price + " gold ");
    	text.append((price == 1) ? "piece." : "pieces.");
    	
    	if (item.isEquippable() && !item.canBeEquippedBy(pc)) {
    		text.append("<br><br><b>Cannot be equipped by this character.</b>");
    	}
    	
    	setDescription(text.toString());
	}
	
	
	//-------------------------------------------------------------------------
	// Inner Classes
	
	/** This class handles the popup menu on stock slots.
	 * @author will
	 */
	private class StockListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
	    	popupMenu(e);
	    }
	    
	    private void popupMenu(MouseEvent e) {
	    	ItemBox box = (ItemBox)e.getComponent();
	    	chosenItem = box.getItem();
	    	
	    	// FIRST, display the item description
	    	displayStockDescription(chosenItem);
	    	
	    	// NEXT, if there's no item in the box do nothing.
	    	if (chosenItem == null) {
	    		return;
	    	}
	    	
	    	
	    	// NEXT, create the menu
	    	JPopupMenu menu = new JPopupMenu();
			JMenuItem menuItem;
			
			// Buy
			menuItem = new JMenuItem("Buy");
			menuItem.setActionCommand("buy");
			menuItem.addActionListener(ShopPanel.this);
			menuItem.setEnabled(keeper.sellFor(chosenItem) <= board.party.gold());
			menu.add(menuItem);

			// NEXT, display it.
	    	menu.show(e.getComponent(), e.getX(), e.getY());
	    }
	}

}