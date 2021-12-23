/**
 * 
 */
package rpg.pc;

import graphics.Mobiles;
import graphics.Sprite;
import rpg.armor.Hat;
import rpg.armor.Overalls;
import rpg.armor.Shoes;
import rpg.item.ScrollOfMagicMapping;
import rpg.item.VialOfHealing;
import rpg.weapon.SmallWrench;

/** The George mobile.  George is the player character.
 * @author will
 */
public final class George extends PlayerCharacter {
	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------------
	// Creation
	
	/** Creates the mobile and places him on the board. 
	 * 
	 * @param region The region in which the mobile moves
	 * @param start The mobile's starting position.
	 */
	public George() {
		// FIRST, initialize the super class.
		super(CharacterClass.FARMER, 10, 12, 
				10,  // STR
				10); // CON
		
		// Give George his basic equipment.
		inventory.add(new VialOfHealing());
		inventory.add(new ScrollOfMagicMapping());
		equipment.equip(new Overalls());
		equipment.equip(new Hat());
		equipment.equip(new Shoes());
		equipment.equip(new SmallWrench());
	}

	@Override public String key()        { return "george"; }
	@Override public Sprite baseSprite() { return Mobiles.GEORGE; }
}
