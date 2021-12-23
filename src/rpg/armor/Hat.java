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
public class Hat extends Item {
	private static final long serialVersionUID = 1L;

	@Override public String name()   { return "Hat"; }
	@Override public Sprite sprite() { return Items.HAT; }
	@Override public Slot   slot()   { return Slot.HELMET; }
	@Override public int    ac()     { return 0; }
	@Override public int    value()  { return 1; }

	@Override
	public String getDescription() {
		return "A floppy peasant's hat, rather dirty and worn. " +
				"You can wear it on your head. " +
				describeDefense();
	}
}
