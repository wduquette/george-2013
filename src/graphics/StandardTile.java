/**
 * 
 */
package graphics;

import java.awt.Image;

import javax.swing.ImageIcon;

import map.TerrainTile;
import map.TerrainType;

/** This tile set defines the tiles currently available for use in 
 * algorithmically defined regions (though they can also define their own).
 * These tiles can also be used as the basis for tile sets for 
 * regions with predefined maps.
 *
 * @author will
 */
@SuppressWarnings("javadoc")
public enum StandardTile implements TerrainTile {
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
	MOUNTAIN("Mountain", TerrainType.WALL),
	CAVE("Cave entrance", TerrainType.FLOOR),
	WOOD_FLOOR_NS("Hardwood floor", TerrainType.FLOOR),
	WOOD_FLOOR_EW("Hardwood floor", TerrainType.FLOOR),
	HBRIDGE("Bridge", TerrainType.FLOOR),
	HBRIDGE_NORTH("Bridge", TerrainType.FLOOR),
	FOREST("Forest", TerrainType.WALL),
	BRICK_SPIRAL("Brick walk", TerrainType.FLOOR),
	BRICK_NS("Brick walk", TerrainType.FLOOR),
	BRICK_EW("Brick walk", TerrainType.FLOOR),
	VBRIDGE_WEST("Bridge", TerrainType.FLOOR),
	VBRIDGE_EAST("Bridge", TerrainType.FLOOR),
	VBRIDGE("Bridge", TerrainType.FLOOR),
	HBRIDGE_SOUTH("Bridge", TerrainType.FLOOR);

	// Instance variables
	private String name;
	private TerrainType terrain;
	
	StandardTile(String name, TerrainType terrain) {
		this.name = name;
		this.terrain = terrain;
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
		images = ImageUtils.loadTileSet(StandardTile.class, "standard.png");
	}
}
