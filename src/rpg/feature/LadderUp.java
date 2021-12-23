/**
 * 
 */
package rpg.feature;

import graphics.Features;

/** A ladder leading up to another region.
 * @author will
 */
public class LadderUp extends Exit {
	private static final long serialVersionUID = 1L;

	/** Creates a new ladder.
	 * 
	 * @param region The region in which it appears.
	 * @param start Its position.
	 * @param toRegion The ID of the region it links to
	 * @param point The point of interest in that region.
	 */
	public LadderUp(String toRegion, String point) {
		super("Ladder Up", Features.LADDER_UP, toRegion, point);
	}
}
