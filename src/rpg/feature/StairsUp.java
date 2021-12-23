/**
 * 
 */
package rpg.feature;

import graphics.Features;

/** A stairway leading up to another region.
 * @author will
 */
public class StairsUp extends Exit {
	private static final long serialVersionUID = 1L;

	/** Creates a new staircase.
	 * 
	 * @param toRegion The ID of the region it links to
	 * @param point The point of interest in that region.
	 */
	public StairsUp(String toRegion, String point) {
		super("Stairs Up", Features.STAIRS_UP, toRegion, point);
	}
}
