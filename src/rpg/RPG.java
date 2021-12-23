/**
 * 
 */
package rpg;

import util.RandomPlus;

/** This class gathers together general algorithms required by
 * the RPG, such as die rolls and so forth. 
 * @author will
 *
 */
public class RPG {
	// Not instantiable
	private RPG() {}
	
	// Gurps needs a source of random numbers.
	
	private static RandomPlus random = new RandomPlus();
	
	//-------------------------------------------------------------------------
	
	/** Makes a success roll against an effective skill.  Returns
	 * the resulting roll, with an assessment as to whether or not it's
	 * successful and whether or not it's critical.  
	 * 
	 * @param skill
	 * @return The result record.
	 * @deprecated  This is a GURPS-style success roll; it doesn't look
	 * like we're going to use it.
	 */
	public static Result rollSuccess(int skill) {
		// FIRST, roll the dice.
		int roll = roll3D6();
		boolean success;
		boolean critical;
		
		// NEXT, apply the rules to determine success/failure and
		// criticality.
		if (roll <= 4) {
			success = true;
			critical = true;
		} else if (roll == 5 && skill >= 15) {
			success = true;
			critical = true;
		} else if (roll == 6 && skill >= 16) {
			success = true;
			critical = true;
		} else if (roll == 18) {
			success = false;
			critical = true;
		} else if (roll == 17) {
			success = false;
			critical = (skill <= 15);
		} else if (roll - skill >= 10) {
			success = false;
			critical = true;
		} else if (roll <= skill) {
			success = true;
			critical = false;
		} else {
			success = false;
			critical = false;
		}
		
		return new Result(roll, success, critical);
	}
	
	/** @return the result of rolling three six-sided dice. */
	public static int roll3D6() {
		return random.roll(1,6) + random.roll(1,6) + random.roll(1,6);
	}
	
	/** Rolls a random number of points from min to max,
	 * uniformly distributed.
	 *  
	 * @param min The minimum value
	 * @param max The maximum value
	 * @return A random number from min to max inclusive.
	 */
	public static int roll(int min, int max) {
		return random.roll(min,max);
	}
	
	/** Rolls one die with the given number of sides.
	 * 
	 * @param sides the number of sides
	 * @return the result
	 */
	public static int roll(int sides) {
		return random.roll(1,sides);
	}
	
	/** Computed the Angband probability to hit given the attacker's
	 * skill and the defenders armor class.
	 * @param skill  The attacker's skill level
	 * @param ac The defender's armor class
	 * @return A probability between 5 and 95 inclusive.
	 */
	public static int chanceToHit(int skill, int ac) {
		// FIRST, compute the probability using the Angband formula
		double n = Math.max(0, skill - 0.75*ac);
		double p = 100*n/skill;
		
		// NEXT, clamp it between 5 and 95, and return it as an
		// integer.
		return (int)Math.max(5, Math.min(95, p));
	}

}
