/**
 * 
 */
package rpg.feature;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import rpg.Item;
import rpg.pc.PlayerCharacter;
import util.Script;
import util.ScriptAction;
import app.Board;
import app.ItemBox;

/** A PlainChest is a chest with an inventory, from
 * which items can be taken.
 * 
 * @author will
 *
 */
public class PlainChest extends Chest {
	private static final long serialVersionUID = 1L;

	// Constants
	/** Maximum number of items in a chest. */
	public static final int MAX_ITEMS = 32;
	
	// Instance Variables
	private List<Item> inventory;
	
	// Transient Items, while handling the popup.
	
	/** The PC who opened the chest and is getting
	 * the contents.
	 */
	private PlayerCharacter opener;

	/** Creates a new, empty plain chest.
	 */
	public PlainChest() {
		super();
		
		inventory = new ArrayList<>(MAX_ITEMS);
		
		for (int i = 0; i < MAX_ITEMS; i++) {
			inventory.add(null);
		}
	}
	
	/** Poke the chest to open and close it.  On open,
	 * if there's anything in it pop up a pane allowing
	 * the player to take stuff from it.
	 */
	@Override
	public final void poke(Script script, PlayerCharacter m) {
		// FIRST, toggle its state
		super.poke(script, m);
		
		// NEXT, if it will be open, let them loot it.
		if (!isOpen()) {
			script.add(new LootChest(m));
		}
	}
	
	
	//-------------------------------------------------------------------------
	// ScriptActions
		
	/** Allows a mobile to loot the chest. */
	protected class LootChest implements ScriptAction {
		PlayerCharacter looter;
		
		/** Creates a new action.
		 * 
		 * @param looter The mobile who will loot the chest.
		 */
		public LootChest(PlayerCharacter looter) {
			this.looter = looter;
		}
		
		public void execute() {
			// NEXT, is there anything in it?
			int count = itemCount();
			
			if (count == 0) {
				looter.log("The chest is empty.");
				return;
			}
			
			// NEXT, let the mobile pick stuff from the chest.
			// TBD: If NPCs can over poke chests, we'll need to revisit this.
			pickItems(looter);
		}
	}

	/** @return the number of items in the chest. */
	private int itemCount() {
		int count = 0;
		
		for (Item item : inventory) {
			if (item != null)
				count++;
		}
		
		return count;
	}
	
	/** Allow the user to pick items from the chest.
	 * 
	 * @param m  The mobile who opened the chest.
	 */
	private void pickItems(PlayerCharacter pc) {
		// FIRST, create a JPanel of item boxes.
		JPanel panel = new JPanel(new GridLayout(0,8));
		MouseListener listener = new ItemListener();
		
		for (int i = 0; i < MAX_ITEMS; i++) {
			ItemBox box = new ItemBox();
			box.setItem(inventory.get(i));
			panel.add(box);
			box.addMouseListener(listener);
		}
		
		// NEXT, pop up the option pane.
		opener = pc;
		JOptionPane.showMessageDialog(Board.get(), 
									  panel, 
									  pc + " rummages through the stuff in the chest", 
									  JOptionPane.PLAIN_MESSAGE, 
									  bigIcon());
	}
	
	//-------------------------------------------------------------------------
	// Inner Classes

	/** This class handles the popup menu on inventory slots.
	 * @author will
	 *
	 */
	private class ItemListener extends MouseAdapter {
	    public void mousePressed(MouseEvent e) {
	    	ItemBox box = (ItemBox)e.getComponent();
	    	Item item = box.getItem();
	    	
	    	if (item != null) {
	    		if (opener.inventory.add(item)) {
	    			removeItem(item);
	    			box.setItem(null);
	    			opener.log("Got the " + item + " from the chest.");
	    		} else {
	    			opener.log("Can't take the " + item + "; my inventory is full.");
	    		}
	    	}
	    }
	}
	
	//-------------------------------------------------------------------------
	// Inventory API

	/** @return an unmodifiable list of the items in the inventory. */
	public List<Item> getItems() {
		return Collections.unmodifiableList(inventory);
	}

	/** Add an item to the inventory.  Returns false
	 * if the inventory is full.
	 * 
	 * @param item The item to be added.
	 * @return true on success, and false otherwise.
	 */
	public boolean putItem(Item item) {
		// FIRST, find an empty slot.
		int idx = inventory.indexOf(null);
		
		if (idx == -1) {
			// Inventory is full
			return false;
		}
		
		// NEXT, put the item in the empty slot.
		inventory.set(idx, item);
		return true;
	}
	

	/** Remove an item from the inventory, if it is
	 * present.
	 * @param item  The item in question.
	 */
	public void removeItem(Item item) {
		int idx = inventory.indexOf(item);
		
		if (idx != -1)
			inventory.set(idx, null);
	}
	
	/** @return whether the inventory is filled to 
	 * capacity or not.
	 */
	public boolean isFull() {
		// It is full if there are no null slots.
		return inventory.indexOf(null) == -1;
	} 
	

}
