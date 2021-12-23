/**
 * 
 */
package rpg.behavior;

import rpg.monster.Monster;
import util.Script;

/** A Behavior is an object that governs a monster's behavior, i.e., how it 
 * moves.  Given a script and some number of movement points, it scripts
 * the monster's next move.
 * @author will
 *
 */
public interface Behavior {
	/** Determine the mobile's move given its current situation and the
	 * number of movement points, adding the necessary steps to the
	 * script.
	 * 
	 * @param mob The monster
	 * @param mp The number of movement points available.
	 * @param script The script to receive the steps
	 */
	public void move(Monster mob, int mp, Script script);
}
