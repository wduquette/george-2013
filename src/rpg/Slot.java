/**
 * 
 */
package rpg;

import graphics.Slots;
import graphics.Sprite;
import graphics.StandardTile;

/** Equipment Slot: A slot in which a PlayerCharacter can equip an item.
 * Each item knows the kind of slot it can go in.  Items that can't be
 * equipped have slot NONE.
 * @author will
 *
 */
public enum Slot {
	/** None: for items that can't be equipped. */
	NONE("None", StandardTile.UNKNOWN),
	
	/** Armor worn on the PC's body. */
	ARMOR("Armor", Slots.ARMOR_SLOT),
	
	/** Armor worn on the PC's head. */
	HELMET("Helmet", Slots.HELMET_SLOT),
	
	/** Armor worn on the PC's feet. */
	FOOTWEAR("Footwear", Slots.FOOTWEAR_SLOT),
	
	/** Hand-to-hand weapon wielded by the PC. */
	HAND("Weapon", Slots.WEAPON_SLOT),
	
	/** Ranged-weapon wielded by the PC. */
	RANGED("Bow", Slots.BOW_SLOT),
	
	/** Shield wielded by the PC. */
	SHIELD("Shield", Slots.SHIELD_SLOT);
	
	// Constructor
	Slot(String name, Sprite sprite) {
		this.name = name;
		this.sprite = sprite;
	}
	
	// Instance variables
	private String name;    // Name of the slot.
	private Sprite sprite;  // Sprite used for empty slot.

	/** @return the name of the slot. */
	public String getName() {
		return name;
	}
	
	/** @return the sprite for empty slots. */ 
	public Sprite getSprite() {
		return sprite;
	}
}
