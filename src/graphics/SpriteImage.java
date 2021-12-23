/**
 * 
 */
package graphics;

import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

/**
 * @author will
 *
 */
public class SpriteImage implements Sprite {
	private BufferedImage image;

	/** Creates a new SpriteImage from a BufferedImage.
	 * 
	 * @param image The buffered image.
	 */
	public SpriteImage(BufferedImage image) {
		this.image = image;
	}

	/** @return the Sprite's image */
	@Override
	public Image image() {
		return image;
	}
	
	/** @return the sprite's image, magnified. */
	@Override
	public Image bigImage() {
		int size = 3*Sprite.SIZE;
		return image.getScaledInstance(size, size, Image.SCALE_DEFAULT);
	}
	
	/** @return the sprite's icon. */
	@Override
	public ImageIcon icon() {
		return new ImageIcon(image);
	}

	/** @return a sprite's icon, magnified. */
	@Override
	public ImageIcon bigIcon() {
		return new ImageIcon(bigImage());
	}
}
