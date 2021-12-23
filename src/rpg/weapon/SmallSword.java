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
public class SmallSword extends HandWeapon {
	private static final long serialVersionUID = 1L;

	/** Creates a new item. */
	public SmallSword() {
		super();
	}
	
	@Override public String name()   { return "Small Sword"; }
	@Override public Sprite sprite() { return Items.SHORT_SWORD; }
	@Override public int    value()  { return 48; }
	@Override public Dice   damage() { return new Dice(1,6); }

	@Override
	public String getDescription() {
		return " It can be used for close combat attacks, " +
				"and does " + damage() + " points of damage.";
	}
}
