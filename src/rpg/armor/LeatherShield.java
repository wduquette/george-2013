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
public class LeatherShield extends Item {
	private static final long serialVersionUID = 1L;

	@Override public String name()   { return "Leather Shield"; }
	@Override public Sprite sprite() { return Items.LEATHER_SHIELD; }
	@Override public Slot   slot()   { return Slot.SHIELD; }
	@Override public int    ac()     { return 4; }
	@Override public int    value()  { return 120; }

	@Override
	public String getDescription() {
		return "It's a studded leather shield, big enough to hide behind. " +
				describeDefense();
	}
}
