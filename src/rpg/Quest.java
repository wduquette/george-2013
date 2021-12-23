/**
 * 
 */
package rpg;

/** A Quest is an object with a title and description that represents 
 * some task the party needs to perform.  Quests can be defined in
 * various places, and often by particular Regions.  The Board keeps
 * track of the accepted and completed quests, and the QuestPanel
 * displays them. 
 * @author will
 *
 */
public interface Quest {
	/** @return the title of the quest. */
	public String title();
	
	/** @return the description of the quest. */
	public String description();
	
	/** @return a description of the completion of the quest. */
	public String completion();
}
