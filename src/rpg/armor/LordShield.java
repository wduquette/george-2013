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
public class LordShield extends Item {
	private static final long serialVersionUID = 1L;

	@Override public String name()   { return "Lord's Shield"; }
	@Override public Sprite sprite() { return Items.LORD_SHIELD; }
	@Override public Slot slot()     { return Slot.SHIELD; }
	@Override public int    ac()     { return 10; }
	@Override public int value()     { return 10000; }

	@Override
	public String getDescription() {
		return "A noble lord's shield, though you can't make out the coat " +
				"of arms under all of the curlicues. " +
				describeDefense();
	}

}
