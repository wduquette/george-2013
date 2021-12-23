/**
 * 
 */
package rpg.feature;

import graphics.Features;
import graphics.Sprite;
import rpg.EventType;
import rpg.Feature;
import rpg.pc.PlayerCharacter;
import util.Script;
import util.ScriptAction;

/** A Chest, which can be opened and closed.
 * @author will
 */
public class Chest extends Feature {
	private static final long serialVersionUID = 1L;

	@Override public String name()   { return "Chest"; }
	@Override public Sprite sprite() { return open ? openSprite : closedSprite; }

	// Instance variables
	boolean open = false;
	private Sprite openSprite = Features.OPEN_CHEST;
	private Sprite closedSprite = Features.CHEST;
	

	
	//-------------------------------------------------------------------------
	// Queries

	/** @return true if the feature is poke-triggered, and false otherwise. */
	@Override
	public boolean isPokeTriggered() {
		// You can poke a Chest to open and close it.
		return true;
	}

	/** You can walk over chests. */
	@Override
	public boolean isWalkable() {
		return true;
	}

	/** You can fly over chests. */
	@Override
	public boolean isFlyable() {
		return true;
	}
	
	//-------------------------------------------------------------------------
	
	/** Sets the sprites to use for this chest.
	 * 
	 * @param closedSprite The sprite to use when the chest is closed.
	 * @param openSprite  The sprite to use when the chest is open
	 */
	public void setSprites(Sprite closedSprite, Sprite openSprite) {
		this.openSprite = openSprite;
		this.closedSprite = closedSprite;
	}

	/** @return true if the chest is open and false otherwise. */
	public boolean isOpen() {
		return open;
	}
	
	/** Opens or closes the chest.
	 * TBD: Someday we might have multiple states, e.g., LOCKED.
	 * @param flag True if the chest is open and false if it is closed.
	 */
	public void setOpen(boolean flag) {
		open = flag;
		
		if (open) {
			onOpen();
		}
	}

	/** Subclasses can override this to provide additional behavior
	 * when the chest is opened.  The default behavior is to notify
	 * any listeners.
	 */
	public void onOpen() {
		callListeners(EventType.OPEN);
	}

	//-------------------------------------------------------------------------
	// Feature Behavior
	
	/** A mobile calls this to interact with the feature. */
	@Override
	public void poke(Script script, PlayerCharacter m) {
		// If the chest is open, close it; if it is closed, open it.
		script.add(new ToggleChest());
	}
	
	//-------------------------------------------------------------------------
	// ScriptActions
	
	/** Toggles the chest state. */
	protected class ToggleChest implements ScriptAction {
		public void execute() {
			setOpen(!open);
		}
	}
		
}
