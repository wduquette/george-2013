/**
 * 
 */
package map;

import graphics.StandardTile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/** A rectangular array of Terrain objects.  Cells can be null.
 * @author will
 */
public final class TerrainGrid implements TerrainMap, Serializable {
	private static final long serialVersionUID = 1L;

	private final int rows;
	private final int cols;
	private TerrainTile[][] map;
	
	/** Creates a new Grid with the given number of rows and columns.
	 * @param rows  The number of rows in the array (1 or more)
	 * @param cols  The number of columns in the array (1 or more)
	 */
	public TerrainGrid(int rows, int cols) {
		if (rows <= 0)
			throw new IllegalArgumentException("rows <= 0");
		if (cols <= 0)
			throw new IllegalArgumentException("cols <= 0");
		
		this.rows = rows;
		this.cols = cols;
		
		map = new TerrainTile[rows][];
		
		for (int r = 0; r < this.rows; r++) {
			map[r] = new TerrainTile[cols];
		}
		
		clear();
	}
	
	/** @return the number of columns */
	@Override
	public int cols() {
		return cols;
	}
	
	/** @return the number of rows. */
	@Override
	public int rows() {
		return rows;
	}
	
	/** Is the cell within the bounds of the map?
	 * 
	 * @param cell The cell
	 * @return true if so and false otherwise. 
	 */
	public boolean inBounds(Cell cell) {
		return cell.row >= 0 &&
			   cell.row < rows &&
			   cell.col >= 0 &&
			   cell.col < cols;		
	}
	
	/** Clear to walls */
	public void clear() {
		clearTo(StandardTile.STONE_WALL);
	}
	
	/** Clear to a given tile.
	 * @param t the tile to clear to.
	 */
	public void clearTo(TerrainTile t) {
		for (int r = 0; r < this.rows; r++) {
			for (int c = 0; c < this.cols; c++) {
				map[r][c] = t;
			}
		}
	}
	
	/** Sets the terrain in the given cell.
	 * 
	 * @param cell The cell
	 * @param t The new terrain, or null.
	 */
	public void set(Cell cell, TerrainTile t) {
		set(cell.row, cell.col, t);
	}
	
	/** Sets the terrain in cell r,c of the array.
	 * @param r the row index
	 * @param c the column index
	 * @param t the new terrain, or null.
	 */
	public void set(int r, int c, TerrainTile t) {
		map[r][c] = t;
	}
	
	/** Sets the terrain in a rectangle with upperleft
	 * corner at cell r,c of the array.
	 * @param r the row index
	 * @param c the column index
	 * @param height height of the rectangle in tiles.
	 * @param width width of the rectangle in tiles.
	 * @param t the new terrain, or null.
	 */
	public void set(int r, int c, int height, int width, TerrainTile t) {
		for (int i = r; i < r + height; i++) {
			for (int j = c; j < c + width; j++) {
				map[i][j] = t;
			}
		}
	}
	
	/** Gets the terrain in the given cell.
	 * 
	 * @param cell The cell
	 * @return The terrain, or null if none.
	 */
	@Override
	public TerrainTile terrainAt(Cell cell) {
		return get(cell.row, cell.col);
	}
	
	/** Get the terrain in cell r,c of the array
	 * @param r the row index
	 * @param c the column index
	 * @return the tile in the cell, or null.
	 */
	public TerrainTile get(int r, int c) {
		return map[r][c];
	}
	
	//-------------------------------------------------------------------------
	// Lines of Sight and Trajectories
	
	/** Computes a straight line path from point a to point b,
	 * suitable for line-of-sight computations and trajectories.
	 * The code is a translation of the Bresenham line-of-sight
	 * path algorithm from _AI for Game Developers_, published
	 * by O'Reilly and Associates.
	 * 
	 * @param a The first point
	 * @param b The second point
	 * @return A list of cells from a to b, excluding a.
	 */
	public static List<Cell> getLine(Cell a, Cell b) {
		List<Cell> path = new ArrayList<>();
		
		int rNext = a.row;
		int cNext = a.col;
		int rEnd = b.row;
		int cEnd = b.col;
		int rDelta = rEnd - rNext;
		int cDelta = cEnd - cNext;
		
		int rStep = (rDelta < 0) ? -1 : 1;
		int cStep = (cDelta < 0) ? -1 : 1;
		
		int rDelta2 = 2*Math.abs(rDelta);
		int cDelta2 = 2*Math.abs(cDelta);
		
		if (cDelta2 > rDelta2) {
			int fraction = 2*rDelta2 - cDelta2;
			
			while (cNext != cEnd) {
				if (fraction >= 0) {
					rNext += rStep;
					fraction -= cDelta2;
				}
				
				cNext += cStep;
				fraction += rDelta2;
				path.add(new Cell(rNext, cNext));
			}
		} else {
			int fraction = 2*cDelta2 - rDelta2;
			
			while (rNext != rEnd) {
				if (fraction >= 0) {
					cNext += cStep;
					fraction -= rDelta2;
				}
				
				rNext += rStep;
				fraction += cDelta2;
				path.add(new Cell(rNext, cNext));
			}
		}
		
		return path;
	}
	
	//-------------------------------------------------------------------------
	// A* route-finding algorithm.
	

	private final class Walkability implements MovementAssessor {
		@Override
		public boolean isPassable(Cell cell) {
			return map[cell.row][cell.col].isWalkable();
		}
	}
	
	/** The A* algorithm, as described at Wikipedia.  Finds an efficient route from
	 * the starting point to the goal, if one exists.  The caller's assessor
	 * is used to find valid moves.  If no assessor is given, then the algorithm
	 * uses walkability of the terrain.
	 *
	 * @param start	The starting point (usually "here")
	 * @param goal The point to go to.
	 * @param assessor The terrain assessor to use, or null.
	 * @return The route from start to goal, or null if there is none.  The returned
	 * route does not include the starting point.
	 */
	public List<Cell> findAStarRoute(Cell start, Cell goal, MovementAssessor assessor) {
		if (assessor == null) {
			assessor = new Walkability();
		}
		
		// The set of nodes already evaluated
		Set<Cell> closedSet = new HashSet<Cell>();
	
		// The set of tentative nodes to be evaluated.
		Set<Cell> openSet = new HashSet<Cell>();
		openSet.add(start);
		
		// The map of navigated nodes
		Map<Cell,Cell> cameFrom = new HashMap<>();
		
		// Scores for the positions
		Map<Cell,Double> gScore = new HashMap<>();
		Map<Cell,Double> fScore = new HashMap<>();
		
		// Cost from start along best known path. 
		gScore.put(start, 0.0);
		
		// Estimated total cost from start to goal through y
		fScore.put(start, 0.0 + start.cartesian(goal));
		
		while (openSet.size() > 0) {
			// FIRST, find the node with the best fScore in the open set.
			Cell current = null;
			
			for (Cell pos : openSet) {
				if (current == null || fScore.get(pos) < fScore.get(current)) {
					current = pos;
				}
			}
			
			// NEXT, if it's the goal, then we are there.
			if (current.equals(goal)) {
				return reconstructRoute(cameFrom, goal);
			}
			
			openSet.remove(current);
			closedSet.add(current);
			
			for (Cell neighbor : neighborsOf(current, goal, assessor)) {
				if (closedSet.contains(neighbor))
					continue;
				
				double tentativeGScore = gScore.get(current) + current.cartesian(neighbor);
				
				if (!openSet.contains(neighbor) || tentativeGScore <= gScore.get(neighbor)) {
					cameFrom.put(neighbor, current);
					gScore.put(neighbor, tentativeGScore);
					fScore.put(neighbor, gScore.get(neighbor) + neighbor.cartesian(goal));
					if (!openSet.contains(neighbor))
						openSet.add(neighbor);
				}
			}
		}
		
		return null;
	}
	
	/** Return a list of passable spaces adjacent to a particular cell,
	 * taking the caller's definition of passability into account.
	 * If a goal cell is specified, it is assumed to be passable.
	 * @param cell the cell in question. 
	 * @param goal the goal cell.
	 * @param assessor the passability assessor.
	 * @return a list of passable cells. 
	 */
	public List<Cell> neighborsOf(Cell cell, Cell goal, MovementAssessor assessor) {
		assert assessor != null;
		
		List<Cell> moves = new ArrayList<>(0);
		
		for (int r = Math.max(cell.row-1,0); r <= Math.min(cell.row+1, rows - 1); r++) {
			for (int c = Math.max(cell.col-1,0); c <= Math.min(cell.col+1, cols - 1); c++) {
				Cell candidate = new Cell(r,c);
				
				if (candidate.equals(goal) || assessor.isPassable(candidate)) {
					moves.add(candidate);
				}
			}
		}
		
		return moves;
	}
	
	/** Builds the actual route from the tree of routes computed by A*.
	 * 
	 * @param cameFrom A* route tree
	 * @param node The last node in the route (i.e, the goal)
	 * @return A list of positions leading from the start point to the goal.
	 */
	private List<Cell> reconstructRoute(Map<Cell,Cell> cameFrom, Cell node) {
		List<Cell> route = new ArrayList<>(0);
		
		while (cameFrom.containsKey(node)) {
			route.add(node);
			node = cameFrom.get(node);
		}
		
		Collections.reverse(route);
		
		return route;
	}
	
	/** Returns the length of the best route from start to goal.  The caller's assessor
	 * is used to find valid moves.  If no assessor is given, then the algorithm
	 * uses walkability of the terrain.
	 *
	 * @param start	The starting point (usually "here")
	 * @param goal The point to go to.
	 * @param assessor The terrain assessor to use, or null.
	 * @return The length of the route, or Integer.MAX_VALUE if there is no route.
	 */
	public int travelDistance(Cell start, Cell goal, MovementAssessor assessor) {
		List<Cell> route = findAStarRoute(start, goal, assessor);
		
		if (route != null)
			return route.size();
		else 
			return Integer.MAX_VALUE;
	}
	
	
	//-------------------------------------------------------------------------

	/** 
	 * @return the tile array as a BufferedImage.
	 */
	@Override
	public BufferedImage toBufferedImage() {
		int width = cols*TerrainTile.SIZE;
		int height = rows*TerrainTile.SIZE;
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = img.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				// Remember, i is vertical but x is horizontal
				g.drawImage(map[r][c].image(), c*TerrainTile.SIZE, r*TerrainTile.SIZE, null);
			}
		}
		
		g.dispose();
		
		return img;		
	}
}
