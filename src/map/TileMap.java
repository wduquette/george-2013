/**
 * 
 */
package map;

import graphics.StandardTile;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** This class loads text-formatted tile maps export by PyxelEdit.  It presumes
 * that tile maps have up to three layers: Terrain, Features, and Mobiles.  The
 * tile indices are presumed to match those in Sprite.java, and so the TileMap is
 * a map of sprites.  The Terrain is stored as a rectangular array of Sprites; 
 * the Features and Mobiles layers are stored as Maps<Cell,Sprite>.
 * 
 * @author will
 *
 */
public final class TileMap {
	/** This exception is thrown if the tile map file's format is wrong. */
	@SuppressWarnings("serial") // Won't be serializing the exception.
	public class FormatException extends RuntimeException {
		FormatException(String s) {
			super(s);
		}
	};
	
	// Static Code
	
	/** Create a tile map given a URL; halt on error.
	 * 
	 * @param url  The URL of the tile map text file.
	 * @param tiles A list of the tiles to associate with the text file.
	 * @return The new tile map.
	 */
	public static TileMap load(URL url, TerrainTile[] tiles) {
	   	try {
    		return new TileMap(url, tiles);
    	} catch (IOException e) {
    		System.err.println("Could not read tile map file: " + url + ", " + e);
    		System.exit(1);
    	}

	   	return null;
	}
	
	// Instance Variables
	
	// Height and width of the map in tiles.
	private TerrainTile[] tiles;
	private int rows;  
	private int cols;  
	
	// Terrain map, height by width
	private TerrainTile terrain[][];
	
	// Features and mobile pre-positioned on the map.
	private Map<Cell,TerrainTile> features = new HashMap<>();
	private Map<Cell,TerrainTile> mobiles = new HashMap<>();
	
	// Constructor

	/** Creates a new tile map, given the URL of the tile map 
	 * specification.
	 * 
	 * @param url The URL of the tile map text file.
	 * @param tiles A list of the tiles to associate with the text file.
	 * @throws IOException if the tile map cannot be read from the disk.
	 */
	public TileMap(URL url, TerrainTile[] tiles) throws IOException {
		// FIRST, save the tiles
		this.tiles = tiles;
		
		// NEXT, read the file.
		File file = new File(url.getFile());
		List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.US_ASCII);
		
		// NEXT, get the metadata
		cols = readMetaInteger(lines, "tileswide");
		rows = readMetaInteger(lines, "tileshigh");
		skipLine(lines);   					 		// Don't need tilewidth
		skipLine(lines);							// Don't need tileheight
		skipLine(lines);                            // Blank line
		
		// NEXT, we will always have a terrain layer.
		readTerrain(lines);
		skipLine(lines);
		
		// NEXT, if we've got features, read them.
		if (lines.size() >= rows) {
			readLayer(lines, features);
			skipLine(lines);
		}
		
		// NEXT, if we've got mobiles, read them.
		if (lines.size() >= rows) {
			readLayer(lines, mobiles);
			skipLine(lines);
		}
		
	}
	
	/** Skip one line in the input.
	 * 
	 * @param lines The lines of input data.
	 */
	private void skipLine(List<String> lines) {
		if (lines.size() > 0)
			lines.remove(0);
	}
	
	/** Read an integer data field on the next line in the input.
	 * 
	 * @param lines The lines of input data.
	 * @param name The name of the data field
	 * @return The integer value
	 * @throws FormatException If the data couldn't be read.
	 */
	private int readMetaInteger(List<String> lines, String name) throws FormatException {
		if (lines.size() == 0)
			throw new FormatException("Missing data item: " + name);

		String line = lines.remove(0);
		
		if (!line.startsWith(name))
			throw new FormatException("Missing data item: " + name);

		try {
			return Integer.valueOf(line.substring(name.length() + 1));
		} catch (NumberFormatException e) {
			throw new FormatException("Data item " + name + " is malformed: " + e);
		}
	}
	
	/** Reads the terrain layer (layer 0) from the map.
	 * 
	 * @param lines the lines of input data.
	 */
	private void readTerrain(List<String> lines) throws FormatException {
		// FIRST, skip the layer tag
		skipLine(lines);
		
		// NEXT, there must be a line for each row in the map.
		if (lines.size() < rows)
			throw new FormatException("Missing rows in the terrain layer");
		
		// NEXT, allow the row vector.
		terrain = new TerrainTile[rows][];
		
		// NEXT, read each row.
		for (int r = 0; r < rows; r++) {
			// FIRST, allocate the columns.
			terrain[r] = new TerrainTile[cols];
			
			String[] indices = lines.remove(0).split(",");
			
			if (indices.length < cols)
				throw new FormatException("Missing columns in the terrain layer");
			
			for (int c = 0; c < cols; c++) {
				int index = -1;
				
				try {
					index = Integer.valueOf(indices[c]);
				} catch (NumberFormatException e) {
					throw new FormatException("Invalid tile index: " + e);
				}
				
				terrain[r][c] = tiles[index];
			}
		}
	}
	
	private void readLayer(List<String> lines, Map<Cell,TerrainTile> layerMap) throws FormatException {
		// FIRST, skip the layer tag
		skipLine(lines);
		
		// NEXT, there must be a line for each row in the map.
		if (lines.size() < rows)
			throw new FormatException("Missing rows in optional layer");
		
		for (int r = 0; r < rows; r++) {
			String[] indices = lines.remove(0).split(",");
			
			if (indices.length < cols)
				throw new FormatException("Missing columns in optional layer");
			
			for (int c = 0; c < cols; c++) {
				int index = -1;
				
				try {
					index = Integer.valueOf(indices[c]);
				} catch (NumberFormatException e) {
					throw new FormatException("Invalid tile index: " + e);
				}
				
				if (index != -1) {
					layerMap.put(new Cell(r,c), tiles[index]);
				}
			}
		}
	}
	
	// Queries
	
	/** @return the height of the tile map in tiles. */
	public int rows() {
		return rows;
	}
	
	/** @return the width of the tile map in tiles. */
	public int cols() {
		return cols;
	}
	
	/** Get the terrain sprite at a particular cell.
	 * 
	 * @param r The row index
	 * @param c The column index
	 * @return The sprite
	 */
	public TerrainTile getTerrain(int r, int c) {
		return terrain[r][c];
	}
	
	/** Get the terrain sprite at a particular cell.
	 * 
	 * @param cell The cell
	 * @return The sprite
	 */
	public TerrainTile getTerrain(Cell cell) {
		return terrain[cell.row][cell.col];
	}
	
	/** Return the feature sprite at r,c, or null if none.
	 * 
	 * @param r The row index
	 * @param c The column index
	 * @return The sprite, or null.
	 */
	private TerrainTile featureAt(int r, int c) {
		return featureAt(new Cell(r,c));
	}
	
	/** Return the feature sprite at the given cell.
	 * 
	 * @param cell The cell
	 * @return The sprite or null.
	 */
	private TerrainTile featureAt(Cell cell) {
		return features.get(cell);
	}

	/** Return the mobile sprite at r,c, or null if none.
	 * 
	 * @param r The row index
	 * @param c The column index
	 * @return The sprite, or null.
	 */
	private TerrainTile mobileAt(int r, int c) {
		return mobileAt(new Cell(r,c));
	}
	
	/** Return the mobile sprite at the given cell.
	 * 
	 * @param cell The cell
	 * @return The sprite or null.
	 */
	private TerrainTile mobileAt(Cell cell) {
		return mobiles.get(cell);
	}
	
	/** Retrieve the mobiles, by location.
	 * 
	 * @return The mapping from cells to mobiles.
	 */
	public Map<Cell,TerrainTile> getMobiles() {
		return Collections.unmodifiableMap(mobiles);
	}
	
	/** Retrieve the features, by location.
	 * 
	 * @return The mapping from cells to features.
	 */
	public Map<Cell,TerrainTile> getFeatures() {
		return Collections.unmodifiableMap(features);
	}
	
	/** Draws the tile map to an image using the Sprite images, and 
	 * overlays the layers.
	 * @return the tile array as a BufferedImage.
	 */
	public BufferedImage toBufferedImage() {
		int iwidth = cols*StandardTile.SIZE;
		int iheight = rows*StandardTile.SIZE;
		
		BufferedImage img = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = img.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, cols, rows);
		
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				// Remember, i is vertical but x is horizontal
				g.drawImage(terrain[r][c].image(), c*StandardTile.SIZE, r*StandardTile.SIZE, null);
				
				TerrainTile feature = featureAt(r,c);
				
				if (feature != null) {
					g.drawImage(feature.image(), c*StandardTile.SIZE, r*StandardTile.SIZE, null);
				}

				TerrainTile mobile = mobileAt(r,c);
				
				if (mobile != null) {
					g.drawImage(mobile.image(), c*StandardTile.SIZE, r*StandardTile.SIZE, null);
				}
			}
		}
		
		g.dispose();
		
		return img;		
	}
	
	/** @return a TerrainGrid containing the given terrain for each
	 * cell.
	 */
	public TerrainGrid toTerrainGrid() {
		TerrainGrid grid = new TerrainGrid(rows(), cols());

		// FIRST, look up the terrain that matches the sprites.
		for (int r = 0; r < grid.rows(); r++) {
			for (int c = 0; c < grid.cols(); c++) {
				grid.set(r, c, getTerrain(r, c));
			}
		}
		return grid;
	}
	
}
