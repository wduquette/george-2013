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
import rpg.behavior.GoblinAggressive;
import util.Script;

/** The Giant Rat mobile.  Rats chase enemies naively, wanting to eat them.
 * @author will
 */
public class GiantRat extends Monster {
	private static final long serialVersionUID = 1L;

	Attack bite = new Attack(meleeSkill(), "bites", new Dice(1,6), Effects.JAWS, null);
	Attack claw = new Attack(meleeSkill(), "claws", new Dice(1,4), Effects.CLAW, null);
	

	// Creation
	
	/** Creates the mobile and places it on the board. 
	 */
	public GiantRat() {
		super(20);
				
		defense.addArmor(4);
		
		setBehavior(new GoblinAggressive());
	}

	@Override public String name()         { return "Giant Rat"; }
	@Override public Sprite sprite()       { return Mobiles.GIANT_RAT; }
	@Override public int    level()        { return 5; }
	@Override public int    expAwarded()   { return 10; }
	@Override public int    mp()           { return 5; }
	@Override public int    noticeRange()  { return 8; }
	@Override public int    wakeupRange()  { return 5; }
	@Override public int    sleepiness()   { return 30; }
	@Override public int    restlessness() { return 40; }

	
	// This Mobile's behavior
	@Override
	public void attack(Cell from, Combatant victim, Script script) {
		engage(script, from, victim, bite, "slavers");
		if (random.nextInt(100) < 50)
			engage(script, from, victim, claw, "roars");
	}
}
