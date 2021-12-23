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

/** This class implements aggressive behavior for roaches and other
 * similar bugs.  The mobile might be wandering or chasing an enemy.
 * If it notices an enemy it will chase it as food unless it is too
 * far off.   Bugs detect prey by smell or some more arcane sense,
 * and have no grasp of spatial geometry.
 * 
 * <b>Assumptions</b>
 * 
 * <ul>
 * <li> Bugs don't need to see their prey.
 * <li> Bugs move in a straight line toward their prey, and can get caught
 * <li> in cul-de-sacs.
 * </ul>
 * 
 * <b>Transition Rules</b><p>
 *
 * <ul>
 * <li>If he is wandering and an enemy is within the notice range, 
 * he starts chasing.
 * 
 * <li>If he is chasing and no enemy is within the notice range, he 
 * starts wandering.
 * </ul><p>
 * 
 * <b>Specific Behaviors</b><p>
 *
 * <ul>
 * <li>If he is wandering, he might move randomly.
 * <li>If he is chasing he closes naively with an enemy and attacks if possible.
 * </ul><p>
 *
 * @author will
 */
public class RoachAggressive implements Behavior, Serializable {
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
		Combatant enemy = mob.getClosestEnemy();
		
		// NEXT, determine the current posture, according to the rules.
		switch (mob.getPosture()) {
		case WANDERING:
			if (enemy != null && mob.distanceTo(enemy) <= mob.noticeRange()) {
				mob.setPosture(Posture.CHASING);
			}
			break;
		case CHASING:
			if (enemy == null || mob.distanceTo(enemy) > mob.chaseRange()) {
				mob.setPosture(Posture.WANDERING);
			}
			break;
		default:
			// TBD: Throw error?
			mob.println("Invalid posture: " + mob.getPosture());
			break;
		}
		
		// NEXT, act according to the current posture.
		switch (mob.getPosture()) {
		case WANDERING:
			mob.wander(mp, mob.restlessness(), script);
			return;
			
		case CHASING:
			mob.onEnemy(enemy);

			Cell here = mob.closeWithEnemyNaively(enemy, mp, script);

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
