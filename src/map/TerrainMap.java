/**
 * 
 */
package map;

import java.awt.image.BufferedImage;
import java.util.List;

/** A TerrainMap is the interface required to query a TerrainGrid
 * without modifying it.
 * @author will
 *
 */
public interface TerrainMap {
	/** @return the number of columns */
	public int cols();

	/** @return the number of rows. */
	public int rows();
	
	/** Gets the terrain in the given cell.
	 * 
	 * @param cell The cell
	 * @return The terrain, or null if none.
	 */
	public TerrainTile terrainAt(Cell cell);

	
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
	public List<Cell> findAStarRoute(Cell start, Cell goal, MovementAssessor assessor);

	/** Return a list of passable spaces adjacent to a particular cell,
	 * taking the caller's definition of passability into account.
	 * If a goal cell is specified, it is assumed to be passable.
	 * @param cell the cell in question. 
	 * @param goal the goal cell.
	 * @param assessor the passability assessor.
	 * @return a list of passable cells. 
	 */
	public List<Cell> neighborsOf(Cell cell, Cell goal, MovementAssessor assessor);
	
	/** Returns the length of the best route from start to goal.  The caller's assessor
	 * is used to find valid moves.  If no assessor is given, then the algorithm
	 * uses walkability of the terrain.
	 *
	 * @param start	The starting point (usually "here")
	 * @param goal The point to go to.
	 * @param assessor The terrain assessor to use, or null.
	 * @return The length of the route, or Integer.MAX_VALUE if there is no route.
	 */
	public int travelDistance(Cell start, Cell goal, MovementAssessor assessor);
	
	/** 
	 * @return the tile array as a BufferedImage.
	 */
	public BufferedImage toBufferedImage();
}
