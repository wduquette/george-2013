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
import rpg.behavior.LadyBugTimid;
import util.Script;

/** GoldBugs are like LadyBugs on steroids: faster, tougher, and stronger,
 * but just as timid.  Plus, they drop treasure.
 * @author will
 */
public class GoldBug extends Monster {
	private static final long serialVersionUID = 1L;

	// Constants
	private static final int RANGE = 12;
	
	// Instance Variables
	private Attack bite = 
			new Attack(meleeSkill(), "bites", new Dice(4,12), Effects.JAWS, null);	

	// Creation
	
	/** Creates the mobile and places it on the board. 
	 */
	public GoldBug() {
		super(24);
		defense.addArmor(5);
		setBehavior(new LadyBugTimid());
		drops.addGold(random.roll(20,50));
		setPosture(Posture.WANDERING);
	}
	
	@Override public String name()         { return "Gold Bug"; }
	@Override public Sprite sprite()       { return Mobiles.GOLD_BUG; }
	@Override public int    level()        { return 4; }
	@Override public int    expAwarded()   { return 10; }
	@Override public int    mp()           { return RANGE; }
	@Override public int    noticeRange()  { return RANGE; }
	@Override public int    restlessness() { return 25; }
	
	/** GoldBugs are hostile once alerted. */
	@Override public boolean isHostile() { return isAlerted(); }


	//-------------------------------------------------------------------------
	// Required Mobile Behavior
		
	@Override
	public void attack(Cell from, Combatant victim, Script script) {
		engage(script, from, victim, bite, "hisses in fear");
	}
}
