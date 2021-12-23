/**
 * 
 */
package map;

/** An enumeration of maze directions, with information about them. */
public enum Direction {
	/** North, i.e., up */
	N(-1,  0, 1), 
	
	/** South, i.e., down */
	S( 1,  0, 2), 

	/** East, i.e., right */
	E( 0,  1, 4), 
	
	/** West, i.e., left */ 
	W( 0, -1, 8);
	
	/** The row offset for this direction */
	public final int roff;
	
	/** The column offset for this direction */
	public final int coff;
	
	/** The bit mask for this direction. */
	public final int mask;
	
	Direction(int roff, int coff, int mask) {
		this.roff = roff;
		this.coff = coff;
		this.mask = mask;
	}
	
	/** @return the direction opposite to this direction. */
	public Direction opposite() {
		switch (this) {
		case N:
			return S;
		case S: 
			return N;
		case E: 
			return W;
		case W: 
		default:
			return E;
		}
	}
}