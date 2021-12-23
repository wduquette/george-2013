/**
 * 
 */
package app;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import rpg.Avoid;
import rpg.Item;
import rpg.Shopkeeper;
import rpg.Slot;
import rpg.pc.PlayerCharacter;

/** A widget for displaying a PC's equipment and inventory, and allowing
 * him to change them.<p>
 * 
 * Supported actions:<p>
 * 
 * <li> select: selectedItem() selected
 * <li> change: equipment/inventory change
 * <li> sell: chosenItem should be sold
 * 
 * @author will
 */
@SuppressWarnings("serial") // Won't be serializing the actions.
final class InventoryBar extends JPanel implements ActionListener {	
	// Constants
	
	/** If player A is within GIFT_RANGE of player B, B can give
	 * him items.
	 */
	private static final int GIFT_RANGE = 4;
	
	// Instance Variables
	
	/** The leader of the party; set on refresh. */
	private PlayerCharacter pc = null;
	
	/** The shopkeeper with whom the party is dealing, in a Shop. */
	private Shopkeeper keeper = null;
	
	// Components
	private Board board;
	
	private MouseListener equipListener;
	private JPanel equipPanel;
	private Map<Slot,ItemBox> equip;
	
	private MouseListener invListener;
	private JPanel invPanel;
	private List<ItemBox> inv;
		
	// Transient Variables, used with popup menus.
	private Item chosenItem;	// The item to use, equip, etc.
	
	private List<ActionListener> listeners = new ArrayList<>();
	
	// Constructor
	
	/** Builds the inventory bar.  The layout is as shown:
	 * 
	 * <code><pre>
	 * EI
	 * </pre></code>
	 * 
	 * where
	 * E is the equipment area
	 * I is the inventory area.
	 * 
	 * @param board The game board
	 */
	
	InventoryBar(Board board) {
		super(new GridBagLayout());
		setBackground(App.BACKGROUND);
		
		this.board = board;
		
		// NEXT, create the components
		GridBagConstraints gbc;

		// Equipment
		equipListener = new EquipMouseListener();
		equip = new HashMap<>();
		equipPanel = new JPanel(new GridLayout(0,2));
		equipPanel.setBackground(App.BACKGROUND);
		equipPanel.setBorder(BorderFactory.createTitledBorder("Equipment"));
		
		addEquipSlot(Slot.HAND, "Weapon Slot");
		addEquipSlot(Slot.RANGED, "Ranged Weapon Slot");
		addEquipSlot(Slot.SHIELD, "Shield Slot");
		addEquipSlot(Slot.HELMET, "Helmet Slot");
		addEquipSlot(Slot.ARMOR, "Armor Slot");
		addEquipSlot(Slot.FOOTWEAR, "Footgear Slot");
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
 		gbc.anchor = GridBagConstraints.LINE_START;
		add(equipPanel, gbc);
		
		// Inventory
		invListener = new InvMouseListener();
		inv = new ArrayList<>();
		invPanel = new JPanel(new GridLayout(0, 12));
		invPanel.setBackground(App.BACKGROUND);
		invPanel.setBorder(BorderFactory.createTitledBorder("Inventory"));
		
		for (int i = 0; i < PlayerCharacter.Inventory.MAX_ITEMS; i++) {
			addInvSlot(i);
		}
		
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
 		gbc.anchor = GridBagConstraints.LINE_START;
 		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(invPanel, gbc);
	}
	
	private void addEquipSlot(Slot slot, String name) {
		ItemBox box = new ItemBox(slot.getSprite(), name);
		equip.put(slot, box);
		equipPanel.add(box);
		box.addMouseListener(equipListener);
	}

	private void addInvSlot(int i) {
		ItemBox box = new ItemBox();
		inv.add(box);
		invPanel.add(box);
		box.addMouseListener(invListener);
	}

	// Setters/Getters
	
	public void setShopkeeper(Shopkeeper keeper) {
		this.keeper = keeper;
	}
	
	/** Refresh the display for the party leader. */
	public void refresh() {
		// FIRST, get the current party leader.
		pc = board.party.leader();
		
		// NEXT, get his equipment.
		for (Slot slot : equip.keySet())
			equip.get(slot).setItem(pc.equipment.get(slot));
		
		// NEXT, get his items.
		List<Item> items = pc.inventory.toList();
		
		for (int i = 0; i < PlayerCharacter.Inventory.MAX_ITEMS; i++) {
			inv.get(i).setItem(items.get(i));
		}
		
		// NEXT, repainting is necessary (I think); revalidation might not be.
		repaint();  // TBD: revalidate Needed?
	}
	
	/** @return the chosen item. */
	public Item selectedItem() {
		return chosenItem;
	}
	
	//-------------------------------------------------------------------------
	// Action Listener Support
	
	/** Adds an action listener to this widget.
	 * 
	 * @param listener The listener
	 */
	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}
	
	/** Calls the actionListeners with the given action command. 
	 * 
	 * @param action The action command.
	 */
	private void callAction(String action) {
		ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_FIRST, action);
		
		for (ActionListener listener : listeners) {
			listener.actionPerformed(e);
		}
	}
	
	//-------------------------------------------------------------------------
	// Button Handling
	
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "drop":
			dropItem(chosenItem);
			break;
		case "equip":
			equipItem(chosenItem);
			break;
			
		case "give0":
			giveItemToPC(0, chosenItem);
			break;
			
		case "give1":
			giveItemToPC(1, chosenItem);
			break;
			
		case "give2":
			giveItemToPC(2, chosenItem);
			break;
			
		case "give3":
			giveItemToPC(3, chosenItem);
			break;
			
		case "sell":
			callAction("sell");
			break;
			
		case "unequip":
			unequipItem(chosenItem);
			break;
			
		case "use":  
			useItem(chosenItem);
			break;

		default:
			board.println("Error, unknown action on InventoryBar: " + 
					e.getActionCommand());
			break;
		}
		
		// Ask the board to repaint itself; some actions will change things.
		callAction("change");
		refresh();
		board.repaint();
	}
	
	/** The PC tries to drop the chosen item. 
	 * 
	 * @param item the item.
	 */
	private void dropItem(Item item) {
		if (pc.region().dropItem(pc.place(), chosenItem)) {
			pc.log("Dropped the " + chosenItem);
			pc.inventory.remove(chosenItem);
		} else {
			pc.log("Could not drop the " + chosenItem + "; " +
				   "there's too much stuff lying here as it is.");
		}
	}
	
	/** The PC tries to equip the chosen item.
	 * 
	 * @param item the item.
	 */
	private void equipItem(Item item) {
		// The player wants to equip an item.
		if (pc.equipment.equip(chosenItem)) {
			pc.log("Equipped the " + chosenItem);
		} else {
			pc.log("Could not equip the " + chosenItem + ".");
		}
	}
	
	/** The PC tries to unequip the chosen item.
	 * 
	 * @param item the item.
	 */
	private void unequipItem(Item item) {
		if (pc.equipment.unequip(chosenItem)) {
			pc.log("Returned the " + chosenItem + " to the inventory.");
		} else {
			pc.log("Could not take off the " + chosenItem);
		}
	}
	
	/** The PC tries to use the chosen item.
	 * 
	 * @param item the item.
	 */
	private void useItem(Item item) {
		// We let the item handle all logging. And if the item needs to be
		// discarded, the item handles that as well.
		chosenItem.use(pc);
	}
	
	/** Give the item to the player character with the given index.
	 * This is used to handle the "Give To <PC>" menu items.
	 * @param idx The index of the player in the party.
	 * @param item The item to give.
	 */
	private void giveItemToPC(int idx, Item item) {
		PlayerCharacter target = board.party.members().get(idx);
		
		// FIRST, is the target close enough?
		if (pc.travelDistance(pc.place(), target.place(), Avoid.HAZARDS) > GIFT_RANGE) {
			pc.log(target + " is too far away!");
			return;
		}
		
		if (target.inventory.add(item)) {
			pc.inventory.remove(item);
			pc.log("Gave the " + item + " to " + target);
		} else {
			pc.log(target + " has no room for the " + item + ".");
		}
		
	}
		
	//-------------------------------------------------------------------------
	// Inner Classes

	/** This class handles the popup menu on inventory slots.
	 * @author will
	 */
	private class InvMouseListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
	    	popupMenu(e);
	    }
	    
	    private void popupMenu(MouseEvent e) {
	    	ItemBox box = (ItemBox)e.getComponent();
	    	chosenItem = box.getItem();
	    	
	    	// FIRST, allow the client to know that the item selection has 
	    	// changed.
	    	callAction("select");
	    	
	    	// NEXT, if there's no item in the box do nothing.
	    	if (chosenItem == null) {
	    		return;
	    	}
	    	
	    	// NEXT, create the menu
	    	JPopupMenu menu = new JPopupMenu();
			JMenuItem menuItem;
			
			if (keeper != null) {
				// Sell
				menuItem = new JMenuItem("Sell");
				menuItem.setActionCommand("sell");
				menuItem.addActionListener(InventoryBar.this);
				menuItem.setEnabled(keeper.buyFor(chosenItem) > 0);
				menu.add(menuItem);
			}
			
			// Use
			menuItem = new JMenuItem("Use");
			menuItem.setActionCommand("use");
			menuItem.addActionListener(InventoryBar.this);
			menuItem.setEnabled(chosenItem.canBeUsedBy(pc));
			menu.add(menuItem);
			
			// Equip
			menuItem = new JMenuItem("Equip");
			menuItem.setActionCommand("equip");
			menuItem.addActionListener(InventoryBar.this);
			menuItem.setEnabled(chosenItem.canBeEquippedBy(pc));
			menu.add(menuItem);
			
			// Drop
			menuItem = new JMenuItem("Drop");
			menuItem.setActionCommand("drop");
			menuItem.addActionListener(InventoryBar.this);
			menuItem.setEnabled(true);
			menu.add(menuItem);

			// NEXT, if there are multiple player characters add
			// the "give to" menu items.  The action commands
			// are "give0", "give1", etc., where the digit is
			// the index of the PC in the party.
			List<PlayerCharacter> party = board.party.members();

			if (party.size() > 1) {
				menu.addSeparator();
				
				int i = 0;
				for (PlayerCharacter c : party) {
					if (!c.equals(pc)) {
						// Give To
						menuItem = new JMenuItem("Give to " + c);
						menuItem.setActionCommand("give" + i);
						menuItem.addActionListener(InventoryBar.this);
						menu.add(menuItem);
					}
					
					i++;
				}
			}

			// NEXT, display it.
	    	menu.show(e.getComponent(), e.getX(), e.getY());
	    }
	}

	/** This class handles the popup menu on equipment slots.
	 * @author will
	 *
	 */
	private class EquipMouseListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
	    	popupMenu(e);
	    }
	    
	    private void popupMenu(MouseEvent e) {
	    	ItemBox box = (ItemBox)e.getComponent();
	    	chosenItem = box.getItem();
	    	
	    	// FIRST, allow the client to know that the item selection
	    	// has changed.
	    	callAction("select");
	    	
	    	// NEXT, if there's no item in the box do nothing.
	    	if (chosenItem == null) {
	    		return;
	    	}

	    	// NEXT, create the menu
	    	JPopupMenu menu = new JPopupMenu();
			JMenuItem menuItem;
			menuItem = new JMenuItem("Unequip");
			menuItem.setActionCommand("unequip");
			menuItem.addActionListener(InventoryBar.this);
			// TBD: There might be times (e.g., combat) where
			// a PC can't change his equipment.
			menuItem.setEnabled(true);
			menu.add(menuItem);

			// NEXT, display it.
	    	menu.show(e.getComponent(), e.getX(), e.getY());
	    }
	}
}