/**
 * 
 */
package rpg.world.floobham;

import graphics.ImageUtils;
import graphics.Items;
import graphics.SpriteImage;

import java.awt.Image;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import map.Cell;
import map.Direction;
import map.TerrainGrid;
import map.TerrainTile;
import map.TerrainType;
import map.maze.ClassicMaze;
import rpg.EntityEvent;
import rpg.EntityListener;
import rpg.EventType;
import rpg.Gold;
import rpg.MovementType;
import rpg.RandomClass;
import rpg.Region;
import rpg.armor.LeatherHelmet;
import rpg.armor.PizzaPan;
import rpg.feature.Door;
import rpg.feature.PlainChest;
import rpg.feature.StairsUp;
import rpg.item.ScrollOfMagicMapping;
import rpg.item.Skull;
import rpg.item.VialOfHealing;
import rpg.monster.Bat;
import rpg.monster.GiantRat;
import rpg.monster.Goblin;
import rpg.monster.IckyWhiteThing;
import rpg.monster.Monster;
import rpg.monster.Rat;
import rpg.monster.Roach;
import rpg.weapon.Dagger;
import rpg.weapon.SmallSword;
import util.StringsTable;
import app.Board;
import app.Strings;

/**
 * @author will
 *
 */
public final class Sewers extends Region implements EntityListener {
	private static final long serialVersionUID = 1L;

	/** This region's ID */
	public static final String ID = "sewers";
	
	// Points of Interest
	
	/** The stairs leading into the Sewers. */
	public static final String ORIGIN = "origin";
	
	// Constants
	private final int MROWS = 6;
	private final int MCOLS = 6;
	private final int FTILES = 7;
	private final int WTILES = 9;
	private final int NUM_EXIT_ROACHES = 10;
	private final int NUM_MAGUFFIN_ROACHES = 20;
	
	/** The strings table. */
	private static StringsTable strings;

	static {
		// FIRST, loads the strings.
		strings = Strings.register(ID, Sewers.class, "sewers.strings");
	}
	
	// Instance Variables
	
	/** A list of the center cells of candidate chambers, produced while adding
	 * connectors.
	 */
	transient private Set<Cell> chamberSet = new HashSet<>();
	
	private RandomClass<Monster> chamberMonster = new RandomClass<>();
	private RandomClass<Monster> corpseMonster = new RandomClass<>();
	private RandomClass<Monster> walkwayMonster = new RandomClass<>();
	private Boss boss;
	
	// Constructor
	
	/** Creates the region.	 
	 */
	public Sewers() {
		super(ID, 1);
		
		// FIRST, set up the monster profiles.
		corpseMonster
			.add(50, Rat.class)
			.add(50, Roach.class);
		
		chamberMonster
			.add(100, Rat.class)
			.add(60, Roach.class)
			.add(20, Goblin.class);
		
		walkwayMonster
			.add(100, Roach.class)
			.add(50, IckyWhiteThing.class)
			.add(30, Rat.class);
		
		// NEXT, create random sewers.
		createMap();
	}
	
	@Override public String title() { return "Floobham Sewers"; }

	@Override
	public void handleEvent(EntityEvent e) {
		if (e.entity.equals(boss) && e.type.equals(EventType.DEATH)) {
			bossIsDead();
		}
	}
	
	/** The boss is dead; do the necessary. */
	private void bossIsDead() {
		// FIRST, show him a congratulations message.
		JOptionPane.showMessageDialog(Board.get(), 
				strings.get("boss.death"), 
				"Got Him!", 
				JOptionPane.PLAIN_MESSAGE, 
				Items.TROPHY.bigIcon());
		
		// NEXT, add roaches near the origin.
		Cell origin = getPointOfInterest(Sewers.ORIGIN);
		
		List<Cell> open = getOpenCellsNear(origin, MovementType.WALKING, 4);
		origin.sortByDiagonal(open);

		for (int i = 0; i < NUM_EXIT_ROACHES; i++) {
			addMonster(open.get(i), new Roach());
		}

		open = getOpenCellsWith(MyTile.CHECKER);
		
		for (int i = 0; i < NUM_MAGUFFIN_ROACHES; i++) {
			addMonster(random.takeFrom(open), new Roach());
		}
	}

	
	//-------------------------------------------------------------------------
	// Map Creation
	
	/** Generates a random maze, and produces a map of the sewers based
	 * upon it.	 */
	private final void createMap() {
		// FIRST, create a maze to base the sewers on.
		ClassicMaze cm =  new ClassicMaze(MROWS,MCOLS)
			.recursiveBacktracker(true)
			.connectify(25);
		
		// NEXT, create a terrain grid of the appropriate size.
		// Maze rooms need to be 7x7, and walls are 9 wide.
		int grows = m2g(MROWS);
		int gcols = m2g(MCOLS);
		
		TerrainGrid map = new TerrainGrid(grows, gcols);
		map.clearTo(MyTile.BRICK);
		
		// NEXT, save the map.
		setMap(map);
		
		// NEXT, the map consists of four kinds of thing: junctions,
		// corresponding to the map rooms;  NS and EW connectors,
		// which correspond to doors between rooms, and chambers,
		// which fill some of the spaces between the connectors.
		
		// NEXT, add the junctions and connectors.
		for (int mi = 0; mi < MROWS; mi++) {
			for (int mj = 0; mj < MCOLS; mj++) {
				Cell mij = new Cell(mi, mj);
				addJunction(map, cm, mi, mj);
				
				if (cm.isOpen(mij, Direction.S)) {
					addConnectorNS(map, mij);
				}
				
				if (cm.isOpen(mij, Direction.E)) {
					addConnectorEW(map, mij);
				}
			}
		}
		
		// NEXT, add some chambers.  First, get a list of all of the
		// possible chamber locations.  Chambers are 5x5 square, 
		// starting at 2,2; they are spaced WTILES+FTILES apart.
		// However, they can only go next to a connector.
		
		List<Cell> chambers = new ArrayList<>();
		chambers.addAll(chamberSet);
				
		// Origin
		Cell origin = random.takeFrom(chambers);
		addChamber(map, origin);
		
		addPointOfInterest(ORIGIN, origin);
		StairsUp exit = new StairsUp(Floobham.ID, Floobham.SEWERS);
		exit.setConfirm(true);
		addFeature(origin, exit);
		
		// Boss
		boss = new Boss();
		boss.addListener(this);
		Cell bossCell = random.takeFrom(chambers);
		addChamber(map, bossCell);
		addMonster(bossCell, boss);
		addChamberMonsters(bossCell);
		
		// Chamber with short sword
		Cell chamber = random.takeFrom(chambers);
		addChamber(map, chamber);
		PlainChest chest = new PlainChest();
		chest.putItem(new SmallSword());
		chest.putItem(new Gold(1 + random.nextInt(5)));
		addFeature(chamber, chest);
		addChamberMonsters(chamber);
		
		// Chamber with leather helmet
		chamber = random.takeFrom(chambers);
		addChamber(map, chamber);
		chest = new PlainChest();
		chest.putItem(new LeatherHelmet());
		chest.putItem(new Gold(1 + random.nextInt(5)));
		addFeature(chamber, chest);
		addChamberMonsters(chamber);
		
		// Chamber with pizza pan 
		chamber = random.takeFrom(chambers);
		addChamber(map, chamber);
		chest = new PlainChest();
		chest.putItem(new PizzaPan());
		chest.putItem(new Gold(1 + random.nextInt(10)));
		addFeature(chamber, chest);
		addChamberMonsters(chamber);
		
		// NEXT, add more chambers.

		for (int n = 0; n < 12; n++) {
			chamber = random.takeFrom(chambers);
			addChamber(map, chamber);
			
			if (random.chance(50)) {
				chest = new PlainChest();
				addFeature(chamber, chest);
				
				if (random.chance(50)) {
					chest.putItem(new Gold(1 + random.nextInt(8)));
				}
				
				if (random.chance(75)) {
					chest.putItem(new VialOfHealing());
				}
				
				if (random.chance(25)) {
					chest.putItem(new ScrollOfMagicMapping());
				}
			}
			
			addChamberMonsters(chamber);
		}
		
		// NEXT, get the list of walkway cells. */
		List<Cell> walkways = getOpenCellsWith(MyTile.CHECKER);
		
		// NEXT, add three corpses on the walkways.  Each
		// corpse should have a monster or two around it.
		for (int n = 0; n < 3; n++) {
			addCorpse(random.takeFrom(walkways));
		}
		
		// NEXT, add some monsters on the walkways.
		for (int n = 0; n < 50; n++) {
			Cell mcell = random.takeFrom(walkways);
			
			if (mcell == null) 
				break;
			
			addMonster(mcell, walkwayMonster.newInstance());
		}
		
		// NEXT, add some bats.
		for (int n = 0; n < 20; n++) {
			Cell mcell = random.takeFrom(walkways);
			
			if (mcell == null) {
				break;
			}
			
			addMonster(mcell, new Bat());
		}
	}
	
	/** Given a maze coordinate, returns the grid coordinate for the
	 * upper left corner.
	 * @param m The maze coordinate
	 * @return The grid coordinate
	 */
	private int m2g(int m) {
		return m*FTILES + (m+1)*WTILES;
	}
	
	/** Adds a junction to the map.  A junction is a 7x7 tile space
	 * with a 3x3 patch of sewage in the middle and 2-tile-wide checker
	 * walkways around the edges.  3-tile-wide patches of sewage 
	 * are placed on the edges where there are open doors.
	 * 
	 * @param map The map being built.
	 * @param cm The maze it is based on
	 * @param mi The row-coordinate of a maze room
	 * @param mj The column-coordinate of a maze room.
	 */
	private void addJunction(TerrainGrid map, ClassicMaze cm, int mi, int mj) {
		// FIRST, get the coordinates of the upper-left tile.
		int gi = m2g(mi);
		int gj = m2g(mj);
		
		// NEXT, fill with checker.
		map.set(gi, gj, FTILES, FTILES, MyTile.CHECKER);
		
		// NEXT, fill in the sewage in the middle.
		map.set(gi+2, gj+2, 3, 3, MyTile.SEWAGE);
		
		// NEXT, fill in the connections .
		Cell mij = new Cell(mi, mj);
		
		if (cm.isOpen(mij, Direction.N)) {
			map.set(gi, gj+2, 2, 3, MyTile.SEWAGE);
		}
		
		if (cm.isOpen(mij, Direction.S)) {
			map.set(gi+5, gj+2, 2, 3, MyTile.SEWAGE);
		}
		
		if (cm.isOpen(mij, Direction.E)) {
			map.set(gi+2, gj+5, 3, 2, MyTile.SEWAGE);
		}
		
		if (cm.isOpen(mij, Direction.W)) {
			map.set(gi+2, gj, 3, 2, MyTile.SEWAGE);
		}
	}
	
	private void addConnectorNS(TerrainGrid map, Cell mij) {
		// FIRST, get the coordinates of the upper-left tile.
		int gi = m2g(mij.row) + FTILES;
		int gj = m2g(mij.col);

		// NEXT, do the checker.
		map.set(gi, gj, WTILES, FTILES, MyTile.CHECKER);
		
		// NEXT, do the sewage
		map.set(gi, gj+2, WTILES, 3, MyTile.SEWAGE);
		
		// NEXT, note the possible chamber locations;
		int ci = gi + 4;
		int cjleft = gj - WTILES + 4;
		int cjright = gj + FTILES + 4;

 		chamberSet.add(new Cell(ci, cjleft));
		chamberSet.add(new Cell(ci, cjright));

		// NEXT, prepare to add some things.
		List<Integer> candidates = new ArrayList<>();
		
		for (int i = gi; i < gi+WTILES; i++) {
			candidates.add(i);
		}
		
		// NEXT, add a bridge.  It can go anywhere, north to south.
		int bi = random.takeFrom(candidates);
		int bj = gj + 2;
		
		map.set(bi, bj,   MyTile.BRIDGE_EW);
		map.set(bi, bj+1, MyTile.BRIDGE_EW);
		map.set(bi, bj+2, MyTile.BRIDGE_EW);
		
		// NEXT, maybe add trickles.  Remove the chamber row
		// first; we don't want a trickle by a door.
		// Note: Remove it as an Integer; otherwise, it's interpreted
		// as an index.
		candidates.remove(new Integer(ci));
		
		// West trickle
		if (random.chance(60)) {
			int ti = random.takeFrom(candidates);
			map.set(ti, gj,   MyTile.TRICKLE_W);
			map.set(ti, gj+1, MyTile.TRICKLE_EW);
		}
		
		// East trickle 
		if (random.chance(60)) {
			int ti = random.takeFrom(candidates);
			map.set(ti, gj+6, MyTile.TRICKLE_E);
			map.set(ti, gj+5, MyTile.TRICKLE_EW);
		}
		
	}
	
	private void addConnectorEW(TerrainGrid map, Cell mij) {
		// FIRST, get the coordinates of the upper-left tile.
		int gi = m2g(mij.row);
		int gj = m2g(mij.col) + FTILES;

		// NEXT, do the checker.
		map.set(gi, gj, FTILES, WTILES, MyTile.CHECKER);
		
		// NEXT, do the sewage
		map.set(gi+2, gj, 3, WTILES, MyTile.SEWAGE);

		// NEXT, note the possible chamber locations;
		int citop = gi - WTILES + 4;
		int cibottom = gi + FTILES + 4;
		int cj = gj + 4;
		
		chamberSet.add(new Cell(citop, cj));
		chamberSet.add(new Cell(cibottom, cj));

		
		// NEXT, prepare to add some things.
		List<Integer> candidates = new ArrayList<>();
		
		for (int j = gj; j < gj+WTILES; j++) {
			candidates.add(j);
		}
		
		// NEXT, add a bridge.  It can go anywhere, east to west.
		int bi = gi + 2;
		int bj = random.takeFrom(candidates);
		
		map.set(bi,   bj, MyTile.BRIDGE_NS);
		map.set(bi+1, bj, MyTile.BRIDGE_NS);
		map.set(bi+2, bj, MyTile.BRIDGE_NS);
		
		// NEXT, maybe add trickles.  Remove the chamber row
		// first; we don't want a trickle by a door.
		// Note: Remove it as an Integer; otherwise, it's interpreted
		// as an index.
		candidates.remove(new Integer(cj));
		
		// North trickle
		if (random.chance(60)) {
			int tj = random.takeFrom(candidates);
			map.set(gi,   tj, MyTile.TRICKLE_N);
			map.set(gi+1, tj, MyTile.TRICKLE_NS);
		}
		
		// South trickle 
		if (random.chance(60)) {
			int tj = random.takeFrom(candidates);
			map.set(gi+6, tj, MyTile.TRICKLE_S);
			map.set(gi+5, tj, MyTile.TRICKLE_NS);
		}
	}
	
	/** Adds a chamber to the map; the chamber has the given
	 * center.
	 * @param map
	 * @param center
	 * @return a list of the entry cells.
	 */
	private List<Cell> addChamber(TerrainGrid map, Cell center) {
		// FIRST, fill it with gravel.
		map.set(center.row-2, center.col-2, 5, 5, MyTile.GRAVEL);
		
		// NEXT, add at least one door.
		List<Direction> dirs = getDoorDirection(map, center);
		List<Cell> entries = new ArrayList<>();
		
		entries.add(addDoor(map, center, random.takeFrom(dirs)));
		
		if (dirs.size() > 0 && random.chance(25))
			entries.add(addDoor(map, center, random.takeFrom(dirs)));
		
		if (dirs.size() > 0 && random.chance(10))
			entries.add(addDoor(map, center, random.takeFrom(dirs)));
		
		return entries;
	}
	
	/** Adds a door to the chamber with the given center.
	 * 
	 * @param map The map
	 * @param center The cell at the center of the chamber
	 * @param dir The direction in which to add the door.
	 * @return the entry cell, just inside the door.
	 */
	private Cell addDoor(TerrainGrid map, Cell center, Direction dir) {
		Cell door = center.offset(dir, 4);
		Cell entry = center.offset(dir,3);
		
		map.set(door, MyTile.CHECKER);
		addFeature(door, new Door(MyTile.OPEN_DOOR, MyTile.CLOSED_DOOR));
		map.set(entry, MyTile.GRAVEL);
		
		return entry;
	}
	
	/** Given the center cell of a chamber, determine
	 * the directions in which a door may be opened to a connector.
	 * @param map The map
	 * @param center The upper left corner of a chamber
	 * @return The directions in which doors may be opened.
	 */
	private List<Direction> getDoorDirection(TerrainGrid map, Cell center) {
		List<Direction> dirs = new ArrayList<>();
		
		// A connector will have a CHECKER floor five cells away.
		for (Direction d : Direction.values()) {
			if (connectorIn(map, center, d)) {
				dirs.add(d);
			}
		}

		return dirs;
	}
	
	/** Given a cell, presumably the center of a chamber, determine
	 * whether there is a connector in the specified direction.
	 * @param map  The map
	 * @param center  The center of the chamber
 	 * @param dir The direction
	 * @return True if so, and false otherwise.
	 */
	private boolean connectorIn(TerrainGrid map, Cell center, Direction dir) {
		Cell cell = center.offset(dir, 5);
				
		if (map.inBounds(cell))
			return (map.terrainAt(cell).equals(MyTile.CHECKER));
		else
			return false;
	}
	
	/** Adds a corpse with some goodies at the given cell.
	 * 
	 * @param cell The cell
	 */
	private void addCorpse(Cell cell) {
		if (random.chance(75)) {
			dropItem(cell, new Dagger());
		}
		
		dropItem(cell, new Gold(random.roll(1,10)));
		
		dropItem(cell, new Skull());
	
		List<Cell> open = getOpenCellsNear(cell, MovementType.WALKING, 2);
		
		for (int i = 0; i < random.nextInt(3); i++) {
			Cell mcell = random.takeFrom(open);
			
			if (mcell == null)
				break;
			
			addMonster(mcell, corpseMonster.newInstance());
		}
	}
	
	/** Add one or two monsters to the chamber. 
	 * 
	 * @param center The center of the chamber.
	 */
	private void addChamberMonsters(Cell center) {
		int numMonsters = random.nextInt(5);
		List<Cell> open = getOpenCellsNear(center, MovementType.WALKING, 2);
		
		for (int n = 0; n < numMonsters; n++) {
			Cell mcell = random.takeFrom(open);
			if (mcell == null)
				break;

			addMonster(mcell, chamberMonster.newInstance());
		}

	}

	//-------------------------------------------------------------------------
	// Methods for use by the Board
	
	// TBD.

	//-------------------------------------------------------------------------
	// Level Inner Classes
	
	/** The Boss: a Giant Rat with the Maguffin. */
	private class Boss extends GiantRat {
		private static final long serialVersionUID = 1L;

		Boss() {
			super();
			drops.addItem(Floobham.MAGUFFIN);
		}
		
	}
	
	//-------------------------------------------------------------------------
	// The Region's Tile Set
	
	private enum MyTile implements TerrainTile {
		// Terrain Tiles
		
		/** Unknown tile */     UNKNOWN("Unknown", TerrainType.UNKNOWN),
		/** Tile floor */		TILE_FLOOR("Tile floor", TerrainType.FLOOR),
		/** Block wall */		BLOCK_WALL("Block wall", TerrainType.WALL),
		/** Cobble floor */		COBBLE_FLOOR("Cobble floor", TerrainType.FLOOR),
		/** Stone wall */		STONE_WALL("Stone wall", TerrainType.WALL),
		/** Water */			WATER("Water", TerrainType.WATER),
		/** Earth */			EARTH("Earth", TerrainType.FLOOR),
		/** Grass */			GRASS("Grass", TerrainType.FLOOR),
		/** Sand */             SAND("Sand", TerrainType.FLOOR),
		/** Gravel */           GRAVEL("Gravel", TerrainType.FLOOR),
		/** Sewage */           SEWAGE("Sewage", TerrainType.WATER),
		/** Checker tile */     CHECKER("Tile floor", TerrainType.FLOOR),
		/** Brick wall */       BRICK("Brick wall", TerrainType.WALL),
		/** Sewage Bridge EW */ BRIDGE_EW("Bridge", TerrainType.FLOOR),
		/** Sewage Bridge NS */ BRIDGE_NS("Bridge", TerrainType.FLOOR),
		/** Trickle, W */       TRICKLE_W("Sewage", TerrainType.FLOOR),
		/** Trickle, EW */      TRICKLE_EW("Sewage", TerrainType.FLOOR),
		/** Trickle, E */ 		TRICKLE_E("Sewage", TerrainType.FLOOR),
		/** Trickle, N */       TRICKLE_N("Sewage", TerrainType.FLOOR),
		/** Trickle, NS */      TRICKLE_NS("Sewage", TerrainType.FLOOR),
		/** Trickle, S */ 		TRICKLE_S("Sewage", TerrainType.FLOOR),
		
		
		// Feature Sprites
		/** Trickle, W */       FTRICKLE_W("Sewage"),
		/** Trickle, EW */      FTRICKLE_EW("Sewage"),
		/** Trickle, E */ 		FTRICKLE_E("Sewage"),
		/** Trickle, N */       FTRICKLE_N("Sewage"),
		/** Trickle, NS */      FTRICKLE_NS("Sewage"),
		/** Trickle, S */ 		FTRICKLE_S("Sewage"),
		/** Closed Door */		CLOSED_DOOR("Closed door"),
		/** Open Door */		OPEN_DOOR("Open door"),
		/** NS Bridge 1 */      FBRIDGE_NS("Bridge"),
		/** EW Bridge 1 */      FBRIDGE_EW("Bridge"),
		/** NS Bridge 2 */      FBRIDGE_NS2("Bridge");

		// Instance variables
		private String name;
		private TerrainType terrain;
		
		MyTile(String name, TerrainType terrain) {
			this.name = name;
			this.terrain = terrain;
		}
		
		MyTile(String name) {
			this(name, TerrainType.UNKNOWN);
		}

		/** @return the tile's name. */
		public String getName() {
			return name;
		}
		
		/** @return the tile's terrain type. */
		public TerrainType getTerrain() {
			return terrain;
		}
		
		/** @return true if the tile is opaque, and false otherwise. */
		public boolean isOpaque() {
			return terrain.opaque;
		}
		
		/** @return true if a mobile can walk on the tile, and false otherwise. */
		public boolean isWalkable() {
			return terrain.walkable;
		}
		
		/** @return true if the a mobile can fly through the tile, and false otherwise. */
		public boolean isFlyable() {
			return terrain.flyable;
		}
		
		/** @return the sprite's image. */
		public Image image() {
			return images[this.ordinal()].image();
		}
		
		public Image bigImage() {
			return images[this.ordinal()].bigImage();
		}
		
		/** @return the sprite's image as an icon */
		public ImageIcon icon() {
			return images[this.ordinal()].icon();
		}

		/** @return the sprite's image as a magnified icon */
		public ImageIcon bigIcon() {
			return images[this.ordinal()].bigIcon();
		}

		// Static Data

		/** The list of tile images, read from the disk. */
		static private SpriteImage[] images;
		
		static {
			images = ImageUtils.loadTileSet(Sewers.class,"Sewers.png");
		}
	}
}
