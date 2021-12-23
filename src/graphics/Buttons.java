/**
 * 
 */
package graphics;

import java.awt.Image;

import javax.swing.ImageIcon;

/** An enumeration of the sprite images available for use as
 * buttons and other GUI controls.
 * @author will
 */
@SuppressWarnings("javadoc")
public enum Buttons implements Sprite {
	UNKNOWN,
	
	// Control Button Icons
	BACKPACK,
	COMBAT,
	NORMAL,
	SCROLL,
	MAP,
	MAGNIFIER,
	LOAD,
	SAVE;
	
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
		images = ImageUtils.loadTileSet(Buttons.class,"Buttons.png");
	}
}
