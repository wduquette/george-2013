/**
 * 
 */
package graphics;

import java.awt.Image;

import javax.swing.ImageIcon;

/** An enumeration of the sprite images available for use as
 * icons.<p>
 * TBD: Put the different kinds of icon in different .png files,
 * and load them in sequence.
 * @author will
 */
public enum Slots implements Sprite {
    /** Weapon Slot */							WEAPON_SLOT,
    /** Bow Slot */                             BOW_SLOT,
    /** Armor Slot */							ARMOR_SLOT,
    /** Shield Slot */                          SHIELD_SLOT,
    /** Helmet Slot */							HELMET_SLOT,
    /** Footwear Slot */						FOOTWEAR_SLOT,
    /** Item Slot */							ITEM_SLOT;
	
	
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
		images = ImageUtils.loadTileSet(Slots.class, "Slots.png");
	}
}
