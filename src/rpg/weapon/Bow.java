/**
 * 
 */
package rpg.weapon;

import graphics.Items;
import graphics.Sprite;
import rpg.Dice;
import rpg.RangedWeapon;

/**
 * @author will
 *
 */
public class Bow extends RangedWeapon {
	private static final long serialVersionUID = 1L;

	/** Creates a new item. */
	public Bow() {
		super();
	}
	
	@Override public String name()   { return "Bow"; }
	@Override public Sprite sprite() { return Items.BOW; }
	@Override public int    value()  { return 100; }
	@Override public int    range()  { return 8; }
	@Override public Dice   damage() { return new Dice(1,6); }
	
	/** @return the bullet sprite, if any. */
	public Sprite bullet() { return null; }

	@Override
	public String getDescription() {
		return "A basic short bow. " +
				"It can be used for ranged attacks " +
				"on targets up to " + range() + " steps away, " +
				"and does " + damage() + " points of damage.";
	}
}
