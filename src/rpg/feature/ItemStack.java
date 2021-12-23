/**
 * 
 */
package rpg.feature;

import graphics.Sprite;

import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import rpg.Feature;
import rpg.Item;
import rpg.pc.PlayerCharacter;
import util.Script;
import util.ScriptAction;
import app.Board;
import app.ItemBox;

/** An item stack is a stack of items on the floor.  When all items are
 * removed, it goes away.  It shows the first item in the stack.
 * 
 * @author will
 *
 */
public class ItemStack extends Feature {
	private static final long serialVersionUID = 1L;

	// Constants
	/** Maximum number of items in a stack. */
	public static final int MAX_ITEMS = 32;
	
	// Instance Variables
	private List<Item> inventory;
	
	// Transient Items, while handling the popup.
	
	/** The PC who poked the stack and is getting
	 * the contents.
	 */
	private PlayerCharacter opener;

	/** Creates a new, empty stack.
	 */
	public ItemStack() {
		super();
		
		inventory = new ArrayList<>(MAX_ITEMS);
		
		for (int i = 0; i < MAX_ITEMS; i++) {
			inventory.add(null);
		}
	}
	
	@Override public String name()   { return "Stack"; }

	@Override public Sprite sprite() {  
		// Display the sprite of the first item in the stack
		for (int i = 0; i < inventory.size(); i++) {
			Item item = inventory.get(i);
			
			if (item != null) {
				return item.sprite();
			}
		}
		
		return null;
	}

	
	//-------------------------------------------------------------------------
	// Queries

	/** @return true if the feature is poke-triggered, and false otherwise. */
	@Override
	public boolean isPokeTriggered() {
		// You can poke a stack to open and close it.
		return true;
	}

	/** You can walk over stacks. */
	@Override
	public boolean isWalkable() {
		return true;
	}

	/** You can fly over stacks. */
	@Override
	public boolean isFlyable() {
		return true;
	}
	
	
	//------------------------------------------------------------------------
	// Behavior
	
	/** Poke the stack to access the stuff in it.
	 */
	@Override
	public void poke(Script script, PlayerCharacter m) {
		script.add(new LootStack(m));
	}
	
	
	//-------------------------------------------------------------------------
	// ScriptActions
		
	/** Allows a PC to loot the stack. */
	protected class LootStack implements ScriptAction {
		PlayerCharacter looter;
		
		/** Creates a new action.
		 * 
		 * @param looter The mobile who will loot the stack.
		 */
		public LootStack(PlayerCharacter looter) {
			this.looter = looter;
		}
		
		public void execute() {
			// FIRST, let the PC pick stuff from the stack.
			pickItems(looter);
			
			// NEXT, if the stack is empty make the stack go away.
			if (itemCount() == 0) {
				println("Removing " + ItemStack.this);
				region().removeFeature(ItemStack.this);
			}
		}
	}

	/** @return the number of items in the stack. */
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
									  pc + " picks through the stuff on the ground.", 
									  JOptionPane.PLAIN_MESSAGE, 
									  pc.bigIcon());
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
	    			opener.log("Picked up the " + item + ".");
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
		List<Item> result = new ArrayList<>();
		for (Item item : inventory) {
			if (item != null) {
				result.add(item);
			}
		}
		return result;
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
