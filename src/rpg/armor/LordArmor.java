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
public class LordArmor extends Item {
	private static final long serialVersionUID = 1L;

	@Override public String  name()   { return "Lord's Armor"; }
	@Override public Sprite  sprite() { return Items.LORD_ARMOR; }
	@Override public Slot    slot()   { return Slot.ARMOR; }
	@Override public int     ac()     { return 40; }
	@Override public int     value()  { return 20000; }

	@Override
	public String getDescription() {
		return "Fancy, gold-filigree plate mail, suitable for a noble lord. " +
				"You can wear it on your body, and all the ladies will swoon. " +
				describeDefense();
	}
}
