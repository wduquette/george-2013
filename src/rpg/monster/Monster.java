/**
 * 
 */
package rpg.monster;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import map.Cell;
import rpg.Combatant;
import rpg.EventType;
import rpg.Gold;
import rpg.Item;
import rpg.Posture;
import rpg.Side;
import rpg.behavior.Behavior;
import util.Script;
import util.ScriptAction;
import util.StringsTable;

/** A Monster, i.e., a Combatant mobile that a PlayerCharacter can fight
 * and kill for experience.
 * @author will
 *
 */
abstract public class Monster extends Combatant {
	private static final long serialVersionUID = 1L;

	/** The strings table. */
	private static StringsTable strings;

	static {
		// FIRST, loads the strings.
		strings = new StringsTable(Monster.class,"Monster.strings");
	}

	//------------------------------------------------------------------------
	// Instance variables
	
	/** Use the drops manager to control what the monster drops when 
	 * killed.  It is public so that regions can give special requirements
	 * to particular monsters over and above what their class has them 
	 * drop.
	 */
	public DropsManager drops = new DropsManager();

	/** The monster's current posture, as determined and interpreted
	 * by its behavior.
	 */
	private Posture posture = Posture.SLEEPING;
	
	/** The monster's current behavior. */
	private Behavior behavior = null;
	
	/** Alert Flag: true if the monster is in a state of alert, and false
	 * otherwise.  
	 * @see #alert
	 * @see #relax
	 * @see #onAlert
	 */
	private boolean alerted = false;
	
	//------------------------------------------------------------------------
	// Creation
	
	/** Creates a new monster.
	 * @param hp Its initial hit points.
	 */
	public Monster(int hp) {
		super(hp);
	}
	
	//-------------------------------------------------------------------------
	// Attribute Methods
	
	/** Reads the monster's description from the Monsters.strings table
	 * as <i>key</i>.description, and returns it.  If there is no such 
	 * description, the default description is used.  The monster's
	 * key is its name, in lower case with spaces replaced by underscores. 
	 */
	@Override
	public String description() {
		// FIRST, get the monster's key
		String key = name().toLowerCase().replace(' ', '_');
		
		String tag = key + ".description";
		String text = strings.get(tag);
		
		if (text == null) {
			return "TBD: " + super.description();
		}
		
		if (isAsleep()) {
			return text + "\nIt appears to be asleep.";
		} else {
			return text;
		}
	}

	@Override public Side side() { return Side.MONSTER; }
	
	/** Computes and returns the monster's melee combat skill, required
	 * for basic combat.  The equation here is taken from the Angband
	 * "mon-blow" spoiler; it's the basic melee combat skill for 
	 * monsters.
	 * 
	 * The skill level is used with the RPG.chanceToHit() routine to
	 * determine the probability of hitting a defender in melee combat.
	 * @return the skill level.
	 */
	public final int meleeSkill() {
		return 60 + 3*level();
	}
	
	
	/** @return the number of movement points the monster gets during
	 * combat. */
	public int mp() { 
		return 8; 
	}
	
	/** @return the maximum distance from which the monster can attack.
	 * Defaults to 1. */
	public int attackRange() {
		return 1;
	}
	
	//-------------------------------------------------------------------------
	// Behavior Attributes
	//
	// These attributes affect how behaviors operate.  Any given behavior
	// is free to use, override, or ignore these attributes.  In general,
	// Monster methods should not use these directly.  For example, the
	// closeWithEnemyErratically method takes focus as an input; this 
	// allows the behavior to override the monster's natural restlessness.
	
	/** @return the monster's focus, a number from 0 to 100 indicating how
	 * likely the monster is to move directly toward its current goal.
	 * A focus of 100 means the monster never strays; a focus of 0 means
	 * that the monster is hopelessly scatter-brained, and will only
	 * reach its goal by accident.<p>
	 * 
	 * By default, monsters have perfect focus.
	 */
	public int focus() {
		return 100;
	}
	
	/** @return the monster's restlessness, a number from 0 to 100 
	 * indicating how likely the monster is to move randomly when it has
	 * no particular goal.  By default, restlessness is 50.
	 */
	public int restlessness() {
		return 50;
	}
	
	/** @return the monster's sleepiness, a number from 0 to 100
	 * indicating how likely the monster is to fall asleep when it
	 * has no particular goal.  Monsters are not sleepy by default.
	 */
	public int sleepiness() {
		return 0;
	}
	
	/** @return the monster's notice range, the maximum distance at which
	 * it will notice an enemy when awake, using its normal senses.
	 */
	public int noticeRange() {
		return 8;
	}
	
	/** @return the monster's chase range, the maximum distance at which
	 * it will chase a noticed enemy.  Defaults to the notice range.
	 */
	public int chaseRange() {
		return noticeRange();
	}
	
	/** @return the monster's wakeup range, the maximum distance at which
	 * it will notice an enemy while asleep (and presumably wake up).
	 * Defaults to the notice range.
	 */
	public int wakeupRange() {
		return noticeRange();
	}
		
	//-------------------------------------------------------------------------
	// Behavioral Methods
	
	/** Make the mobile move in combat.  Goblinoids wake up when an
	 * enemy moves into range, and after that chase enemies smartly.
	 */
	@Override
	public boolean move(Script script) {
		
		if (behavior != null) {
			// FIRST, get the movement points.
			int mp = 1;

			if (region().inCombat()) {
				mp = mp();
			}

			behavior.move(this, mp, script);
		}
		
		return true;
	}
	

	
	/** The monster scripts its engagement with the enemy using its attacks.
	 * 
	 * @param from The cell the monster is attacking from.
	 * @param victim The enemy to attack
	 * @param script The script to put the steps in.
	 */
	public void attack(Cell from, Combatant victim, Script script) {
		// TBD: Do nothing
	}
	
	//-------------------------------------------------------------------------
	// Setters/Getters
	
	/**
	 * @return the behavior
	 */
	public Behavior getBehavior() {
		return this.behavior;
	}

	/** Sets the monster's behavior.  This is usually done by the
	 * monster itself, but can be done by the region as well.
	 * @param behavior the behavior to set
	 */
	public void setBehavior(Behavior behavior) {
		this.behavior = behavior;
	}
	
	/** @return the mobile's posture
	 */
	public final Posture getPosture() {
		return posture;
	}

	/** Set the mobile's posture.  It will take effect on the mobile's
	 * next move.
	 * @param posture the posture to set
	 */
	public void setPosture(Posture posture) {
		this.posture = posture;
	}
	
	/** @return true if the monster is asleep, and false otherwise. */
	public boolean isAsleep() {
		return posture == Posture.SLEEPING;
	}

	/** @return true if the monster is awake, and false otherwise. */
	public boolean isAwake() {
		return posture != Posture.SLEEPING;
	}


	//-------------------------------------------------------------------------
	// Movement Routines

	/** The combatant moves to close with an enemy, stepping smartly, so as 
	 * to prepare for an attack.  Movement ends when the combatant is adjacent
	 * to an enemy or all movements points have been expended.<p>
	 * 
	 * The routine returns the location at the end of the movement,
	 * only if there is an adjacent enemy.  If the combatant is not
	 * in contact with an enemy, returns null.
	 * 
	 * @param enemy The enemy to close with.
	 * @param mp The remaining movement points.
	 * @param script The script to receive the movement.
	 * @return The final cell of the movement, or null.
	 */
	public final Cell closeWithEnemySmartly(Combatant enemy, int mp, Script script) {
		Cell here = place();

		if (canSeeEnemyInRange(here,1)) {
			return here;
		}
		
		while (mp-- > 0) {
			// FIRST, try to move toward the enemy.  If we can't move, we're
			// done.
			here = stepSmartly(here, enemy.place());
			
			if (here == null) {
				// We aren't next to an enemy, and we can't move any
				// closer.
				return null;
			}
			
			moveTo(script, here);

			// NEXT, is the spot next to an enemy?  We're here; end
			// the move.
			if (canSeeEnemyInRange(here,1)) {
				return here;
			}
		}

		// Expended all movement points; not next to enemy.
		return null;
	}

	/** The combatant moves to close with an enemy, stepping naively, so as 
	 * to prepare for an attack.  Movement ends when the combatant is adjacent
	 * to an enemy or all movements points have been expended.<p>
	 * 
	 * The routine returns the location at the end of the movement.
	 * 
	 * @param enemy The enemy to close with.
	 * @param mp The remaining movement points.
	 * @param script The script to receive the movement.
	 * @return The final cell of the movement.
	 */
	public final Cell closeWithEnemyNaively(Combatant enemy, int mp, Script script) {
		Cell here = place();

		if (canSeeEnemyInRange(here,1)) {
			return here;
		}
		
		while (mp-- > 0) {
			// FIRST, try to move toward the enemy.  If we can't move, we're
			// done.
			Cell next = stepNaively(here, enemy.place());
			
			if (next == null) {
				// We aren't next to an enemy, and we can't move any
				// closer.
				return here;
			}
			
			here = next;
			moveTo(script, here);

			// NEXT, is the spot next to an enemy?  We're here; end
			// the move.
			if (canSeeEnemyInRange(here,1)) {
				return here;
			}
		}

		// Expended all movement points; not next to enemy.
		return here;
	}

	/** The combatant moves to close with an enemy, stepping smartly but
	 * erratically: depending on the focus, it will sometimes move randomly.  
	 * Movement ends when the combatant is adjacent
	 * to an enemy or all movements points have been expended.<p>
	 * 
	 * The routine returns the location at the end of the movement.
	 * 
	 * @param enemy The enemy to close with.
	 * @param mp The remaining movement points.
	 * @param script The script to receive the movement.
	 * @param focus The likelihood it will move toward the enemy.
	 * @return The final cell of the movement, or null.
	 */
	public final Cell closeWithEnemyErratically(Combatant enemy, int mp, int focus, Script script) {
		Cell here = place();

		if (canSeeEnemyInRange(here,1)) {
			return here;
		}
		
		while (mp-- > 0) {
			// FIRST, try to move toward the enemy.  If we can't move, we're
			// done.
			Cell next;
			
			if (random.chance(focus)) {
				next = stepSmartly(here, enemy.place());
			} else {
				next = stepRandomly(here);
			}
			
			if (next == null) {
				// We aren't next to an enemy, and we can't move any
				// closer.
				return here;
			}
			
			here = next;
			moveTo(script, here);

			// NEXT, is the spot next to an enemy?  We're here; end
			// the move.
			if (canSeeEnemyInRange(here,1)) {
				return here;
			}
		}

		// Expended all movement points; not next to enemy.
		return here;
	}
	
	/** The combatant runs away from the closest enemy, stepping naively.  
	 * Movement ends when the combatant can't move farther away from
	 * the enemy  or all movement points have been expended.<p>
	 * 
	 * The routine returns the location at the end of the movement.
	 * 
	 * @param enemy The enemy to run away from.
	 * @param mp The remaining movement points.
	 * @param script The script to receive the movement.
	 * @return The final cell of the movement.
	 */
	public final Cell runAwayNaively(Combatant enemy, int mp, Script script) {
		Cell here = place();

		while (mp-- > 0) {
			// FIRST, try to move toward the enemy.  If we can't move, we're
			// done.
			Cell next = stepAwayNaively(here, enemy.place());
			
			if (next == null) {
				return here;
			}

			here = next;
			moveTo(script, here);
		}

		// Expended all movement points.
		return here;
	}




	//-------------------------------------------------------------------------
	// Events
	
	/** When an enemy is noticed by the monster (or, more typically, by the
	 * monster's behavior.
	 * @param enemy The noticed enemy.
	 */
	public void onEnemy(Combatant enemy) {
		callListeners(EventType.ENEMY);
	}
	
	/** When a monster is damaged, it alerts itself by default. */
	@Override
	public void onDamage(int points) {
		onAlert(this);
	}
	
	//------------------------------------------------------------------------
	// Alerts
	
	
	/** A monster calls this routine to issue an alert to
	 * all of its friends.  Other monsters might or might not
	 * respond.
	 * @param script The script to receive the alert action.
	 */
	public final void alert(Script script) {
		script.add(new AlertFriends(this));
	}

	/** This routine is called to alert the monster of enemy
	 * action; the monster's response depends on the subclass.  By
	 * default, monsters don't care; however, specific monsters can
	 * decide whether and when to be alerted, and the monster's 
	 * behavior can change.
	 * @param source The monster issuing the alert
	 */
	public void onAlert(Monster source) {
		callListeners(EventType.ALERT);
	}
	
	
	/** @return the alerted flag.
	 */
	public boolean isAlerted() {
		return this.alerted;
	}

	/** Set the alerted flag.  The monster can call this in response to
	 * being alerted, or for any other reason.
	 * @param alerted the value to set
	 */
	public void setAlerted(boolean alerted) {
		this.alerted = alerted;
	}

	/** Potentially alerts friends of the mobile whose 
	 * script this is.
	 *
	 * @author will
	 */
	protected final class AlertFriends implements ScriptAction {
		private Monster alerter;
		
		AlertFriends(Monster alerter) {
			this.alerter = alerter;
		}
		
		public void execute() {
			println("alerting friends of " + alerter);
			for (Monster m : region().getMonsters()) {
				if (!m.equals(alerter) && m.isFriendOf(alerter))
					m.onAlert(alerter);
			}
		}
	}
	
	@Override
	public void kill() {
		super.kill();
		
		println("doDrops");
		drops.doDrops();
	}
	
	//------------------------------------------------------------------------
	// Item Drops Manager
	
	protected class DropsManager implements Serializable {
		private static final long serialVersionUID = 1L;

		
		private class Drop implements Serializable {
			private static final long serialVersionUID = 1L;

			Class<? extends Item> itemClass;
			int chance;
			
			Drop(Class<? extends Item> itemClass, int chance) {
				this.itemClass = itemClass;
				this.chance = chance;
			}
			
			void dropIt() {
				if (random.roll(1,100) <= chance) {
					Item item = null;
					try {
						item = itemClass.newInstance();
					} catch (Exception e) {
						println("Error while trying to drop item: " + e);
					}
					dropItem(item);
				}
			}
		}
		
		// Instance Variables
		private List<Item> items = new ArrayList<>();
		private List<Drop> drops = new ArrayList<>();

		/** Adds a specific item that the monster will drop when killed.
		 * 
		 * @param item The item.
		 */
		public void addItem(Item item) {
			items.add(item);
		}
		
		/** Adds an item class; the monster will drop an item of this class
		 * a certain percentage of the time.
		 * @param itemClass The item class
		 * @param chance The percentage chance.
		 */
		public void addItemClass(Class<? extends Item> itemClass, int chance) {
			drops.add(new Drop(itemClass, chance));
		}
		
		/** The amount of gold the monster will drop. It's up to the 
		 * monster class to roll a random amount, if that's desired.
		 * @param gold The amount of gold.
		 */
		public void addGold(int gold) {
			items.add(new Gold(gold));
		}
		
		void doDrops() {
			for (Drop drop : drops) {
				drop.dropIt();
			}
			
			for (Item item : items) {
				dropItem(item);
			}
		}
		
		void dropItem(Item item) {
			if (region().dropItem(place(), item)) {
				log("Dropped " + item);
			} else {
				log("Could not drop the " + item + "; no room!");
			}
		}
	}
}
