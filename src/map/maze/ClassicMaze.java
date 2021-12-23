/**
 * 
 */
package map.maze;

import graphics.StandardTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import map.Cell;
import map.Direction;
import map.TerrainGrid;
import map.TerrainTile;


/** A class for generating mazes.  A variety of algorithms are provided.<p>
 * 
 * A Maze is a rows*cols array of Positions, which might or might not be open to their neighbors
 * to the north, south, east, and west.  Each Position is represented as a bitset; if a direction's
 * bit is set, then the wall is open on that side.<p>  
 * 
 * A new Maze consists entirely of unconnected Positions.  Various methods transform the maze,
 * growing an entirely connected maze, opening new paths, and so forth.  When the maze is
 * complete, convert it toTileArray() to be rendered as an image.<p> 
 * 
 * TBD: Consider using RandomPlus.
 *
 * @author will
 *
 */
public final class ClassicMaze {
	/** Enumeration for the manner in which the growTree() method picks the next
	 * Position to grow from.
	 */
	public enum GrowTreeMode {
		/** Grow from the first Position on the end points list. */
		FIRST,
		
		/** Grow from the second Position on the end points list, moving
		 * the first Position to the back of the line. */
		SECOND,
		
		/** Grow from a random Position on the endpoints list. */
		RANDOM
	}
	
	/** Clients can create a Tracer and give it to the maze using
	 * setTrace(); then it will be called every so often as the maze is grown.
	 * @author will
	 */
	public interface Tracer {
		/** Allows the client to output the maze in some way at each
		 * time step.
		 * @param maze  The maze under construction.
		 */
		public void trace(ClassicMaze maze);
	}
	
	private final Random random = new Random();
	
	private final int rows;   // Number of rows
	private final int cols;   // Number of columns
	
	private Tracer tracer = null;  // Object to trace maze generation.
	
	/* Position array.  A Position's value is a bitmap where a direction's bit is set if the Position
	 * is connected to the Position in that direction.  If a Position's value is 0, then, it is
	 * "free", i.e., unconnected.
	 */
	private byte[][] cells;
	
	// Constructors
	
	/** Creates a new, blank maze with the specified number of rows and columns.  Apply various
	 * methods to generate a real maze.
	 * @param rows The number of rows of Positions in the maze
	 * @param cols The number of rows of columns in the maze
	 */
	public ClassicMaze(int rows, int cols) {
		if (rows < 1 || cols < 1)
			throw new IllegalArgumentException("dimension less than 1");
		
		this.rows = rows;
		this.cols = cols;
		
		clear();
	}

	// queries
	/** @return the number of rows in the maze */
	public int rows() { return rows; }
	
	/** @return the number of columns in the maze. */
	public int cols() { return cols; }

	/** @return a list of the non-free rooms in the maze. */
	public List<Cell> rooms() {
		List<Cell> rooms = new ArrayList<>();
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (cells[i][j] > 0) {
					rooms.add(new Cell(i,j));
				}
			}
		}
		
		return rooms;
	}
	
	
	/** @return a TileArray of the maze using STONE_WALL and TILE_FLOOR tiles;
	 * free Positions show as UNKNOWN. */
	public TerrainGrid toGrid() {
		return toGrid(StandardTile.STONE_WALL, StandardTile.TILE_FLOOR, StandardTile.UNKNOWN);
	}

	/** @return a TileArray of the maze. 
	 * @param wall Tile for walls
	 * @param floor Tile for floors
	 * @param free Tile for free Positions (i.e., no open doors).
	 */
	public TerrainGrid toGrid(TerrainTile wall, TerrainTile floor, TerrainTile free) {
		TerrainGrid ta = new TerrainGrid(2*rows+1, 2*cols+1);
		ta.clearTo(wall);
		
		for (int i = 0; i < rows; i++) {
			int ti = 2*i + 1;
			for (int j = 0; j < cols; j++) {
				int tj = 2*j + 1;
				
				// FIRST, set the tile for the current Position.
				if (cells[i][j] > 0) {
					ta.set(ti, tj, floor);
				} else {
					ta.set(ti, tj, free);
				}
				
				// NEXT, open the doors, leaving floor.
				if (i == 0 && (cells[i][j] & Direction.N.mask) > 0)
					ta.set(ti-1, tj, floor);
				
				if (j == 0 && (cells[i][j] & Direction.W.mask) > 0)
					ta.set(ti, tj-1, floor);
				
				if ((cells[i][j] & Direction.E.mask) > 0)
					ta.set(ti, tj + 1, floor);
				
				if ((cells[i][j] & Direction.S.mask) > 0)
					ta.set(ti+1, tj, floor);
			}
		}

		return ta;
	}

	/** Returns a terrain grid based on the maze, with rooms and walls of
	 * the desired sizes. 
	 * @param wall Terrain tile for walls
	 * @param floor Terrain tile for floors
	 * @param wtiles Width of walls in tiles.
	 * @param rtiles Width of rooms in tiles.
	 * @return the terrain grid
	 */
	public TerrainGrid toGrid(TerrainTile wall, TerrainTile floor, int wtiles, int rtiles) {
		// FIRST, determine the size of the terrain grid.  We need to 
		// allow for the walls as well as the rooms.
		int grows = rows*rtiles + (rows + 1)*wtiles;
		int gcols = cols*rtiles + (cols + 1)*wtiles;
		
		// NEXT, initialize the terrain grid to walls.
		TerrainGrid grid = new TerrainGrid(grows, gcols);
		grid.clearTo(wall);
		
		// NEXT, put in each room, and any open doors next to it.
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				// FIRST, if this room has no open doors, there's nothing
				// that needs to be done.
				if (cells[i][j] == 0)
					continue;
				
				// FIRST, get the coordinates of the upperleft corner
				// of the room in the grid.
				int gi = i*rtiles + (i+1)*wtiles;
				int gj = j*rtiles + (j+1)*wtiles;
				
				// NEXT, set the tiles for the room.
				grid.set(gi, gj, rtiles, rtiles, floor);
				
				// NEXT, open the doors, leaving floor.  We don't
				// need to support doors in the outer walls, so
				// only look to the right (east) and down (south).
				if ((cells[i][j] & Direction.E.mask) > 0) {
					// The door space will be rtiles high by wtiles wide,
					// rtiles to the right of the room.
					grid.set(gi, gj+rtiles, rtiles, wtiles, floor);
				}
				
				if ((cells[i][j] & Direction.S.mask) > 0) {
					// The door space will wtiles high by rtiles wide,
					// rtiles below the room.
					grid.set(gi + rtiles, gj, wtiles, rtiles, floor);
				}
			}
		}

		return grid;
	}

	// Maze Algorithms
	
	/** Assigns a maze tracer, so that maze creation can be traced and animated. 
	 * The tracer's trace() call is called periodically during maze operations.
	 * 
	 * @param tracer The Tracer object.  Pass null to disable tracing.
	 */
	public void setTracer(Tracer tracer) {
		this.tracer = tracer;
	}
	
	/** Clears the maze contents and creates a new maze with the given maxStep size
	 * using the "growing tree" algorithm. The algorithm is as follows:
	 * 
	 * <ol>
	 * <li> Start with a cleared maze, in which no Position is connected to any other Position.
	 *      Such Positions are called "free".
	 * <li> Randomly pick a room as the start point.  Add the coordinates of the room to
	 *      the list of end points.
	 * <li> While an endpoint remains in the endpoint list,
	 *      <ol>
	 *      <li> Take an endpoint from the endpoint list.  If the list is empty, then the 
	 *           maze is complete.
	 *      <li> Determine the directions (N,S,E,W) in which there are free Positions
	 *           next door, and randomly pick one of them.
	 *      <li> Randomly pick a number of Positions, N, to add to the maze, where 1 <= N <= maxStep.
	 *      <li> From the chosen endpoint, connect Positions in the chosen direction until you've
	 *           reached the Nth room, the edge of the map, or a Position that is not free.
	 *      <li> Add the last Position you connected (if any) to the list of endpoints.
	 *      <li> Continue.
	 *      </ol>
	 * </ol>
	 * 
	 * @param mode How to pick the next endpoint to grow from.
	 * @param maxSteps The maximum number of Positions to grow at each step in the algorithm.
	 * @return The same maze again, so that operations can be chained.
 	 */
	public ClassicMaze growTree(GrowTreeMode mode, int maxSteps) {
		// FIRST, clear the maze
		clear();

		// NEXT, create the endpoint list and add the first endpoint.
		List<Cell> endpoints = new ArrayList<Cell>();
		endpoints.add(randomPosition());
		
		// NEXT, we need to track free directions.  Create an EnumSet; we'll reuse it.
		List<Direction> freeDirs = new ArrayList<>();
		
		// NEXT, loop until there are no more endpoints
		while (endpoints.size() > 0) {
			// FIRST, get the next endpoint.  It should have at least one open door.
			Cell here;
			
			switch (mode) {
			case FIRST:
			default:
				here = endpoints.remove(0);
				break;
			case SECOND:
				endpoints.add(endpoints.remove(0));
				here = endpoints.remove(0);
				break;
			case RANDOM:
				here = endpoints.remove(random.nextInt(endpoints.size()));
				break;
			}
			
			// NEXT, get the directions of the free Positions around this endpoint.
			freeDirs = freeDirections(here);
			
			if (freeDirs.size() == 0) {
				continue;
			} else if (freeDirs.size() > 1) {
				endpoints.add(here);
			}
			
			// NEXT, drive up to maxSteps in one of the free directions, and add the final
			// Position to the endpoints.
			Direction d = pickDirFrom(freeDirs);
			endpoints.add(carve(here, d, 1 + random.nextInt(maxSteps), true));
		}
		
		return this;
	}

	/** Uses a recursive backtracker algorithm to grow the maze.  The algorithm works as follows:
	 * Start with a maze of free Positions.  Pick a Position randomly.  When carving, be as greedy as 
	 * possible, and always carve into an free Position if one is next to the current Position.  
	 * Each time you move to a new Position, push the former Position on the stack.  If there are no
	 * free Positions next to the current position, pop the stack.  The maze is done when you
	 * pop everything off of the stack.  This algorithm results in mazes with about as high
	 * a "river" factor as possible, with fewer but longer dead ends, and usually a very
	 * long and twisty solution.  It runs quite fast, although Prim's algorithm is a 
	 * bit faster.
	 * @see http://www.astrolog.org/labyrnth/algrithm.htm
	 * @param inertia If true, the algorithm will be more likely to carve in the same direction 
	 * next time.
	 * @return The maze itself
	 */
	public ClassicMaze recursiveBacktracker(Boolean inertia) {
		// FIRST, clear the maze
		clear();

		// NEXT, we need a stack.
		List<Cell> stack = new ArrayList<Cell>(0);
		
		// NEXT, pick the first entry, and a direction from it.  Open a wall in
		// that direction.
		Cell here = randomPosition();
		Direction lastDir = pickDirFrom(freeDirections(here));
		carve(here, lastDir, 1, true);
		
		// NEXT, push here on the stack
		stack.add(here);
		here = nextPositionInDir(here,lastDir);
		
		// NEXT, the maze is done when there's nothing left on the stack.
		while (stack.size() > 0) {
			// FIRST, are there any free Positions next to the current Position?  If
			// not, pop a Position from the stack and continue.
			List<Direction> freeDirs = freeDirections(here);
			
			if (freeDirs.size() == 0) {
				here = stack.remove(stack.size()-1);
				continue;
			}
			
			// NEXT, given inertia, make it more likely to proceed in the same direction.
			if (inertia && freeDirs.contains(lastDir)) {
				freeDirs.add(lastDir);
			}
			
			// NEXT, carve into one of the adjacent Positions.
			lastDir = pickDirFrom(freeDirs);
			carve(here, lastDir, 1, true);
			stack.add(here);
			here = nextPositionInDir(here,lastDir);
		}
		
		return this;
	}

	
	// mutators
	
	
	/** Clears the maze, i.e., disconnects all of the Positions from each other.
	 * @return The maze itself.
	 */
	public ClassicMaze clear() {
		cells = new byte[rows][cols];
		
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				cells[i][j] = 0;
			}
		}
		
		trace();
		
		return this;
	}
	
	/** Opens an exit on the side of the maze.
	 * <p>
	 * 
	 * Two warnings.  First, adding exits should
	 * usually be the last step; the toTileArray() routine copes with open walls on
	 * the outside of the maze, but other routines generally don't.  Second, this 
	 * routine is only guaranteed to create a nice exit if the maze is not sparse. 
	 * 
	 * @param side side on which to open the exit.
	 * @param i index of the row or column in which to open the exit.
	 * @return the maze itself.
	 */
	public ClassicMaze addExit(Direction side, int i) {
		if (i < 0 || 
			(side == Direction.N && i >= cols) ||
			(side == Direction.S && i >= cols) ||
			(side == Direction.E && i >= rows) ||
			(side == Direction.W && i >= rows))
			throw new IllegalArgumentException("invalid exit, " + side + ":" + i);
		
		switch (side) {
		case N:
			cells[0][i] |= side.mask;
			break;
		case S:
			cells[rows-1][i] |= side.mask;
			break;
		case E:
			cells[i][0] |= side.mask;
			break;
		case W:
			cells[i][cols-1] |= side.mask;
			break;
		}
		
		trace();
		return this;
	}

	/** Shortens/removes dead end paths from a maze by closing off dead end
	 * Positions one at a time.
	 * 
	 * @param times The number of Positions by which dead end paths should be shortened.
	 * Paths shorter than this will be removed altogether.
	 * @param chance The chance of removing a particular dead end Position.  If 1.0,
	 * the Position will always be removed; if less than 1.0, then it will sometimes
	 * be removed.
	 * @return The maze itself
	 */
	public ClassicMaze sparsify(int times, double chance) {
		List<Cell> ends = new ArrayList<>(0);
		
		while (times > 0) {
			times--;
			
			// FIRST, get the current dead end Positions, allowing some to be
			// skipped by chance.
			ends.clear();
			for (int i = 0; i < this.rows; i++) {
				for (int j = 0; j < this.cols; j++) {
					Cell here = new Cell(i,j);
					List<Direction> open = openDirections(here);
					
					if (open.size() == 1 && random.nextDouble() <= chance)
						ends.add(here);
				}
			}
			
			// NEXT, for each of the dead end Positions, close it up.
			for (Cell here : ends) {
				List<Direction> open = openDirections(here);
				closeWall(here, open.get(0));
				trace();
			}
		}
		return this;
	}
	
	/** Converts a perfect (dendritic) maze into a maze with no dead ends by
	 * connecting every dead end to a neighbor (if any).  Ignores free Positions,
	 * so it can be used with sparse mazes.  If chance is less than 1.0, then
	 * some dead ends will be skipped.
	 * 
	 * @param chance The chance that the dead end will be connected as a fraction
	 * from 0.0 to 1.0.
	 * @return
	 */
	public ClassicMaze connectify(double chance) {
		// FIRST, find the current dead end Positions, allowing some to be
		// skipped by chance.
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				Cell here = new Cell(i,j);
				
				// FIRST, is this a dead end?  If not, skip it.
				List<Direction> dirs = openDirections(here);
				
				if (dirs.size() != 1 || random.nextDouble() > chance)
					continue;
				 
				// NEXT, if it's not a dead end, get the directions with closed
				// walls that don't lead to free Positions.
				Direction dopen = dirs.get(0);
				dirs.clear();
				
				for (Direction d : Direction.values()) {
					if (d == dopen)
						continue;
					Cell there = nextPositionInDir(here, d);
					if (there != null && get(there) != 0) {
						dirs.add(d);
					}
				}
				
				// NEXT, if there are no non-free Positions adjacent,
				// skip this one.
				if (dirs.size() == 0)
					continue;
				
				carve(here, pickDirFrom(dirs), 1, false);
			}
		}

		return this;
	}
	
	// Utilities
	
	/** Passes the current state of the maze to the assigned tracer. */
	private void trace() {
		if (tracer != null) {
			tracer.trace(this);
		}
	}
	
	private int get(Cell c) {
		return cells[c.row][c.col];
	}
	
	/** Indicates whether there's a door open from a given room in
	 * the maze in a given direction.
	 * @param c The room's cell
	 * @param d The direction
	 * @return True if the door is open, and false otherwise.
	 */
	public boolean isOpen(Cell c, Direction d) {
		return (d.mask & cells[c.row][c.col]) > 0;
	}
	
	/** Returns the next Position in the given direction, or null if none.
	 * @param here a Position in the maze
	 * @param dir a direction
	 * @return the Position in that direction
	 */
	private Cell nextPositionInDir(Cell here, Direction dir) {
		int drow = here.row + dir.roff;
		int dcol = here.col + dir.coff;
		
		if (drow < 0 || drow >= rows || dcol < 0 || dcol >= cols)
			return null;
		return new Cell(drow, dcol);
	}
	
	/** @return a random Position in the maze.
	 */
	private Cell randomPosition() {
		return new Cell(random.nextInt(rows),random.nextInt(cols));
	}

	/** Get a list of the directions in which there are free Positions.
	 * @param here A Position in the maze
	 * @return a list of free directions.
	 */
	private List<Direction> freeDirections(Cell here) {
		List<Direction> freeDirs = new ArrayList<Direction>(0);
		
		for (Direction d : Direction.values()) {
			Cell there = nextPositionInDir(here,d);
			if (there != null && get(there) == 0)
				freeDirs.add(d);
		}
		
		return freeDirs;
	}

	/** Get a list of the directions in which doors are open from here.
	 * @param here A Position in the maze
	 * @return a list of directions.
	 */
	private List<Direction> openDirections(Cell here) {
		List<Direction> openDirs = new ArrayList<>(0);
		
		for (Direction d : Direction.values()) {
			if (isOpen(here,d))
				openDirs.add(d);
		}
		
		return openDirs;
	}

	/** Picks a random direction from a list of directions, and removes it from the list.
	 * TBD: Consider writing this as a generic static method and putting it somewhere.
	 * @param set The set of directions
	 * @return a random direction from the set
	 */
	private Direction pickDirFrom(List<Direction> list) {
		if (list.size() == 1)
			return list.remove(0);
		else 
			return list.remove(random.nextInt(list.size()));
	}

	/** Carves out one or more free Positions in direction dir from start.
	 * 
	 * @param start The none free Position to start with
	 * @param dir The direction in which to carve
	 * @param steps The maximum number of steps to take
	 * @param mustBeFree If true, the Position carved into must be free.
	 * @return The last Position carved out.
	 */
	private Cell carve(Cell start, Direction dir, int steps, boolean mustBeFree) {
		Cell here = start;
		while (steps > 0) {
			Cell next = nextPositionInDir(here, dir);
			
			// If we hit the edge, or a Position that's not free, we're done driving.
			if (next == null || (mustBeFree && get(next) != 0))
				break;
			
			openWall(here,next,dir);
			
			here = next;
			steps--;
		}
		
		trace();
		return here;
	}
	
	/** Opens the wall in the given direction, if it is closed.
	 * @param c the first Position 
	 * @param d the second Position
	 * @param dir the direction from c to d.
	 * */
	private void openWall(Cell c, Cell d, Direction dir) {
		if (isOpen(c, dir))
			return;
		
		cells[c.row][c.col] += dir.mask;
		cells[d.row][d.col] += dir.opposite().mask; 
	}
	
	/** Closes the wall in the given direction, if it is open.
	 * 
	 * @param c The Position
	 * @param d The direction in which to close the wall.
	 */
	private void closeWall(Cell c, Direction d) {
		if (isOpen(c,d)) {
			cells[c.row][c.col] -= d.mask;
			
			Cell c2 = nextPositionInDir(c,d);
			cells[c2.row][c2.col] -= d.opposite().mask;
		}
	}
}
