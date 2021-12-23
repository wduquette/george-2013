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
public class Dagger extends HandWeapon {
	private static final long serialVersionUID = 1L;

	/** Creates a new item. */
	public Dagger() {
		super();
	}
	
	@Override public String name()   { return "Dagger"; }
	@Override public Sprite sprite() { return Items.DAGGER; }
	@Override public int    value()  { return 10; }
	@Override public Dice   damage() { return new Dice(1,4); }

	@Override
	public String getDescription() {
		return " It can be used for close combat attacks, " +
				"and does " + damage() + " points of damage.";
	}
}
