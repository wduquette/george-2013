/**
 * 
 */
package rpg.weapon;

import graphics.Items;
import graphics.Sprite;
import rpg.Dice;
import rpg.RangedWeapon;

/** A Staple Gun, George's first ranged weapon.
 * TBD: Should only be wielded by George.
 * TBD: The one retrieved from the ladybug quest should be enchanted,
 * yielding the +2.
 * @author will
 */
public class StapleGun extends RangedWeapon {
	private static final long serialVersionUID = 1L;

	/** Creates a new item. */
	public StapleGun() {
		super();
	}

	@Override public String name()   { return "Staple Gun"; }
	@Override public Sprite sprite() { return Items.STAPLE_GUN; }
	@Override public int    range()  { return 4; }
	@Override public int    value()  { return 50; }
	@Override public Dice   damage() { return new Dice(1,4,2); }

	@Override
	public String getDescription() {
		return "A staple gun. " +
				"Useful for a variety of things around the farm, " +
				"it can also be used for ranged attacks " +
				"on targets up " + range() + " steps away, " +
				"and does " + damage() + " points of damage.";
	}
}
