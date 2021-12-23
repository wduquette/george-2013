/**
 * 
 */
package rpg;

import graphics.Effects;
import graphics.Items;
import graphics.Sprite;

import java.io.Serializable;

/** An Attack object aggregates all of the details of a mobile's attack
 * into a neat package.  The attack is included in the Engagement when
 * a mobile engages another mobile.  Eventually it will include modifiers
 * such as fire and acid effects.  For now all damage is physical.<p>
 * 
 * NPCs will usually just create the desired attack and leave it alone.
 * PCs will usually create an attack from their readied weapon, and then add
 * modifiers from their other equipment.
 * @author will
 *
 */
public class Attack implements Serializable {
	private static final long serialVersionUID = 1L;

	// Model Variables
	private int skill;         // The attacker's skill with their mode of attack.
	private Dice damage;       // The damage dice
	private int plusToDamage;  // The damage modifier.

	// UI variables
	private String verb = "hits";
	private Sprite hit = Items.LONG_SWORD;
	private Sprite bullet = Effects.BULLET;
	
	// Constructor
	
	/** Creates a new attack.
	 * 
	 * @param skill  The attacker's skill with their mode of attack.
	 * @param verb   The verb to use to log the attack
	 * @param damage The damage dice should they hit
	 * @param hit    The sprite image to use if you hit
	 * @param bullet The bullet image to use to animate ranged attacks, or
	 * null for none.
	 */
	public Attack(int skill, String verb, Dice damage, Sprite hit, Sprite bullet) {
		this.skill = skill;
		this.damage = damage;
		this.verb = verb;
		this.hit = hit;
		this.bullet = bullet;
		this.plusToDamage = 0;
	}
	
	/** Creates a new attack given a weapon.
	 * 
	 * @param skill  The attacker's skill with their mode of attack.
	 * @param verb   The verb used to log the attack.
	 * @param weapon The weapon in use.
	 */
	public Attack(int skill, String verb, Weapon weapon) {
		this(skill, verb, weapon.damage(), weapon.hit(), weapon.bullet());
	}
	
	// Modifiers

	/** Adds a "to damage" modifier: a number of damage points to add to the
	 * total damage in the case of a hit.
	 * @param modifier The number of points.
	 */
	public void addToDamage(int modifier) {
		plusToDamage += modifier;
	}
	
	// Queries
	
	/** @return the attacker's skill */
	public int skill() { return skill; }
	
	/** @return the attack verb */
	public String verb() { return verb; }
	
	/** @return the amount of damage. */
	public int damage() { 
		return damage.roll() + plusToDamage; 
	}
	
	/** @return the maximum possible amount of damage. */
	public int maxDamage() {
		return damage.max() + plusToDamage;
	}
	
	/** @return the hit sprite */
	public Sprite hit() { return hit; }
	
	/** @return the bullet sprite */
	public Sprite bullet() { return bullet; }
}
