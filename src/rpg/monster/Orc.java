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

/** An Orc.  A big Goblin.
 * @author will
 */
public class Orc extends Monster {
	private static final long serialVersionUID = 1L;

	private Attack attack = 
		    new Attack(meleeSkill(), "hits", new Dice(1,6), Items.SHORT_SWORD, null);


	/** Creates the mobile and places it on the board. 
	 */
	public Orc() {
		// FIRST, initialize the super class.
		super(10);
		defense.addArmor(3);
		
		setBehavior(new GoblinAggressive());
		
		drops.addGold(random.nextInt(20));
		drops.addItemClass(SmallSword.class, 25);
	}
	
	@Override public String name()         { return "Orc"; }
	@Override public Sprite sprite()       { return Mobiles.ORC; }
	@Override public int    level()        { return 2; }
	@Override public int    expAwarded()   { return 2; }
	@Override public int    mp()           { return 6; }
	@Override public int    noticeRange()  { return 10; }
	@Override public int    chaseRange()   { return 12; }
	@Override public int    sleepiness()   { return 10; }
	@Override public int    restlessness() { return 50; }
	
	// This Mobile's behavior
	
	@Override
	public void attack(Cell from, Combatant victim, Script script) {
		engage(script, from, victim, attack, "chuckles evilly");
	}
}
