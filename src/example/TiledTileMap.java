/**
 * 
 */
package example;

/**
 * @author will
 *
 */
public class TiledTileMap {
	int height;
	int width;
	int tileheight;
	int tilewidth;
	int version;
	
	Layer[] layers;
	TileSet[] tilesets;
	
	void dump() {
		puts("height=" + height);
		puts("width=" + width);
		puts("tileheight=" + tileheight);
		puts("tilewidth=" + tilewidth);
		puts("version=" + version);
		puts("layer.size=" + layers.length);
		puts("\n");
		
		for (int i = 0; i < layers.length; i++) {
			puts("Layer " + i + ": " + layers[i].name);
			puts("  type="+layers[i].type);
			if (layers[i].data != null)
				puts("  data="+layers[i].data.length + " tile indices");
		}
		puts("\n");
		
		for (int i = 0; i < tilesets.length; i++) {
			puts("Tile Set " + i + ": " + tilesets[i].name);
			puts("  firstgid=" + tilesets[i].firstgid);
			puts("  image=" + tilesets[i].image);
		}
		puts("\n");
	}
	
	void puts(String line) {
		System.out.println(line);
	}
	
}
