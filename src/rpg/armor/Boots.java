/**
 * 
 */
package rpg.armor;

import graphics.Items;
import graphics.Sprite;
import rpg.Item;
import rpg.Slot;

/** Leather boots.
 * @author will
 *
 */
public class Boots extends Item {
	private static final long serialVersionUID = 1L;

	@Override public String  name()   { return "Leather Boots"; }
	@Override public Sprite  sprite() { return Items.BOOTS; }
	@Override public Slot    slot()   { return Slot.FOOTWEAR; }
	@Override public int     ac()     { return 2; }
	@Override public int     value()  { return 7; }
	@Override public boolean plural() { return true; }

	@Override
	public String getDescription() {
		return "Stout leather boots, not flashy but useful. " +
				describeDefense();
	}
}
