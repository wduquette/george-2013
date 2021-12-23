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
public class PlateBoots extends Item {
	private static final long serialVersionUID = 1L;

	@Override public String  name()   { return "Plate Boots"; }
	@Override public Sprite  sprite() { return Items.PLATE_BOOTS; }
	@Override public Slot    slot()   { return Slot.FOOTWEAR; }
	@Override public int     ac()     { return 4; }
	@Override public int     value()  { return 50; }
	@Override public boolean plural() { return true; }
	
	@Override
	public String getDescription() {
		return "Plate-mail boots for a rather junior knight. " +
				"You can wear them on your feet. " +
				describeDefense();
	}
}
