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

/** This class implements timid behavior for LadyBugs, which is their
 * default behavior.  LadyBugs are neutral unless alerted by an attack
 * on themselves or other LadyBugs; then they become hostile, and attempt
 * to run away.  They aren't very smart.
 * 
 * <b>Transition Rules</b><p>
 *
 * <ul>
 * <li>If she is wandering and is alerted, she starts running away.
 * 
 * <li>If she is running away and no enemy is within the notice range, she 
 * starts wandering.
 * </ul><p>
 * 
 * <b>Specific Behaviors</b><p>
 *
 * <ul>
 * <li>If she is wandering, she might move randomly.
 * <li>If she is running away, she moves naively away from enemies, and will
 *     attack only if cornered.
 * </ul><p>
 *
 * @author will
 */
public class LadyBugTimid implements Behavior, Serializable {
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
				mob.setPosture(Posture.RUNNING_AWAY);
			}
			break;
		case WANDERING:
			if (mob.isAlerted()) {
				mob.setPosture(Posture.RUNNING_AWAY);
			}
			break;
		case RUNNING_AWAY:
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
			
		case RUNNING_AWAY:
			mob.onEnemy(enemy);
			
			// Alert other LadyBugs.
			mob.alert(script);

			Cell here = mob.runAwayNaively(enemy, mp, script);

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
