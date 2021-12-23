/**
 * 
 */
package rpg.item;

import graphics.Items;
import graphics.Sprite;
import rpg.Item;
import rpg.pc.PlayerCharacter;

/**
 * @author will
 *
 */
public class Handkerchief extends Item {
	private static final long serialVersionUID = 1L;

	// Constructor
	
	@Override public String  name()     { return "Handkerchief"; }
	@Override public Sprite  sprite()   { return Items.HANDKERCHIEF; }
	@Override public int     value()    { return 4; }
	@Override public boolean isUsable() { return true; }
	
	/** The player blows his nose.  Might eventually heal a status ailment.
	 * @param pc The PC.
	 */
	public void use(PlayerCharacter pc) {
		pc.log("blows his nose.");
		// TBD: Perhaps this will heal a status ailment, hayfever.
		pc.discard(this);
	}
	
	/** @return the description */
	public String getDescription() {
		return " a lady's handkerchief.  It might be useful if your " + 
				"nose is runny.";
	}	
}
