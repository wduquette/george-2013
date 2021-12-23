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
public class PlateHelmet extends Item {
	private static final long serialVersionUID = 1L;

	@Override public String name()   { return "Plate Helmet"; }
	@Override public Sprite sprite() { return Items.PLATE_HELMET; }
	@Override public Slot   slot()   { return Slot.HELMET; }
	@Override public int    ac()     { return 6; }
	@Override public int    value()  { return 200; }

	@Override
	public String getDescription() {
		return "A junior knight's helmet, complete with visor. " +
				"You can wear it on your head. " +
				describeDefense();
	}
}
