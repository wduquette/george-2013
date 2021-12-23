/**
 * 
 */
package rpg.item;

import graphics.Items;
import graphics.Sprite;
import rpg.Item;
import rpg.pc.PlayerCharacter;

/**
 * @author will
 *
 */
public class Spittoon extends Item {
	private static final long serialVersionUID = 1L;

	// Constructor
	
	@Override public String  name()     { return "Spittoon"; }
	@Override public Sprite  sprite()   { return Items.SPITTOON; }
	@Override public int     value()    { return 2; }
	@Override public boolean isUsable() { return true; }
	
	/** Does nothing useful.
	 * @param pc The PC.
	 */
	public void use(PlayerCharacter pc) {
		pc.log("spits in the spittoon.");
	}
	
	/** @return the description */
	public String getDescription() {
		return " a spittoon.  It might be empty, but then again, it might " +
			   " not.  Perhaps it's best to leave the question open.";
	}	
}
