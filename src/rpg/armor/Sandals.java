/**
 * 
 */
package rpg.armor;

import graphics.Items;
import graphics.Sprite;
import rpg.Item;
import rpg.Slot;

/** Sandals, as worn by a Friar.
 * @author will
 *
 */
public class Sandals extends Item {
	private static final long serialVersionUID = 1L;

	@Override public String  name()   { return "Sandals"; }
	@Override public Sprite  sprite() { return Items.SANDALS; }
	@Override public Slot    slot()   { return Slot.FOOTWEAR; }
	@Override public int     ac()     { return 1; }
	@Override public int     value()  { return 3; }
	@Override public boolean plural() { return true; }

	@Override
	public String getDescription() {
		return "Leather sandals.  The soles are good, but they look chilly. " +
				describeDefense();
	}
}
