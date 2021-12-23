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
import rpg.behavior.RatAggressive;
import util.Script;

/** The Rat mobile.  Rats chase enemies naively, wanting to eat them.
 * @author will
 */
public class Rat extends Monster {
	private static final long serialVersionUID = 1L;

	Attack bite = new Attack(meleeSkill(), "bites", new Dice(1,2), Effects.JAWS, null);
	Attack claw = new Attack(meleeSkill(), "claws", new Dice(1,2), Effects.CLAW, null);
	
	// Creation
	
	/** Creates the mobile and places it on the board. 
	 */
	public Rat() {
		super(6);
		defense.addArmor(0);
		
		setBehavior(new RatAggressive());
	}

	@Override public String name()         { return "Rat"; }
	@Override public Sprite sprite()       { return Mobiles.RAT; }
	@Override public int    level()        { return 1; }
	@Override public int    expAwarded()   { return 1; }
	@Override public int    mp()           { return 8; }
	@Override public int    noticeRange()  { return 8; }
	@Override public int    wakeupRange()  { return 5; }
	@Override public int    sleepiness()   { return 75; }
	@Override public int    restlessness() { return 75; }
	
	// This Mobile's behavior
	@Override
	public void attack(Cell from, Combatant victim, Script script) {
		engage(script, from, victim, bite, "drools");
		if (random.nextInt(100) < 50)
			engage(script, from, victim, claw, "squeaks");
	}

}
