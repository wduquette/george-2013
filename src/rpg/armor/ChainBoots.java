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
public class ChainBoots extends Item {
	private static final long serialVersionUID = 1L;

	@Override public String  name()   { return "Chain Boots"; }
	@Override public Sprite  sprite() { return Items.CHAIN_BOOTS; }
	@Override public Slot    slot()   { return Slot.FOOTWEAR; }
	@Override public int     ac()     { return 3; }
	@Override public int     value()  { return 12; }
	@Override public boolean plural() { return true; }

	/** @return the description */
	public String getDescription() {
		return "Boots made of iron chain.  Why don't they puddle around your ankles? " +
				describeDefense();
	}
}
