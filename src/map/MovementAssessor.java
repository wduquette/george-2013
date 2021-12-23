/**
 * 
 */
package map;

/** An Assessor assesses a cell for passability.  Which assessor is used
 * depends on the requirements; for example, a Mobile plotting a route
 * will usually want to exclude cells including features and other mobiles.
 * In other cases, only features should be included.
 * @author will
 */
public interface MovementAssessor {
	/** Is the cell passable for movement?
	 * 
	 * @param cell The cell in question.
	 * @return true if it is, and false otherwise.
	 */
	public boolean isPassable(Cell cell);
}