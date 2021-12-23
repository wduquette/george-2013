/**
 * 
 */
package map;

import graphics.Sprite;

/** A TerrainTile is a sprite with a terrain type.
 * TBD: Consider replacing isWalkable, isFlyable with isPassable(MovementType)
 * @author will
 *
 */
public interface TerrainTile extends Sprite {
	/** @return the tile's name. */
	public String getName();
	
	/** @return the tile's terrain type. */
	public TerrainType getTerrain();
	
	/** @return true if the tile is opaque, and false otherwise. */
	public boolean isOpaque();
	
	/** @return true if a mobile can walk on the tile, and false otherwise. */
	public boolean isWalkable();
	
	/** @return true if the a mobile can fly through the tile, and false otherwise. */
	public boolean isFlyable();
}
