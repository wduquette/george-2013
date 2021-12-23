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
public class Overalls extends Item {
	private static final long serialVersionUID = 1L;

	@Override public String name()   { return "Grubby Overalls"; }
	@Override public Sprite sprite() { return Items.OVERALLS; }
	@Override public Slot   slot()   { return Slot.ARMOR; }
	@Override public int    ac()     { return 2; }
	@Override public int    value()  { return 4; }

	@Override
	public String getDescription() {
		return "A farmer's overalls.  They are grubby and patched, " +
				"though they do look comfortable. " +
				"You can wear them on your body. " +
				describeDefense();
	}
}
