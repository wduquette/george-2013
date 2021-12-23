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
public class ChainArmor extends Item {
	private static final long serialVersionUID = 1L;

	@Override public String name()   { return "Chain Armor"; }
	@Override public Sprite sprite() { return Items.CHAIN_ARMOR; }
	@Override public Slot   slot()   { return Slot.ARMOR; }
	@Override public int    ac()     { return 11; }
	@Override public int    value()  { return 550; }

	@Override
	public String getDescription() {
		return "Rusty chain armor. Not too nice, but better than a poke in " +
				"the eye with a sharp stick. " +
				"You can wear it on your body. " +
				describeDefense();
	}
}
