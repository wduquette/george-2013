/**
 * 
 */
package rpg.armor;

import graphics.Items;
import graphics.Sprite;
import rpg.Item;
import rpg.Slot;

/**
 * @author will
 *
 */
public class WoodenShield extends Item {
	private static final long serialVersionUID = 1L;

	@Override public String name()   { return "Wooden Shield"; }
	@Override public Sprite sprite() { return Items.WOODEN_SHIELD; }
	@Override public Slot   slot()   { return Slot.SHIELD; }
	@Override public int    ac()     { return 2; }
	@Override public int    value()  { return 40; }

	@Override
	public String getDescription() {
		return "It's a shield of sorts.  It might be some help. " +
				describeDefense();
	}
}
