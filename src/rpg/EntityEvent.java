/**
 * 
 */
package rpg;

/**
 * @author will
 *
 */
public class EntityEvent {
	/** The entity to which the event occurred. */
	public final Entity entity;
	
	/** The type of the event. */
	public final EventType type;
	
	/** A tag provided by the client who added the event listener. */
	public final Object tag;
	
	/** Creates an initializes an EntityEvent. 
	 * 
	 * @param entity  The entity which experience the event.
	 * @param type The type of the vent.
	 * @param tag The listener's tag.
	 */
	public EntityEvent(Entity entity, EventType type, Object tag) {
		this.entity = entity;
		this.type = type;
		this.tag = tag;
	}
}
