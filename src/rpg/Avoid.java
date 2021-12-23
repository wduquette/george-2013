/**
 * 
 */
package rpg;

/** Things to avoid while moving.  Each item in the enumeration
 * specifies a particular set of things of interest.
 * TBD: make the the public variables private, and define methods.
 * @author will
 *
 */
public enum Avoid {
	/** Avoid everything. */     					EVERYTHING(true, true, true),
	/** Avoid hazards and impassable features. */ 	HAZARDS(false, true, true),
	/** Avoid only impassable terrain. */			TERRAIN_ONLY(false, false, false);

	/** Do avoid all mobiles? */
	public final boolean mobiles;
	
	/** Do we avoid hazards in general? */
	public final boolean hazards;
	
	/** Do we avoid features? */
	public final boolean features;
	
	Avoid(Boolean mobiles, Boolean hazards, Boolean features) {
		this.mobiles = mobiles;
		this.hazards = hazards;
		this.features = features;
	}
}
