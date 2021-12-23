/**
 * 
 */
package rpg;

import graphics.Sprite;

/** A weapon is an object that can be equipped and can do damage to 
 * an enemy.  Weapons have a range; hand-to-hand weapons have a 
 * range of 1 and ranged weapons have a longer range.  The game
 * can animate the attack using a hit sprite (for the actual hit)
 * and a bullet sprite (crossing the distance between the attacker
 * and the victim).  Other equipped items can modify the effect
 * of the attack.
 * @author will
 *
 */
public abstract class Weapon extends Item {
	private static final long serialVersionUID = 1L;

	//------------------------------------------------------------------------
	// Attribute Methods
	
	/** @return the weapon's damage dice. */
	abstract public Dice damage();
	
	/** @return the hit sprite. This defaults to the weapon's own sprite.  */
	public Sprite hit() { return sprite(); }
	
	/** @return the bullet sprite, if any. */
	public Sprite bullet() { return null; }
}
