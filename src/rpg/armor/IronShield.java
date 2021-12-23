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
public class IronShield extends Item {
	private static final long serialVersionUID = 1L;

	@Override public String name()   { return "Iron Shield"; }
	@Override public Sprite sprite() { return Items.PLATE_SHIELD; }
	@Override public Slot   slot()   { return Slot.SHIELD; }
	@Override public int    ac()     { return 5; }
	@Override public int    value()  { return 200; }

	@Override
	public String getDescription() {
		return "It looks like a knight's shield, but the device on it is too " +
				"battered to read. " +
				"You're a nobody, so I guess you can use it. " +
				describeDefense();
	}

}
