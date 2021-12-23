/**
 * 
 */
package app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import rpg.Quest;

/** A widget for displaying quest status.
 * 
 * @author will
 */
@SuppressWarnings("serial") // Won't be serializing the actions.
final class QuestPanel extends JPanel implements ActionListener, ListSelectionListener {
	// GUI Constants
	private static final int MARGIN = 10;
	
	/** The background color for the panel. */
	static final Color BACKGROUND = new Color(0x8A4C0F);
	
	// Other Constants
		
	// Instance Variables
	private Board board;
	
	// Components
	private JList<String> acceptedList;
	private JList<String> completedList;
	private JTextArea textPane;
	
	// Constructor
	
	/** Builds up the panel.  The layout is as shown:
	 * 
	 * <code><pre>
	 * AD
	 * CD
	 * -B
	 * </pre></code>
	 * 
	 * where
	 * 
	 * A is a scrolling list of quests that have been accepted but
	 *   not completed.
	 * C is a scrolling list of quests that been completed.
	 * D is the description area
	 * B is the back button.
	 *
	 * Selecting a quest in either list displays that quest's
	 * description in the description area.
	 * 
	 * @param board The game board.
	 */
	
	QuestPanel(Board board) {
		super(new GridBagLayout());
		setBackground(BACKGROUND);
		setBorder(BorderFactory.createEmptyBorder(MARGIN, MARGIN, MARGIN, MARGIN));
		
		// FIRST, save the inputs.
		this.board = board;
		
		// NEXT, create the components
		acceptedList = createList(0, "Accepted Quests");
		completedList = createList(1, "Completed Quests");
		textPane = createDescription();
		createBackButton();
	}
	
 	private JList<String> createList(int row, String label) {
 		JList<String> list = new JList<>();
 		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
 		list.setVisibleRowCount(-1);
 		list.setBackground(BACKGROUND);
 		list.addListSelectionListener(this);
 		
 		JScrollPane listScroller = new JScrollPane(list);
 		listScroller.setPreferredSize(new Dimension(250, 350));
 		listScroller.setBackground(BACKGROUND);
 		
 		listScroller.setBorder(BorderFactory.createTitledBorder(label));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = row;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		if (row == 0)
			gbc.insets = new Insets(0, 0, 0, 10);
		else
			gbc.insets = new Insets(10, 0, 0, 10);
		gbc.anchor = GridBagConstraints.LINE_START;

 		add(listScroller, gbc);
		
 		return list;
	}

	/** Create the PC's description box */
	private JTextArea createDescription() {
		JTextArea pane = new JTextArea(20,40);
		pane.setBackground(BACKGROUND);
		pane.setBorder(BorderFactory.createTitledBorder("Description"));
		pane.setEditable(false);
	
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.gridheight = 4;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.PAGE_START;

 		add(pane, gbc);
 		
 		return pane;
	}

	/** Create the Back button. */
	private void createBackButton() {
		JButton back = new JButton("Back");
		back.setActionCommand("back");
		back.addActionListener(this);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.anchor = GridBagConstraints.LINE_END;
		gbc.weightx = 1;

		add(back, gbc);
	}
	
	// Utilities
	
	/** Refreshes the content of the panel. */
	public void refresh() {
		refreshList(acceptedList, board.quest.accepted());
		refreshList(completedList, board.quest.completed());
	}
	
	private void refreshList(JList<String> list, List<Quest> quests) {
		String titles[] = new String[quests.size()];
		
		for (int i = 0; i < quests.size(); i++) {
			titles[i] = quests.get(i).title();
		}

		list.setListData(titles);
	}
	
	/** Displays the given quest in the description box.
	 * 
	 * @param quest The quest to display
	 */
	private void setQuest(Quest quest) {
		String title;
		
		if (board.quest.isCompleted(quest)) {
			title = "Completed: " + quest.title();
			textPane.setText(title + "\n\n" + 
							 quest.completion() + "\n\n" +
							 "Original Description:\n" +
							 quest.description());
		} else {
			title = quest.title();
			textPane.setText(title + "\n\n" + quest.description());
		}
		
	}
	
	
	//-------------------------------------------------------------------------
	// Event Handling
	
	/** Handles list clicks. */
	public void valueChanged(ListSelectionEvent e) {
		// FIRST, ignore it if they are not done.
		if (e.getValueIsAdjusting())
			return;
		
		// NEXT, which list is it?
		int idx;
		List<Quest> quests;
		
		if (e.getSource().equals(acceptedList)) {
			quests = board.quest.accepted();
			idx = acceptedList.getSelectedIndex();
		} else {
			quests = board.quest.completed();
			idx = completedList.getSelectedIndex();
		}
		
		if (idx >= 0)
			setQuest(quests.get(idx));
	}
	
	/** Handles button presses. */
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "back":
			board.setCard(Board.MAP);
			break;

		default:
			board.println("Error, unknown action on QuestPanel: " + e.getActionCommand());
			break;
		}
	}
}