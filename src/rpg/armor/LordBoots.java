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
public class LordBoots extends Item {
	private static final long serialVersionUID = 1L;

	@Override public String  name()   { return "Lord's Boots"; }
	@Override public Sprite  sprite() { return Items.LORD_BOOTS; }
	@Override public Slot    slot()   { return Slot.FOOTWEAR; }
	@Override public int     ac()     { return 10; }
	@Override public int     value()  { return 10000; }
	@Override public boolean plural() { return true; }

	@Override
	public String getDescription() {
		return "Fancy plate-mail boots for a noble lord. Wow the girls! " +
				describeDefense();
	}
}
