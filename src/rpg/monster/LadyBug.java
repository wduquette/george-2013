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
import rpg.item.Handkerchief;
import util.Script;

/** LadyBugs won't attack unless cornered.  When alerted, they 
 * avoid enemies naively.  When a LadyBug is attacked, it alerts 
 * its friends.<p>
 * @author will
 */
public class LadyBug extends Monster {
	private static final long serialVersionUID = 1L;

	// Instance Variables
	private Attack bite = 
			new Attack(meleeSkill(), "bites", new Dice(1,8), Effects.JAWS, null);	

	// Creation
	
	/** Creates the mobile and places it on the board. 
	 */
	public LadyBug() {
		super(6);
		defense.addArmor(1);
		setBehavior(new LadyBugTimid());
		
		drops.addItemClass(Handkerchief.class, 50);

		// LadyBugs wander by default.
		setPosture(Posture.WANDERING);
	}
	
	@Override public String name()         { return "Lady Bug"; }
	@Override public Sprite sprite()       { return Mobiles.LADY_BUG; }
	@Override public int    level()        { return 1; }
	@Override public int    expAwarded()   { return 1; }
	@Override public int    mp()           { return 4; }
	@Override public int    noticeRange()  { return 12; }
	@Override public int    restlessness() { return 25; }
	

	//-------------------------------------------------------------------------
	// Required Mobile Behavior
	
	/** LadyBugs are hostile once alerted. */
	@Override public boolean isHostile() { return isAlerted(); }

	/** The LadyBug is alerted if the alert was sent by some other LadyBug
	 * within range.
	 */
	@Override
	public void onAlert(Monster alerter) {
		if (alerter.equals(this)) {
			setAlerted(true);
		} else if (alerter instanceof LadyBug && 
				distanceTo(alerter) <= noticeRange()) 
		{
			setAlerted(true);
		}
	}

	
	@Override
	public void attack(Cell from, Combatant victim, Script script) {
		engage(script, from, victim, bite, "hisses in fear");
	}
}
