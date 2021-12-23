/**
 * 
 */
package rpg.feature;

import javax.swing.JOptionPane;

import rpg.Mobile;
import util.Script;
import util.ScriptAction;
import app.Board;

/** Narrative text, that appears when the player steps on the cell 
 * containing the feature.  The narrative associated with a given 
 * name will only be displayed once.<p>
 * 
 * The name should be a unique ID associated with the specific 
 * narrative text: Narrative objects with the same name should refer
 * to the same narrative text.<p>
 * 
 * If a player should be shown the same text for any of a number of 
 * cells he steps in (i.e., a row of cells at the entrance to a town
 * or dungeon) then create multiple Narrative features all with the
 * same name.
 * 
 * @author will
 */
public class Narrative extends Trap {
	private static final long serialVersionUID = 1L;

	
	// Instance variables
	private String name = "";  // The identifier for this narrative string.
	private String text = "";  // The text on the sign.
	
	
	// Constructor
	
	/** Creates a new narrative with text.
	 * 
	 * @param name The unique identifier for this narrative.
	 * @param text The narrative text.
	 */
	public Narrative(String name, String text) {
		super();
		this.name = name;
		this.text = text;
	}
	
	/** Set the text on the sign.
	 * 
	 * @param text the new text.
	 */
	public final void setText(String text) {
		this.text = text;
	}
		
	//-------------------------------------------------------------------------
	// Feature Behavior
	
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
		
		// NEXT, skip empty narrative objects
		if (text == null)
			return false;
		
		// NEXT, if it's already been read skip it.
		if (Board.get().memory.containsKey(name))
			return false;

		// NEXT, add the entry to the script.
		script.add(new ShowNarrative(m));
		return true;
	}	
	
	
	//-------------------------------------------------------------------------
	// ScriptActions
		
	/** Displays the narrative. */
	protected class ShowNarrative implements ScriptAction {
		Mobile reader;
		
		/** Creates a new action.
		 * 
		 * @param reader The mobile who triggers the narrative.
		 */
		public ShowNarrative(Mobile reader) {
			this.reader = reader;
		}
		
		public void execute() {
			JOptionPane.showMessageDialog(Board.get(), text, "Narrative", 
					JOptionPane.PLAIN_MESSAGE, reader.bigIcon());
			Board.get().memory.put(name, "seen");
		}
	}
}
