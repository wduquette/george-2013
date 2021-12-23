/**
 * 
 */
package rpg;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import map.Cell;
import map.MovementAssessor;
import rpg.pc.PlayerCharacter;
import util.Script;
import util.ScriptAction;
import app.Board;

/** This class collects all of the shared code/interface/behavior
 * for Mobiles.
 * @author will
 *
 */
public abstract class Mobile extends Entity {	
	private static final long serialVersionUID = 1L;

	// Instance Variables
	
	/** The mobile's preferred terrain assessor.
	 * TBD: See if we can somehow make this a nested class rather than
	 * an inner class, and then have one that's shared across all
	 * mobiles (since everybody avoids everything by default). */
	private MovementAssessor preferredAssessor;
	
	/** When a mobile has a status condition, the condition is 
	 * added to this map with the last time tick for which the
	 * condition exists.  Thus, the mobile *doesn't* have the
	 * condition if the condition isn't contained in the map
	 * or if it is but the time is past.
	 */
	private Map<Condition,Integer> conditions = new HashMap<>();
	
	
	// Constructor

	/** Creates a new Mobile.
	 */
	protected Mobile() {
		super();
		
		preferredAssessor = getAssessor(Avoid.EVERYTHING);
	}
	
	//------------------------------------------------------------------------
	// Attribute Methods
	
	/** By default, mobiles move by walking.  Override this to fly, swim,
	 * etc.
	 * @return The mobile's movement type.
	 */
	public MovementType movementType() {
		return MovementType.WALKING;
	}
	
	/** By default mobiles are neutral; monsters and player characters will
	 * override this.
	 * @return the side
	 */
	public Side side() {
		return Side.NEUTRAL;
	}

	/** Mobiles are alive by default; non-Combatants cannot be killed.
	 * @return true if the mobile is alive, and false otherwise.
	 */
	public boolean isAlive() {
		return true;
	}
	
	/** Mobiles are awake by default.
	 * @return true if the mobile is awake, and false otherwise.
	 */
	public boolean isAwake() {
		return true;
	}
	
	/** In general, you can interact with mobiles.
	 * @return true if you can interact with it.
	 */
	@Override
	public boolean canInteract() {
		return true;
	}
	
	/** Can the mobile talk with a player character? 
	 * Subclasses can override this.
	 * @return true if so. 
	 */
	public boolean canTalk() {
		return false;
	}
	
	/** Mobiles are not hostile by default; but see combatants.
	 * @return true if the mobile is hostile, and false otherwise.
	 */
	public boolean isHostile() {
		return false;
	}
	

	//-------------------------------------------------------------------------
	// Customizable Behavior
	
	
	/** Make the mobile talk to the player character.
	 * The subclass should override this if canTalk() is true.
	 * @param pc The player character 
	 */
	public void talk(PlayerCharacter pc) {
		// Do nothing.
	}
	
	/** This routine is called to ask the Mobile to 
	 * determine its move during this turn. The
	 * mobile fills in the animation script, and 
	 * returns true if it's all done and false if it
	 * needs to be called again after the script is
	 * done (i.e., because it has movement points left
	 * and needs to wait for user input.
	 * 
	 * @param script The script object to fill in.
	 * @return true if the turn is complete, and false
	 * otherwise.
	 */
	public abstract boolean move(Script script);
	
	//------------------------------------------------------------------------
	// Condition Methods
	
	/** Gives the mobile a particular status condition until the specified
	 * number of ticks from right now.  Note that it doesn't matter whether
	 * the player already had the condition: if he's supposed to sleep
	 * for 10 more ticks, he sleeps for 10 more ticks.
	 * @param condition The condition
	 * @param ticks The number of ticks.
	 */
	public final void setCondition(Condition condition, int ticks) {
		conditions.put(condition, Board.get().time() + ticks);
	}
	
	/** Clears the given condition. 
	 * 
	 * @param condition The status condition
	 */
	public final void clearCondition(Condition condition) {
		conditions.remove(condition);
	}
	
	/** Determines whether or not the mobile has the given condition.
	 * 
	 * @param condition the condition
	 * @return true if so, and false otherwise.
	 */
	public final boolean hasCondition(Condition condition) {
		if (conditions.containsKey(condition)) {
			int time = conditions.get(condition);

			if (Board.get().time() <= time) {
				return true;
			}
		}
		return false;
	}
	
	//------------------------------------------------------------------------
	// Scripting Methods
	//
	// These methods add steps to an animation script.

	/** Scripts the movement to a new cell, and returns
	 * the cell.
	 * @param script The script receiving the movement.
	 * @param cell The cell to move to
	 * @return The cell moved to
	 */
	public final Cell moveTo(Script script, Cell cell) {
		script.add(new Mobile.MoveToCell(cell));
		return cell;
	}

	//------------------------------------------------------------------------
	// Queries

	/** @return true if the mobile can move onto the cell,
	 * and false otherwise, using the mobile's preferred
	 * assessor.
	 * @param cell the cell in question.
	 */
	public final boolean isPassable(Cell cell) {
		return preferredAssessor.isPassable(cell);
	}
	
	/** Determine whether another mobile is a friend of
	 * this mobile. 
	 * 
	 * @param other The other mobile
	 * @return true if it is a friend, and false otherwise.
	 */
	public final boolean isFriendOf(Mobile other) {
		return side().isFriend(other.side());
	}
	
	/** Determine whether another mobile is an enemy of
	 * this mobile. 
	 * 
	 * @param other The other mobile
	 * @return true if it is an enemy, and false otherwise.
	 */
	public final boolean isEnemyOf(Mobile other) {
		return side().isEnemy(other.side());
	}

	/** Determine whether another mobile is neutral to
	 * this mobile. 
	 * 
	 * @param other The other mobile
	 * @return true if it is neutral, and false otherwise.
	 */
	public final boolean isNeutralTo(Mobile other) {
		return side().isNeutral(other.side());
	}
	
	/** Find the enemies of this mobile.  
	 * 
	 * @return A list of the mobile's enemies.
	 */
	public final List<Combatant> getEnemies() {
		List<Combatant> enemies = new ArrayList<>(0);
		
		for (Mobile m : region().getMobiles()) {
			if (m.isEnemyOf(this)) {
				// TBD: Get rid of the cast, when possible.
				assert m instanceof Combatant;
				enemies.add((Combatant)m);
			}
		}
		
		return enemies;
	}
	
	/** Return the mobile's closest enemy, in cartesian terms.
	 * 
	 * @return Its enemy, or null.
	 */
	public final Combatant getClosestEnemy() {
		Combatant enemy = null;
		double minDist = 0.0;
		
		for (Combatant m : getEnemies()) {
			Double dist = place().cartesian(m.place());
			if (enemy == null || dist < minDist) {
				enemy = m;
				minDist = dist;
			}
		}
		
		return enemy;
	}
	
	
	/** Determine whether this mobile can see a given cell.
	 * 
	 * @param cell The cell in question
	 * @return True if the mobile has a line of sight to it,
	 * and false otherwise.
	 */
	public final boolean canSee(Cell cell) {
		return region().inLOS(place(),  cell);
	}
	
	/** Determine whether this mobile can see the entity
	 * or not.
	 * @param e The entity is question.
	 * @return true if this mobile has a line of sight to it,
	 * and false otherwise.
	 */
	public final boolean canSee(Entity e) {
		if (e.isVisible()) {
			return region().inLOS(place(), e.place());
		} else { 
			return false;
		}
	}

	/** Determines whether there is a visible, hostile enemy within range.
	 * 
	 * @param range The diagonal range
	 * @return true if there is, and false otherwise.
	 */
	public final boolean canSeeEnemyInRange(int range) {
		return canSeeEnemyInRange(place(), range);
	}

	/** Determines whether there is a visible, hostile enemy within range.
	 * 
	 * @param from  From this cell
	 * @param range The diagonal range
	 * @return true if there is, and false otherwise.
	 */
	public final boolean canSeeEnemyInRange(Cell from, int range) {
		for (Mobile e : getEnemies()) {
			if (e.place().diagonal(from) <= range && e.isHostile() && canSee(e)) {
				return true;
			}
		}
				
		return false;
	}
	
	
	/** Find all enemies in line-of-sight of a particular cell.
	 * @param cell  The cell of interest
	 * @param range The range.
	 * @return The list of enemies.
	 */
	public final List<Combatant> getVisibleEnemies(Cell cell, int range) {
		List<Combatant> result = new ArrayList<>();
		
		for (Combatant e : getEnemies()) {
			if (e.isVisible() &&
				e.place().diagonal(cell) <= range &&
				region().inLOS(cell, e.place())) {
				result.add(e);
			}
		}
		
		return result;
	}
	
	/** Return the mobile's closest enemy, in cartesian terms.
	 * 
	 * @param range the mobile's visual range
	 * @return Its enemy, or null.
	 */
	public final Combatant getClosestVisibleEnemy(int range) {
		Combatant enemy = null;
		double minDist = 0.0;
		
		for (Combatant m : getVisibleEnemies(place(), range)) {
			Double dist = place().cartesian(m.place());
			if (enemy == null || dist < minDist) {
				enemy = m;
				minDist = dist;
			}
		}
		
		return enemy;
	}


	/** Find all enemies within a given travel distance of a particular
	 * cell.
	 * @param cell  The cell of interest
	 * @param range The range.
	 * @return The list of enemies.
	 */
	public final List<Combatant> getEnemiesNear(Cell cell, int range) {
		List<Combatant> result = new ArrayList<>();
		
		for (Combatant e : getEnemies()) {
			if (e.isVisible() &&
				e.place().diagonal(cell) <= range &&
				travelDistanceTo(e.place()) <= range) {
				result.add(e);
			}
		}
		
		return result;
	}
	
	/** Find all enemies within a given travel distance of the PC.
	 * @param range The range.
	 * @return The list of enemies.
	 */
	public final List<Combatant> getEnemiesNear(int range) {
		return getEnemiesNear(place(), range);
	}
	
	//-------------------------------------------------------------------------
	// Movement Algorithms
	//
	// These algorithms script mobile movement.
	
	/** The mobile moves randomly.  For each movement point, the mobile will
	 * determine whether to take a step, and if so will stepRandomly().
	 * Movement ends when all movement points are expended or when no
	 * move is possible.
	 * 
	 * @param mp  The movement points available.
	 * @param restlessness  How likely (1D100) the mobile is to take a step
	 * @param script The script to receive the movement.
	 */
	public final void wander(int mp, int restlessness, Script script) {
		// The mobile starts from its current location.
		Cell here = place();
		
		while (mp-- > 0) {
			// NOTE: Don't script a step if mobile doesn't move; it
			// just makes combat take longer.
			if (random.chance(restlessness)) {
				here = stepRandomly(here);
				
				if (here == null) {
					return;
				}
				
				moveTo(script, here);
			}
		}
		
	
	}
	
	//-------------------------------------------------------------------------
	// Step Algorithms
	//
	// These routines compute one step according to some movement
	// strategy.
	
	/** The mobile steps randomly to some adjacent location, if possible. 
	 * 
	 * @param from The cell to move from.
	 * @return the next cell, or null if there's no move to make.
	 */
	public final Cell stepRandomly(Cell from) {
		// FIRST, get the legal moves in all directions.
		List<Cell> moves = neighborsOf(from);
		
		// NEXT, move to a random adjacent position, if possible.
		if (moves.size() > 0) {
			Cell move = random.pickFrom(moves);
			return move;
		} else {
			return null;
		}
	}
	
	/** The mobile steps so as to minimize the Cartesian distance to its goal, 
	 * even if this means that it gets stuck in a corner.
	 * @param cell The cell to move from
	 * @param goal The goal position
	 * @return the cell to step to, or null if none.
	 */
	public final Cell stepNaively(Cell cell, Cell goal) {
		// FIRST, if there's a move that takes him closer to the goal, take it.
		List<Cell> moves = neighborsOf(cell);
		
		double minDist = cell.cartesian(goal);
		Cell minMove = null;
		
		for (Cell move : moves) {
			// FIRST, if this is the goal we're done.
			if (move.equals(goal)) {
				return move;
			}
			
			// NEXT, get the distance from the move location to goal
			double dist = move.cartesian(goal);
			
			if (dist < minDist) {
				minDist = dist;
				minMove = move;
			}
		}
		
		return minMove;
	}
	
	/** The mobile steps one cell closer to its goal, using an A* route.
	 * @param from The cell to move from
	 * @param goal The goal cell
	 * @return the cell to step to, or null if none.
	 */
	public final Cell stepSmartly(Cell from, Cell goal) {
		List<Cell> route = findAStarRoute(from, goal);
		
		if (route == null || route.size() == 0) {
			return null;
		} else {
			return route.get(0);
		}
	}

	/** The mobile steps so as to maximize the Cartesian distance to the threat, 
	 * even if this means that it gets stuck in a corner.
	 * TBD: Should possibly return the cell and let the mobile make the move.
	 * 
	 * @param cell The cell to move away from
	 * @param threat The position of the threat to be avoided.
	 * @return the cell to step to, or null if none.
	 */
	public final Cell stepAwayNaively(Cell cell, Cell threat) {
		// FIRST, if there's a move that takes him away from the threat, make it.
		List<Cell> moves = neighborsOf(cell);
		
		double maxDist = cell.cartesian(threat);
		Cell maxMove = null;
		
		for (Cell move : moves) {
			// FIRST, get the distance from the move location to the threat
			double dist = move.cartesian(threat);
			
			if (dist > maxDist) {
				maxDist = dist;
				maxMove = move;
			}
		}
		
		return maxMove;
	}

	//-------------------------------------------------------------------------
	// Movement Aid: A* route-finding algorithm.
	
	
	/** Uses the A* algorithm, as described at Wikipedia.  Finds an efficient route from
	 * the starting point to the goal, if one exists.  The mobile's preferred terrain assessor
	 * is used to find valid moves.
	 *
	 * @param start	The starting point (usually "here")
	 * @param goal The point to go to.
	 * @return The route from start to goal, or null if there is none.  The returned
	 * route does not include the starting point.
	 */
	protected final List<Cell> findAStarRoute(Cell start, Cell goal) {
		return region().findAStarRoute(start, goal, preferredAssessor);
	}
	
	/** Uses the A* algorithm, as described at Wikipedia.  Finds an efficient route from
	 * the starting point to the goal, if one exists.  The mobile's preferred terrain assessor
	 * is used to find valid moves.
	 *
	 * @param start	The starting point (usually "here")
	 * @param goal The point to go to.
	 * @param avoidance The set of things to avoid.
	 * @return The route from start to goal, or null if there is none.  The returned
	 * route does not include the starting point.
	 */
	protected final List<Cell> findAStarRoute(Cell start, Cell goal, Avoid avoidance) {
		return region().findAStarRoute(start, goal, getAssessor(avoidance));
	}
	
	//-------------------------------------------------------------------------
	// Distance measures
	
	/** Diagonal distance to a cell.  For most ranges, diagonal distance
	 * is what's wanted.
	 * 
	 * @param cell The cell
	 * @return The distance in cells
	 */
	public int distanceTo(Cell cell) {
		return place().diagonal(cell);
	}
	
	/** Diagonal distance to an entity.  For most ranges, diagonal distance
	 * is what's wanted.
	 * 
	 * @param entity The entity
	 * @return The distance in cells
	 */
	public int distanceTo(Entity entity) {
		return place().diagonal(entity.place());
	}
	
	/** The travel distance to another cell, using the best route, using
	 * the mobile's preferred terrain assessor.
	 * @param start The starting cell
	 * @param goal the goal cell
	 * @return The travel distance, or Integer.MAX_VALUE if there is no path.
	 */
	public final int travelDistanceTo(Cell goal) {
		return region().travelDistance(place(), goal, preferredAssessor);
	}

	/** The travel distance to another entity, using the best route, using
	 * the mobile's preferred terrain assessor.
	 * @param start The starting cell
	 * @param entity the goal entity
	 * @return The travel distance, or Integer.MAX_VALUE if there is no path.
	 */
	public final int travelDistanceTo(Entity entity) {
		return region().travelDistance(place(), entity.place(), 
									   preferredAssessor);
	}

	/** The travel distance between cells, using the best route, using
	 * the mobile's preferred terrain assessor.
	 * @param start The starting cell
	 * @param goal the goal cell
	 * @return The travel distance, or Integer.MAX_VALUE if there is no path.
	 */
	public final int travelDistance(Cell start, Cell goal) {
		return region().travelDistance(start, goal, preferredAssessor);
	}

	/** The travel distance to another cell, using the best route, avoiding
	 * particular things.
	 * @param goal the goal cell
	 * @param avoidance The set of things to avoid.
	 * @return The travel distance, or Integer.MAX_VALUE if there is no path.
	 */
	public final int travelDistance(Cell goal, Avoid avoidance) {
		return region().travelDistance(place(), goal, getAssessor(avoidance));
	}

	/** The travel distance between cells, using the best route, avoiding
	 * particular things.
	 * @param start The starting cell
	 * @param goal the goal cell
	 * @param avoidance The set of things to avoid.
	 * @return The travel distance, or Integer.MAX_VALUE if there is no path.
	 */
	public final int travelDistance(Cell start, Cell goal, Avoid avoidance) {
		return region().travelDistance(start, goal, getAssessor(avoidance));
	}
	
	/** Return a list of passable spaces adjacent to a particular cell,
	 * taking the mobile's definition of passability into account.
	 * @param cell the cell in question. 
	 * @return a list of walkable spaces. */
	public final List<Cell> neighborsOf(Cell cell) {
		return region().neighborsOf(cell, null, preferredAssessor);
	}
	
	/** Return a list of passable spaces adjacent to a particular cell,
	 * taking the mobile's definition of passability into account.
	 * If a goal cell is specified, it is assumed to be passable.
	 * @param cell the cell in question. 
	 * @param goal the goal cell.
	 * @return a list of passable cells. */
	public final List<Cell> neighborsOf(Cell cell, Cell goal) {
		return region().neighborsOf(cell, goal, preferredAssessor);
	}
	
	//-------------------------------------------------------------------------
	// Inner Classes
	
	/** Returns an assessor for this mobile given the
	 * degree of avoidance.
	 * @param avoidance
	 * @return the new assessor.
	 */
	public final MovementAssessor getAssessor(Avoid avoidance) {
		return new MobileMovementAssessor(avoidance);
	}

	private final class MobileMovementAssessor implements MovementAssessor, Serializable {
		private static final long serialVersionUID = 1L;

		// Instance Variables
		private Avoid avoidance;
		
		private MobileMovementAssessor(Avoid avoidance) {
			this.avoidance = avoidance;
		}
		
		public boolean isPassable(Cell cell) {
			// FIRST, check for general mobiles.
			Mobile m = region().mobileAt(cell);
			
			if (avoidance.mobiles && m != null)
				return false;

			// NEXT, check for hazards.  At present, the only
			// hazard of interest is enemy mobiles.
			// TBD: We'll need a notion of visible and avoidable
			// traps.
			if (avoidance.hazards && m != null && isEnemyOf(m))
				return false;

			// NEXT, check for features.
			Feature f = region().featureAt(cell);
			
			if (avoidance.features && f != null)
				return f.isPassableBy(movementType());
			
			// FINALLY, if the terrain is not passable, then the cell isn't passable.
			if (!region().isPassableBy(movementType(), cell))
				return false;
			
			return true;
		}
	}
	
	//-------------------------------------------------------------------------
	// ScriptActions
	
	/** Moves the mobile to a cell without triggering any
	 * feature at that cell.
	 * TBD: Consider making this a nested class.  In fact, there's
	 * nothing specific to a Mobile here; this would work for any
	 * entity.
	 * @author will
	 *
	 */
	protected final class MoveToCell implements ScriptAction {
		private Cell cell; 
		
		/** Action: Move to a particular cell.
		 * 
		 * @param cell The cell
		 */
		public MoveToCell(Cell cell) {
			this.cell = cell;
		}
		
		/** Move to the cell. */
		public void execute() {
			place(cell);
		}
	}
}
