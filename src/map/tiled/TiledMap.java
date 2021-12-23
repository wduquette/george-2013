/**
 * 
 */
package map.tiled;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import map.Cell;

import com.google.gson.Gson;

/** A class representing a Tiled Map Editor tile map, as exported into
 * JSON format and loaded by Gson.  This is expected to be a transient
 * object, loaded, used to build the real data structures, and then
 * discarded; hence it only has minimal convenience methods.
 * @author will
 *
 */
public class TiledMap {
	/** Height of the tile map, in tiles. */
	public int height;
	
	/** Width of the tile map, in tiles. */
	public int width;
	
	/** Height of one tile, in pixels.  */
	public int tileheight;
	
	/** Width of one tile, in pixels. */
	public int tilewidth;
	
	/** Version number for this object layout */
	public int version;
	
	/** Array of layers, which may be tile layers or object layers. */
	public Layer[] layers;
	
	/** Array of tile sets used by this tile map.  Not clear we're going
	 * to use it, but here it is.
	 */
	public TileSet[] tilesets;
	
	
	//------------------------------------------------------------------------
	// Convenience Methods
	
	/** Returns the tile layer with the given name, or null if no
	 * such layer is found.
	 * @param name The name.
	 * @return A tile layer, or null.
	 */
	public Layer getTileLayer(String name) {
		for (Layer x : layers) {
			if (x.name.equals(name) && x.type.equals("tilelayer")) {
				return x;
			}
		}
		
		return null;
	}
	
	/** Returns the object group with the given name, or null if no
	 * such layer is found.
	 * @param name The name.
	 * @return A Layer, or null.
	 */
	public Layer getObjectGroup(String name) {
		for (Layer x : layers) {
			if (x.name.equals(name) && x.type.equals("objectgroup")) {
				return x;
			}
		}
		
		return null;
	}
	
	/** Given a MapObject, return the Cell of its upper left corner,
	 * using the map's tile size.
	 *
	 * @param o A MapObject
	 * @return A Cell coordinate
	 */
	public Cell getObjectCell(MapObject o) {
		return new Cell(o.y / tileheight, o.x / tilewidth);
	}
	

	//------------------------------------------------------------------------
	// Nested classes
	
	/** A layer in the tile map */
	public static class Layer {
		/** The name of the layer, as displayed in Tiled's UI. */
		public String name;
		
		/** The type of the layer, either "tilelayer" or "objectgroup". */
		public String type;

		/** For tile layers, the array of tile GIDs, presumably in
		 * row-major order.
		 */
		public int[] data;
		
		/** For object groups, the array of objects. */
		public MapObject[] objects;
		
		//---------------------------------------------------------------------
		// Omitted fields
		
		// TBD: opacity
		// TBD: width
		// TBD: height
		// TBD: x
		// TBD: y
		// TBD: visible
		// TBD: properties
	}
	
	/** An object on a map (usually a feature or mobile location).  An
	 * object represents a point or some bounded area on the map.  It
	 * is defined in pixel coordinates rather than tile coordinates. */
	public static class MapObject {
		/** The name of the object. */
		public String name;
		
		/** The type, which is interpreted by the region. */
		public String type;
		
		/** The upper-left x-coordinate, in pixels. */
		public int x;

		/** The upper-left y-coordinate, in pixels. */
		public int y;
		
		/** The width of the object, in pixels. */
		public int width;
		
		/** The height of the object, in pixels. */
		public int height;
		
		/** Properties associated with the object. */
		public Properties properties;
				
		//---------------------------------------------------------------------
		// Omitted fields
		
		// TBD: visible
		// TBD: properties
		
	}

	/** A tile set used by the tile map */
	public static class TileSet {
		/** The global index for the first tile in this particular
		 * tile set.
		 */
		public int firstgid;
		
		/** The name of the image file that defines this tile set. */
		public String image;
		
		/** The name of the tile set in the Tiled UI. */
		public String name;
		
		/** The height of the tile set image in pixels. */
		public int imageheight;
		
		/** The width of the tile set image in pixels. */
		public int imagewidth;
		
		/** Height of one tile, in pixels.  */
		public int tileheight;
		
		/** Width of one tile, in pixels. */
		public int tilewidth;
		
		/** Margin around the outside of the image, in pixels. */
		public int margin;
		
		/** Spacing between tiles, in pixels. */
		public int spacing;
		
		// TBD: Properties, if needed.
	}
	
	/** A property structure for objects.  Unused properties will be null. */
	public static class Properties {
		/** Name of the Point-of-Interest associated with the object's
		 * location.
		 */
		public String point;
		
		/** Name of a Sprite to use for this object.  The enum from which
		 * the sprite is drawn may vary depending on the type of the object.
		 */
		public String sprite;
	}
	
	//------------------------------------------------------------------------
	// Static Methods.
	
	/** Read a Tiled Map Editor tile map from disk.  The tile map must be
	 * saved in JSON format as a resource of a known class.  If the resource
	 * cannot be read, the application terminates.
	 * @param cls The class
	 * @param resource The resource name
	 * @return The TiledMap object.
	 */
	public static TiledMap read(Class<?> cls, String resource) {
		URL url = cls.getResource(resource);
		StringBuilder jsonText = new StringBuilder();
		
		try {
			BufferedReader in = new BufferedReader(
				new InputStreamReader(url.openStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
				jsonText.append(inputLine);
			in.close();
		} catch (Exception e) {
			System.err.println("Error reading TiledMap from disk: " + e);
			System.exit(1);
		}
		
		// NEXT, parse the JSON.
		Gson gson = new Gson();
		
		return gson.fromJson(jsonText.toString(), TiledMap.class);
	}

	/** Main: test routine.
	 * @param args
	 */
	public static void main(String[] args) {
		TiledMap map = read(TiledMap.class, "test.json");
		
		dump(map);
	}

	/** Dump the read data to stdout, for testing.
	 */
	static private void dump(TiledMap map) {
		puts("height=" + map.height);
		puts("width=" + map.width);
		puts("tileheight=" + map.tileheight);
		puts("tilewidth=" + map.tilewidth);
		puts("version=" + map.version);
		puts("layer.size=" + map.layers.length);
		puts("\n");
		
		for (int i = 0; i < map.layers.length; i++) {
			puts("Layer " + i + ": " + map.layers[i].name);
			puts("  type="+map.layers[i].type);
			if (map.layers[i].data != null) {
				puts("  data="+map.layers[i].data.length + " tile indices");
			}
			
			if (map.layers[i].objects != null) {
				puts("  objects="+map.layers[i].objects.length + " map objects");
			}
		}
		puts("\n");
		
		for (int i = 0; i < map.tilesets.length; i++) {
			puts("Tile Set " + i + ": " + map.tilesets[i].name);
			puts("  firstgid=" + map.tilesets[i].firstgid);
			puts("  image=" + map.tilesets[i].image);
		}
		puts("\n");
	}

	/** Write to stdout, for testing.
	 * 
	 * @param line The line to write.
	 */
	private static void puts(String line) {
		System.out.println(line);
	}

}
