/**
 * 
 */
package rpg;

import graphics.ImageUtils;

import java.awt.Image;

import javax.swing.ImageIcon;

/** An enumeration of the status condition icons that are currently
 * available.
 * @author will
 */
@SuppressWarnings("javadoc")
public enum Condition {
	SLEEP,
	HAYFEVER,
	GIDDY,
	NEWT;
		
	/** @return the sprite's image. */
	public Image image() {
		return images[this.ordinal()];
	}
	
	/** @return the sprite's image as an icon */
	public ImageIcon icon() {
		return new ImageIcon(images[this.ordinal()]);
	}

	// Static Data

	/** The list of tile images, read from the disk. */
	static private Image[] images;
	
	static {
		images = ImageUtils.loadTileSet(Condition.class,"condition.png", 10);
	}
}
