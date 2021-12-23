package util;

import java.util.LinkedList;
import java.util.Queue;

/** An AnimationScript is a queue of ScriptInstructions
 * to be executed in sequence by the Board.  It is usually
 * returned by a Mobile so that the Board can animate the 
 * Mobile's actions.
 * 
 * If the "complete" flag is set, then the Mobile's turn is
 * over when the script has been fully executed; otherwise
 * not.
 * 
 * @author will
 *
 */
public final class Script {
	// Instance Variables
	private Queue<ScriptAction> queue = new LinkedList<>();

	/** Adds an action to the script.
	 * 
	 * @param action The action to add.
	 */
	public void add(ScriptAction action) {
		queue.add(action);
	}
	
	/** @return true if the script is empty, and false otherwise. */
	public boolean isEmpty() {
		return queue.size() == 0;
	}
	
	/** @return true if the script has more instructions, and false otherwise. */
	public boolean isNotEmpty() {
		return queue.size() > 0;
	}
	
	/** Executes the next script action.
	 * 
	 * @return true if there are more actions to execute, and false otherwise.
	 */
	public boolean step() {
		ScriptAction action = queue.remove();
		action.execute();
		return isNotEmpty();
	}
	
	/** Artificially clears the script contents. */
	public void clear () {
		queue.clear();
	}
}
