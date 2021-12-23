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

/** This class implements aggressive behavior for ManlyBugs, which is their
 * default behavior.  ManlyBugs are neutral unless alerted by an attack
 * on themselves or on LadyBugs; then they become hostile, and attack
 * aggressively.  They aren't very smart.
 * 
 * <b>Transition Rules</b><p>
 *
 * <ul>
 * <li>If he is wandering and is alerted, he starts chasing.
 * 
 * <li>If he is chasing and no enemy is within the notice range, he 
 * starts wandering.
 * </ul><p>
 * 
 * <b>Specific Behaviors</b><p>
 *
 * <ul>
 * <li>If he is wandering, he might move randomly.
 * <li>If he is chasing away, he moves naively toward enemies, and will
 *     attack as soon as he's in range.  Although he has a ranged
 *     attack, he'll move to close combat distance ASAP.
 * </ul><p>
 *
 * @author will
 */
public class ManlyBugAggressive implements Behavior, Serializable {
	private static final long serialVersionUID = 1L;
	private final RandomPlus random = new RandomPlus();
	
	//-----------------------------------------------------------------------
	// Movement

	/* (non-Javadoc)
	 * @see rpg.behavior.Behavior#move(rpg.Mobile, int, util.Script)
	 */
	@Override
	public void move(Monster mob, int mp, Script script) {
		// FIRST, is it alerted, and should it be?
		Combatant enemy = mob.getClosestEnemy();
		
		if (mob.isAlerted()) {
			if (enemy == null || mob.distanceTo(enemy) > mob.noticeRange()) {
				mob.setAlerted(false);
			}
		}
		
		// NEXT, determine the current posture, according to the rules.
		switch (mob.getPosture()) {
		case SITTING:
			if (mob.isAlerted()) {
				mob.setPosture(Posture.CHASING);
			}
			break;
		case WANDERING:
			if (mob.isAlerted()) {
				mob.setPosture(Posture.CHASING);
			}
			break;
		case CHASING:
			if (!mob.isAlerted()) {
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
		case SITTING:
			// Just sitting; nothing to do.
			return;
			
		case WANDERING:
			mob.wander(mp, mob.restlessness(), script);
			return;
			
		case CHASING:
			mob.onEnemy(enemy);

			// There's an enemy within range.  Spread the alert.
			mob.alert(script);

			Cell here = mob.closeWithEnemyNaively(enemy, mp, script);
			
			List<Combatant> enemies = mob.getVisibleEnemies(here,1);
			
			if (enemies.size() == 0) {
				enemies = mob.getVisibleEnemies(here, mob.attackRange());
			}
			
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
	
	// None Yet
}
