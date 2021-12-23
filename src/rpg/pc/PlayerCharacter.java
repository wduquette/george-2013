/**
 * 
 */
package rpg.pc;

import graphics.Effects;
import graphics.Mobiles;
import graphics.Sprite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import map.Cell;
import rpg.Attack;
import rpg.Combatant;
import rpg.Condition;
import rpg.Dice;
import rpg.Feature;
import rpg.Gold;
import rpg.HandWeapon;
import rpg.Item;
import rpg.Mobile;
import rpg.Side;
import rpg.Slot;
import rpg.Weapon;
import util.Script;
import util.StringsTable;
import app.Board;

/** A PlayerCharacter is a player character: a mobile moved by the user.  
 * The class is laid out according to functional area.  Constants and Instance 
 * Variables are listed in the appropriate area.
 * @author will
 */
public abstract class PlayerCharacter extends Combatant {
	private static final long serialVersionUID = 1L;
	
	/** The strings table. */
	private static StringsTable strings;

	static {
		// FIRST, loads the strings.
		strings = new StringsTable(PlayerCharacter.class,"PlayerCharacter.strings");
	}


	//------------------------------------------------------------------------
	// Character Stats and Creation
	
	// Major Components
	
	/** The character's statistics, which will evolve over time. */
	public final Stats stats;

	/** The character's equipment. */
	public final Equipment equipment = new Equipment();
	
	/** The character's inventory. */
	public final Inventory inventory = new Inventory();
	
	/** The character's fist, which he'll use if he has no other weapon. */
	public final HandWeapon FIST = new Fist();
	
	// Other instance variables
	
	/** The player character's current mana. */
	private int mana;
	
	/** Creates a new player character, giving him his stats.
	 * @param myclass The class to which he belongs
	 * @param myclass The PC's class
	 * @param str The PC's initial STRength
	 * @param dex The PC's initial DEXterity
	 * @param wis The PC's initial WISdom
	 * @param con The PC's initial CONstitution
	 */
	public PlayerCharacter(CharacterClass myclass, int str, 
			int dex, int wis, int con) {
		super(0);
		this.stats = new Stats(myclass, str, dex, wis, con);
		
		setMaxHP(stats.hpMax());
		mana = stats.manaMax();
	}
	
	//------------------------------------------------------------------------
	// Attribute Methods
	
	@Override public String name() { 
		return strings.get(key() + ".name").trim(); 
	}
	
	@Override public String description() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(strings.get(key() + ".description"));
		
		for (Condition cond : Condition.values()) {
			if (hasCondition(cond)) {
				sb.append("\n\n" + strings.get(key() + ".condition." + cond));
			}
		}
		
		return sb.toString();
	}

	@Override public Side side() { return Side.HERO; }
	
	/** @return the PC's base sprite; conditions may modify it. */
	protected abstract Sprite baseSprite();
	
	@Override public final Sprite sprite() {
		if (hasCondition(Condition.NEWT)) {
			return Mobiles.NEWT;
		} else {
			return baseSprite();
		}
	}

	
	/** @return the PC's level. */
	public final int level() { return stats.level(); }
	
	/** Heal the player character, and log the result.
	 * @param points The number of points of healing to do.
	 * @return the number of points of healing actually done.
	 */
	@Override
	public final int heal(int points) {
		int realPoints = super.heal(points);
		
		if (realPoints > 0) {
			log("received " + realPoints + " points of healing");
		}
		
		return realPoints;
	}
	
	/** @return the PC's current mana. */
	public final int mana() {
		return mana;
	}
	
	/** Award experience points to the PC.
	 * @param points The number of points to award.
	 */
	public final void award(int points) {
		stats.award(points);
	}
	
	//-------------------------------------------------------------------------
	// Newts are invulnerable
	
	@Override public void damage(int points) {
		if (hasCondition(Condition.NEWT)) {
			log("Somehow you escape unscathed.  You're one tough newt.");
			return;
		}
		
		super.damage(points);
	}

	//-------------------------------------------------------------------------
	// User Interactions
	//
	// The user interacts with the PC by setting its goal; the PC then acts
	// accordingly.

	/** The longest A* route the PC will accept in Normal mode. */
	private final static int MAX_ROUTE_SIZE = 30;

	/** The maximum LOS range for talking to an NPC. */
	private final static int MAX_TALK_RANGE = 5;

	/** What kinds of goal can a player character seek? */
	protected enum GoalType {
		STOP,               // Stop where he is, even if movement points remain.
		MOVE,				// Move to cell at end of route.
		ATTACK_MOBILE,		// Move to cell at end of route and attack a mobile.
		POKE_FEATURE;		// Move to cell at end of route, and poke a feature.
	}

	/** How many movement points the PC has remaining, in Combat mode. */
	private int movementPoints = 0;

	/** The cell indicated by the user as the PC's goal. */
	private Cell goal = null;

	/** The goal type: what the PC wishes to do there. */
	private GoalType goalType = GoalType.MOVE;

	/** If the player is attacking a combatant, the combatant in question.
	 * Otherwise null.
	 */
	private Combatant goalCombatant = null;
	
	/** If the player is moving to or poking a feature, the feature in 
	 * question.  Otherwise null.
	 */
	private Feature goalFeature = null;

	/** If the player has to move somewhere, the computed route to get
	 * him there.
	 */
	private List<Cell> route = null;


	/** Sets the character's goal.  Given the cell he should walk to,
	 * determines how to get there and what he'll do there.  In particular, if 
	 * there's an enemy there he'll attack it,
	 * and if there's a pokeable feature there he'll poke it.
	 * 
	 * If the goal is his own location, he will simply end his move.  If he
	 * is standing on a step-triggered feature, he'll trigger it.  This way,
	 * if he's standing on the stairs and can't exit because his companions
	 * are too far away, you can simply move them close and then click on
	 * him when it's his turn.
	 * 
	 * TBD: If the player is next to a pokeable feature, he should probably
	 * just poke it immediately.  This might trigger an animation.  It
	 * should be possible to save the animation and have it run in this case.
	 * 
	 * @param goal  The goal.
	 */
	public final void setGoal(Cell goal) {
		// FIRST, clear all goal state.
		this.goal = null;
		this.goalType = null;
		this.goalCombatant = null;
		
		this.goalFeature = null;
		
		// NEXT, if goal is null he has no goal.
		if (goal == null)
			return;
		
		// NEXT, if the goal is his current location, he wants to wait.
		// If there's a step-triggered feature, here, though, then
		// trigger it.
		if (goal.equals(place())) {
			this.goal = goal;
			goalType = GoalType.STOP;
			route = new ArrayList<Cell>();
			println("Goal: Stopping");
	
			/* TBD: need to handle this properly; but get the animation working first.		
	 		// NEXT, if there's a step-triggered feature, trigger it.
			Feature f = getRegion().featureAt(here);
			
			if (f != null && f.isStepTriggered()) {
				f.step(script, this);
				goal = null;
			}*/
	
			return;
		}
		
		// NEXT, is it a valid goal?  A valid goal is a cell
		// that's either passable or has something in it that
		// the PC can interact with.
		if (!isPassable(goal) && !region().canInteractAt(goal)) {
			return;
		}
		
		// NEXT, is it an enemy?  If so, and we are within range,
		// we can attack it.
		Mobile m = region().mobileAt(goal);
		Item w = equipment.getWeapon();
		if (m != null && canSee(m)) {
			int dist = place().diagonal(m.place());
			
			// Immediate attack on enemy?
			if (m.isEnemyOf(this) && w != null && dist <= w.range()) {
				if (hasCondition(Condition.NEWT)) {
					log("You know you're a newt, right?");
					return;
				}
				
				this.goal = goal;
				goalType = GoalType.ATTACK_MOBILE;
				// TBD: See if we can avoid this cast.
				goalCombatant = (Combatant)m;
				route = null;
	
				if (!region().inCombat())
					Board.get().enterCombat();
				println("Goal: Immediate attack on " + m);
				return;
			}
			
			// Talk to friend, neutral?
			if (!m.isEnemyOf(this) && m.canTalk() && dist <= MAX_TALK_RANGE) {
				if (hasCondition(Condition.NEWT)) {
					log("You're not a talking newt.");
					return;
				}
				m.talk(this);
				return;
			}
		}
		
		// NEXT, can we get there?
		this.goal = goal;
		route = findAStarRoute(place(), goal);
		
		if (route == null) {
			this.goal = null;
			log("My path is blocked.");
			return;
		}
		
		if (route.size() > MAX_ROUTE_SIZE && !Board.DEBUG) {
			this.goal = null;
			log("That's too far away.  Pick something closer.");
			return;
		}
		
		// NEXT, We can get to the goal.  Now, what do we do
		// there?  Assume we're just going to move.
		goalType = GoalType.MOVE;
		
		// NEXT, Is there a mobile?  If there is, we can't
		// move onto it; we can only move next to it.  And
		// then, if it's an enemy we can attack it.
		
		if (m != null) {
			// Prepare to attack an enemy.
			if (m.isEnemyOf(this)) {
				goalType = GoalType.ATTACK_MOBILE;
				goalCombatant = (Combatant)m;
				println("Goal: Moving to attack " + m);
				if (!region().inCombat()) {
					Board.get().enterCombat();
					// This will reset movement, so just return.
					return;
				}
			} else {
				println("Goal: Moving up to " + m);
			}
			
			// Shorten the route and reset the goal; 
			// we don't want to move onto it.
			route.remove(goal);
	
			if (route.size() > 0) {
				this.goal = route.get(route.size() - 1);
			} else {
				this.goal = place();
			}
			
			return;
		}
		
		// NEXT, is there a feature to poke?
		Feature f = region().featureAt(goal);
		
		if (f != null && f.canInteract()) {
			if (hasCondition(Condition.NEWT)) {
				log("You know you're a newt, right?");
				return;
			}
			
			// Prepare to poke the feature.
			goalType = GoalType.POKE_FEATURE;
			goalFeature = f;
			println("Goal: Moving to " + f);
			
			// Shorten the route and reset the goal; we
			// don't want to move onto it.
			route.remove(goal);
	
			if (route.size() > 0) {
				this.goal = route.get(route.size() - 1);
			} else {
				this.goal = place();
			}
			
			return;
		}
		
		// NEXT, we're just walking.
		println("Goal: Walking to " + goal);
	}

	/** @return The character's current goal, or null if none. */
	public final Cell getGoal() {
		return goal;
	}

	/** Resets the PC's movement; he will wait for a goal, and get
	 * a full allotment of movement points next time. */
	public final void resetGoal() {
		this.movementPoints = 0;
		this.goal = null;
		this.route = null;
	}
	

	//-------------------------------------------------------------------------
	// Overridden Behavior
		
	/** @return the experience points awarded for killing the PC. 
	 * This is never used; it's the cost of making the PC a Combatant. */
	@Override
	public int expAwarded() {
		return 0;
	}
		
	/** Make the character move in combat.  Player characters
	 * move according to their goals.
	 */
	@Override
	public final boolean move(Script script) {
		Cell lastMove = null;
		
		// FIRST, if he has no movement points then he's starting a new move;
		// give him his movement points.  Otherwise, he has movement points 
		// left from last time.
		if (movementPoints == 0) {
			movementPoints = 1; // Normal mode
			
			if (region().inCombat())
				movementPoints = stats.mp();
		}
		
		// NEXT, if he has no goal, he needs to wait for one.
		if (goal == null) {
			return false;  // Turn not complete
		}
		
		// NEXT, he has a goal.  If he's just moving, and he's here, he loses a 
		// movement point for that.  This would usually happen when a PC is
		// playing follow the leader in Normal mode.
		if (goal.equals(place()) && goalType == GoalType.MOVE) {
			movementPoints--;
		}
		
		// NEXT, is he still moving towards it?
		if (route != null) {
			while (route.size() > 0) {
				// FIRST, schedule the move to the next cell.
				Cell next = route.remove(0);
				
				lastMove = moveTo(script, next);
				movementPoints--;
				
				// NEXT, if there's a step-triggered feature
				// prepare to trigger it.
				Feature f = region().featureAt(next);
				
				if (f != null && f.isStepTriggered()) {
					if (f.step(script, this)) {
						// If it did anything, set the PC's goal to null;
						// the player will need to react.
						goal = null;
						return movementPoints == 0;
					}
				}

				// NEXT, if we've used up our movement, stop moving.
				// Forget the goal, because it's no longer important.
				if (movementPoints == 0) {
					if (region().inCombat()) {
						goal = null;
					}
					return true;
				}
			}
		}
		
		// NEXT, we've reached the goal, and we
		// have movement points left to do something with.  Clear
		// the goal, because we're here.
		goal = null;

		// NEXT, Do what was intended.
		switch (goalType) {
		case MOVE:
			// Nothing special to do here.
			break;
		case STOP:
			movementPoints = 0;
			return true;
		case ATTACK_MOBILE:
			movementPoints = 0;
			engage(script, lastMove, goalCombatant, getAttack(), null);
			return true;
		case POKE_FEATURE:
			movementPoints--;
			goalFeature.poke(script, this);
			break;
		default:
			println("Error, unknown goal type " + goalType);
			break;
		}

		// NEXT, if he has no movement points left, the turn is over;
		// otherwise he needs to wait for input.
		return movementPoints == 0;
	}
	
	/** @return the PC's current attack. */
	private final Attack getAttack() {
		Weapon w = equipment.getWeapon();
		
		if (w == null) {
			w = FIST;
		}

		Attack attack = new Attack(stats.meleeSkill(), "hits", w);
		attack.addToDamage(stats.toDamage());
		
		return attack;
	}
	
	//-------------------------------------------------------------------------
	// Monetary Transactions
	//
	// All money goes to the party's common purse.
	
	/** The PC receives some gold coins.
	 * 
	 * @param amount  The number of coins.
	 */
	public void earn(int amount) {
		Board.get().party.earn(amount);
		log("receives " + amount + " gold coins.");
	}

	/** The PC pays some gold coins.
	 * 
	 * @param amount  The number of coins.
	 */
	public void pay(int amount) {
		Board.get().party.pay(amount);
		log("gives " + amount + " gold coins.");
	}
	
	/** @return the number of gold coins held by the party. */
	public int gold() {
		return Board.get().party.gold();
	}
	
	//-------------------------------------------------------------------------
	// Item Management
	//
	// Most item management is done by the equipment and inventory objects;
	// the rest is done here.
	
	/** Directs the player to discard the item, i.e., to remove it from
	 * his inventory or equipment, if it is present there, and to forget
	 * about it completely.
	 * 
	 * This method is usually used because the item is used up, and needs
	 * to disappear.  If the item is to be retained in some way, it is up 
	 * to the caller to do that.
	 * @param item The item to discard.
	 */
	public void discard(Item item) {
		inventory.remove(item);
		equipment.unequip(item);
	}

	//-------------------------------------------------------------------------
	// Enumerations
	
	/** The different player character classes.  Each PC has one, and it will
	 * affect what he can do and what equipment he can use.
	 * @author will
	 *
	 */
	public static enum CharacterClass {
		/** Farmer */ FARMER("Farmer", 56, 4.5),
		/** Knight */ KNIGHT("Knight", 68, 4.5),
		/** Friar  */ FRIAR ("Friar",  48, 3.0),
		/** Wizard */ WIZARD("Wizard", 34, 2.5);
		
		// Instance variables and Constructor
		
		CharacterClass(String name, int meleeBase, double meleeLevelBonus) { 
			this.name = name; 
			this.meleeBase = meleeBase;
			this.meleeLevelBonus = meleeLevelBonus;
		}
		
		/** The name of the class. */
		public final String name; 
		
		/** The base melee skill for this class. */
		public final int meleeBase;
		
		/** The bonus to melee skill for each level. */
		public final double meleeLevelBonus;
		
		@Override 
		public String toString() { 
			return name; 
		}
	}
	
	//-------------------------------------------------------------------------
	// Stats Component

	/** The Stats components encapsulates the PC's basic stats and how they
	 * change over time.
	 * @author will
	 */
	public final class Stats implements Serializable {
		private static final long serialVersionUID = 1L;

		//---------------------------------------------------------------------
		// Constants
		
		// Experience Thresholds
		
		/** The maximum PC level. */
		public final static int MAX_LEVEL = 50;
		private final int[] expBase = {
			0, 0, 10, 25, 45, 70, 100, 140, 200, 280, 380, 500, 650, 850,
			1100, 1400, 1800, 2300, 2900, 3600, 4400, 5400, 6800, 8400,
			10200, 12500, 17500, 25000, 34000, 50000, 75000, 100000, 150000,
			200000, 275000, 350000, 450000, 550000, 700000, 850000, 1000000,
			1250000, 1500000, 1800000, 2100000, 2400000, 2700000, 3000000,
			3500000, 4000000, 4500000
		};
		
		// Cost of stats in character points.
		private final static int STR_COST = 10;
		private final static int DEX_COST = 20;
		private final static int WIS_COST = 20;
		private final static int CON_COST = 10;
		
		// Instance Variables
		
		// The character's class and level
		private CharacterClass myclass;      // Class, e.g., FARMER or KNIGHT
		private int exp = 0;        // Experience
		private int level = 1;      // Level
		
		// Base stats
		private int strBase;      
		private int dexBase;
		private int wisBase;
		private int conBase;
		
		// Modifiers to base stats, due to status effects, equipment, etc.
		private int strMod = 0;
		private int dexMod = 0;
		private int intMod = 0;
		private int conMod = 0;
		
		// Hit Points
		private int hpBase;
		private int hpMax;
		private Dice hitDice;
		
		// Mana
		// TBD: No model for this, yet.  Give him 20, just to show.
		private int manaMax = 20;
		
		
		//---------------------------------------------------------------------
		// Constructor
		
		/** Creates a new stats object for a new character.
		 * 
		 * @param myclass  The player character's class.
		 * @param str  Initial STRength
		 * @param dex  Initial DEXterity
		 * @param wis  Initial WISdom
		 * @param con  Initial CONstitution
		 */
		public Stats(CharacterClass myclass, int str, int dex, int wis, int con) {
			// FIRST, save the initial stats.
			this.myclass = myclass;
			strBase = str;
			dexBase = dex;
			wisBase = wis;
			conBase = con;
				
			// NEXT, set up the HP.
			hpBase = con;
			hpMax = hpBase + computeHpBonus();
			hitDice = new Dice(con);  // Used when leveling up
		}
		
		//---------------------------------------------------------------------
		// Helpers
		
		/** @return the bonus to HP given the character's current level and CON. */
		private int computeHpBonus() {
			double bonus = 0.0;
			int con = con();
			
			if      (con <= 3) 					bonus = -2.5;
			else if (con == 4) 					bonus = -1.5;
			else if (con == 5)				 	bonus = -1.0;
			else if (con == 6)               	bonus = -0.5;
			else if (con == 15 || con == 16) 	bonus = 0.5;
			else if (con == 17)					bonus = 1.0;
			else if (con == 18 || con == 19)    bonus = 1.5;
			else if (con == 20 || con == 21)    bonus = 2.0;
			else  								bonus = 2.5;
			
			int total = (int)(bonus * level);
			
			return total;
		}
		
		//---------------------------------------------------------------------
		// Mutators
		
		/** Awards experience points to the PC. If the next level threshold
		 * is crossed, the player levels up.
		 * @param points  The experience points to award.
		 */
		public final void award(int points) {
			// FIRST, award the points.
			exp += points;
			
			// NEXT, level him up if need be.
			while (level < MAX_LEVEL && exp >= expNext()) {
				log("levels up!");
				++level;
				
				// NEXT, notify player
				JOptionPane.showMessageDialog(Board.get(), 
						name() + " is now level " + level + "!",
						name() + " Levels Up", 
						JOptionPane.PLAIN_MESSAGE, 
						bigIcon());
				

				
				// NEXT, compute the new max HP
				hpBase += hitDice.roll();
				hpMax = hpBase + computeHpBonus();
				
				// NEXT, update the Mobile's view of HP.  In particular,
				// add the increase to the Mobile's current HP.
				setMaxHP(hpMax);
			}
		}
		
		//---------------------------------------------------------------------
		// Queries
			
		/** @return the character's class */
		public final CharacterClass myclass() {
			return myclass;
		}
		
		/** @return the character's level */
		public final int level() {
			return level;
		}
		
		/** @return the character's max HP */
		public final int hpMax() { return hpMax; }
		
		/** @return the character's max MANA */
		public final int manaMax() { return manaMax; }
		
		/** @return the character's experience */
		public final int exp() {
			return exp;
		}
		
		/** @return the base experience for the current level */
		public final int expBase() {
			return expBase[level];
		}
		
		/** @return the base experience for the next level */
		public final int expNext() {
			int nextLevel = Math.min(MAX_LEVEL, level + 1);
			return expBase[nextLevel];
		}
		
		/** @return the character's cost in character points */
		public final int cost() {
			return str() * STR_COST + dex() * DEX_COST +
					wis() * WIS_COST + con() * CON_COST;
		}
		
		/** @return the character's current STRength */
		public final int str() { return strBase + strMod; }
		
		/** @return the character's current DEXterity */
		public final int dex() { return dexBase + dexMod; }
		
		/** @return the character's current WISdom */
		public final int wis() { return wisBase + intMod; }
		
		/** @return the character's current CONstitution */
		public final int con() { return conBase + conMod; }
		
		/** @return the character's SPD. */
		public final double spd() { return (dex() + con())/4.0; }
		
		/** @return the character's movement points (MP). */
		public final int mp() { return (int)Math.round(spd()); }
		
		/** @return the character's attack points (AP). */
		public final int ap() { return 1; }
				
		/** @return the player's armor class. */
		public final int ac() {
			// FIRST, get the total AC from his armor.
			int ac = defense.ac();
			
			// NEXT, add his dexterity modifier: 1 point for every
			// DEX over 12.
			ac += Math.max(0, dex() - 12);
			
			return ac; 
		}
		
		/** The player's "to damage" modifier is based on his STR, plus
		 * (in principle) equipment and spell effects, plus status
		 * conditions.  For now, we simply have the STR modifier.
		 * @return the player's "to damage" modifier.
		 */
		public final int toDamage() {
			int toDamage = 0;
			
			// STR modifier: 1 point for each STR over 12.
			toDamage += Math.max(0, str() - 12);
			
			return toDamage;
		}
		
		/** The player's melee combat skill depends in principle on
		 * his class, level, stats, equipment, status conditions, and so
		 * forth.
		 * @return the player's melee combat skill.
		 */
		public final int meleeSkill() {
			// FIRST, get the base, which depends on his class and level.
			int base = myclass.meleeBase + (int)(level*myclass.meleeLevelBonus);
			
			// NEXT, total up the "to hit" bonus.
			int toHit = 0;
			
			// DEX bonus
			toHit += Math.max(0, dex() - 15);
			
			// NEXT, compute and return the skill.
			return base + 3*toHit;
		}
	}
	
	//-------------------------------------------------------------------------
	// Equipment Component
	
	/** This component encapsulates the PC's equipment.  It is an inner
	 * class, is it needs to communicate directly with other components.
	 * @author will
	 *
	 */
	public final class Equipment implements Serializable {
		private static final long serialVersionUID = 1L;

		// Instance Variables
		
		/** The equipment slots. */
		private Map<Slot,Item> slots = new HashMap<>();
		
		/** The PC's current weapon preference, hand weapon or ranged weapon. */
		private Slot weaponPreference = Slot.HAND;
		
				
		// Tools
		
		/** Equip the given item.  The item is
		 * removed from the inventory (if needed), and any previously
		 * equipped item is returned to the inventory.
		 * 
		 * @param item The item to equip.
		 * @return true if the item could be equipped, and false otherwise.
		 */
		public final boolean equip(Item item) {
			// FIRST, can he equip it?
			if (!item.canBeEquippedBy(PlayerCharacter.this))
				return false;
			
			// NEXT, remove the item from the inventory, saving its
			// index.
			int idx = inventory.indexOf(item);
			
			inventory.remove(item);
			
			// NEXT, return the previously equipped item to the
			// inventory.
			Slot slot = item.slot();
			
			if (slots.get(slot) != null)
				inventory.set(idx, slots.get(slot));
			
			// NEXT, equip the new item.
			slots.put(slot, item);
			
			// NEXT, recompute stats
			recomputeDefense();
			
			return true;
		}

		/** Unequip the given item.  The item is
		 * returned to the inventory.
		 * 
		 * @param item The item to unequip.
		 * @return false if the item could not be unequipped.
		 */
		public final boolean unequip(Item item) {
			// FIRST, is it equipped?
			Slot slot = item.slot();
			
			if (!item.equals(slots.get(slot))) {
				return false;
			}
			
			// NEXT, is there room in the inventory to receive it?
			if (inventory.isFull()) {
				return false;
			}
			
			// NEXT, unequip the item and add it to the 
			// inventory.
			slots.put(slot,  null);
			inventory.add(item);

			// NEXT, recompute stats
			recomputeDefense();
			
			return true;
		}
		
		/** Get the item stored in the slot, or null if none.
		 * @param slot The equipment slot
		 * @return The item, or null.
		 */
		public final Item get(Slot slot) {
			return slots.get(slot);
		}

		/** Recompute the player's stats based on his equipment. */
		private void recomputeDefense() {
			// FIRST, compute defense.
			defense.clear();
			
			for (Slot slot : slots.keySet()) {
				if (slots.get(slot) != null)
					defense.add(slots.get(slot));
			}
		}
		
		/** Toggle the player's weapon preference. */
		public final void toggleWeaponPreference() {
			if (weaponPreference.equals(Slot.HAND) && slots.get(Slot.RANGED) != null)
				weaponPreference = Slot.RANGED;
			else 
				weaponPreference = Slot.HAND;
		}

		/** @return the weapon currently in use, or null if none. 
		 * TBD: Should return weapon FIST if there's no equipped weapon. */
		public final Weapon getWeapon() {
			if (weaponPreference.equals(Slot.RANGED) && get(Slot.RANGED) != null)
				return (Weapon)get(Slot.RANGED);
			else
				return (Weapon)get(Slot.HAND);
		}
		
		// TBD: Add getters for each slot, with the right class.
		
		/** Has the player equipped a particular item?
		 * 
		 * @param item The item
		 * @return true if so, and false otherwise.
		 */
		public boolean isEquipped(Item item) {
			if (!item.isEquippable())
				return false;
			
			Slot slot = item.slot();
			
			if (item.equals(slots.get(slot)))
				return true;
			else
				return false;
		}
	}
	
	//-------------------------------------------------------------------------
	// Inventory Component
	
	/** This component encapsulates the PC's inventory.  It is an inner
	 * class, is it needs to communicate directly with other components.
	 * TBD: Make this component implement the List<Item> interface?
	 * @author will
	 *
	 */
	public final class Inventory implements Serializable {
		private static final long serialVersionUID = 1L;

		// Constants
		
		/** The maximum number of items the PC can carry in his inventory. */
		public static final int MAX_ITEMS = 36;	
		
		// Instance Variables

		/** The player's inventory list.  It is a fully populated list of
		 * size MAX_ITEMS; empty inventory slots are simply null.
		 */
		private List<Item> slots = new ArrayList<>(MAX_ITEMS);

		// Creation
		
		/** Initialize the inventory list. */
		public Inventory() {
			for (int i = 0; i < MAX_ITEMS; i++)
				slots.add(null);
		}
		
		/** @return an unmodifiable list of the items in the inventory. */
		public final List<Item> toList() {
			return Collections.unmodifiableList(slots);
		}
		
		/** Does the inventory contain a particular item?
		 * @param item The item
		 * @return true if so, and false otherwise.
		 */
		public final boolean contains(Item item) {
			return slots.contains(item);
		}

		/** Add an item to the inventory.  Returns false if the inventory
		 *  is full.<p>
		 *  
		 *  Gold is handled specially; there is always room for gold, and
		 *  the amount is transferred to the party's coffers immediately.
		 * @param item The item to be added.
		 * @return true on success, and false otherwise.
		 */
		public final boolean add(Item item) {
			// FIRST, is it gold?
			if (item instanceof Gold) {
				Gold gold = (Gold)item;
				
				Board.get().party.earn(gold.amount());
				return true;
			}
			
			// NEXT, find an empty slot.
			int idx = slots.indexOf(null);
			
			if (idx == -1) {
				// Inventory is full
				return false;
			}
			
			// NEXT, put the item in the empty slot.
			slots.set(idx, item);
			return true;
		}
		

		/** Remove an item from the inventory, if it is
		 * present.
		 * @param item  The item in question.
		 */
		public final void remove(Item item) {
			int idx = slots.indexOf(item);
			
			if (idx != -1)
				slots.set(idx, null);
		}
		
		/** @return true if the inventory is filled to capacity, and
		 * false otherwise.
		 */
		public final boolean isFull() {
			// It is full if there are no null slots.
			return slots.indexOf(null) == -1;
		} 	

		/** Puts the item in the given slot, and returns
		 * any previous item in that slot.
		 * @param index The slot number
		 * @param item The item
		 * @return The previous occupant of the slot, or null if none.
		 */
		public final Item set(int index, Item item) {
			Item oldItem = slots.get(index);
			slots.set(index, item);
			
			return oldItem;
		}

		/** Gets the item in the given slot.
		 * @param idx The slot number
		 * @return the item, or null if the slot is empty.
		 */
		public final Item get(int idx) {
			return slots.get(idx);
		}
		
		/** Gets the index for the given item in the inventory.
		 * @param item The item
		 * @return the index, or -1 if not found.
		 */
		public final int indexOf(Item item) {
			return slots.indexOf(item);
		}
	}
	
	//------------------------------------------------------------------------

	private static final class Fist extends HandWeapon {
		private static final long serialVersionUID = 1L;

		/** Creates a new item. */
		public Fist() {
			super();
		}
		
		@Override public String name()   { return "Fist"; }
		@Override public Sprite sprite() { return Effects.FIST; }
		@Override public Dice   damage() { return new Dice(1,1); }
		
		/** @return the description */
		public String getDescription() {
			return " You've got two; this is your good one.  It " +
					"does " + damage() + " points of damage.";
		}		
	}
}