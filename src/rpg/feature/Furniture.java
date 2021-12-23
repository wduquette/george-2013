/**
 * 
 */
package rpg.feature;

import graphics.Sprite;
import rpg.Feature;

/** Furniture blocks walking but not sight; it has no behavior.
 * @author will
 */
public class Furniture extends Feature {
	private static final long serialVersionUID = 1L;

	// Instance Variables
	private String name;
	private Sprite sprite;
	
	/** Creates a new Chest.
	 * 
	 * @param name The name of the furniture, for display
	 * @param sprite The sprite to display.
	 */
	public Furniture(String name, Sprite sprite) {
		super();
		this.sprite = sprite;
		this.name = name;
	}
	
	@Override public String name()   { return name;   }
	@Override public Sprite sprite() { return sprite; }

	
	//-------------------------------------------------------------------------
	// Queries


	/** You can walk through open doors. */
	@Override
	public boolean isWalkable() {
		return false;
	}

	/** You can fly over chests. */
	@Override
	public boolean isFlyable() {
		return true;
	}
}
