/**
 * 
 */
package rpg;


/** A Weapon with a range of 1, that can be equipped in the
 * WEAPON slot.
 * @author will
 *
 */
public abstract class HandWeapon extends Weapon {
	private static final long serialVersionUID = 1L;

	/* Attribute Methods */
	
	@Override public Slot slot()  { return Slot.HAND; }
	@Override public int  range() { return 1; }
}
