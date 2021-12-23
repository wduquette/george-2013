/**
 * 
 */
package rpg;

import java.io.Serializable;

/**
 * @author will
 *
 */
public class Dice implements Serializable {
	private static final long serialVersionUID = 1L;

	// Instance variables
	private int n;    // Number of dice
	private int x;    // Number of sides
	private int y;    // constant plus/minus
	
	// Constructor
	
	/** Creates a dice roll to be rolled later.
	 * 
	 * @param n  The number of dice.
	 * @param x  The number of sides on each die.
	 * @param y  The plus/minus constant.
	 */
	public Dice(int n, int x, int y) {
		this.n = n;
		this.x = x;
		this.y = y;
	}
	
	/** Creates a dice roll to be rolled later.
	 * 
	 * @param n  The number of dice.
	 * @param x  The number of sides on each die.
	 */
	public Dice(int n, int x) {
		this(n, x, 0);
	}
	
	/** Creates a dice roll to be rolled later.
	 * 
	 * @param x  The number of sides on each die.
	 */
	public Dice(int x) {
		this(1, x, 0);
	}

	@Override
	public String toString() {
		if (y > 0)
			return n + "D" + x + "+" + y;
		else if (y < 0)
			return n + "D" + x + y;
		else
			return n + "D" + x;
	}
	
	/** @return the result of rolling the dice. */
	public int roll() {
		int result = y;
		
		for (int i = 0; i < n; i++) {
			int roll = RPG.roll(x);
			result += roll;
		}
		
		return result;
	}
	
	/** @return the maximum result of rolling the dice. */
	public int max() {
		return n*x + y;
	}
	
	/** Test routine.
	 * 
	 * @param args ignored
	 */
	public static void main(String[] args) {
		System.out.println("1D6   = " + new Dice(1,6));
		System.out.println("1D6+2 = " + new Dice(1,6,2));
		System.out.println("1D6-2 = " + new Dice(1,6,-2));
		
		Dice dice1D2 = new Dice(1,2);
		
		for (int i = 0; i < 20; i++) {
			System.out.println(dice1D2 + "=" + dice1D2.roll());
		}
	}
}

