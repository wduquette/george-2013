/**
 * 
 */
package rpg.feature;

import graphics.Features;
import graphics.Sprite;

import javax.swing.JOptionPane;

import rpg.Feature;
import rpg.Mobile;
import rpg.pc.PlayerCharacter;
import util.Script;
import util.ScriptAction;
import app.Board;

/** A sign, which can be read by the user.  If no
 * text is given, the user will be told they can't read it.
 * @author will
 */
public class Sign extends Feature {
	private static final long serialVersionUID = 1L;

	// Instance variables
	private String text = "";  // The text on the sign.
	
	
	// Constructor
	
	/** Creates a new sign.
	 */
	public Sign() {
		this("");
	}
	
	/** Creates a new sign with text.
	 * 
	 * @param text The sign's text.
	 */
	public Sign(String text) {
		super();
		this.text = text;
	}
	
	@Override public String name()   { return "Sign"; }
	@Override public Sprite sprite() { return Features.SIGN; }

	
	/** Set the text on the sign.
	 * 
	 * @param text the new text.
	 */
	public void setText(String text) {
		this.text = text;
	}
		
	//-------------------------------------------------------------------------
	// Queries
	/** @return true if the feature is poke-triggered, and false otherwise. */
	@Override
	public boolean isPokeTriggered() {
		// You can poke a sign to read it.
		return true;
	}

	/** You can walk onto stair cases. */
	@Override
	public boolean isWalkable() {
		return false;
	}

	/** You can fly over them. */
	@Override
	public boolean isFlyable() {
		return true;
	}
	
	/** @return true if the feature can be triggered by being
	 * stepped on.
	 */
	@Override
	public boolean isStepTriggered() {
		return false;
	}
	
	
	//-------------------------------------------------------------------------
	// Feature Behavior
	
	/** A mobile calls this to interact with the feature. */
	@Override
	public void poke(Script script, PlayerCharacter m) {
		script.add(new ReadSign(m));
	}
	
	
	//-------------------------------------------------------------------------
	// ScriptActions
		
	/** Allows a mobile to loot the chest. */
	protected class ReadSign implements ScriptAction {
		Mobile reader;
		
		/** Creates a new action.
		 * 
		 * @param reader The mobile who will loot the chest.
		 */
		public ReadSign(Mobile reader) {
			this.reader = reader;
		}
		
		public void execute() {
			String signText;
			
			if (text == null || text.equals("")) {
				signText = "The sign is in a language you don't understand.";
			} else {
				signText = text;
			}
			
			// Add some extra lines so that the text isn't stretched
			// vertically.
			JOptionPane.showMessageDialog(Board.get(), 
					signText + "\n\n\n\n", 
					"Sign", 
					JOptionPane.PLAIN_MESSAGE, bigIcon());
		}
	}
}
