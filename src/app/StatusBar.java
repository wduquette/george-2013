/**
 * 
 */
package app;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import rpg.pc.PlayerCharacter;

/** This is the PlayerCharacter status bar, a vertical bar containing
 * StatusBox widgets.  This object is responsible for managing the selection
 * and for enabling and disabling the whole set.
 * 
 * The individual status boxes are given action commands pc0 through pcN,
 * which they pass along to the setActionListener().
 * 
 * TBD: Consider firing a property change event instead of using ActionListener.
 * TBD: Consider taking Board as an input and getting property change events
 * from Board for the player character.
 * @author will
 */
@SuppressWarnings("serial")
final class StatusBar extends JPanel implements ActionListener {
	// Constants
	private static final int MAX_PCS = 4;
	
	// Instance Variables
	private boolean enabled = true;
	private ActionListener delegate = null;
	private ButtonGroup bgroup = new ButtonGroup();
	private List<StatusBox> boxes = new ArrayList<>(MAX_PCS);
	
	// Constructor
	StatusBar() {
		// FIRST, configure the component
		super();
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		setBackground(Color.BLACK);
		
		// NEXT, add status boxes.
		for (int i = 0; i < MAX_PCS; i++) {
			StatusBox sb = new StatusBox();
			sb.addActionListener(this);
			sb.setActionCommand("pc" + i);
				
			add(sb);
			boxes.add(sb);
			bgroup.add(sb);
		}
			
		add(Box.createVerticalGlue());

		boxes.get(0).setSelected(true);
		setEnabled(enabled);
	}
	
	/** Programmatically selects a particular player character.
	 * 
	 * @param pc  The PC to select.
	 */
	void select(PlayerCharacter pc) {
		for (StatusBox sb : boxes) {
			if (pc.equals(sb.getPlayerCharacter())) {
				sb.setSelected(true);
				break;
			}
		}
	}

	/** Programmatically selects a particular player character
	 * by party index.
	 * @param idx The index.
	 */
	void select(int idx) {
		assert 0 <= idx && idx < boxes.size();
		
		boxes.get(idx).setSelected(true);
	}
	
	/** @return the currently selected player character, or null if none. */
	PlayerCharacter getSelection() {
		for (StatusBox sb : boxes) {
			if (sb.isSelected()) {
				return sb.getPlayerCharacter();
			}
		}

		return null;
	}
	
	// Party Management
	
	/** Sets up the status bar to display a particular party.
	 * Call this on start-up, and when the party members change.
	 * @param party The party of player characters.
	 */
	void setParty(List<PlayerCharacter> party) {
		assert party.size() <= MAX_PCS;
		
		// FIRST, assign party members to the boxes.
		for (int i = 0; i < MAX_PCS; i++) {
			if (i < party.size())
				boxes.get(i).setPlayerCharacter(party.get(i));
			else 
				boxes.get(i).setPlayerCharacter(null);
		}
		
		// NEXT, the first box is selected by default, and
		// the enabled flag is retained.
		boxes.get(0).setSelected(true);
		setEnabled(enabled);
	}
	
	
	/** Enables or disables user input for the status bar.
	 * 
	 * @param enabled true if enabled and false is disabled.
	 */
	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		for (StatusBox sb : boxes) {
			if (sb.getPlayerCharacter() != null)
				sb.setEnabled(enabled);
			else
				sb.setEnabled(false);
		}		
	}
		
	/** Sets (not adds) an action listener on this object.
	 * It will be called when one of the buttons are pressed.
	 * 
	 * @param listener the listener.
	 */
	void setActionListener(ActionListener listener) {
		this.delegate = listener;
	}
	
	/** Action listener for the buttons.  Calls the
	 * bar's action listener.
	 * 
	 * @param e The action event.
	 */
	public void actionPerformed(ActionEvent e) {
		if (delegate != null) {
			delegate.actionPerformed(e);
		}
	}
}
