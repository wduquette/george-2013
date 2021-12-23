/**
 * 
 */
package tools;

import rpg.RPG;


/**
 * @author will
 *
 */
public class SkillHitTable {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("    Attacker's Skill");
		System.out.print("AC ");
		
		for (int skill = 5; skill <= 250; skill += 5) {
			System.out.print(String.format(" %03d", skill));
		}
		System.out.println("");
		
		for (int ac = 0; ac <= 100; ac++) {
			System.out.print(String.format("%03d", ac));
			
			for (int skill = 5; skill <= 250; skill += 5) {
				int p = RPG.chanceToHit(skill, ac);
				
				System.out.print(String.format(" %03d", p));
			}
			
			System.out.println("");
		}
	}
}
