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
public class LeatherArmor extends Item {
	private static final long serialVersionUID = 1L;

	@Override public String name()   { return "Leather Armor"; }
	@Override public Sprite sprite() { return Items.LEATHER_ARMOR; }
	@Override public Slot   slot()   { return Slot.ARMOR; }
	@Override public int    ac()     { return 4; }
	@Override public int    value()  { return 18; }

	@Override
	public String getDescription() {
		return "Musty old leather armor. " +
				"You can wear it on your body. " +
				describeDefense();
	}
}
