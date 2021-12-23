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
import rpg.behavior.RoachAggressive;
import util.Script;

/** The Roach mobile.  Roaches chase enemies naively, wanting to eat them.
 * @author will
 */
public class Roach extends Monster {
	private static final long serialVersionUID = 1L;

	Attack bite = new Attack(meleeSkill(), "bites", new Dice(1,5), Effects.JAWS, null);
	
	// Creation
	
	/** Creates the mobile and places it on the board. 
	 */
	public Roach() {
		super(2);
		defense.addArmor(3);
		setBehavior(new RoachAggressive());

		setPosture(Posture.WANDERING);
	}

	@Override public String name()         { return "Roach"; }
	@Override public Sprite sprite()       { return Mobiles.ROACH; }
	@Override public int    level()        { return 1; }
	@Override public int    expAwarded()   { return 1; }
	@Override public int    mp()           { return 5; }
	@Override public int    noticeRange()  { return 12; }
	@Override public int    restlessness() { return 25; }


	@Override
	public void attack(Cell from, Combatant victim, Script script) {
		engage(script, from, victim, bite, "chitters");
	}
}
