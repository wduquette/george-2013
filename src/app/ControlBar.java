/**
 * 
 */
package app;

import graphics.Buttons;
import graphics.Effects;
import graphics.Slots;
import graphics.Sprite;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import rpg.Item;
import rpg.pc.PlayerCharacter;

/** This is the Application Control Bar, a horizontal bar containing
 * various kinds of buttons.  Many relate to the current state of the
 * game, so the refresh() method needs to be called when things change.
 *
 * @author will
 */
@SuppressWarnings("serial")
final class ControlBar extends JPanel implements ActionListener {
	// GUI Constants
	private static final Color BACKGROUND = new Color(0xCC9F72);
	private static final Color BUTTON_BORDER = new Color(0x75400C);
	
	// Instance Variables
	private Board board;
	private WeaponButton weaponButton;
	private JButton magnifierButton;
	private List<AbstractButton> buttons = new ArrayList<>();
	private ModeButton modeButton;
	
	// Constructor
	ControlBar(Board board) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		setBackground(BACKGROUND);
		
		this.board = board;
		
		// FIRST, add buttons
		weaponButton = new WeaponButton();
		addButton(weaponButton);
		
		addButton(Buttons.BACKPACK, "inventory", "Inventory");
		addButton(Buttons.SCROLL, "quest", "Quest Log");
		addButton(Buttons.MAP, "map", "Overview Map");
		
		magnifierButton = 
				addButton(Buttons.MAGNIFIER, "magnifier", "Magnifier");
		
		// NEXT, add some space
		add(Box.createRigidArea(new Dimension(300,0)));
		
		// NEXT, add the Normal/Combat mode button.
		modeButton = new ModeButton(Mode.NORMAL);
		addButton(modeButton);

		// NEXT, add some glue
		add(Box.createHorizontalGlue());

		// NEXT, add the Save Button
		addButton(Buttons.SAVE, "save", "Save Game");

		// NEXT, add the Load Button
		addButton(Buttons.LOAD, "load", "Load Game");
	}
		
	/** Add a button to the control bar.
	 * 
	 * @param sprite The sprite
	 * @param command The action command
	 * @param tip The tooltip string.
	 */
	private JButton addButton(Sprite sprite, String command, String tip) {
		JButton button = new JButton(sprite.icon());
		button.setActionCommand(command);
		button.setToolTipText(tip);
		addButton(button);
		
		return button;
	}
	
	/** Add a button to the control bar.  The caller must
	 * set the action command.
	 * 
	 * @param button The button
	 */
	private void addButton(AbstractButton button) {
		button.addActionListener(this);
		button.setContentAreaFilled(false);
		button.setBorder(BorderFactory.createLineBorder(BUTTON_BORDER));
		add(button);
		buttons.add(button);
	}
	

	
	// Setters/getters
	
	/** Call this to have the ControlBar update its content. 
	 * TBD: Use a propertyChangeListener instead? */
	void refresh() {
		// FIRST, be consistent with the mode.
		modeButton.setMode(board.getMode());

		// NEXT, buttons are not always enabled.
		if (board.isUsingMagnifier()) {
			setAllEnabled(false);
			magnifierButton.setEnabled(true);
		} else {
			setAllEnabled(board.isWaiting() && board.getCard().equals(Board.MAP));
		}
		
		weaponButton.refresh(board.getSelectedPlayerCharacter());

	}
	
	/** Sets the enabled flag for all buttons.
	 * 
	 * @param flag  The value of the flag.
	 */
	private void setAllEnabled(Boolean flag) {
		for (AbstractButton button : buttons) {
			button.setEnabled(flag);
		}
	}
			
	/** Action listener for the buttons.  Calls the
	 * board's action listener.
	 * 
	 * @param e The action event.
	 */
	public void actionPerformed(ActionEvent e) {
		board.actionPerformed(e);
	}
	
	//-------------------------------------------------------------------------
	// Static Nested Classes
	
	private static class WeaponButton extends JButton {
		WeaponButton() {
			super(Effects.FIST.icon());
			setActionCommand("weapon");
			setToolTipText("Select preferred weapon");
			
			setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		}
		
		/** Display the player character's chosen weapon
		 * 
		 * @param pc The player character, or null.
		 */
		void refresh(PlayerCharacter pc) {
			if (pc == null) {
				setIcon(Slots.WEAPON_SLOT.icon());
				return;
			}
			
			Item weapon = pc.equipment.getWeapon();
			
			if (weapon != null) {
				setIcon(weapon.sprite().icon());
			} else {
				setIcon(Effects.FIST.icon());
			}
		}
	}

	private static class ModeButton extends JButton {
		/** Creates a button for controlling the game mode,
		 * NORMAL, or COMBAT. The button will display a different icon 
		 * in each case.
		 * @param mode The initial mode.
		 */
		ModeButton(Mode mode) {
			super(Buttons.NORMAL.icon());
			
			setContentAreaFilled(false);
			setMode(mode);
		}
		
		/** Sets the mode.  The button displays the mode,
		 * and updates the tool tip and action command accordingly.
		 * @param mode The new game mode
		 */
		void setMode(Mode mode) {
			if (mode.equals(Mode.NORMAL)) {
				setIcon(Buttons.NORMAL.icon());
				setToolTipText("Start Combat");
				setActionCommand("combat");
			} else {
				setIcon(Buttons.COMBAT.icon());
				setToolTipText("End Combat");
				setActionCommand("normal");
			}
		}
	}

}
