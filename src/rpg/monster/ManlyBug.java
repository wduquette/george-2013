/**
 * 
 */
package rpg.monster;

import graphics.Effects;
import graphics.Mobiles;
import graphics.Sprite;
import map.Cell;
import rpg.Attack;
import rpg.Combatant;
import rpg.Dice;
import rpg.Posture;
import rpg.behavior.ManlyBugAggressive;
import rpg.item.Spittoon;
import util.Script;

/** ManlyBugs don't attack unless they are alerted.  A ManlyBug is
 * alerted if it is damaged, and if it is alerted by a 
 * LadyBug.<p>
 * 
 * ManlyBugs can bite, and can spit from a distance of four cells.
 * 
 * @author will
 */
public class ManlyBug extends Monster {
	private static final long serialVersionUID = 1L;

	// Instance Variables
	private Attack bite
		= new Attack(meleeSkill(), "bites", new Dice(1,4,3), Effects.JAWS, null);
	private Attack spit
		= new Attack(meleeSkill(), "spits at", new Dice(1,3), 
					 Effects.TARGET, Effects.BULLET);

	// Creation
	
	/** Creates the mobile and places it on the board. 
	 */
	public ManlyBug() {
		// FIRST, initialize the super class.
		super(6);
				
		// TBD: Make monster AC an attribute.
		defense.addArmor(2);

		setBehavior(new ManlyBugAggressive());
		setPosture(Posture.WANDERING);

		drops.addItemClass(Spittoon.class, 25);
	}

	@Override public String name()         { return "Manly Bug"; }
	@Override public Sprite sprite()       { return Mobiles.MANLY_BUG; }
	@Override public int    level()        { return 2; }
	@Override public int    expAwarded()   { return 2; }
	@Override public int    mp()           { return 4; }
	@Override public int    attackRange()  { return 4; }
	@Override public int    noticeRange()  { return 12; }
	@Override public int    restlessness() { return 15; }

	/** ManlyBugs are hostile once alerted. */
	@Override public boolean isHostile() { return isAlerted(); }

	//-------------------------------------------------------------------------
	// Required Mobile Behavior
	
	/** The ManlyBug is alerted if the alert was sent by some LadyBug
	 * within range.
	 */
	public void onAlert(Monster alerter) {
		if (alerter.equals(this)) {
			setAlerted(true);
		} else if (alerter instanceof LadyBug && 
			distanceTo(alerter) <= noticeRange()) {
			setAlerted(true);
		}
	}

	@Override
	public void attack(Cell from, Combatant victim, Script script) {
		if (distanceTo(victim) == 1) {
			engage(script, from, victim, bite, "hisses in rage");
		} else {
			engage(script, from, victim, spit, "hawks a big one");
		}
	}

}
