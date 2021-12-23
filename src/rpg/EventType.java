/**
 * 
 */
package rpg;

/** The type of an EntityEvent.
 * @author will
 *
 */
public enum EventType {
	/** The entity has been alerted. */      ALERT,
	/** The entity has been killed. */       DEATH,
	/** The entity has detected an enemy. */ ENEMY,
	/** The entity has been opened. */       OPEN;
}
