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

/** This class implements peaceful behavior for Bats, which is their
 * default behavior.  Bats are neutral unless alerted by an attack
 * on themselves or on other Bats; then they become hostile, and attack
 * aggressively.  They aren't very smart.
 * 
 * <b>Transition Rules</b><p>
 *
 * <ul>
 * <li>If it is sleeping, it might wake up.  If it wakes up and is alerted,
 *     it starts chasing; otherwise it starts wandering.
 *     
 * <li>If it is wandering and is alerted, he starts chasing.  Otherwise,
 *     it might go to sleep.
 * 
 * <li>If it is chasing and no enemy is within the notice range, it 
 * either starts wandering or falls asleep.
 * </ul><p>
 * 
 * <b>Specific Behaviors</b><p>
 *
 * <ul>
 * <li>If it is wandering, it moves randomly.
 * <li>If it is chasing, its moves naively and erratically toward enemies, 
 *     and will attack as soon as it's in range.
 * </ul><p>
 *
 * @author will
 */
public class BatPeaceful implements Behavior, Serializable {
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
		case SLEEPING:
			if (enemy != null && mob.distanceTo(enemy) <= mob.wakeupRange()) {
				if (mob.isAlerted()) {
					mob.setPosture(Posture.CHASING);
				} else {
					mob.setPosture(Posture.WANDERING);
				}
			}
			break;
		case WANDERING:
			if (mob.isAlerted()) {
				mob.setPosture(Posture.CHASING);
			} else if (random.chance(mob.sleepiness())) {
				mob.setPosture(Posture.SLEEPING);
			}
			break;
		case CHASING:
			if (!mob.isAlerted()) {
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
			// Nothing to do.
			return;
			
		case WANDERING:
			mob.wander(mp, mob.restlessness(), script);
			return;
			
		case CHASING:
			mob.onEnemy(enemy);

			// There's an enemy within range.  Spread the alert.
			mob.alert(script);

			Cell here = mob.closeWithEnemyErratically(enemy, mp, mob.focus(), script);
			
			List<Combatant> enemies = mob.getVisibleEnemies(here,1);
			
			if (enemies.size() == 0 && mob.attackRange() > 1) {
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
	
	// None, at present.
}
