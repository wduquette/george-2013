/**
 * 
 */
package rpg.feature;

import graphics.Features;

/** A ladder leading down to another region.
 * @author will
 */
public class LadderDown extends Exit {
	private static final long serialVersionUID = 1L;

	/** Creates a new ladder.
	 * 
	 * @param name The name to display to the user
	 * @param toRegion The ID of the region it links to
	 * @param point The point of interest in that region.
	 */
	public LadderDown(String name, String toRegion, String point) {
		super(name, Features.LADDER_DOWN, toRegion, point);
	}
	
	/** Creates a new ladder.
	 * 
	 * @param toRegion The ID of the region it links to
	 * @param point The point of interest in that region.
	 */
	public LadderDown(String toRegion, String point) {
		this("Ladder Down", toRegion, point);
	}
	
}
