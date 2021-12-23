/**
 * 
 */
package rpg;

/** The result of making a success roll.
 * @author will
 *
 */
public final class Result {
	// Instance Variables
	
	/** The actual roll. */
	public final int roll;
	
	/** True if the roll was a success, and false for a failure. */
	public final boolean success;
	
	/** True if the success or failure was "critical". */
	public final boolean critical;
	
	/** Creates a new Result record.
	 * 
	 * @param roll The actual roll, 3 to 18.
	 * @param success  True if the roll was a success, and false for a failure.
	 * @param critical True if the success or failure was "critical".
	 */
	public Result(int roll, boolean success, boolean critical) {
		this.roll = roll;
		this.success = success;
		this.critical = critical;
	}
	
	@Override 
	public String toString() {
		return "Result(" + roll + "," + 
				(success ? "success," : "failure,") +
				(critical ? "critical" : "not critical") + ")";
	}
}
