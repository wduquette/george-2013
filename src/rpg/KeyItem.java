/**
 * 
 */
package rpg;

import graphics.Sprite;

/** A key item is a key object used to drive the story.  Key items cannot
 * be lost or accidentally disposed of.  All key items with the same name
 * are identical.
 * 
 * @author will
 *
 */
public class KeyItem extends Item {
	private static final long serialVersionUID = 1L;

	private String name;
	private Sprite sprite;
	
	/** Creates a new Maguffin.
	 * 
	 * @param name The maguffin's name
	 * @param sprite The maguffin's sprite
	 */
	public KeyItem(String name, Sprite sprite) {
		this.name = name;
		this.sprite = sprite;
	}
	
	/* (non-Javadoc)
	 * @see rpg.Item#name()
	 */
	@Override
	public String name() {
		return name;
	}

	/* (non-Javadoc)
	 * @see rpg.Item#sprite()
	 */
	@Override
	public Sprite sprite() {
		return sprite;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyItem other = (KeyItem) obj;
		if (this.name == null) {
			if (other.name != null)
				return false;
		} else if (!this.name.equals(other.name))
			return false;
		return true;
	}
}
