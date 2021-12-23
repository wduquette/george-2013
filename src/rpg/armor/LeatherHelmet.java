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
public class LeatherHelmet extends Item {
	private static final long serialVersionUID = 1L;

	@Override public String name()   { return "Leather Helmet"; }
	@Override public Sprite sprite() { return Items.LEATHER_HELMET; }
	@Override public Slot   slot()   { return Slot.HELMET; }
	@Override public int    ac()     { return 2; }
	@Override public int    value()  { return 12; }

	@Override
	public String getDescription() {
		return "A hard cap of boiled leather.  It smells of garlic and cabbage. " +
				"You can wear it on your head. " +
				describeDefense();
	}
}
