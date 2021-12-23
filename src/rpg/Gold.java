/**
 * 
 */
package rpg;

import graphics.Items;
import graphics.Sprite;

/** Contains gold in a chest or item stack.  If a PC picks it up, the gold
 * will be transferred to the party.
 * @author will
 *
 */
public final class Gold extends Item {
	private static final long serialVersionUID = 1L;

	// Instance Variables
	private int amount;
	
	@Override public Sprite sprite() { return Items.GOLD_COIN; }

	/** Creates a Gold item containing some number of gold coins.
	 * 
	 * @param amount The number of gold coins.
	 */
	public Gold(int amount) {
		super();
		this.amount = amount;
	}
	
	@Override public String name() { 
		return amount == 1 ? "gold coin" : (amount + " gold coins"); 
	}

	/** @return the number of gold coins. 
	 * TBD: Should probably just use value()!  */
	public int amount() {
		return amount;
	}
}
