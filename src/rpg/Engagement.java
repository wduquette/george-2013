/**
 * 
 */
package rpg;

import graphics.Effects;
import graphics.Items;
import graphics.Sprite;

import java.util.List;

import rpg.pc.PlayerCharacter;

import map.Cell;
import map.TerrainGrid;
import util.Script;
import util.ScriptAction;
import app.Board;


/** This object packages up an engagement between an attacker and a defender.
  * @author will
 *
 */
public final class Engagement {
	private enum Outcome {
		MISS,
		DEFEND,
		HIT,
		CRITICAL
	}
	
	// Instance variables
	
	/** The attacker's location at the time of the attack. */
	private final Cell location;
	
	/** The mobile that is attacking. */
	private final Combatant attacker;
	
	/** The details of the attacker's attack. */
	private final Attack attack;
	
	/** The mobile that is defending. */
	private final Combatant defender;
	
	/** The nature of the outcome. */
	private Outcome outcome;
	
	/** The points of damage actually done. */
	private int actual = 0;
	
	// Constructor

	/** Creates a new Damage record.
	 * 
	 * @param attacker The mobile inflicting the damage
	 * @param attack The details of the attack
	 * @param defender The mobile receiving the damage
	 * @param location The attack's location at the time of the attack.
	 */
	public Engagement(Combatant attacker, Attack attack, Combatant defender, Cell location) {
		/** FIRST, get the data. */
		this.attacker = attacker;
		this.attack = attack;
		this.defender = defender;
		this.location = location;
	}
	
	/** Animates the engagement into the given script.
	 * 
	 * @param script The animation script.
	 */
	public void animate(Script script) {
		// FIRST, animate the track of the bullet, if any.
		Cell here;
		
		// TBD: Require location not null; this is an unnecessary complication.
		
		if (location == null)
			here = attacker.place();
		else
			here = location;
		
		Cell target = defender.place();
		
		int dist = here.diagonal(target);

		// If combatants aren't adjacent, animate a ranged attack.
		if (dist > 1) {
			// FIRST, get a line of sight between the two.
			List<Cell> los = TerrainGrid.getLine(here, target);
			
			// NEXT, animate the bullet.
			for (int i = 0; i < los.size() - 1; i++) {
				script.add(new ShowEffectAnimation(los.get(i), attack.bullet()));
			}
		}
		
		// NEXT, figure out what happens.
		outcome = determineOutcome();
		attacker.println("Engagement outcome = " + outcome + ", damage=" + actual);

		// NEXT, if he hit, show the hit effect.
		ScriptAction anim = null;
		
		if (actual > 0) {
			anim = new ShowEffectAnimation(target, attack.hit());
		} else if (outcome == Outcome.MISS) {
			anim = new ShowEffectAnimation(target, Effects.SWOOSH);
		} else {
			anim = new ShowEffectAnimation(target, Items.PLATE_SHIELD);
		}
		
		// Add it several times, so it doesn't go by too quickly.
		// (Should have an optional duration on ScriptAction!
		script.add(anim);
		script.add(anim);
		script.add(anim);
		script.add(anim);
		script.add(anim);
		script.add(anim);
		script.add(anim);
		script.add(anim);
		
		// NEXT, apply the result.
		script.add(new ScriptAction() {
			public void execute() {
				Board.get().clearEffect();
				
				switch (outcome) {
				case MISS:
					attacker.log("misses " + defender + " entirely.");
					break;
				case DEFEND:
					defender.log("defends against " + attacker + "'s attack.");
					break;
				case HIT:
					attacker.log(attack.verb() + " " + defender + " for " +
							actual + " points of damage.");
					defender.damage(actual);
					break;
				case CRITICAL:
					Board.get().log("Critical Hit!");
					attacker.log(attack.verb() + " " + defender + " for " +
							actual + " points of damage.");
					defender.damage(actual);
					break;
				}
				
				// NEXT, was the defender killed?
				if (!defender.isAlive()) {
					Board.get().log(defender + " is dead.");
					
					// If the attacker is a PC, award experience to the
					// party.
					if (attacker instanceof PlayerCharacter) {
						Board.get().party.award(defender.expAwarded());
					}
				}
			}
		});
	}
	
	private Outcome determineOutcome() {
		// FIRST, get the chance to hit, and roll against it.
		int chance = RPG.chanceToHit(attack.skill(), defender.defense().ac());
		int roll = RPG.roll(100);
		
		Board.get().println(attacker + " attacks " + defender);
		Board.get().println("attack skill = " + attack.skill());
		Board.get().println("defense AC = " + defender.defense().ac());
		Board.get().println("chance to hit = " + chance);
		Board.get().println("roll = " + roll);

		// NEXT, if the roll > 95, it's an outright miss.
		if (roll > 95) {
			return Outcome.MISS;
		}
		
		// NEXT, did the defender successfully defend?
		if (roll > chance && roll > 5) {
			return Outcome.DEFEND;
		}
		
		// NEXT, if the roll is <= 5, that's a critical.  We do max
		// damage.
		if (roll <= 5) {
			actual = attack.maxDamage();
			
			return Outcome.CRITICAL;
		} else {
			actual = attack.damage();
			
			return Outcome.HIT;
		}
	}
			
	/** TBD: Possibly this should still be implemented in Board.
	 * Consider a "showEffect(script, sprite, cell)" command to add
	 * it to the script.
	 * @author will
	 *
	 */
	private class ShowEffectAnimation implements ScriptAction {
		private Cell cell;
		private Sprite sprite;
		
		/** Action: show an attack icon at a particular cell
		 * 
		 * @param cell The cell
		 * @param sprite the sprite
		 */
		public ShowEffectAnimation(Cell cell, Sprite sprite) {
			this.cell = cell;
			this.sprite = sprite;
		}
		
		/** Show the attack */
		public void execute() {
			Board.get().setEffect(sprite, cell);
		}
	}
}
