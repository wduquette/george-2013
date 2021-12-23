/**
 * 
 */
package rpg.feature;

import graphics.Features;
import graphics.Sprite;

import java.util.List;

import map.Cell;
import rpg.Feature;
import rpg.Mobile;
import util.Script;
import util.ScriptAction;

/**
 * @author will
 *
 */
public class Portal extends Feature {
	private static final long serialVersionUID = 1L;

	@Override public String name()   { return "Portal"; }
	@Override public Sprite sprite() { return Features.PORTAL_ACTIVE; }
	
	//-------------------------------------------------------------------------
	// Queries

	/** Portals are opaque when active. */
	@Override
	public boolean isOpaque() {
		// Always active, at present.
		return true;
	}
	
	/** You can walk into portals. */
	@Override
	public boolean isWalkable() {
		return true;
	}
	
	/** You can fly into portals. */
	@Override
	public boolean isFlyable() {
		return true;
	}
	
	/** Is triggered by stepping into it. */
	@Override
	public boolean isStepTriggered() {
		return true;
	}
	
	
	//-------------------------------------------------------------------------
	// Feature Behavior
	
	/** When a mobile enters the portal, make it go somewhere random. 
	 * @param m The mobile walking on this feature.
	 * @return true if anything happened, and false otherwise.
	 */
	@Override
	public boolean step(Script script, Mobile m) {
		List<Cell> open = region().getOpenCells();
		
		if (open.size() > 0) {
			int index = random.nextInt(open.size());
			script.add(new MagicMove(m, open.get(index)));
			return true;
		}
		
		return false;
	}
	
	//-------------------------------------------------------------------------
	// ScriptActions
	
	/** Moves the mobile to a cell without triggering any
	 * feature at that cell.
	 * @author will
	 *
	 */
	protected class MagicMove implements ScriptAction {
		private Mobile mover;
		private Cell cell; 
		
		/** Action: Move the mobile to a particular cell.
		 * 
		 * @param mover the mobile
		 * @param cell The cell
		 */
		public MagicMove(Mobile mover, Cell cell) {
			this.mover = mover;
			this.cell = cell;
		}
		
		/** Move to the cell. */
		public void execute() {
			mover.place(cell);
		}
	}
}
