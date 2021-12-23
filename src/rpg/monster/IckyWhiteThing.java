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
import rpg.behavior.IckyAggressive;
import util.Script;

/** The Rat mobile.  Rats chase enemies naively, wanting to eat them.
 * @author will
 */
public class IckyWhiteThing extends Monster {
	private static final long serialVersionUID = 1L;

	Attack slime = new Attack(meleeSkill(), "slimes", new Dice(1,3), Effects.SLIME, null);
	
	// Creation
	
	/** Creates the mobile and places it on the board. 
	 */
	public IckyWhiteThing() {
		// FIRST, initialize the super class.
		super(4);
		
		setBehavior(new IckyAggressive());
		
		setPosture(Posture.WANDERING);
		
		// Slimes are hard to injure.
		defense.addArmor(4);
	}

	@Override public String name()         { return "Icky White Thing"; }
	@Override public Sprite sprite()       { return Mobiles.WHITE_SLIME; }
	@Override public int    level()        { return 1; }
	@Override public int    expAwarded()   { return 1; }
	@Override public int    mp()           { return 5; }
	@Override public int    noticeRange()  { return 6; }
	@Override public int    focus()        { return 50; }


	//-------------------------------------------------------------------------
	// Required Mobile Behavior

	
	// This Mobile's behavior
	@Override
	public void attack(Cell from, Combatant victim, Script script) {
		engage(script, from, victim, slime, "leaves a trail");
	}


}
