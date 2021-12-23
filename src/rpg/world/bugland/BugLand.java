/**
 * 
 */
package rpg.world.bugland;

import graphics.ImageUtils;
import graphics.SpriteImage;

import java.awt.Image;
import java.util.List;

import javax.swing.ImageIcon;

import map.Cell;
import map.TerrainTile;
import map.TerrainType;
import map.tiled.TiledMap;
import rpg.EntityEvent;
import rpg.EntityListener;
import rpg.Gold;
import rpg.Posture;
import rpg.RandomClass;
import rpg.Region;
import rpg.feature.PlainChest;
import rpg.item.VialOfHealing;
import rpg.monster.Goblin;
import rpg.monster.GoldBug;
import rpg.monster.LadyBug;
import rpg.monster.ManlyBug;
import rpg.monster.Monster;
import rpg.monster.Orc;
import rpg.monster.Roach;
import rpg.weapon.StapleGun;
import rpg.world.Quests;
import util.StringsTable;
import app.Strings;

/** BugLand is a dungeon with bugs in it.
 * @author will
 *
 */
public final class BugLand extends Region implements EntityListener {
	private static final long serialVersionUID = 1L;

	/** This region's ID */
	public static final String ID = "bugland";
	
	/** The strings table. */
	@SuppressWarnings("unused")
	private static StringsTable strings;

	static {
		// FIRST, loads the strings.
		strings = Strings.register(ID, BugLand.class,"bugland.strings");
	}
	
	private RandomClass<Monster> valleyMonster = new RandomClass<>();
	
	
	// TBD
	
	// Constructor
	
	/** Creates the region.	 
	 */
	public BugLand() {
		super(ID, 1);
		
		// FIRST, load the region Tiled map and set up the region.
		TiledMap tm = TiledMap.read(BugLand.class, "bugland.json");
		
		setupRegion(tm, MyTile.values(), ID);
		
		// NEXT, add monsters.
		valleyMonster
			.add(100, LadyBug.class)
			.add(60,  ManlyBug.class)
			.add(20,  Roach.class)
			.add(6,   Goblin.class)
			.add(1,   Orc.class)
			.add(1,   GoldBug.class);
		
		List<Cell> grass = getOpenCellsWith(MyTile.GRASS);
		for (int n = 0; n < 50; n++) {
			Cell cell = random.takeFrom(grass);
			addMonster(cell, valleyMonster.newInstance());
		}
	}
	
	@Override public String title() { return "BugLand"; }
	
	@Override
	protected void setupObjectFeature(Cell cell, TiledMap.MapObject o) {
		switch (o.type) {
		case "Chest":
			PlainChest chest = new PlainChest();
			switch (o.name) {
			case "chest.supplies":
				chest.putItem(new VialOfHealing());
				chest.putItem(new Gold(5));
				break;
			case "chest.treasure":
				chest.addListener(this, Quests.BUG_VALLEY);
				chest.putItem(new StapleGun());
				chest.putItem(new Gold(30));
				break;
			}

			addFeature(cell, chest);
			break;
		default:
			// Handle unimplemented features in the usual way.
			super.setupObjectFeature(cell, o);
			break;
		}
	}

	@Override
	protected void setupMobile(Cell cell, TiledMap.MapObject o) {
		switch (o.name) {
		case "nuisance":
			LadyBug nuisance = new LadyBug();
			nuisance.setPosture(Posture.SITTING);
			nuisance.setMaxHP(8);
			addMonster(cell, nuisance);
			break;
		default:
			// Handle unimplemented mobiles in the usual way.
			super.setupMobile(cell, o);
			break;
		}
	}

	/** Handles entity events.
	 * 
	 * @param e  The entity event.
	 */
	public void handleEvent(EntityEvent e) {
		// FIRST, did they complete the quest?
		if (e.tag.equals(Quests.BUG_VALLEY)) {
			board().quest.complete(Quests.BUG_VALLEY);
		}
	}

	//-------------------------------------------------------------------------
	private enum MyTile implements TerrainTile {
		// Terrain Tiles
		
		/** Tile floor */		TILE_FLOOR("Tile floor", TerrainType.FLOOR),
		/** Block wall */		BLOCK_WALL("Block wall", TerrainType.WALL),
		/** Cobble floor */		COBBLE_FLOOR("Cobblestone floor", TerrainType.FLOOR),
		/** Stone wall */		STONE_WALL("Stone wall", TerrainType.WALL),
		/** Water */			WATER("Water", TerrainType.WATER),
		/** Earth */			EARTH("Earth", TerrainType.FLOOR),
		/** Grass */			GRASS("Grass", TerrainType.FLOOR),
		/** Sand */				SAND("Sand", TerrainType.FLOOR),
		/** Gravel */			GRAVEL("Gravel", TerrainType.FLOOR),
		/** Mountain */			MOUNTAIN("Mountain", TerrainType.WALL),
		/** Bridge, EW */		BRIDGE_EW("Bridge", TerrainType.FLOOR),
		/** Bridge, NS */       BRIDGE_NS("Bridge", TerrainType.FLOOR),
		/** Brick Center */     BRICK_CENTER("Brick", TerrainType.FLOOR),
		/** Brick, NS */        BRICK_NS("Brick", TerrainType.FLOOR),
		/** Brick, EW */        BRICK_EW("Brick", TerrainType.FLOOR),
		/** Tunnel Entrance */  TUNNEL("Tunnel Entrance");
		
		// Instance variables
		private String name;
		private TerrainType terrain;
		
		// Use this for terrain tiles
		MyTile(String name, TerrainType terrain) {
			this.name = name;
			this.terrain = terrain;
		}
		
		// Use this for feature tiles
		MyTile(String name) {
			this(name, TerrainType.UNKNOWN);
		}
		
		// Use this for blanks.
		MyTile() {
			this("", TerrainType.UNKNOWN);
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
			images = ImageUtils.loadTileSet(BugLand.class,"BugLand.png");
		}
	}
}
