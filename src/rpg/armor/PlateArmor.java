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
public class PlateArmor extends Item {
	private static final long serialVersionUID = 1L;

	@Override public String name()   { return "Plate Armor"; }
	@Override public Sprite sprite() { return Items.PLATE_ARMOR; }
	@Override public Slot   slot()   { return Slot.ARMOR; }
	@Override public int    ac()     { return 22; }
	@Override public int    value()  { return 1500; }

	@Override
	public String getDescription() {
		return "A junior knight's plate armor. " +
				"You can wear it on your body. " +
				describeDefense();
	}
}
