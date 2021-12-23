/**
 * 
 */
package rpg;

/** This enum names the different sides in the game, and determines whether
 * two sides are friends, enemies, or neutral to each other.  Essentially,
 * HERO is friends with HERO and enemies with MONSTER, MONSTER is friends
 * with MONSTER and enemies with HERO, and all other combinations are neutral.
 * @author will
 *
 */
public enum Side {
	/** The heroes and their friends.  The player's character(s) are always 
	 * on this side. */
	HERO(1),
	
	/** The monsters; or, really, anything currently antagonistic to the
	 * HERO. */
	MONSTER(-1),
	
	/** Everything else. */
	NEUTRAL(0);
	
	private final int weight;
	
	Side(int weight) {
		this.weight = weight;
	}
	
	/** Determines whether the sides are true friends or not.
	 * 
	 * @param otherSide the other side
	 * @return true if friends, and false otherwise.
	 */
	public boolean isFriend(Side otherSide) {
		return this.weight * otherSide.weight == 1;
	}

	/** Determines whether the sides are enemies or not.
	 * 
	 * @param otherSide the other side
	 * @return true if enemies, and false otherwise.
	 */
	public boolean isEnemy(Side otherSide) {
		return this.weight * otherSide.weight == -1;
	}
	
	/** Determines whether the sides are neutral to each other or not.
	 * 
	 * @param otherSide The other side.
	 * @return true if neutral to each other, and false otherwise.
	 */
	public boolean isNeutral(Side otherSide) {
		return this.weight * otherSide.weight == 0;
	}
}
