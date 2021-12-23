/**
 * 
 */
package rpg;

/** An EntityListener can request to listen to entity events.  The
 * When adding an EntityListener to an Entity, the client specifies
 * an ID string that will be included in the EventEvent, rather like
 * an action command string for action listeners.
 * @author will
 *
 */
public interface EntityListener {
	/** The method called by the entity when the event occurs.
	 * 
	 * @param event  Details of the event.
	 */
	public void handleEvent(EntityEvent event);
}
