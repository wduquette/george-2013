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
public class ChainHelmet extends Item {
	private static final long serialVersionUID = 1L;

	@Override public String name()   { return "Chain Helmet"; }
	@Override public Sprite sprite() { return Items.CHAIN_HELMET; }
	@Override public Slot   slot()   { return Slot.HELMET; }
	@Override public int    ac()     { return 5; }
	@Override public int    value()  { return 75; }

	@Override
	public String getDescription() {
		return "A sturdy coif of chain mail, this helmet doesn't look like a " +
				"pot at all.  The soup would run right out of it. " +
				"You can wear it on your head. " +
				describeDefense();
	}
}
