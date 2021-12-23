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
import rpg.weapon.Dagger;
import util.Script;

/** A Goblin: a small, weak humanoid monster, usually Aggressive.
 * @author will
 *
 */
public class Goblin extends Monster {
	private static final long serialVersionUID = 1L;
	
	private Attack attack = 
	    new Attack(meleeSkill(), "hits", new Dice(1,4), Items.DAGGER, null);

	/** Creates a new goblin-like mobile.
	 */
	public Goblin() {
		super(8);
		defense.addArmor(1);
		setBehavior(new GoblinAggressive());
		
		drops.addGold(random.nextInt(10));
		drops.addItemClass(Dagger.class, 25);
	}

	@Override public String name()         { return "Goblin"; }
	@Override public Sprite sprite()       { return Mobiles.GOBLIN; }
	@Override public int    level()        { return 1; }
	@Override public int    expAwarded()   { return 2; }
	@Override public int    mp()           { return 8; }
	@Override public int    noticeRange()  { return 8; }
	@Override public int    wakeupRange()  { return 5; }
	@Override public int    chaseRange()   { return 12; }
	@Override public int    sleepiness()   { return 10; }
	@Override public int    restlessness() { return 25; }

	@Override
	public void attack(Cell from, Combatant victim, Script script) {
		engage(script, from, victim, attack, "giggles");
	}
}