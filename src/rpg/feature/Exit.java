/**
 * 
 */
package rpg.feature;

import graphics.Sprite;
import graphics.StandardTile;

import javax.swing.JOptionPane;

import rpg.Avoid;
import rpg.Feature;
import rpg.Mobile;
import rpg.pc.PlayerCharacter;
import util.Script;
import util.ScriptAction;
import app.Board;

/** An exit to another region.
 * @author will
 */
public class Exit extends Feature {
	private static final long serialVersionUID = 1L;

	/** All living PCs must be within this range to leave the region. */
	public final int EXIT_RANGE = 3;
	private String name;
	private Sprite sprite;
	private String toRegion;
	private String point;
	private boolean confirm = false;
	
	
	/** Creates a new region exit.  If the sprite is null, the feature
	 * will be invisible, i.e., the appearance will be provided by the
	 * underlying terrain.
	 * 
	 * @param name The name of the feature
	 * @param sprite The sprite to display, or null if none.
	 * @param toRegion The ID of the region it links to
	 * @param point The point of interest in the given region.
	 */
	public Exit(String name, Sprite sprite, String toRegion, String point) {
		super();

		if (sprite == null) {
			setVisible(false);
			sprite = StandardTile.UNKNOWN;
		}

		this.sprite = sprite;
		this.name = name; // TBD: Is name really needed here?
		this.toRegion = toRegion;
		this.point = point;
	}

	/** Creates a new invisible region exit.  The appearance will be 
	 * provided by the underlying terrain.
	 * 
	 * @param toRegion The ID of the region it links to
	 * @param point The point of interest in the given region.
	 */
	public Exit(String toRegion, String point) {
		this("", null, toRegion, point);
	}
	
	@Override public String name()   { return name; }
	@Override public Sprite sprite() { return sprite; }
	
	//-------------------------------------------------------------------------
	// Getters, setters
	
	/** Sets the confirmation flag.  If true, the user must confirm 
	 * before leaving the region.
	 * @param confirm true to require confirmation, and false otherwise.
	 */
	public void setConfirm(boolean confirm) {
		this.confirm = confirm;
	}
	
	/** @return the confirmation flag. */
	public boolean getConfirm() {
		return confirm;
	}
	
		
	//-------------------------------------------------------------------------
	// Queries

	/** You can walk onto traps. */
	@Override
	public boolean isWalkable() {
		return true;
	}

	/** You can fly over them. */
	@Override
	public boolean isFlyable() {
		return true;
	}
	
	/** @return true if the feature can be triggered by being
	 * stepped on.
	 * TBD: This should actually be: is the feature triggered
	 * by a given movement mode.
	 */
	@Override
	public boolean isStepTriggered() {
		return true;
	}
	
	/** The mobile steps on the exit, and the party is taken
	 * to the new region, if they are all close enough.
	 * 
	 * @param m The mobile
	 * @return true if anything happened, and false otherwise.
	 */
	@Override
	public boolean step(Script script, Mobile m) {
		// FIRST, skip non-party mobiles.
		if (!Board.get().party.contains(m))
			return false;

		// NEXT, add the entry to the script.
		script.add(new ExitRegion());
		return true;
	}
	
	//-------------------------------------------------------------------------
	// ScriptActions
	
	/** Exit the region. */
	protected final class ExitRegion implements ScriptAction {
		/** Creates a new action. */
		
		public void execute() {
			// FIRST, if any them are too far from the exit,
			// tell them so.
			for (PlayerCharacter pc : Board.get().party.members()) {
				if (pc.travelDistance(place(), Avoid.HAZARDS) > EXIT_RANGE) {
					log("I can't leave " + pc + " behind!");
					return;
				}
			}
			
			if (getConfirm()) {
				String text = "Do you really want to this leave\n" +
							  "this area?";
				// NEXT, pop up the option pane.
				int answer = JOptionPane.showConfirmDialog(Board.get(), 
						text, 
						"Leave this area?", 
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, 
						sprite().icon());
				
				if (answer != JOptionPane.YES_OPTION)
					return;
			}
			
			// NEXT, we're close enough, so go.
			region().exit(toRegion, point);
		}
	}
}
