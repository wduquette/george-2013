/**
 * 
 */
package rpg.feature;

import graphics.Features;
import graphics.Sprite;
import rpg.Feature;

/** A generic trap.  It's invisible by default, and does nothing.
 * @author will
 */
public class Trap extends Feature {
	private static final long serialVersionUID = 1L;

	/** Creates a new trap.
	 */
	public Trap() {
		super();
		setVisible(false);
	}
	
	@Override public String name()   { return "Trap"; }
	@Override public Sprite sprite() { return Features.BLANK; }
		
	//-------------------------------------------------------------------------
	// Queries

	/** You can walk onto traps. */
	@Override
	public boolean isWalkable() {
		return true;
	}

	/** You can fly over them. */
	@Override
	public boolean isFlyable() {
		return true;
	}
	
	/** @return true if the feature can be triggered by being
	 * stepped on.
	 */
	@Override
	public boolean isStepTriggered() {
		return true;
	}	
	
	//-------------------------------------------------------------------------
	// Feature Behavior
	
}
