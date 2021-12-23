/**
 * 
 */
package rpg.behavior;

import java.io.Serializable;
import java.util.List;

import map.Cell;
import rpg.Combatant;
import rpg.Posture;
import rpg.monster.Monster;
import util.RandomPlus;
import util.Script;

/** This class implements standard aggressive behavior.  The mobile
 * might be sleeping or wandering; if it notices an enemy, it will 
 * chase it and attack unless the enemy is too far off.<p>
 * 
 * <b>Transition Rules</b><p>
 *
 * <ul>
 * <li>If he's asleep and no one is close enough to wake him, he
 * stays asleep.
 * 
 * <li>If he is asleep, and an enemy is visible within the wakeup range,
 * he wakes up and starts chasing.
 * 
 * <li>If he is wandering and an enemy is visible within the notice range, 
 * he starts chasing.
 * 
 * <li>If he is wandering and no enemy is visible, he might go to sleep.
 * 
 * <li>If he is chasing and no enemy is within the chase range, he 
 * either starts wandering or goes to sleep.
 * </ul><p>
 * 
 * <b>Specific Behaviors</b><p>
 *
 * <ul>
 * <li>If he is asleep, he does nothing.
 * <li>If he is wandering, he might move randomly.
 * <li>If he is chasing he closes with an enemy and attacks if possible.
 * </ul><p>
 *
 * @author will
 */
public class GoblinAggressive implements Behavior, Serializable {
	private static final long serialVersionUID = 1L;
	private final RandomPlus random = new RandomPlus();
		
	//-----------------------------------------------------------------------
	// Movement

	/* (non-Javadoc)
	 * @see rpg.behavior.Behavior#move(rpg.Mobile, int, util.Script)
	 */
	@Override
	public void move(Monster mob, int mp, Script script) {
		// FIRST, determine the context.
		Combatant visible = mob.getClosestVisibleEnemy(mob.noticeRange());
		Combatant enemy = mob.getClosestEnemy();
		
		// NEXT, determine the current posture, according to the rules.
		switch (mob.getPosture()) {
		case SLEEPING:
			if (visible != null && mob.distanceTo(visible) <= mob.wakeupRange()) {
				mob.log("wakes up and sees you");
				mob.setPosture(Posture.CHASING);
			}
			break;
		case WANDERING:
			if (visible != null && mob.distanceTo(visible) <= mob.noticeRange()) {
				mob.log("notices you");
				mob.setPosture(Posture.CHASING);
			} else if (random.chance(mob.sleepiness())) {
				mob.setPosture(Posture.SLEEPING);
			}
			break;
		case CHASING:
			if (enemy == null || mob.distanceTo(enemy) > mob.chaseRange()) {
				if (random.chance(mob.sleepiness())) {
					mob.setPosture(Posture.SLEEPING);
				} else {
					mob.setPosture(Posture.WANDERING);
				}
			}
			break;
		default:
			// TBD: Throw error?
			mob.println("Invalid posture: " + mob.getPosture());
			break;
		}
		
		// NEXT, act according to the current posture.
		switch (mob.getPosture()) {
		case SLEEPING:
			return;

		case WANDERING:
			// TBD: it would be nice if it could notice enemies if it
			// wandered within range or sight of them.
			mob.wander(mp, mob.restlessness(), script);
			return;
			
		case CHASING:
			mob.onEnemy(enemy);

			Cell here = mob.closeWithEnemySmartly(enemy, mp, script);

			if (here == null) {
				return;
			}
			
			List<Combatant> enemies = mob.getVisibleEnemies(here,1);

			if (enemies.size() > 0) {
				Combatant victim = random.pickFrom(enemies);
			
				mob.attack(here, victim, script);
			}
			return;
		default:
			// TBD: Throw error?
			mob.println("Invalid posture: " + mob.getPosture());
			return;
		}
	}

	//-----------------------------------------------------------------------
	// Setters/Getters
	
	// None yet
}
