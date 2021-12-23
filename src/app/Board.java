/**
 * 
 */
package app;

import graphics.Items;
import graphics.Sprite;
import gui.ScrollingLog;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.Timer;

import map.Cell;
import rpg.Entity;
import rpg.Item;
import rpg.Mobile;
import rpg.MovementType;
import rpg.Quest;
import rpg.Region;
import rpg.Shopkeeper;
import rpg.feature.Furniture;
import rpg.monster.Monster;
import rpg.pc.George;
import rpg.pc.PlayerCharacter;
import rpg.world.World;
import rpg.world.bugland.BugLand;
import rpg.world.castle_floob.FloobEntry;
import rpg.world.floobham.Floobham;
import rpg.world.floobham.Sewers;
import util.Dict;
import util.Script;

/** This module is the main GUI and logic module.  It creates the
 * maze, and displays George walking it.
 * @author will
 */
 // Won't be serializing the actions.
public class Board extends JPanel implements ActionListener {
	private static final long serialVersionUID = 6360525226613004197L;

	//-------------------------------------------------------------------------
	// Development Constants
	
	/** Enable to turn on debug mode. */
	public static boolean DEBUG = false;

	//-------------------------------------------------------------------------
	// Singleton Implementation
	
	private static Board theBoard = null;
	
	/** Use this to acquire the sole instance of the Board object.
	 * 
	 * @return the Board singleton.
	 */
	public static Board get() {
		if (theBoard == null) {
			theBoard = new Board();
		}
		
		return theBoard;
	}
	
	//-------------------------------------------------------------------------
	// Major Components
	
	/** The API for managing the party of player characters. */
	public PartyManager party;
	
	/** The API for accepting and completing quests. */
	public QuestManager quest;
	
	/** The key/value memory: a Dict for stashing arbitrary data. */
	public Dict memory;
	
	//-------------------------------------------------------------------------
	// Application Constants
	
	// Model Constants
	
	/** Idle Range: Monsters outside this range of the party don't try to
	 * move.
	 */
	private static final int IDLE_RANGE = 15;

	/** Enter and remain in combat if there are visible, hostile, awake enemies 
	 *  within this range of any member of the party. */
	private static final int COMBAT_RANGE = 8;
	
	/** Can't leave combat if the PCs are more than this apart. */
	private static final int LEAVE_COMBAT_RANGE = 4;
	
	/** The number of ticks that pass during one full found of combat. */
	private static final int COMBAT_TICKS = 8;

	
	// Card Names
	
	/** Identifies the Map card. */
	public static final String MAP = "Map";
	
	/** Identifies the Inventory card. */
	public static final String INVENTORY = "Inventory";
	
	/** Identifies the Quest Log card. */
	public static final String QUEST = "QUEST";
	
	/** Identifies the Over Map card. */
	public static final String BIGMAP = "BIGMAP";
	
	/** Identifies the Shop card. */
	public static final String SHOP = "SHOP";
	
	// GUI constants
	
	// Size of the map panel, in tiles
	private static final int VISIBLE_ROWS = 20;
	private static final int VISIBLE_COLS = 25;
	
	// Delay, in milliseconds, between steps.
	private static final int DELAY = 50;
	
	//-------------------------------------------------------------------------
	// Static Variables
	
	private static Map<String,Class<? extends Region>> regionRegistry 
		= registerRegions();
	
	private static Map<String,Class<? extends Region>> registerRegions() {
		Map<String,Class<? extends Region>> registry = new HashMap<>();
		
		registry.put(Floobham.ID, Floobham.class);
		registry.put(Sewers.ID, Sewers.class);
		registry.put(BugLand.ID, BugLand.class);
		registry.put(FloobEntry.ID, FloobEntry.class);
		
		return registry;
	}
		
	//-------------------------------------------------------------------------
	// Instance variables
	
	// GUI 
	private StatusBar statusBar;
	private JPanel cards;
	private MapPanel mapCard;
	private InventoryPanel inventoryCard;
	private QuestPanel questCard;
	private BigMapPanel bigMapCard;
	private ShopPanel shopCard;
	private String currentCard = MAP;
	private ControlBar controlBar;
	private ScrollingLog slog;
	private Entity effectSprite = null;

	// Model 
	private Timer timer;
	private Script script = new Script();
	
	private Map<String,Region> regions;
	private Region region;  // The current region
	
	private boolean inGame = true;
	private int clock = 0;
	private Mode mode = Mode.NORMAL;
	private boolean usingMagnifier = false;
	
	// This variable indicates whether or not there's a player character's
	// turn in progress.  If false, there is; if true, there isn't.
	// It's initially false, because, of course, the first thing that's going
	// to happen is the first player gets his move.
	private boolean turnOver = false;  
	
	// Mover Management
	private List<Mobile> moveOrder = new ArrayList<>();
	
	//-------------------------------------------------------------------------
	// Creation and Initialization of the Application
	//
	// This section contains all routines required to create the GUI.
	
	/** Creates a new board and the entities on it. The layout
	 * of the components is as follows:
	 * <code><pre>
	 * +---+-----------+
	 * | S |   M       |
	 * |---+-----------+
	 * |       C       |
	 * |---------------+
	 * |       L       |
	 * +---------------+ 
	 * </pre></code> 
	 * 
	 * where S is the status bar, M is the map/inventory panel, C is
	 * the control bar, and L is the scrolling log.
	 */
	private Board() {
		// FIRST, give the Board a GridBagLayout.
		setLayout(new GridBagLayout());
		
		// NEXT, create the status bar
		statusBar = new StatusBar();
		statusBar.setActionListener(this);
		
		GridBagConstraints spc = new GridBagConstraints();
		spc.gridx = 0;
		spc.gridy = 0;
		spc.gridwidth = 1;
		spc.gridheight = 1;
		spc.fill = GridBagConstraints.VERTICAL;
		add(statusBar, spc);
		
		
		// NEXT, create the cards panel to contain the map, inventory,
		// etc.
		
		cards = new JPanel(new CardLayout());
		GridBagConstraints mpc = new GridBagConstraints();
		mpc.gridx = 1;
		mpc.gridy = 0;
		mpc.fill = GridBagConstraints.BOTH;
		add(cards, mpc);
		
		// NEXT, create the map card, adding a mouse listener to get
		// mouse clicks.
		mapCard = new MapPanel(this, VISIBLE_ROWS, VISIBLE_COLS, region);
		cards.add(mapCard, MAP);
		mapCard.addMouseListener(new MapMouseHandler());		
		
		// NEXT, create the inventory card.
		inventoryCard = new InventoryPanel(this);
		cards.add(inventoryCard, INVENTORY);
		
		// NEXT, create the quest card
		questCard = new QuestPanel(this);
		cards.add(questCard, QUEST);
		
		// NEXT, create the big map card
		bigMapCard = new BigMapPanel(this, 
				VISIBLE_COLS*Sprite.SIZE, VISIBLE_ROWS*Sprite.SIZE);
		cards.add(bigMapCard, BIGMAP);
		
		// NEXT, create the shop card.  This one has no control button;
		// rather, it's displayed when the player talks to a shopkeeper.
		shopCard = new ShopPanel(this);
		cards.add(shopCard, SHOP);
		
		// NEXT, Create the Control bar.  Let's try a flow layout.
		controlBar = new ControlBar(this);
		GridBagConstraints cpc = new GridBagConstraints();
		cpc.gridx = 0;
		cpc.gridy = 1;
		cpc.gridwidth = 2;
		cpc.fill = GridBagConstraints.HORIZONTAL;
		add(controlBar, cpc);
		
		// NEXT, create the scrolling log
		slog = new ScrollingLog(8,40);
		slog.setLogFont(App.LOG_FONT);
		GridBagConstraints lpc = new GridBagConstraints();
		lpc.gridx = 0;
		lpc.gridy = 2;
		lpc.gridwidth = 2;
		lpc.fill = GridBagConstraints.HORIZONTAL;
		add(slog, lpc);

		// NEXT, create the timer; George will move as the timer fires.  The
		// timer will be started when George gets his first goal.
		timer = new Timer(DELAY, this);
		timer.setActionCommand("timer");
	}
	
	//-------------------------------------------------------------------------
	// Initialization of the Model
	// 
	// This section contains the routines required to get a new game going.
	
	/** Starts the game. */
	public void start() {
		// FIRST, create the memory objects.
		party = new PartyManager();
		quest = new QuestManager();
		memory = new Dict();
		regions = new HashMap<>(); 

		// NEXT, create the initial region.
		region = new World();
		regions.put(World.ID,  region);
		
		// NEXT, set the time.
		clock = 0;
		
		// NEXT, create George and add him to the party.
		PlayerCharacter george = new George();
		join(george);
		
		// NEXT, our heroes enter the region.
		enter(World.ID, World.ORIGIN);
		
		// NEXT, set the mode.
		selectPlayerCharacter(party.leader());
		setMode(Mode.NORMAL);

		repaint();
	}
	
	/** End the game.  Output the message, so the user knows
	 * that it's over, stop the timer so that nothing more happens,
	 * and restart.
	 */
	public void finish() {
		log("Your party is dead!");
		JOptionPane.showMessageDialog(this,
				"Your entire party is dead;\ntime to start over.",
				"You have died.",
				JOptionPane.PLAIN_MESSAGE,
				Items.SKULL.bigIcon());
		timer.stop();
		start();
	}

	/** A player character joins the party.   This is called first
	 * when adding George at start-up.
	 * @param pc The new party member */
	public void join(PlayerCharacter pc) {
		party.add(pc);
		statusBar.setParty(party.allMembers());
		
		if (party.allMembers().size() > 1) {
			pc.place(region, party.leader().place());
			resetMovement();

			JOptionPane.showMessageDialog(this, 
					pc.name() + " joins the party.",
					pc.name(), 
					JOptionPane.PLAIN_MESSAGE, 
					pc.bigIcon());
		}
	}
	
	//-------------------------------------------------------------------------
	// Clock Management
	
	/** @return the current game time in ticks. */
	public int time() {
		return clock;
	}
	
	/** Increment the game clock. This occurs for each step in NORMAL mode,
	 * and each time the party leader's turn begins in COMBAT mode. */
	private void tick() {
		if (inCombat()) {
			clock += COMBAT_TICKS; 
		} else {
			clock += 1;
		}

		if (DEBUG) {
			println("Tick: " + clock);
		}
	}

	//-------------------------------------------------------------------------
	// User Interaction Handling
	//
	// This section includes all of the code that responds to user mouse clicks
	// on the Board or MapPanel.  (The InventoryPanel has its own internal
	// handling.)
	
	
	/** This routine dispatches actions sent by various buttons and timers to
	 * the action handlers.
	 * @param e The action event
	 */
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "timer": 				// The animation timer has fired.
			if (inCombat())
				doCombatTimer(e);
			else
				doNormalTimer(e);
			break;
		case "combat":
			tryToEnterCombat();
			repaint();
			break;
		case "debug":
			doDebug(e);
			repaint();
			break;
		case "normal":
			tryToLeaveCombat();
			repaint();
			break;
		case "inventory":		// The user has pressed the Inventory button.
			doInventory(e);
			break;
		case "quest":  			// The user has pressed the Quest Log button.
			doQuestLog(e);
			break;
		case "map":             // The user pressed the Overview Map button.
			doBigMap(e);
			break;
		case "magnifier":       // The user pressed the Magnifier button.
			doMagnifier();
			break;
		case "pc0":
			doSelectPlayerCharacter(0);
			break;
		case "pc1":
			doSelectPlayerCharacter(1);
			break;
		case "pc2":
			doSelectPlayerCharacter(2);
			break;
		case "pc3":
			doSelectPlayerCharacter(3);
			break;	
		case "save":
			if (inCombat()) {
				log("Can't save game while in combat.");
				return;
			}
			GameSaver saver = new GameSaver();
			saver.popup();
			break;
		case "load":
			GameLoader loader = new GameLoader();
			loader.popup();
			break;
		case "weapon":
			doWeapon();
			break;
		default:
			println("Error, unknown control action: " + e.getActionCommand());
			break;
		}
	}
	
	
	/** This action does whatever's needed to exercise code during 
	 * debugging. 
	 * @param e
	 */
	private void doDebug(ActionEvent ae) {
		println("Debug: nothing here");
	}
	


	/** Timer Animation Handler, NORMAL mode.  This method handles movement and 
	 * animation updates each time the animation timer goes off.
	 * 
	 * Note: In NORMAL mode, the moveOrder doesn't matter.
	 * 
	 * @param e The action event.
	 */
	private void doNormalTimer(ActionEvent e) {
		// FIRST, increment the clock.
		tick();
		
		// NEXT, the selected PC gets to move.  If he has no goal, we
		// wait for input.  When he moves, we run the animation script immediately;
		// The other PCs are invisible and follow him, i.e., their move commands aren't 
		// called.  We animate his activities.
		
		// Get the selected PC
		PlayerCharacter leader = party.leader();
		hideAllButLeader();
		
		// If the leader has no goal, stop the timer and wait.
		if (leader.getGoal() == null) {
			timer.stop();
			return;
		}
		
		// Let him try to move.  This is NORMAL mode, so he'll only take one step.
	
		boolean over = leader.move(script);
		
		// Animate his script and move the other PCs to be on top of him.
		while (script.isNotEmpty()) {
			script.step();
		}
		
		for (PlayerCharacter pc : party.allMembers()) {
			if (!pc.equals(leader)) {
				pc.place(leader.place());
			}
		}

		repaint();
		
		// NEXT, if the party is dead (he stepped on a trap) we're done.
		if (party.isDead()) {
			finish();
			return;
		}
		
		// If his turn is not over, that's because the goal didn't work, and
		// we need to wait for input again.  Similarly, if we're in combat
		// we need to end and wait for instructions.
		if (!over || inCombat()) {
			timer.stop();
			return;
		}
		
		// NEXT, we animate all of the monsters and NPCs.  For each one, call 
		// its move, then run its animation.  If it's too far away from the 
		// leader, ignore it.
		for (Mobile m : region.getNonPartyMobiles()) {
			if (m.place().diagonal(leader.place()) >= IDLE_RANGE) {
				continue;
			}
			
			// FIRST, get his move.
			m.move(script);
			
			// NEXT, animate it.
			while (script.isNotEmpty())
				script.step();

			// NEXT, if he killed the last party member, it's all over.
			if (party.isDead()) {
				finish();
				return;
			}
			
			
			// NEXT, should we enter combat?
			if (shouldBeInCombatMode()) {
				setMode(Mode.COMBAT);
				timer.stop();
				repaint();
				return;
			}
		}
		
		repaint();
	}
	
	/** Timer Animation Handler, COMBAT mode.  This method handles movement and 
	 * animation updates each time the animation timer goes off.
	 * @param e The action event.
	 */
	private void doCombatTimer(ActionEvent e) {
		assert inGame == true;
		
		// FIRST, if the animation script isn't empty, animate and go on;
		// otherwise, assess the current state of play.
		if (script.isNotEmpty()) {
			script.step();
			repaint();
			return;
		}
		
		// NEXT, the script is complete; assess the outcome.
		if (party.isDead()) {
			finish();
			return;
		}
		
		// NEXT, if the current mover is dead, his turn is over.
		if (!moveOrder.get(0).isAlive()) {
			turnOver = true;
		}

		// NEXT, remove dead mobiles from the region and the move order.
		// TBD: This is funky; there has to be a better way to do this.
		List<Mobile> dead = region.removeDeadMobiles();
		removeDeadMovers(dead);
		
		// NEXT, find a mover who actually moves; don't delay for
		// movers who don't.
		
		while (script.isEmpty()) {
			// FIRST, the previous mover's turn is not over, then he's a PC.
			// If he doesn't have a goal, wait for one.
			if (!turnOver) {
				PlayerCharacter pc = (PlayerCharacter)moveOrder.get(0);
			
				selectPlayerCharacter(pc);

				if (pc.getGoal() == null) {
					timer.stop();
					return;
				}
			} else {
				// It's time for the next mover to go.  Remove the current
				// one, and reset the move order if the list is empty.
				moveOrder.remove(0);

				if (moveOrder.size() == 0) {
					tick();  // Tick once after everyone moves.
					resetCombatMoveOrder();
				}
			}

			turnOver = moveOrder.get(0).move(script);
			
			controlBar.refresh();
			repaint();
		}
	}
	
	/** Inventory Button Handler.  When the user presses the inventory button,
	 * this routine displays the inventory panel.
	 * @param e The action event
	 */
	private void doInventory(ActionEvent e) {
		if (isWaiting()) {
			inventoryCard.refresh();
			setCard(Board.INVENTORY);
		}
	}
	
	/** Quest Log Button Handler.  When the user presses the button,
	 * this routine displays the quest log panel.
	 * @param e The action event
	 */
	private void doQuestLog(ActionEvent e) {
		if (isWaiting()) {
			questCard.refresh();
			setCard(Board.QUEST);
		}
	}

	/** Overview Map Button Handler.  When the user presses the button,
	 * this routine displays the overview map panel.
	 * @param e The action event
	 */
	private void doBigMap(ActionEvent e) {
		if (isWaiting()) {
			bigMapCard.setRegion(region);
			setCard(Board.BIGMAP);
		}
	}

	/** Weapon Button Handler.  Toggles the selected PC's weapon preference.
	 */
	private void doWeapon() {
		PlayerCharacter pc = getSelectedPlayerCharacter();
		
		if (pc != null) {
			pc.equipment.toggleWeaponPreference();
		}
		
		controlBar.refresh();
	}
	
	/** Magnifier Button Handler.  When the user presses the button,
	 * the board enters magnifier mode; clicking on a cell displays
	 * a description of that cell.
	 * @param e The action event
	 */
	private void doMagnifier() {
		if (!isWaiting()) {
			return;
		}
		
		usingMagnifier = !usingMagnifier;
		
		controlBar.refresh();
	}
	
	/** @return true if the player is using the magnifier, and false
	 * otherwise.
	 */
	public boolean isUsingMagnifier() {
		return usingMagnifier;
	}



	/** Selects the given player character.  The action taken
	 * depends on the mode and the state of the GUI.  We allow
	 * for up to four PCs at one time, but not all of them might
	 * be there.
	 * 
	 * NOTE: The status box buttons are disabled while in COMBAT.
	 * @param idx The PC's index in the party.
	 */
	private void doSelectPlayerCharacter(int idx) {
		assert !inCombat();

		
		// FIRST, if there's no active PC with this index, move on.
		if (idx >= party.size())
			return;
		
		// NEXT, if we're in NORMAL mode, switch the PC that's
		// shown.
		if (!inCombat())
			hideAllButLeader();
			
		// NEXT, if the inventory is displayed, show this PC.
		// TBD: Make the party leader a property, so that we can
		// use a propertyChange event for this.
		if (getCard().equals(INVENTORY)) {
			inventoryCard.refresh();
		} else if (getCard().equals(SHOP)) {
			shopCard.refresh();
		}
		
		controlBar.refresh();
	}

	/** Selects the particular PC.
	 * 
	 * @param pc  The PC.
	 */
	private void selectPlayerCharacter(PlayerCharacter pc) {
		statusBar.select(pc);
		
		// If we are in normal mode, hide all but the leader.
		if (!inCombat())
			hideAllButLeader();
		
		controlBar.refresh();
	}
	
	/** @return the PC with selected status box. */
	public PlayerCharacter getSelectedPlayerCharacter() {
		return statusBar.getSelection();
	}
	
	
	/** Detects a click on the map, and notifies the object. */
	private class MapMouseHandler extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			Cell rc = mapCard.xy2cell(e.getX(), e.getY());
			
			if (rc != null) {
				handleMapClick(rc);
			}
		}
	}

	/** This is called when the user clicks on the map with a mouse. 
	 * If the application is waiting for input for a player character,
	 * then the the chosen cell is given to that PC as his new
	 * goal.  Then we start the timer so that he can move to it.
	 * 
	 * @param cell The cell the user clicked on.
	 */
	private void handleMapClick(Cell cell) {
		// FIRST, if the game is over there's no point.
		if (!inGame)
			return;
		
		// NEXT, for now at least, if the cell hasn't been seen
		// you can't click there.
		if (!region.hasBeenSeen(cell))
			return;
		
		// NEXT, if we're using the magnifier, display the description of
		// the thing you clicked on.
		if (usingMagnifier) {
			magnifyCell(cell);
			
			return;
		}
		
		// NEXT, get the current mover; if he's not a player character,
		// we're done.
		//
		// TBD: There's got to be a better way to know that we're 
		// waiting on a PC.
		Mobile current = getMover();
	
		if (!(current instanceof PlayerCharacter))
			return;
		
		PlayerCharacter pc = (PlayerCharacter)current;

		pc.setGoal(cell);
		timer.start();
	}
	
	/** This call is used in NORMAL mode to hide all
	 * PCs but the party leader.
	 */
	private void hideAllButLeader() {
		PlayerCharacter leader = party.leader();
		leader.setVisible(true);
		
		// Make all other PCs invisible
		for (PlayerCharacter pc : party.allMembers()) {
			if (!pc.equals(leader)) {
				pc.setVisible(false);
			}
		}	
		
		repaint();
	}
	
	/** Displays a detailed description of the current cell, if there
	 * is one.  At present, there are only descriptions for monsters.
	 * 
	 * @param cell The cell to magnify
	 */
	private void magnifyCell(Cell cell) {
		// If there are multiple, this doesn't necessarily get the top one.
		// It should always get the top one, and it must be visible.
		Entity e = region.visibleEntityAt(cell);
		
		if (e == null) {
			return;
		}
		
		JOptionPane.showMessageDialog(this, e.description(), e.name(),
				JOptionPane.PLAIN_MESSAGE, e.bigIcon());
		
	}
	
	//-------------------------------------------------------------------------
	// GUI Management
	
	/** Shows the given card in the Map/Inventory area.
	 * 
	 * @param name The name of the card.
	 */
	public void setCard(String name) {
		CardLayout cl = (CardLayout)(cards.getLayout());
		currentCard = name;
		cl.show(cards, name);
		
		controlBar.refresh();
	}
	
	/** @return the name of the currently-displayed UI card. */
	public String getCard() {
		return currentCard;
	}
	
	/** Allow the party to go shopping at the keeper's shop.
	 * 
	 * @param keeper The shop keeper
	 */
	public void goShopping(Shopkeeper keeper) {
		shopCard.setShopkeeper(keeper);
		setCard(SHOP);
	}
	

	//-------------------------------------------------------------------------
	// Game Management

	/** The user has asked to enter combat.  See if the constraints are
	 * met.
	 */
	private void tryToEnterCombat() {
		if (region.level() <= 0) {
			log("This is a friendly area; there's no need to fight.");
			return;
		}
		setMode(Mode.COMBAT);
	}
	
	/** The user has asked to leave combat.  See if the constraints are met. */
	private void tryToLeaveCombat() {
		// FIRST, there mustn't be any visible enemies within LEAVE_COMBAT_RANGE
		// of any PC, by travel distance. 
		if (shouldBeInCombatMode()) {
	 		log("Cannot leave combat; there are enemies in sight!");
			return;
		}
		
		// NEXT, the PCs must be within LEAVE_COMBAT_RANGE of the active
		// player.
		for (PlayerCharacter pc : party.allMembers()) {
			if (pc.isAlive() &&
				pc.travelDistanceTo(party.leader().place()) > LEAVE_COMBAT_RANGE) {
				log("Cannot leave combat; the party members are too far apart.");
				return;
			}
		}
		
		
		setMode(Mode.NORMAL);
	}
	
	/** Should we be in combat mode?  Yes, if there's an awake hostile visible
	 * enemy within the combat range.
	 * @return true if we should be in combat mode, and false otherwise.
	 */
	private boolean shouldBeInCombatMode() {
		for (PlayerCharacter pc : party.allMembers()) {
		 	if (!pc.isAlive()) {
		 		continue;
		 	}
		 	
			for (Mobile m : pc.getEnemies()) {
				if (pc.distanceTo(m) <= COMBAT_RANGE &&
					m.isAwake() && m.isHostile() && pc.canSee(m)) {
					return true;
				}
			}

			// If we're in normal mode, all PCs are at the same spot.
			if (!inCombat()) {
				break;
			}
		}
	
		return false;
	}

	/** Sets the game mode.<p>
	 * 
	 * Note that this routine isn't responsible for deciding whether
	 * changing the game mode is appropriate; it simply carries out the
	 * decision.
	 * 
	 * @param mode The new mode.
	 */
	private void setMode(Mode mode) {
		// FIRST, if we're already in the mode don't enter it again.
		if (mode.equals(this.mode))
			return;

		// NEXT, set the mode.
		this.mode = mode;
		
		// NEXT, log what's happening.
		log(inCombat() ? "Entering combat mode" : "Leaving combat mode");
		
		// NEXT, reset movement.
		resetMovement();
		
		// NEXT, set up the GUI.
		if (inCombat()) {
			statusBar.setEnabled(false);
		} else {
			statusBar.setEnabled(true);
		}
		
		controlBar.refresh();
	}
	
	/** @return the game mode */
	public Mode getMode() {
		return mode;
	}
	
	/** Resets the movement state variables on transition from 
	 * one mode to another.
	 */
	private void resetMovement() {
		// FIRST, general clean-up.
		moveOrder.clear();
		script.clear();
		for (PlayerCharacter pc : party.allMembers()) {
			pc.resetGoal();
		}
		
		
		// NEXT, get the leading PC
		PlayerCharacter leader = party.leader();

		// NEXT, do arrangements for each mode.
		if (inCombat()) {
			// FIRST, re-establish the move order.
			resetCombatMoveOrder();
			
			// NEXT, reposition the PCs in open cells near the leader.
			List<Cell> open = region.getOpenCellsNear(leader.place(), MovementType.WALKING, 2);
			leader.place().sortByDiagonal(open);

			// NEXT, position them in the region, and make them visible.
			for (int i = 0; i < party.size(); i++) {
				PlayerCharacter pc = party.get(i);
				if (pc.isAlive())
					pc.setVisible(true);

				if (pc.equals(leader))
					continue;
				
				Cell cell = open.get(i % open.size());
				party.get(i).place(cell);
			}

		} else {
			// We follow the leader.  All other PCs are invisible,
			// and co-located with him.
			leader.setVisible(true);
			turnOver = false;
			
			// Make all other PCs invisible
			for (PlayerCharacter pc : party.allMembers()) {
				if (!pc.equals(leader)) {
					pc.setVisible(false);
					pc.place(leader.place());
				}
			}
		}
		
		repaint();
	}
	
	/** Resets the combat move order, on mode change or when the
	 * moveOrder list is depleted.
	 */
	private void resetCombatMoveOrder() {
		// TBD: For now, don't worry about speed.  Do it like we 
		// always have.
		addToMoveOrder(party.members());

		for (Monster m : region.getMonsters()) {
			if (region.diagonal(m.place()) < IDLE_RANGE)
				addToMoveOrder(m);
		}

		turnOver = false;  // The first PC gets to go again.
	}

	/** @return true if we are in combat mode, and false otherwise. */
	public boolean inCombat() {
		return mode == Mode.COMBAT;
	}
		
	
	

	//-------------------------------------------------------------------------
	// Region Management
	
	/** Enter a region by a given point of interest.  We are always in
	 * normal mode when entering a region.
	 * 
	 * @param regionID The region ID
	 * @param point The point of interest ID
	 */
	public void enter(String regionID, String point) {
		println("enter("+regionID+","+point+")");
		
		// FIRST, get the region.
		region = regions.get(regionID);
		
		if (region == null) {
			assert regionRegistry.containsKey(regionID);

			try {
				region = regionRegistry.get(regionID).newInstance();
			} catch (Exception e) {
				System.err.println("Could not create region " 
						+ regionID + ": " + e);
				System.exit(1);
			}
			
			regions.put(regionID, region);
		}
		
		// NEXT, We are always in normal mode when entering a region.
		setMode(Mode.NORMAL);
		
		// NEXT, enter the region.
		region.enter(party.allMembers(), point);

		// NEXT, establish the initial move order.
		resetMovement();

		// NEXT, make it all purty.
		mapCard.setRegion(region);
		repaint();
	}
	
	/** Adds a new region to the registry.
	 * 
	 * @param regionID The string identifier for this region.
	 * @param region The region itself.
	 */
	public void addRegion(String regionID, Region region) {
		regions.put(regionID, region);
	}
	
	/** Retrieves a region given its registered ID, or 
	 * null if no such region is registered.
	 * @param regionID The region ID
	 * @return The region or NULL.
	 */
	public Region getRegion(String regionID) {
		return regions.get(regionID);
	}

	//-------------------------------------------------------------------------
	// Mover Management
	
	/** @return true if the Board is waiting for user input,
	 * i.e., if there's some PC whose turn is not over. */
	public Boolean isWaiting() {
		return !turnOver;
	}
	
	/** @return the Mobile at the front of the move order,
	 * or null if none. In NORMAL mode, the current mover
	 * is (effectively) always the party leader. */
	public Mobile getMover() {
		if (inCombat()) {
			if (moveOrder.size() > 0)
				return moveOrder.get(0);
			else
				return null;
		} else {
			return party.leader();
		}
	}
	
	/** Remove dead movers from the move order.
	 * 
	 * @param dead A list of the dead movers.
	 */
	private void removeDeadMovers(List<Mobile> dead) {
		moveOrder.removeAll(dead);
	}
	
	/** Adds a mobile to the move order.
	 * 
	 * @param m the Mobile
	 */
	public void addToMoveOrder(Mobile m) {
		moveOrder.add(m);
	}
	
	/** Adds a list of mobiles to the move order.
	 * 
	 * @param list the mobiles to add
	 */
	public void addToMoveOrder(List<? extends Mobile> list) {
		moveOrder.addAll(list);
	}
		

	
	//-------------------------------------------------------------------------
	// Queries for use by clients
	
	/** Computes a tool-tip text string for the given map cell.
	 * 
	 * @param cell The cell in questions
	 * @return The text string.
	 */
	public String getCellToolTipText(Cell cell) {
      	Entity entity = region.entityAt(cell);

      	if (!DEBUG) {
      		if (entity != null && entity.isVisible()) {
      			return entity.toString();
      		} else {
      			return region.terrainAt(cell).getName();
      		}
      	} else {
      		if (entity != null && entity.isVisible()) {
      			return entity.details();
      		} else {
      			return region.terrainAt(cell).toString() + cell.toString();
      		}
      	}
	}
	
	//-------------------------------------------------------------------------
	// Utility Commands for use by the model classes.
	
	/** Logs a message from the mobile to the log.
	 * 
	 * @param m  The mobile
	 * @param text The message
	 */
	public void log(String text) {
		slog.log(text);
	}
	
	/** Write debugging info to stdout.
	 * 
	 * @param text The text to output.
	 */
	public void println(String text) {
		System.out.println(text);
	}

	//--------------------------------------------------------------------------
	// Effect animation
	
	/** Sets up a sprite to be displayed on the map at a particular
	 * location.
	 * @param sprite  The sprite
	 * @param cell The location
	 */
	public void setEffect(Sprite sprite, Cell cell) {
		effectSprite = new Furniture("", sprite);
		effectSprite.place(region,cell);
	}
	
	/** Clears away the effect sprite. */
	public void clearEffect() {
		effectSprite = null;
	}

	/** @return the current effect sprite, or null if none. */
	public Entity getEffect() {
		return effectSprite;
	}
		

	//--------------------------------------------------------------------------
	// Entering Combat on Mobile Alert

	/** Enters combat right now.  This should only be used in player character
	 * code outside of animation scripts.
	 */
	public void enterCombat() {
		if (!inCombat())
			setMode(Mode.COMBAT);
	}
	

	//-------------------------------------------------------------------------
	// Party manager
	
	/** This inner class manages the party of PCs for the Board. */
	public static class PartyManager implements Serializable {
		private static final long serialVersionUID = 1L;

		// Instance Variables
		private int gold = 0;
		private List<PlayerCharacter> members = new ArrayList<>();
		
		/** @return the board(). */
		private final Board board() {
			return Board.get();
		}
			
		/** Adds a PC to the party.
		 * 
		 * @param pc The new pc
		 */
		public void add(PlayerCharacter pc) {
			members.add(pc);
		}
		
		
		/** @return the number of PCs in the party. */
		public int size() {
			return members.size();
		}
		
		/** Retrieves the nth party member. 
		 * 
		 * @param idx  The index
		 * @return The PC
		 */
		public PlayerCharacter get(int idx) {
			return members.get(idx);
		}
		
		/** Determines whether the current mobile is a player character 
		 * or not.
		 * @param m the mobile
		 * @return true if it is and false otherwise.
		 */
		public boolean contains(Mobile m) {
			return members.contains(m);
		}

		/** @return the current party, including dead members */
		public List<PlayerCharacter> allMembers() {
			return Collections.unmodifiableList(members);
		}
		
		/** @return the living members of the current party. */
		public List<PlayerCharacter> members() {
			List<PlayerCharacter> list = new ArrayList<>();
			
			for (PlayerCharacter pc : members) {
				if (pc.isAlive())
					list.add(pc);
			}
			
			return list;
		}

		/** Returns the character who is leading the party: the currently
		 * selected PC, if he's still alive, or the first living character
		 * in the party otherwise.
		 * @return the leading player character or null if all PCs are dead. */
		public PlayerCharacter leader() {
			if (board().getSelectedPlayerCharacter().isAlive())
				return board().getSelectedPlayerCharacter();
			
			for (PlayerCharacter pc : members) {
				if (pc.isAlive())
					return pc;
			}
			
			return null;
		}
		
		/** @return the party's gold. */
		public int gold() {
			return gold;
		}
		
		/** Gives the party some gold.
		 * 
		 * @param amount  The amount of gold received.
		 */
		public void earn(int amount) {
			gold += amount;
		}
					
		/** The party pays some amount of gold (but not more gold than 
		 * they have.
		 * 
		 * @param amount  The amount of gold paid.
		 */
		public void pay(int amount) {
			gold -= Math.min(amount, gold);
		}

		/** Does any player character have a particular item?
		 * 
		 * @param item the item
		 * @return true if one does, and false otherwise.
		 */
		public boolean has(Item item) {
			for (PlayerCharacter pc : members) {
				if (pc.inventory.contains(item)) {
					return true;
				}
			}
			
			return false;
		}
		
		/** Remove an item from the party, whoever happens to be holding it.
		 * 
		 * @param item the item
		 */
		public void removeItem(Item item) {
			for (PlayerCharacter pc : members) {
				if (pc.inventory.contains(item)) {
					pc.inventory.remove(item);
				}
			}
		}

		/** @return true if everyone in the party is dead, and false otherwise. */
		public boolean isDead() {
			for (PlayerCharacter pc : members) {
				if (pc.isAlive())
					return false;
			}
			
			return true;
		}
		
		/** Awards experience points to the living members of the party.
		 * 
		 * @param exp the number of points to award.
		 */
		public void award(int exp) {
			for (PlayerCharacter pc : members) {
				if (pc.isAlive())
					pc.award(exp);
			}
		}
							
	}
	
	//-------------------------------------------------------------------------
	// Quest Manager
	
	/** The game's quest manager.  This saves the data needed to display
	 * the accepted and completed quests to the user.
	 * @author will
	 */
	public static final class QuestManager implements Serializable {
		private static final long serialVersionUID = 1L;

		// Instance variables
		private List<Quest> accepted = new ArrayList<>();
		private List<Quest> completed = new ArrayList<>();
		
		/** The party accepts a quest.
		 * 
		 * @param quest The quest to accept.
		 */
		public void accept(Quest quest) {
			if (!accepted.contains(quest)) {
				accepted.add(quest);
				Board.get().log("New quest: " + quest.title());
			}
		}

		/** The party completes a quest.
		 * 
		 * @param quest  The quest that has been completed.
		 */
		public void complete(Quest quest) {
			accepted.remove(quest);
			
			if (!completed.contains(quest)) {
				completed.add(quest);
				Board.get().log("Quest completed: " + quest.title());
			}
		}
		
		/** @return a list of the accepted but not yet completed quests. */
		public List<Quest> accepted() {
			return Collections.unmodifiableList(accepted);
		}
		
		/** @return a list of the completed quests. */
		public List<Quest> completed() {
			return Collections.unmodifiableList(completed);
		}

		/** Has the party has accepted a particular quest or not?
		 * Completed quests are necessarily accepted.
		 * 
		 * @param quest The quest
		 * @return True if so, and false otherwise.
		 */
		public boolean isAccepted(Quest quest) {
			return accepted.contains(quest) || completed.contains(quest);
		}

		/** Has the party has completed a particular quest or not?
		 * 
		 * @param quest The quest
		 * @return True if so, and false otherwise.
		 */
		public boolean isCompleted(Quest quest) {
			return completed.contains(quest);
		}
	}
	
	//------------------------------------------------------------------------
	// Game Save/Load
	
	/** The save set pulls together all of the object trees that need to
	 * included in a game save.  This is simply a structure object for
	 * use by Board.
	 * @author will
	 */
	static class SaveSet implements Serializable {
		private static final long serialVersionUID = 1L;

		int clock = 0;
		PartyManager party = null;
		Region region = null;
		Map<String,Region> regions = null;
		QuestManager quest = null;
		Dict memory = null;
	}
	
	class GameSaver implements ActionListener {
		/** slot The metadata object. */
		SaveMetadata meta = new SaveMetadata();
		
		/** The chosen slot */
		int slot = 0;
		
		void popup() {
			// FIRST, get the save slot metadata
			meta.load();

			// NEXT, create the panel.  It will contain the
			// save slot radio buttons.
			JPanel panel = new JPanel(new GridLayout(0,1));
			ButtonGroup bg = new ButtonGroup();
			
			for (int slot = SaveMetadata.MIN; slot <= SaveMetadata.MAX; slot++) {
				JRadioButton rb = new JRadioButton(meta.getSlotText(slot));
				rb.setActionCommand("slot" + slot);
				rb.addActionListener(this);
				bg.add(rb);
				panel.add(rb);
				
				if (slot == SaveMetadata.MIN) {
					rb.setSelected(true);
				}
			}
			
			// NEXT, ask the question.
			int result = JOptionPane.showConfirmDialog(Board.get(),
										  panel,
										  "Save Game To Slot",
										  JOptionPane.YES_NO_OPTION,
										  JOptionPane.QUESTION_MESSAGE,
										  party.leader().bigIcon());

			if (result == JOptionPane.YES_OPTION) {
				saveGame();
			}
		}
		
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "slot0":
				slot = 0;
				break;
			case "slot1":
				slot = 1;
				break;
			case "slot2":
				slot = 2;
				break;
			case "slot3":
				slot = 3;
				break;
			case "slot4":
				slot = 4;
				break;
			case "slot5":
				slot = 5;
				break;
			case "slot6":
				slot = 6;
			case "slot7":
				slot = 7;
				break;
			default:
				println("Error, unknown action: " + e.getActionCommand());
				break;
			}
		}

		/** Saves the game in the given slot, and sets and saves the
		 * metadata.
		 */
		private void saveGame() {
			// FIRST, we need to be in normal mode.
			if (inCombat()) {
				throw new IllegalStateException("Cannot save game while in combat!");
			}
			
			// NEXT, pack up the scenario.
			SaveSet save = new SaveSet();
			
			save.clock = clock;
			save.party = party;
			save.region = region;
			save.regions = regions;
			save.quest = quest;
			save.memory = memory;
			
			// NEXT, serialize it.
			try {
				// FIRST, get the paths
				File saveFile = meta.slotFile(slot);
				ObjectOutput out = 
						new ObjectOutputStream(new FileOutputStream(saveFile));
				out.writeObject(save);
				out.close();
				log("Saved game to save file: " + saveFile.getName());
			} catch (Exception e) {
				log("Error, could not save game: " + e);
				return;
			}
			
			meta.set(slot, save);
			meta.save();
		}

	}
	
	class GameLoader implements ActionListener {
		/** slot The metadata object. */
		SaveMetadata meta = new SaveMetadata();
		
		/** The chosen slot */
		int slot = 0;
		
		void popup() {
			// FIRST, get the save slot metadata
			meta.load();

			// NEXT, create the panel.  It will contain the
			// save slot radio buttons.
			JPanel panel = new JPanel(new GridLayout(0,1));
			ButtonGroup bg = new ButtonGroup();
			
			int slotsWithData = 0;
			
			for (int slot = SaveMetadata.MIN; slot <= SaveMetadata.MAX; slot++) {
				if (!meta.hasData(slot)) {
					continue;
				}
				
				slotsWithData++;
				
				JRadioButton rb = new JRadioButton(meta.getSlotText(slot));
				rb.setActionCommand("slot" + slot);
				rb.addActionListener(this);
				bg.add(rb);
				panel.add(rb);
				
				// Select the first slot with data
				if (slotsWithData == 1) {
					rb.setSelected(true);
				}
			}
			
			if (slotsWithData == 0) {
				JOptionPane.showMessageDialog(Board.get(),
						"There are no saved games to load.",
						"No saved games",
						JOptionPane.PLAIN_MESSAGE,
						party.leader().bigIcon());
				return;
			}
			
			// NEXT, ask the question.
			int result = JOptionPane.showConfirmDialog(Board.get(),
										  panel,
										  "Load a Saved Game",
										  JOptionPane.YES_NO_OPTION,
										  JOptionPane.QUESTION_MESSAGE,
										  party.leader().bigIcon());

			if (result == JOptionPane.YES_OPTION) {
				loadGame();
			}
		}
		
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "slot0":
				slot = 0;
				break;
			case "slot1":
				slot = 1;
				break;
			case "slot2":
				slot = 2;
				break;
			case "slot3":
				slot = 3;
				break;
			case "slot4":
				slot = 4;
				break;
			case "slot5":
				slot = 5;
				break;
			case "slot6":
				slot = 6;
			case "slot7":
				slot = 7;
				break;
			default:
				println("Error, unknown action: " + e.getActionCommand());
				break;
			}
		}

		/** Saves the game in the given slot, and sets and saves the
		 * metadata.
		 */
		private void loadGame() {
			// FIRST, try to load the file as a new SaveSet.
			File f = meta.slotFile(slot);
			SaveSet save = null;

			try {
				ObjectInputStream os = new ObjectInputStream(new FileInputStream(f));
				save = (SaveSet)os.readObject();
				os.close();
			} catch (Exception e) {
				log("Error loading saved game: " + e.getMessage());
				return;
			}
			
			if (save == null) {
				log("Error, no data loaded.");
			} else {
				println("Loaded data; restoring GUI");
				clock = save.clock;
				party = save.party;
				region = save.region;
				regions = save.regions;
				quest = save.quest;
				memory = save.memory;
				
				statusBar.setParty(party.allMembers());
				selectPlayerCharacter(party.leader());
			    mapCard.setRegion(region);
				resetMovement();

				log("Loaded saved game from " + f.getName());
			}
		}
	}
}
