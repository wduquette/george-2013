/**
 * 
 */
package graphics;

import java.awt.Image;

import javax.swing.ImageIcon;

/** An enumeration of the sprite images available for use as
 * feature icons.
 * @author will
 */
public enum Features implements Sprite {
	/** Blank */                                BLANK,
	/** Closed Door */							CLOSED_DOOR,
	/** Open Door */							OPEN_DOOR,
	/** Stairs Down */							STAIRS_DOWN,
	/** Stairs Up */							STAIRS_UP,
	/** Chest, closed */						CHEST,
	/** Chest, open */                          OPEN_CHEST,
	/** Ladder down */ 							LADDER_DOWN,
	/** Ladder up */							LADDER_UP,
	/** Active nether portal */ 				PORTAL_ACTIVE,
	/** Inactive nether portal */				PORTAL_INACTIVE,
	/** Sign */									SIGN,
	/** Spikes, to go on floor. */				SPIKES,
	/** Pedestal */								PEDESTAL,
	/** Save Pedestal */						SAVE_PEDESTAL,
	/** Broken Orb */                           BROKEN_ORB,
	/** Yellow Orb */                           YELLOW_ORB,
	/** Blue Orb */                             BLUE_ORB,
	/** Green Orb */                            GREEN_ORB,
	/** Red Orb */                              RED_ORB,
	/** Tall Pedestal */                        TALL_PEDESTAL,
	/** Heart Pedestal */						HEART_PEDESTAL;
	
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
		images = ImageUtils.loadTileSet(Features.class,"Features.png");
	}
}
