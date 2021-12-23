/**
 * 
 */
package graphics;

import java.awt.Image;

import javax.swing.ImageIcon;

/** An enumeration of the sprite images available for use as
 * item icons.
 * @author will
 */
public enum Items implements Sprite {
    /** Sandals */					SANDALS,
    /** Shoes */					SHOES,
    /** Leather Boots */            BOOTS,
    /** Chain Boots */              CHAIN_BOOTS,
    /** Plate Boots */              PLATE_BOOTS,
    /** Lord's Boots */				LORD_BOOTS,
    /** Leather Armor */			LEATHER_ARMOR,
    /** Chain Armor */ 				CHAIN_ARMOR,
    /** Plate Armor */				PLATE_ARMOR,
    /** Lord's Armor */				LORD_ARMOR,
    /** Leather Helmet */			LEATHER_HELMET,
    /** Pot Helmet */				POT_HELMET,
    /** Chain Helmet */				CHAIN_HELMET,
    /** Plate Helmet */				PLATE_HELMET,
    /** Lord's Helmet */			LORD_HELMET,
    /** Dagger */					DAGGER,
    /** Short Sword */				SHORT_SWORD,
    /** Long Sword */				LONG_SWORD,
    /** Pizza Pan */				PIZZA_PAN,
	/** Backpack */                 BACKPACK,
    /** Wooden Shield */			WOODEN_SHIELD,
    /** Leather Shield */			LEATHER_SHIELD,
    /** Plate Shield */				PLATE_SHIELD,
    /** Lord's Shield */			LORD_SHIELD,
    /** Bow */     					BOW,
    /** Fancy Bow */ 				FANCY_BOW,
	/** Staple Gun */ 				STAPLE_GUN,
    /** Hat */						HAT,
    /** Overalls */					OVERALLS,
    /** Pitchfork */                PITCHFORK,
    /** Monk's Habit */             MONK_HABIT,
    /** Holy Book */				HOLY_BOOK,
    /** Staff */          			STAFF,
    /** Holy Orb */					HOLY_ORB,
	/** Gold Key */					GOLD_KEY,
	/** Trophy */					TROPHY,
	/** Word Scroll */              WORD_SCROLL,
	/** Map Scroll */               MAP_SCROLL,
	/** Red Vial */					RED_VIAL,
	/** Red Flask */				RED_FLASK,
	/** Blue Vial */				BLUE_VIAL,
	/** Blue Flask */				BLUE_FLASK,
	/** Green Vial */				GREEN_VIAL,
	/** Green Flask */				GREEN_FLASK,
	/** Gold Vial */				GOLD_VIAL,
	/** Gold Flask */				GOLD_FLASK,
	/** Purple Vial */				PURPLE_VIAL,
	/** Purple Flask */				PURPLE_FLASK,
	/** Small Wrench */             SMALL_WRENCH,
	/** Skull */                    SKULL,
	/** Gold Coin */                GOLD_COIN,
	/** Handkerchief */             HANDKERCHIEF,
	/** Spitoon */                  SPITTOON;
	
	
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
		images = ImageUtils.loadTileSet(Items.class,"Items.png");
	}
}
