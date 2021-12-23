/**
 * 
 */
package tools;

import rpg.RPG;


/**
 * @author will
 *
 */
public class MonsterHitTable {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("    Monster's Level");
		System.out.print("AC ");
		
		for (int level = 1; level <= 50; level++) {
			System.out.print(String.format(" %02d", level));
		}
		System.out.println("");
		
		for (int ac = 0; ac <= 100; ac++) {
			System.out.print(String.format("%03d", ac));
			
			for (int level = 1; level <= 50; level++) {
				int p = RPG.chanceToHit(60 + 3*level, ac);
				
				System.out.print(String.format(" %02d", p));
			}
			
			System.out.println("");
		}
	}
}
