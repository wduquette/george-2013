/**
 * 
 */
package rpg;

import java.io.Serializable;


/** A Defense object contains the information about a Mobile's defense.
 * For a PlayerCharacter, it will be the aggregate of all of the PC's
 * defenses from Armor, talismans, and native stats; for simpler mobiles,
 * it is set explicitly by the mobile.
 * @author will
 *
 */
public final class Defense  implements Serializable {
	private static final long serialVersionUID = 1L;

	// Instance variables
	private int ac;     // Armor Class
	
	// Constructor
	
	/** Creates an empty defense, to which physical armor and modifiers 
	 * (e.g., immunity to acid) can be added.
	 */
	public Defense() {
		clear();
	}

	/** Creates a new defense that copies the old.
	 * 
	 * @param def an existing Defense object.
	 */
	public Defense(Defense def) {
		this();
		
		this.ac = def.ac;
	}
	
	/** Clears the defense.
	 * 
	 * @return the object itself
	 */
	public final Defense clear() {
		ac = 0;
		return this;
	}
	
	/** Add armor.
	 * 
	 * @param points The number of armor points to add.
	 * @return The defense object itself.
	 */
	public Defense addArmor(int points) {
		ac += points;
		return this;
	}
	
	/** Adds the input defense to the existing defense.
	 * 
	 * @param def input defense
	 * @return the defense object itself.
	 */
	public Defense add(Defense def) {
		this.ac += def.ac;
		return this;
	}
	
	/** Adds the item's contribution to defense to the existing
	 * defense.
	 * @param item The item
	 * @return The defense object itself.
	 */
	public Defense add(Item item) {
		this.ac += item.ac();
		return this;
	}
	
	/** @return The defender's effective armor class. */
	public int ac() {
		return ac;
	}
	

}
