/**
 * 
 */
package rpg;

import rpg.pc.PlayerCharacter;
import util.Script;

/** A feature is a thing that appears on the map in a fixed location.  
 * Mobiles, and particularly PlayerCharacters, can interact with features.
 * Some features, such as Doors and Chests, can be "poked" to make them 
 * act or change state; others, such as traps, have an effect when stepped on.
 * @author will
 *
 */
abstract public class Feature extends Entity {
	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------------
	// Queries
	
	/** @return true if player characters can interact with the feature.
	 */
	public final boolean canInteract() {
		return isPokeTriggered();
	}
	
	/** @return true if the feature blocks visibility. */
	public boolean isOpaque() {
		return false;
	}
	
	/** @return true if a Mobile can walk on this feature, and false
	 * otherwise.
	 */
	public boolean isWalkable() {
		return false;
	}
	
	/** @return true if a Mobile can fly on this feature, and false
	 * otherwise.
	 */
	public boolean isFlyable() {
		return false;
	}
	
	/** Determines whether the cell is passable to a 
	 * given movement type, disregarding avoidances.
	 * @param type Movement type
	 * @return True if it is passable, and false otherwise.
	 */
	public final boolean isPassableBy(MovementType type) {
		switch (type) {
		case WALKING:
			return isWalkable();
		case FLYING:
			return isFlyable();
		default:
			println("Error, unknown movement type: " + type);
			return false;
		}
	}
	

	/** @return true if the feature is poke-triggered, and false otherwise. */
	public boolean isPokeTriggered() {
		return false;
	}
	
	/** Features can be poked by a player character; when poked, they
	 * add an appropriate action to the player character's animation
	 * script.  There is no default behavior.
	 * @param script The script to add the effect to.
	 * @param pc The mobile who poked the feature.
	 */
	public void poke(Script script, PlayerCharacter pc) {
		// No default behavior.
	}
	
	/** @return true if the feature can be triggered by being
	 * stepped on.
	 */
	public boolean isStepTriggered() {
		return false;
	}
	
	/** Adds an appropriate action to the mobile's 
	 * script when the mobile steps on the feature.
	 * There is no default behavior.
	 * 
	 * @param script the animation script
	 * @param m The mobile
	 * @return true if the routine did anything, and false otherwise.
	 */
	public boolean step(Script script, Mobile m) {
		return false;
	}
}
