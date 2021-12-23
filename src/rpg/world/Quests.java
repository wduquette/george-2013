/**
 * 
 */
package rpg.world;

import rpg.Quest;
import util.StringsTable;
import app.Board;

/** The quests found in the game world.
 * @author will
 *
 */
@SuppressWarnings("javadoc")
public enum Quests implements Quest {
	SIR_FRED,
	BUG_VALLEY,
	MAGUFFIN,
	UNHAPPY_PRINCESS,
	MAGIC_RING;
	
	@Override
	public String title() { 
		return strings.get(this.toString().toLowerCase() + ".title").trim(); 
	}
	
	@Override
	public String description() { 
		return strings.get(this.toString().toLowerCase() + ".description"); 
	}

	@Override
	public String completion() { 
		return strings.get(this.toString().toLowerCase() + ".completion"); 
	}
	
	/** @return true if the party has accepted the quest, and false
	 * otherwise.
	 */
	public boolean isAccepted() {
		return Board.get().quest.isAccepted(this);
	}
	
	/** Accept this quest. */
	public void accept() {
		Board.get().quest.accept(this);
	}
	
	/** Complete this quest. */
	public void complete() {
		Board.get().quest.complete(this);
	}

	/** @return true if the party has completed the quest, and false
	 * otherwise.
	 */
	public boolean isCompleted() {
		return Board.get().quest.isCompleted(this);
	}

	/** The strings table. */
	private static StringsTable strings;

	static {
		// FIRST, loads the strings.
		strings = new StringsTable(Quests.class,"Quests.strings");
	}
}