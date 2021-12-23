/** Static Utility Functions.
 * 
 */
package graphics;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/** Static Utility Functions.  These are functions for use in main programs in this package;
 * they mostly do I/O and halt on error.
 * @author will
 *
 */
public final class ImageUtils {
	// Can't instantiate
	private ImageUtils() {}
	
	/** Saves a buffered image to disk as a PNG file with a given name; 
	 * halts with an error if the file cannot be saved.
	 * 
	 * @param filename the name of the file on the disk
	 * @param img the image to save
	 * @return The file object containing the file name.
	 */
	public static File savePngImage(String filename, BufferedImage img) {
		try {
		    // retrieve image
		    File outputfile = new File(filename);
		    ImageIO.write(img, "png", outputfile);
		    System.out.println("Wrote image " + outputfile);
		    return outputfile;
		} catch (IOException e) {
		    System.out.println("Could not write " + filename + ":" + e);
		    System.exit(1);
		}
		
		return null;
	}
	
	/** Saves a sequence of frames as an animated GIF file with the specified name.
	 * Halts if the file cannot be written.
	 * @param filename The filename for the animated GIF
	 * @param frames A list of the frames, which should all be the same size.
	 * @param delayTime The delay between frames in hundredths of a second.
	 * @return The file object containing the file name.
	 */
    public static File saveAnimatedGif(String filename, List<BufferedImage> frames, int delayTime) {
		// FIRST, create a new file; delete any existing file with the same name.
        File f = new File(filename);

        if (f.exists()) {
            if (!f.delete()) {
                System.err.println("Cannot delete file! " + f);
                System.exit(1);
            }
        }
        
        // save an animated GIF at 5 FPS, and display it.
        try {
        	f.createNewFile();
            AnimatedGif.saveAnimation(frames, delayTime + "", f);
        } catch (Exception e) {
        	System.err.println("Cannot save animation! " + e);
        	System.exit(1);
        }
	
        return f;
    }
    
    /**
     * Pops up a message dialog displaying the image in the specified file.
     * TBD: Just display an image and leave the file out of it.
     * @param f The file containing the image.
     */
    public static void previewImageFile(File f) {
    	try {
    		JOptionPane.showMessageDialog(null, new ImageIcon(f.toURI().toURL()));
    	} catch (Exception e) {
    		System.err.println("Cannot show image file! " + e);
    		System.exit(1);
    	}
    }
    
    /** Loads a tile set image into memory, returning a list of the tile images
     * as Sprites.
     * 
     * @param cls The class that owns the resource
     * @param resource  The resource name of the image file.
     * @return The array of sprite images.
     */
    public static SpriteImage[] loadTileSet(Class<?> cls, String resource) {
		// FIRST, read the image.
    	BufferedImage tileSet = null;
    	
    	try {
    		InputStream istream = cls.getResourceAsStream(resource);
    		tileSet = ImageIO.read(istream);
    	} catch (IOException e) {
    		System.err.println("Could not read tile set from " + resource);
    		System.exit(1);
    	}
    	    	
		// NEXT, get some metrics.
		int cols = tileSet.getWidth(null) / Sprite.SIZE;
		int rows = tileSet.getHeight(null) / Sprite.SIZE;
		
		// NEXT, prepare the array.
		SpriteImage[] tiles = new SpriteImage[rows*cols];
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				BufferedImage tile = 
					tileSet.getSubimage(j*Sprite.SIZE, i*Sprite.SIZE, 
										Sprite.SIZE, Sprite.SIZE);
				tiles[i*cols + j] = new SpriteImage(tile);
			}
		}
		
		return tiles;
    }
    
    
    
    /** Loads a tile set image into memory, returning a list of the tile images.
     * 
     * @param cls  The class that owns the resource
     * @param resource  The resource name of the image file.
     * @param tileSize  The size of the tile in pixels.
     * @return
     */
    public static Image[] loadTileSet(Class<?> cls, String resource, int tileSize) {
		// FIRST, read the image.
    	BufferedImage tileSet = null;
    	
    	try {
    		InputStream istream = cls.getResourceAsStream(resource);
    		tileSet = ImageIO.read(istream);
    	} catch (IOException e) {
    		System.err.println("Could not read tile set from " + resource);
    		System.exit(1);
    	}
    	    	
		// NEXT, get some metrics.
		int cols = tileSet.getWidth(null) / tileSize;
		int rows = tileSet.getHeight(null) / tileSize;
		
		// NEXT, prepare the array.
		Image[] tiles = new Image[rows*cols];
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				BufferedImage tile = 
					tileSet.getSubimage(j*tileSize, i*tileSize, 
										tileSize, tileSize);
				tiles[i*cols + j] = tile;
			}
		}
		
		return tiles;
    }
}
