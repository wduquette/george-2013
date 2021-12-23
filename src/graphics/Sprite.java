/**
 * 
 */
package graphics;

import java.awt.Image;

import javax.swing.ImageIcon;

/**
 * @author will
 *
 */
public interface Sprite {
	/** The height and width of sprites in pixels. */
	static public final int SIZE = 40;
	
	/** @return the sprite's image. */
	public Image image();
	
	/** @return the sprite's image as an ImageIcon */
	public ImageIcon icon();
	
	/** @return the sprite's image, magnified. */
	public Image bigImage();
	
	/** @return the sprite's icon, magnified. */
	public ImageIcon bigIcon();
}
