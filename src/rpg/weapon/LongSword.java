/**
 * 
 */
package rpg.weapon;

import graphics.Items;
import graphics.Sprite;
import rpg.Dice;
import rpg.HandWeapon;

/**
 * @author will
 *
 */
public class LongSword extends HandWeapon {
	private static final long serialVersionUID = 1L;

	/** Creates a new item. */
	public LongSword() {
		super();
	}
	
	@Override public String name()   { return "Long Sword"; }
	@Override public Sprite sprite() { return Items.LONG_SWORD; }
	@Override public int    value()  { return 300; }
	@Override public Dice   damage() { return new Dice(2,5); }

	@Override
	public String getDescription() {
		return " It can be used for close combat attacks, " +
				"and does " + damage() + " points of damage.";
	}
}
