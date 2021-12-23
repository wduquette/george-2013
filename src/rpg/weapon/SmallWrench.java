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
public class SmallWrench extends HandWeapon {
	private static final long serialVersionUID = 1L;

	/** Creates a new item. */
	public SmallWrench() {
		super();
	}
	
	@Override public String name()   { return "Small Wrench"; }
	@Override public Sprite sprite() { return Items.SMALL_WRENCH; }
	@Override public int    value()  { return 20; }
	@Override public Dice   damage() { return new Dice(1,4); }

	@Override
	public String getDescription() {
		return "A small end wrench. " +
				"It can be used to tighten or loosen bolts on farm " +
				"machinery, and it also makes a nice club, doing " +
				damage() + " points of damage to whatever you hit with it.";
	}
}
