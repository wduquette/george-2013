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
public class PotHelmet extends Item {
	private static final long serialVersionUID = 1L;

	@Override public String name()   { return "Pot Helmet"; }
	@Override public Sprite sprite() { return Items.POT_HELMET; }
	@Override public Slot   slot()   { return Slot.HELMET; }
	@Override public int    ac()     { return 3; }
	@Override public int    value()  { return 30; }

	@Override
	public String getDescription() {
		return "It's not actually a cooking pot, but you could use it for one. " +
				"You can wear it on your head. " +
				describeDefense();
	}
}
