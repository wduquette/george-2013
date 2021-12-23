/**
 * 
 */
package app;

import graphics.Items;

import javax.swing.JLabel;

/** A label that displays the party's money.
 * @author will
 *
 */
class MoneyLabel extends JLabel {
	private static final long serialVersionUID = 1L;

	MoneyLabel() {
		super("The party has 0 gold pieces", 
				Items.GOLD_COIN.icon(), JLabel.LEFT);
	}
	
	/** Asks the money label to refresh its display. */
	void refresh() {
		int gold = Board.get().party.gold();
		if (gold == 1)
			setText("The party has " + gold + " gold piece");
		else
			setText("The party has " + gold + " gold pieces");
	}
}
