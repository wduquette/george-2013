/**
 * 
 */
package rpg;

import graphics.Sprite;

import java.awt.Image;
import java.io.Serializable;

import rpg.pc.PlayerCharacter;

import util.RandomPlus;

/** An item is something that can be contained in an Inventory.
 * It might be equippable, it might be usable, it might contribute
 * to defense if equipped, and it might do damage when used as
 * a weapon.
 * @author will
 *
 */
public abstract class Item implements Serializable {
	private static final long serialVersionUID = 1L;

	// Static Member Variables
	
	/** A random number generator for use by items. */
	protected static RandomPlus random = new RandomPlus();
	
	// Constructor
		
	//------------------------------------------------------------------------
	// Constant Attributes
	//
	// These attribute methods can be (and in some cases must be) overridden 
	// by subclasses as needed.

	/** Subclasses should override this to indicate the name to be shown
	 * to the user.
	 * @return the name.
	 */
	abstract public String name();
	
	
	/** Subclasses should override this to indicate the item's 
	 * sprite.
	 * @return the sprite
	 */
	abstract public Sprite sprite();

	/** Subclasses should override this to indicate the item's 
	 * equipment slot.  If NONE, the item cannot be equipped. 
	 * @return the slot.
	 */
	public Slot slot() { return Slot.NONE; }
	
	
	/** Returns the value of the item; this is used to determine prices
	 * in shops.  Items with value 0 cannot be sold.
	 * @return the value of the item.
	 */
	public int value() { return 0; }
	
	/** Subclasses should override this to indicate the range of the item,
	 * if that even makes sense.  Hand weapons should have a range of 1;
	 * ranged weapons greater than 1.
	 * @return the item's range.
	 */
	public int range() { return 0; }
	
	/** Subclasses should override this to indicate the item's contribution
	 * to armor class (AC) when equipped.
	 * @return the item's contribution to AC.
	 */
	public int ac() {return 0; }
	
	/** Is the item something that can be used?  
	 * This should be overridden by subclasses as needed.
	 * @return True if the item is usable, and false otherwise.
	 */
	public boolean isUsable() {	return false; }
	
	/** Is the item singular (i.e., a helmet) or plural (i.e., boots).
	 * @return true if the item is plural.
	 */
	public boolean plural() { return false; }
	
	
	//------------------------------------------------------------------------
	// Computed Attributes
	//
	// These attributes are computed from the others, though they may in 
	// some cases be overridden by subclasses.
	
	@Override
	public String toString() {
		return name();
	}

	/** Subclasses will usually override this to provide a better
	 * description.
	 * @return a detailed HTML description of the item. */
	public String getDescription() {
		// By default, just return the name.
		return name();
	}
	
	/** Get a description of this defense, for use in
	 * an item description.
	 * @return The description.
	 */
	public final String describeDefense() {
		if (ac() == 0) {
			return "";
		}
		
		
		StringBuilder buf = new StringBuilder();
		String suff = "";
		
		if (plural()) {
			buf.append("They ");
		} else {
			buf.append("It ");
			suff = "s";
		}

		buf.append("block" + suff + " " + ac() + " points of damage");
				
		buf.append(".");

		return buf.toString();
	}
	
	/**
	 * @return the sprite image
	 */
	public final Image getImage() {
		return sprite().image();
	}

	/** Is the item usable by a particular player character?
	 * Override if the PC's class, etc., makes a difference.
	 * @param pc The player character
	 * @return True if it is, and false otherwise.
	 */
	public boolean canBeUsedBy(PlayerCharacter pc) {
		// By default, if it's usable it's usable.
		return isUsable();
	}
	
	/** Is the item something that can be equipped?
	 * @return true if it can go in a normal equipment slot, and false otherwise.
	 */
	public final boolean isEquippable() {
		return slot() != Slot.NONE;
	}
	
	/** Can the item be equipped by a particular player character?
	 * Override if the PC's class, etc., makes a difference.
	 * @param pc The player character
	 * @return True if it is, and false otherwise.
	 */
	public boolean canBeEquippedBy(PlayerCharacter pc) {
		// By default, if it's equippable it's equippable.
		return isEquippable();
	}
	
	/** The player character makes use of the item.
	 * Subclasses should override this if they are usable.
	 * If the item is used up, it should ask the player character to
	 * discard it.
	 * 
	 * @param pc the PC who owns the item.
	 */
	public void use(PlayerCharacter pc) {
		// Does nothing by default.
	}
}
