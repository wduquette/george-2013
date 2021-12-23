/**
 * 
 */
package rpg;

import map.Cell;
import util.Script;

/** A Combatant is a object that can be the attacker or defender in an
 * Engagement.
 * @author will
 *
 */
public abstract class Combatant extends Mobile {
	private static final long serialVersionUID = 1L;

	//-------------------------------------------------------------------------
	// Instance Variables
	
	private int hp    = 0;
	private int maxHP = 0;
	protected Defense defense = new Defense();
	
	//-------------------------------------------------------------------------
	// Creation
	
	/** Creates a new combatant.
	 * @param hp Its initial hit points.
	 */
	public Combatant(int hp) {
		super();
		this.hp = this.maxHP = hp;
	}

	//------------------------------------------------------------------------
	// Attribute Methods
	
	
	/** @return the combatant's level */
	abstract public int level();
	
	/** @return the experience points awarded for slaying the combatant. */
	public int expAwarded() { return 0; }
	
	
	//-------------------------------------------------------------------------
	// Customizable behavior
	
	/** Notifies the combatant that it has received damage.
	 * The combatant's HP has already been updated, but this allows the
	 * combatant to take action.
	 * @param points The number of points of damage received.
	 */
	public void onDamage(int points) {
		// By default, do nothing.
	}
	
	/** This routine is called when the entity is killed.  Entities
	 * may override it to do something special (like, maybe, explode!).
	 * The default behavior is notify listeners.
	 */
	public void onDeath() {
		callListeners(EventType.DEATH);
	}
	
	//-------------------------------------------------------------------------
	// Stats Queries
	
	/** @return the combatant's defense */
	public final Defense defense() { return defense; }
	
	/** @return whether the combatant is alive or not. */
	public final boolean isAlive() {
		return hp != 0;
	}
	
	/** @return true if the combatant is injured (i.e., 
	 * has taken any damage) and false otherwise.
	 */
	public final boolean isInjured() {
		return hp < maxHP;
	}

	/** @return the mobile's maximum hit points. */
	public final int maxHP() {
		return maxHP;
	}
	
	/** @return the mobile's current hit points. */
	public final int HP() {
		return hp;
	}

	/** Every combatant has a hostile flag.  If it is true, i.e.,
	 * if the combatant is hostile, then it will attack enemies in its
	 * vicinity.  If the flag is false, then it will ignore enemies...
	 * unless they do something to make it angry.<p>
	 * 
	 * All monsters are hostile by default, as indicated by this 
	 * definition.  Particular classes of monster can implement a
	 * more nuanced set of behaviors.
	 * 
	 * PCs are also hostile by default, but (at present) monsters
	 * do not look at it.  It just makes it convenient if the flag
	 * is defined for all combatants.
	 * @return true if the combatant is hostile, and false otherwise.
	 */
	public boolean isHostile() {
		return true;
	}
	
	//-------------------------------------------------------------------------
	// Health Mutators
	
	/** Set the combatant's max hit points, e.g. on leveling up.
	 * The current HP is changed by the delta.
	 * @param hpMax The new max hit points.
	 */
	public final void setMaxHP(int hpMax) {
		int delta = hpMax - this.maxHP;
		this.maxHP = hpMax;
		this.hp += delta;
	}

	/** Damages combatant.
	 * 
	 * @param points The points of damage to do.
	 */
	public void damage(int points) {
		// FIRST, apply the damage.
		if (points >= hp) {
			kill();
		} else {
			hp -= points;
			
			// Being damaged alerts you.
			onDamage(points);
		}
	}
	

	/** Mobiles can be killed. */
	public void kill() {
		hp = 0;
		onDeath();
		setVisible(false);
	}

	/** Heals the mobile by the given number of points.
	 * 
	 * @param points the number of points of damage to heal.
	 * @return the number of points of healing done.
	 */
	public int heal(int points) {
		assert hp > 0;
		
		int oldLife = hp;
		hp = Math.min(maxHP, hp + points);
		
		return hp - oldLife;
	}
	
	/** Heals the mobile completely.
	 * @return the number of points of healing done.
	 */
	public int heal() {
		return heal(maxHP);
	}

	/** Revives the mobile with half his life points. */
	public void revive() {
		assert hp == 0;
		hp = maxHP/2;
	}
	
	//-------------------------------------------------------------------------
	// Combat Routines
	
	/** The mobile engages with an enemy, in order to attack it.
	 * 
	 * @param script  The script in which the engagement is animated.
	 * @param location The attacker's location at the time of the attack.
	 * @param enemy The mobile being attacked.
	 * @param attack The details of the attack.
	 * @param colorText  Color text to log.
	 */
	public final void engage(Script script, Cell location, Combatant enemy, Attack attack, String colorText) {
		Engagement engagement = new Engagement(this, attack, enemy, location);
		engagement.animate(script);
		
		if (colorText != null)
			log(colorText);
	}

}
