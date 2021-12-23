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
public class PizzaPan extends Item {
	private static final long serialVersionUID = 1L;

	@Override public String name()   { return "Pizza Pan"; }
	@Override public Sprite sprite() { return Items.PIZZA_PAN; }
	@Override public Slot   slot()   { return Slot.SHIELD; }
	@Override public int    ac()     { return 2; }
	@Override public int    value()  { return 30; }

	@Override
	public String getDescription() {
		return "It's a pizza pan.  It's a little greasy, but you could " +
			   "probably use it as a shield of sorts.  It might even be " +
			   "of some help. " +
			   describeDefense();
	}
}
