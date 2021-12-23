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
public class VialOfHealing extends Item {
	private static final long serialVersionUID = 1L;

	private static final int MIN = 10;
	private static final int MAX = 20;
	
	// Constructor
	
	/** Creates a new item */
	public VialOfHealing() {
		super();
	}
	
	@Override public String  name()     { return "Vial of Healing"; }
	@Override public Sprite  sprite()   { return Items.RED_VIAL; }
	@Override public int     value()    { return 15; }
	@Override public boolean isUsable() { return true; }
	
	/** But a PC can only use one if he needs health.
	 * @return true if the PC needs the potion, and false otherwise.
	 */
	public boolean canBeUsedBy(PlayerCharacter pc) {
		return pc.HP() < pc.maxHP();
	}
	
	/** Heals the player character of 10 to 20 points of 
	 * damage.  The potion is then used up.
	 * @param pc The PC.
	 */
	public void use(PlayerCharacter pc) {
		pc.log("makes a face.");
		pc.heal(random.roll(MIN,MAX));
		pc.discard(this);
	}
	
	/** @return the description */
	public String getDescription() {
		return " a magic potion that can restore " + 
				MIN + " to " + MAX + " points of health. " +
				"It probably tastes bad.";
	}	
}
