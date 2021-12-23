/**
 *
 */
package map;

/** The basic kinds of terrain that can appear in a map.  This type has nothing
 * to do with how tiles appear, and everything to do with how they affect movement,
 * vision, and so forth.
 * TBD: Go back to using the methods, not the variables.
 * @author will
 */
public enum TerrainType {
	/** Unknown terrain */
	UNKNOWN(true, false, false),

	/** Floor: You can walk and fly, and it doesn't block vision.*/
	FLOOR(false, true, true),

	/** Wall: Blocks both vision and movement. */
	WALL(true, false, false),

	/** Fence: Blocks walking, but not vision or flight. */
	FENCE(false, false, true),

	/** Water: Blocks walking, but not vision or flight.  Swimming
	 * or boating are future possibilities. */
	WATER(false, false, true),

	/** Pit: Blocks walking, but not vision or flight. */
	PIT(false, false, true);

	// Attributes

	/** Does it block vision? */
	public final boolean opaque;

	/** Can it be walked on? */
	public final boolean walkable;

	/** Can it be flown through? */
	public final boolean flyable;

	// Constructor

	TerrainType(boolean opaque, boolean walkable, boolean flyable) {
		this.opaque = opaque;
		this.walkable = walkable;
		this.flyable = flyable;
	}

	/** Can you see through this terrain?
	 *
	 * @return The opaque flag.
	 * @deprecated
	 */
	public boolean isOpaque() {
		return opaque;
	}

	/** Can one walk on this tile or not?
	 * @return the walkable flag.
	 * @deprecated
	 */
	public boolean isWalkable() {
		return walkable;
	}

	/** Can one fly over this tile or not?
	 * @return the flyable flag.
	 * @deprecated
	 */
	public boolean isFlyable() {
		return flyable;
	}
}
