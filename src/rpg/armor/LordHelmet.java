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
public class LordHelmet extends Item {
	private static final long serialVersionUID = 1L;

	@Override public String name()   { return "Lord's Helmet"; }
	@Override public Sprite sprite() { return Items.LORD_HELMET; }
	@Override public Slot   slot()   { return Slot.HELMET; }
	@Override public int    ac()     { return 8; }
	@Override public int    value()  { return 8000; }

	@Override
	public String getDescription() {
		return "A noble lord's helmet, with gold leaf and a fancy plume. " +
				"You can wear it on your head. " +
				describeDefense();
	}
}
