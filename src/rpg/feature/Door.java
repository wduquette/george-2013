/**
 * 
 */
package rpg.feature;

import graphics.Features;
import graphics.Sprite;
import rpg.Feature;
import rpg.pc.PlayerCharacter;
import util.Script;
import util.ScriptAction;

/** A Door, which can be opened and closed.
 * @author will
 */
public class Door extends Feature {
	private static final long serialVersionUID = 1L;

	private final Sprite openDoor;
	private final Sprite closedDoor;
	
	/** Creates a new Door, initially closed.
	 */
	public Door() {
		this(Features.OPEN_DOOR, Features.CLOSED_DOOR);
	}

	/** Creates a new Door, initially closed, with given
	 * sprites.
	 * @param openDoor The open door sprite.
	 * @param closedDoor The closed door sprite.
	 */
	public Door(Sprite openDoor, Sprite closedDoor) {
		super();
		this.openDoor = openDoor;
		this.closedDoor = closedDoor;
	}
	
	@Override public String name()   { return "Door"; }
	@Override public Sprite sprite() { return open ? openDoor : closedDoor; }

	// Instance variables
	boolean open = false;
	
	//-------------------------------------------------------------------------
	// Queries

	/** @return true if the feature is poke-triggered, and false otherwise. */
	@Override
	public boolean isPokeTriggered() {
		// You can poke a Door to open and close it.
		return true;
	}
	
	/** Doors are opaque when closed. */
	@Override
	public boolean isOpaque() {
		return !open;
	}
	
	/** You can walk through open doors. */
	@Override
	public boolean isWalkable() {
		return open;
	}

	/** You can fly through open doors. */
	@Override
	public boolean isFlyable() {
		return open;
	}
	
	//-------------------------------------------------------------------------

	/** @return true if the door is open and false otherwise. */
	public boolean isOpen() {
		return open;
	}
	
	/** Opens or closes the door.
	 * TBD: Someday we might have multiple states, e.g., JAMMED.
	 * @param flag True if the door is open and false if it is closed.
	 */
	public void setOpen(boolean flag) {
		open = flag;
	}
	
	//-------------------------------------------------------------------------
	// Feature Behavior
	
	/** A mobile calls this to interact with the feature. */
	@Override
	public void poke(Script script, PlayerCharacter m) {
		// If the door is open, close it; if it is closed, open it.
		script.add(new ToggleDoor());
	}
	
	//-------------------------------------------------------------------------
	// ScriptActions
	
	/** Toggles the door state. */
	protected class ToggleDoor implements ScriptAction {
		public void execute() {
			setOpen(!open);
		}
	}
}
