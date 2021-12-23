/**
 * 
 */
package rpg.world.castle_floob;

import graphics.ImageUtils;
import graphics.Mobiles;
import graphics.SpriteImage;

import java.awt.Image;

import javax.swing.ImageIcon;

import map.Cell;
import map.TerrainTile;
import map.TerrainType;
import map.tiled.TiledMap;
import rpg.Gold;
import rpg.Mannikin;
import rpg.Region;
import rpg.feature.PlainChest;
import rpg.item.VialOfHealing;
import rpg.pc.PlayerCharacter;
import rpg.world.Quests;
import util.StringsTable;
import app.Strings;

/** Entry to Castle Floob
 * @author will
 *
 */
public final class FloobEntry extends Region {
	private static final long serialVersionUID = 1L;

	/** This region's ID */
	public static final String ID = "floob_entry";
	
	// Points of Interest
	
	/** The strings table. */
	private static StringsTable strings;

	static {
		// FIRST, loads the strings.
		strings = Strings.register(ID, FloobEntry.class,"floob_entry.strings");
	}
	
	// Instance Variables
	EntryGuard leftGuard = new EntryGuard("left");
	EntryGuard rightGuard = new EntryGuard("right");
	
	
	// Constructor
	
	/** Creates the region.	 
	 */
	public FloobEntry() {
		super(ID, 0);
		
		println("FloobEntry");
		for (String key : strings.keyList()) {
			println("string: " + key);
		}
		
		// FIRST, load the region Tiled map.
		TiledMap tm = TiledMap.read(FloobEntry.class, "floob_entry.json");
		
		println("Got TiledMap " + tm.width + "," + tm.height);
				
		setupRegion(tm, MyTile.values(), ID);
	}

	@Override public String title() { return "Floob Entry"; }

	@Override
	protected void setupObjectFeature(Cell cell, TiledMap.MapObject o) {
		switch (o.type) {
		case "Chest":
			PlainChest chest = new PlainChest();
			switch (o.name) {
			case "chest":
				chest.putItem(new VialOfHealing());
				chest.putItem(new VialOfHealing());
				chest.putItem(new Gold(25));
				break;
			}

			addFeature(cell, chest);
			break;
		case "Point":
			addPointOfInterest(o.name,cell);
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
		case "princess":
			// TBD: I need the princess icon!
			addNPC(cell, new Princess());
			break;
		case "left":
			addNPC(cell, leftGuard);
			break;
		case "right":
			addNPC(cell, rightGuard);
			break;
		default:
			// Handle unimplemented mobiles in the usual way.
			super.setupMobile(cell, o);
			break;
		}
	}
	

	//-------------------------------------------------------------------------
	// Methods for use by the Board
	
	// TBD.

	//-------------------------------------------------------------------------
	// Level Inner Classes
		

	private class EntryGuard extends Mannikin {
		private static final long serialVersionUID = 1L;

		/** The point-of-interest to which the mannikin will retreat.
		 * when satisfied. */
		private String retreat;
		private boolean retreated = false;
		
		EntryGuard(String retreat) {
			super("guard", Mobiles.PEASANT_ROBOT, FloobEntry.ID);
			this.retreat = retreat;
		}
		
		@Override
		public void talk(PlayerCharacter pc) {
			// FIRST, set the greeting
			if (retreated) {
				setGreetingTag("retreated");
			} else if (Quests.UNHAPPY_PRINCESS.isAccepted()) {
				setGreetingTag("gotquest");
			} else  {
				setGreetingTag("noquest");
			}
			
			// NEXT, have the conversation
			super.talk(pc);
			
			// NEXT, if the quest has been accepted, the guards move.
			if (Quests.UNHAPPY_PRINCESS.isAccepted()) {
				leftGuard.retreat();
				rightGuard.retreat();
			}
		}
		
		/** Makes the guard retreat. */
		public void retreat() {
			place(region().getPointOfInterest(retreat));
			retreated = true;
		}
	}

	private class Princess extends Mannikin {
		private static final long serialVersionUID = 1L;

		Princess() {
			super("princess", Mobiles.PRINCESS, FloobEntry.ID);
		}
		
		@Override
		public void talk(PlayerCharacter pc) {
			if (!Quests.MAGIC_RING.isAccepted()) {
				setGreetingTag("noquest1");
				super.talk(pc);

				setGreetingTag("noquest2");
				super.talk(pc);

				setGreetingTag("noquest3");
				super.talk(pc);

				setGreetingTag("noquest4");
				super.talk(pc);

				setGreetingTag("noquest5");
				super.talk(pc);
				
				Quests.MAGIC_RING.accept();
				Quests.UNHAPPY_PRINCESS.complete();
			} else if (!Quests.MAGIC_RING.isCompleted()) {
				setGreetingTag("gotquest");
				super.talk(pc);
			} else {
				// Don't know yet what she'll say when the quest is completed.
				setGreeting("TBD");
				super.talk(pc);
			}
		}
	}

	
	
	//-------------------------------------------------------------------------
	// Tile Set
	
	
	private enum MyTile implements TerrainTile {
		// Terrain Tiles
		
		UNKNOWN("Unknown", TerrainType.UNKNOWN),
		TILE_FLOOR("Tile floor", TerrainType.FLOOR),
		BLOCK_WALL("Block wall", TerrainType.WALL),
		COBBLE_FLOOR("Cobblestone floor", TerrainType.FLOOR),
		STONE_WALL("Stone wall", TerrainType.WALL),
		WATER("Water", TerrainType.WATER),
		EARTH("Earth", TerrainType.FLOOR),
		GRASS("Grass", TerrainType.FLOOR),
		SAND("Sand", TerrainType.FLOOR),
		GRAVEL("Gravel", TerrainType.FLOOR),
		WOOD_FLOOR_NS("Hardwood floor", TerrainType.FLOOR),
		WOOD_FLOOR_EW("Hardwood floor", TerrainType.FLOOR),
		HBRIDGE("Bridge", TerrainType.FLOOR),
		HBRIDGE_NORTH("Bridge", TerrainType.FLOOR),
		TREE("Tree", TerrainType.WALL),
		BRICK_SPIRAL("Brick walk", TerrainType.FLOOR),
		BRICK_NS("Brick walk", TerrainType.FLOOR),
		BRICK_EW("Brick walk", TerrainType.FLOOR),
		VBRIDGE_WEST("Bridge", TerrainType.FLOOR),
		VBRIDGE_EAST("Bridge", TerrainType.FLOOR),
		VBRIDGE("Bridge", TerrainType.FLOOR),
		HBRIDGE_SOUTH("Bridge", TerrainType.FLOOR),
		TILE_NS("Tile floor", TerrainType.FLOOR),
		TILE_EW("Tile floor", TerrainType.FLOOR),
		WET_NW("Wet earth", TerrainType.FLOOR),
		WET_N("Wet earth", TerrainType.FLOOR),
		WET_NE("Wet earth", TerrainType.FLOOR),
		BLOCK_EW("Block wall", TerrainType.WALL),
		CHEST("Chest"),
		UNUSED1(),
		UNUSED2(),
		UNUSED3(),
		WET_W("Wet earth", TerrainType.FLOOR),
		GRATE("Grating", TerrainType.FLOOR),
		WET_E("Wet earth", TerrainType.FLOOR),
		BED_HEAD("Bed"),
		BED_FOOT("Bed"),
		UNUSED4(),
		UNUSED5(),
		UNUSED6(),
		WET_SW("Wet earth", TerrainType.FLOOR),
		WET_S("Wet earth", TerrainType.FLOOR),
		WET_SE("Wet earth", TerrainType.FLOOR),
		ARCH_LEFT("Castle entrance"),
		ARCH_RIGHT("Castle entrance");

		// Instance variables
		private String name;
		private TerrainType terrain;
		
		MyTile(String name, TerrainType terrain) {
			this.name = name;
			this.terrain = terrain;
		}
		
		// Use this for feature tiles.
		MyTile(String name) {
			this(name, TerrainType.UNKNOWN);
		}
		
		// Use this for unused type.s
		MyTile() {
			this("Unused");
		}
		
		
		/** @return the tile's name. */
		@Override
		public String getName() {
			return name;
		}
		
		/** @return the tile's terrain type. */
		@Override
		public TerrainType getTerrain() {
			return terrain;
		}
		
		/** @return true if the tile is opaque, and false otherwise. */
		@Override
		public boolean isOpaque() {
			return terrain.opaque;
		}
		
		/** @return true if a mobile can walk on the tile, and false otherwise. */
		@Override
		public boolean isWalkable() {
			return terrain.walkable;
		}
		
		/** @return true if the a mobile can fly through the tile, and false otherwise. */
		@Override
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
			images = ImageUtils.loadTileSet(FloobEntry.class,"floob_entry.png");
		}
	}
}
