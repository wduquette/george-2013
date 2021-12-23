/**
 * 
 */
package map;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** A row/column position in an array.  Supports comparison, etc.
 * TBD: Revise all methods to use Cell rather than row,col.
 * TBD: Consider requiring method access to row and col.
 * @author will
 */
public final class Cell implements Serializable {
	private static final long serialVersionUID = 1L;

	/** The row index */
	public final int row;
	
	/** The column index */
	public final int col;
	
	/** Creates a new position.
	 * 
	 * @param row  The row index
	 * @param col  The column index
	 */
	public Cell(int row, int col) {
		this.row = row;
		this.col = col;
	}
	
	/** Creates a new cell offset from this cell.
	 * 
	 * @param rdelt Row offset
	 * @param cdelt Column offset
	 * @return The new cell.
	 */
	public Cell offset(int rdelt, int cdelt) {
		return new Cell(row + rdelt, col + cdelt);
	}
	
	/** Creates a new cell, an offset of steps away in the
	 * given direction.
	 * @param d The direction
	 * @param steps The number of steps
	 * @return The new cell.
	 */
	public Cell offset(Direction d, int steps) {
		return new Cell(row + steps*d.roff, 
					    col + steps*d.coff);
	}

 	@Override
	public String toString() {
		return "(" + row + "," + col + ")";
	}

 	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + this.col;
		result = prime * result + this.row;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cell other = (Cell) obj;
		if (this.col != other.col)
			return false;
		if (this.row != other.row)
			return false;
		return true;
	}

	/** Compute the Cartesian distance between this and the other cell.
	 * 
	 * @param cell The other cell.
	 * @return The Cartesian distance between the two cells.
	 */
	public double cartesian(Cell cell) {
		return cartesian(row, col, cell.row, cell.col);
	}

	/** Compute the cartesian distance between two points.
	 * 
	 * @param r1 Row coordinate of point 1
	 * @param c1 Column coordinate of point 1
	 * @param r2 Row coordinate of point 2
	 * @param c2 Column coordinate of point 2
	 * @return The cartesian distance.
	 */
	public static double cartesian(int r1, int c1, int r2, int c2) {
		return Math.sqrt((r2 - r1)*(r2 - r1) + (c2 - c1)*(c2 - c1));
	}
	
	/** Compute the "diagonal" distance between this and the other cell: the
	 * maximum of the horizontal and vertical distances.  
	 * This is the number of moves required to get between the
	 * two cells in open terrain when diagonal moves are allowed.
	 * 
	 * @param cell The other cell
	 * @return The diagonal distance.
	 */
	public int diagonal(Cell cell) {
		return diagonal(row, col, cell.row, cell.col);
	}

	/** Compute the "diagonal" distance between two cells: the
	 * maximum of the horizontal and vertical distances.  
	 * This is the number of moves required to get between the
	 * two cells in open terrain when diagonal moves are allowed.
	 * 
	 * @param r1 Row coordinate of point 1
	 * @param c1 Column coordinate of point 1
	 * @param r2 Row coordinate of point 2
	 * @param c2 Column coordinate of point 2
	 * @return The diagonal distance.
	 */
	public int diagonal(int r1, int c1, int r2, int c2) {
		return Math.max(Math.abs(r1 - r2), Math.abs(c1 - c2));
	}

	/** Sort a list of cells by diagonal distance from
	 * this cell, closest first.
	 * @param list  The list to sort.
	 */
	public void sortByDiagonal(List<Cell> list) {
		// Sort cells by distance from this.
		Collections.sort(list, new Comparator<Cell>() {
		    public int compare(Cell c1, Cell c2) {
		        return diagonal(c1) - diagonal(c2);
		    }});
	}
}
