/**
 * 
 */
package graphics;

import java.awt.Image;

import javax.swing.ImageIcon;

/** An enumeration of the sprite images available for use as
 * attack animations.  I'd like to have an explosion icon
 * (something like a bigger fireball for magic hits.
 * @author will
 */
public enum Effects implements Sprite {
	/** Bullet (arbitrary ranged missile) */		BULLET,
	/** Target (arbitrary ranged hit) */			TARGET,
	/** Fireball (ranged magic) */					FIREBALL,
	/** Jaws (biting attack) */						JAWS,
	/** Fist (Unarmed attack) */                    FIST,
	/** Swoosh (Miss!) */                           SWOOSH,
	/** Claw */                                     CLAW,
	/** Slime */                                    SLIME,
	/** Zzzz's */                                   ZZZ;
	
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
		images = ImageUtils.loadTileSet(Effects.class, "Effects.png");
	}
}
