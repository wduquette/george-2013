/**
 * 
 */
package rpg.item;

import graphics.Items;
import graphics.Sprite;
import rpg.Item;
import rpg.pc.PlayerCharacter;

/** This is a scroll of magic mapping: it marks a region "seen" for
 * a radius around the mobile that reads it.
 * @author will
 *
 */
public class ScrollOfMagicMapping extends Item {
	private static final long serialVersionUID = 1L;

	private static final int RADIUS = 20;
	
	// Constructor
	
	/** Creates a new item */
	public ScrollOfMagicMapping() {
		super();
	}
	
	@Override public String  name()     { return "Scroll of Magic Mapping"; }
	@Override public Sprite  sprite()   { return Items.MAP_SCROLL; }
	@Override public int     value()    { return 40; }
	@Override public boolean isUsable() { return true; }
	
	/** Maps the region.  The scroll is then used up.
	 * @param pc The PC.
	 */
	public void use(PlayerCharacter pc) {
		pc.log("You seem to know more about the area.");
		pc.region().markSeen(pc.place(), RADIUS);
		pc.discard(this);
	}
	
	/** @return the description */
	public String getDescription() {
		return " a magic scroll that maps the region around you " +
				"and imprints it in your brain so thoroughly " +
				"that you'll never forget it.";
	}	
}
