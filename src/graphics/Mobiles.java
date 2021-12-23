/**
 * 
 */
package graphics;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

/** An enumeration of the sprite images available for use as
 * mobiles.
 * @author will
 */
@SuppressWarnings("javadoc")
public enum Mobiles implements Sprite {
	// From mobile_human.png
	GEORGE,
	KNIGHT,
	FRIAR,
	WIZARD1,
	THIEF,
	DESK_WIZARD,
	FILLMORE,
	GOLD_FILLMORE,
	DARK_WIZARD,
	PEASANT1,
	PEASANT2,
	PEASANT3,
	PEASANT4,
	PEASANT5,
	PRINCESS,
	WIZARD2,
	
	// From mobile_animal
	GRAY_SNAKE,
	ORANGE_SNAKE,
	RED_SNAKE,
	GREEN_SNAKE,
	BLUE_SNAKE,
	PURPLE_SNAKE,
	XMAS_SNAKE,
	SPOTTED_SNAKE,
	BAT,
	BLACK_BAT,
	FIRE_BAT,
	ICE_BAT,
	GREEN_BAT,
	PURPLE_BAT,
	ELECTRIC_BAT,
	JET_BAT,
	RAT,
	GIANT_RAT,
	NEWT,
	UNUSED_ANIMAL3,
	UNUSED_ANIMAL4,
	UNUSED_ANIMAL5,
	UNUSED_ANIMAL6,
	UNUSED_ANIMAL7,
	
	// From mobile_demon
	IMP,
	IMP_WIZARD,
	IMP_FIGHTER,
	IMP_LORD,
	FIRE_DEMON,
	CTHULHU,
	UNUSED_DEMON6,
	UNUSED_DEMON7,
	
	// From mobile_fairy
	BLUE_FAIRY,
	ORANGE_FAIRY,
	GREEN_FAIRY,
	UNUSED_FAIRY3,
	UNUSED_FAIRY4,
	UNUSED_FAIRY5,
	UNUSED_FAIRY6,
	UNUSED_FAIRY7,

	
	// From mobile_humanoid
	KOBOLD,
	GOBLIN,
	ORC,
	OGRE,
	TROLL,
	FIRE_CULTIST,
	FIRE_PRIEST,
	CTHULHU_CULTIST,
	CTHULHU_PRIEST,
	UNUSED_HUMANOID1,
	UNUSED_HUMANOID2,
	UNUSED_HUMANOID3,
	UNUSED_HUMANOID4,
	UNUSED_HUMANOID5,
	UNUSED_HUMANOID6,
	UNUSED_HUMANOID7,
	
	// From mobile_insect
	ROACH,
	LADY_BUG,
	MANLY_BUG,
	GOLD_BUG,
	SPIDER,
	UNUSED_INSECT5,
	UNUSED_INSECT6,
	UNUSED_INSECT7,
	
	// From mobile_robot
	PEASANT_ROBOT,
	ROBOT,
	WAR_ROBOT,
	UNUSED_ROBOT3,
	UNUSED_ROBOT4,
	UNUSED_ROBOT5,
	UNUSED_ROBOT6,
	UNUSED_ROBOT7,
	
	// From mobile_slime
	WHITE_SLIME,
	YELLOW_SLIME,
	BLUE_SLIME,
	RED_SLIME,
	ORANGE_SLIME,
	PURPLE_SLIME,
	GREEN_SLIME,
	METAL_SLIME,
	

	// From mobile_undead
	ZOMBIE,
	SKELETON,
	REAPER,
	UNUSED_UNDEAD4,
	UNUSED_UNDEAD5,
	UNUSED_UNDEAD6,
	UNUSED_UNDEAD7;
	
	
	/** @return the sprite's image. */
	public Image image() {
		return images.get(this.ordinal()).image();
	}
	
	public Image bigImage() {
		return images.get(this.ordinal()).bigImage();
	}
	
	/** @return the sprite's image as an icon */
	public ImageIcon icon() {
		return images.get(this.ordinal()).icon();
	}

	/** @return the sprite's image as a magnified icon */
	public ImageIcon bigIcon() {
		return images.get(this.ordinal()).bigIcon();
	}

	// Static Data

	/** The list of tile images, read from the disk. */
	static private List<SpriteImage> images = new ArrayList<>();
	
	static private void loadTileSet(String resource) {
		SpriteImage array[]  = ImageUtils.loadTileSet(Mobiles.class, resource);
		for (SpriteImage bi : array) 
			images.add(bi);
	}

	static {
		loadTileSet("mobile_human.png");
		loadTileSet("mobile_animal.png");
		loadTileSet("mobile_demon.png");
		loadTileSet("mobile_fairy.png");
		loadTileSet("mobile_humanoid.png");
		loadTileSet("mobile_insect.png");
		loadTileSet("mobile_robot.png");
		loadTileSet("mobile_slime.png");
		loadTileSet("mobile_undead.png");
	}
}
