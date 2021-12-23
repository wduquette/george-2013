/**
 * 
 */
package rpg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import util.RandomPlus;

/** A probability distribution of classes with a common ancestor
 * class, i.e., monsters or items.  Allows the application to 
 * select a class randomly and create an instance of it.
 * All classes need to have a good default constructor.
 * @author will
 *
 * @param <T> The common ancestor class.
 */
public class RandomClass <T> implements Serializable {
	private static final long serialVersionUID = 1L;

	// Instance variables
	private RandomPlus random = new RandomPlus();
	private int totalChances = 0;
	private List<Class<? extends T>> classes = new ArrayList<>();
	private List<Integer> chances = new ArrayList<>();
	
	/** Adds a class to the distribution of classes.  Its probability
	 * of being chosen is chance/totalChances
	 * @param chance The number of chances this class has.
	 * @param cls The class
	 * @return The object itself, for chaining
	 */
	public RandomClass<T> add(int chance, Class<? extends T> cls) {
		classes.add(cls);
		chances.add(chance);
		totalChances += chance;
		
		return this;
	}
	
	/** Returns a new object whose class is chosen according to
	 * the distribution.
	 * @return The new object.
	 */
	public T newInstance() {
		assert totalChances > 0;
		
		try {
			return pickClass().newInstance();
		} catch(Exception e) {
			System.err.println("Could not create instance of random class:" + e);
			System.exit(1);
		}
		
		return null;
	}
	
	/** Chooses a class randomly from the distribution.
	 * @return The chosen class
	 */
	public Class<? extends T> pickClass() {
		assert totalChances > 0;
		
		int roll = random.nextInt(totalChances);
		int total = 0;
		
		for (int i = 0; i < chances.size(); i++) {
			total += chances.get(i);
			
			if (roll < total) {
				return classes.get(i);
			}
		}
		
		// We should never get here, since roll is less than than
		// the total chances.
		assert false;
		return null;
	}
}
