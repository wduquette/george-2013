/**
 * 
 */
package rpg.monster;

import graphics.Items;
import graphics.Mobiles;
import graphics.Sprite;
import map.Cell;
import rpg.Attack;
import rpg.Combatant;
import rpg.Dice;
import rpg.behavior.GoblinAggressive;
import rpg.weapon.SmallSword;
import util.Script;

/** The Ogre mobile.  The Ogres are dumb but tough.
 * @author will
 */
public class Ogre extends Monster {
	private static final long serialVersionUID = 1L;

	private Attack attack = 
		    new Attack(meleeSkill(), "hits", new Dice(1,6, 2), Items.SHORT_SWORD, null);

	// Creation
	
	/** Creates the mobile and places it on the board. 
	 */
	public Ogre() {
		// FIRST, initialize the super class.
		super(12);
		defense.addArmor(4);
		
		setBehavior(new GoblinAggressive());
		
		drops.addGold(random.nextInt(20));
		drops.addItemClass(SmallSword.class, 25);
	}

	@Override public String name()         { return "Ogre"; }
	@Override public Sprite sprite()       { return Mobiles.OGRE; }
	@Override public int    level()        { return 4; }
	@Override public int    expAwarded()   { return 4; }
	@Override public int    mp()           { return 5; }
	@Override public int    noticeRange()  { return 10; }
	@Override public int    wakeupRange()  { return 6; }
	@Override public int    chaseRange()   { return 100; }
	@Override public int    sleepiness()   { return 5; }
	@Override public int    restlessness() { return 75; }


	//-------------------------------------------------------------------------
	// Required Mobile Behavior

	@Override
	public void attack(Cell from, Combatant victim, Script script) {
		engage(script, from, victim, attack, "bellows in rage");
	}
}
