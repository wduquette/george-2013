/**
 * 
 */
package rpg;

import graphics.Effects;
import graphics.Sprite;

/** A RangedWeapon is one that can attack from a distance
 * and can be equipped in the BOW slot.
 * @author will
 *
 */
abstract public class RangedWeapon extends Weapon {
	private static final long serialVersionUID = 1L;

	// Attribute Methods
	
	@Override public Slot   slot()   { return Slot.RANGED; }
	@Override public Sprite hit()    { return Effects.TARGET; }
	@Override public Sprite bullet() { return Effects.BULLET; }
}
