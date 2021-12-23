/**
 * 
 */
package rpg;

import graphics.Sprite;

import java.awt.Image;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import map.Cell;
import util.RandomPlus;
import app.Board;

/** Defines an entity in the game: something that can appear on the map.
 * @author will
 */
abstract public class Entity implements Serializable {
	private static final long serialVersionUID = 1L;

	/** Random Number source for use by all entities. */
	protected static final RandomPlus random = new RandomPlus();

	private boolean isVisible = true;
	private Region region = null;
	private Cell place = null;
	private Map<EntityListener,Object> listeners = null;
		
	//------------------------------------------------------------------------
	// Attribute Methods
	//
	// These methods represent normally constant attributes of the entity.
	// They can be, and in some cases *must* be, overridden by
	// subclasses.
	
	/** @return the entity's key.  This is used as a key into strings tables
	 * and the like.  Classes that use it should override the call.
	 */
	public String key() { return "tbd"; }
	
	/** @return the entity's name. */
	abstract public String name();
	
	/** @return the entity's current sprite. */
	abstract public Sprite sprite();

	/** @return whether the user can poke the entity or not.  We assume
	 * not; the subclass should override this if so. */
	public boolean canInteract() { return false; }
	
	/** A more detailed description of the entity, often read from 
	 * a strings table.
	 * @return
	 */
	public String description() { return name(); }
	
	//------------------------------------------------------------------------
	// Properties
	//
	// Properties of the entity: things will usually very over time.
	
	
	/** Sets the mobile's visibility flag.
	 * 
	 * @param isVisible True if the mobile is visible, and false otherwise.
	 */
	public final void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	/** @return whether the entity is visible or not. */
	public final boolean isVisible() {
		return isVisible;
	}

	/** Places the entity at a cell in a region.  This is usually
	 * used by the region when the entity is added to it.
	 * 
	 * @param region The new region.
	 * @param cell The cell
	 */
	public final void place(Region region, Cell cell) {
		this.region = region;
		this.place = cell;
	}

	/** @return the entity's region */
	public final Region region() {
		return region;
	}

	/** Move to the given cell
	 * 
	 * @param cell The cell
	 */
	public final void place(Cell cell) {
		place = cell;
	}

	/** @return The mobile's cell. */
	public final Cell place() {
		return place;
	}

	//------------------------------------------------------------------------
	// Computed Attributes

	/** @return the entity's current image. */
	public Image image() {
		return sprite().image();
	}

	/** @return the entity's current Icon. */
	public Icon icon() {
		return sprite().icon();
	}
	
	/** @return the entity's image, magnified. */
	public Image bigImage() {
		return sprite().bigImage();
	}
	
	/** @return the entity's icon, magnified. */
	public Icon bigIcon() {
		return sprite().bigIcon();
	}

	@Override
	public String toString() {
		return name();
	}
	
	/** @return a short "details" string for the entity. */
	public String details() {
		return name() + place;
	}
	
	//------------------------------------------------------------------------
	// EntityListener methods
	
	/** Adds a listener to this entity.  It will be notified when various
	 * events happen.  A given listener may be added to this entity only
	 * once.
	 * @param listener  The listening object
	 * @param tag A tag with meaning to the listening object.
	 */
	public void addListener(EntityListener listener, Object tag) {
		if (listeners == null) {
			listeners = new HashMap<>(0);
		}
		listeners.put(listener, tag);
	}
	
	/** Adds a listener to this entity.  It will be notified when various
	 * events happen.  A given listener may be added to this entity only
	 * once.
	 * @param listener  The listening object
	 * @param tag A tag with meaning to the listening object.
	 */
	public void addListener(EntityListener listener) {
		addListener(listener, null);
	}
	
	/** Removes a listener from this entity.
	 * 
	 * @param listener  The listener to remove.
	 */
	public void removeListener(EntityListener listener) {
		if (listeners != null) {
			listeners.remove(listener);
		}
	}
	
	protected void callListeners(EventType type) {
		if (listeners == null) {
			return;
		}
		
		for (EntityListener listener : listeners.keySet()) {
			Object tag = listeners.get(listener);
			EntityEvent e = new EntityEvent(this, type, tag);
			listener.handleEvent(e);
		}
	}
	
	//------------------------------------------------------------------------
	// Utility Methods
	
	/** The entity writes a debugging message.
	 * 
	 * @param message The message.
	 */
	public final void println(String message) {
		Board.get().println(this + ": " + message);
	}
	
	/** The entity writes a message to the user.
	 * @param message The message.
	 */
	public final void log(String message) {
		Board.get().log(name() + ": " + message);
	}
	
}