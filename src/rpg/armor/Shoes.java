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
public class Shoes extends Item {
	private static final long serialVersionUID = 1L;

	@Override public String  name()   { return "Shoes"; }
	@Override public Sprite  sprite() { return Items.SHOES; }
	@Override public Slot    slot()   { return Slot.FOOTWEAR; }
	@Override public int     ac()     { return 1; }
	@Override public int     value()  { return 4; }
	@Override public boolean plural() { return true; }

	@Override
	public String getDescription() {
		return "Beat-up leather shoes. " +
				"You can wear them on your feet, for all the good they will do. " +
				describeDefense();
	}
}
