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
import rpg.MovementType;
import rpg.Posture;
import rpg.Side;
import rpg.behavior.BatPeaceful;
import util.Script;

/** A Bat.  Bats flutter about and get in the way.
 * @author will
 */
public class Bat extends Monster {
	private static final long serialVersionUID = 1L;

	private Attack attack = 
		    new Attack(meleeSkill(), "bites", new Dice(1,2), Effects.JAWS, null);


	/** The bat's notice range. */ 
	private static final int RANGE = 12;

	// Creation
	
	/** Creates the mobile and places it on the board. 
	 */
	public Bat() {
		// FIRST, initialize the super class.
		super(5);

		setBehavior(new BatPeaceful());
		
		setPosture(Posture.SLEEPING);
				
		// Bats are hard to hit.
		defense.addArmor(5);
	}

	@Override public String       name()         { return "Bat"; }
	@Override public Sprite       sprite()       { return Mobiles.BAT; }
	@Override public MovementType movementType() { return MovementType.FLYING; }
	@Override public Side         side()         { return Side.MONSTER; }
	@Override public int          level()        { return 1; }
	@Override public int          mp()           { return 8; }
	@Override public int          noticeRange()  { return RANGE; }
	@Override public int          wakeupRange()  { return 2; }
	@Override public int          sleepiness()   { return 25; }
	@Override public int          restlessness() { return 100; }
	@Override public int          focus()        { return 50; }
	

	//-------------------------------------------------------------------------
	// Required Mobile Behavior

	/** Bats are hostile once alerted. */
	@Override public boolean isHostile() { return isAlerted(); }


	/** The bat is alerted if the alert was sent by some other bat
	 * within range.
	 */
	@Override
	public void onAlert(Monster alerter) {
		if (alerter.equals(this)) {
			setAlerted(true);
		} else if (alerter instanceof Bat && distanceTo(alerter) <= RANGE) {
			setAlerted(true);
		}
	}

	@Override
	public void attack(Cell from, Combatant victim, Script script) {
		engage(script, from, victim, attack, "squeaks inaudibly");
	}


}
