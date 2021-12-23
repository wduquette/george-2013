/**
 * 
 */
package rpg.item;

import graphics.Items;
import graphics.Sprite;
import rpg.Item;

/** This is a skull, relic of some poor lost adventurer.
 * @author will
 *
 */
public class Skull extends Item {
	private static final long serialVersionUID = 1L;

	// Constructor
	
	/** Creates a new item */
	public Skull() {
		super();
	}
		
	@Override public String name()   { return "Skull"; }
	@Override public Sprite sprite() { return Items.SKULL; }
	
	/** @return the description */
	public String getDescription() {
		return " the relic of some poor lost adventurer.";
	}	
}
