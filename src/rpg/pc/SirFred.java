/**
 * 
 */
package rpg.pc;

import graphics.Mobiles;
import graphics.Sprite;
import rpg.armor.Boots;
import rpg.armor.LeatherArmor;
import rpg.item.VialOfHealing;
import rpg.weapon.Dagger;


/** The George mobile.  George is the player character.
 * @author will
 */
public final class SirFred extends PlayerCharacter {
	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------------
	// Creation
	
	/** Creates the mobile and places him on the board. 
	 * 
	 * @param region The region in which the mobile moves
	 * @param start The mobile's starting position.
	 */
	public SirFred() {
		// FIRST, initialize the super class.
		super(CharacterClass.KNIGHT, 12, 8,
				8,  // STR
				12); // CON
		
		// Give George his basic equipment.
		inventory.add(new VialOfHealing());
		equipment.equip(new LeatherArmor());
		equipment.equip(new Boots());
		equipment.equip(new Dagger());
	}

	@Override public String key()        { return "sirfred"; }
	@Override public Sprite baseSprite() { return Mobiles.KNIGHT; }
}
