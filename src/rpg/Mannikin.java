/**
 * 
 */
package rpg;

import graphics.Sprite;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import rpg.pc.PlayerCharacter;
import util.Script;
import util.StringsTable;
import app.Board;
import app.Strings;

/** A Mannikin.  It stands there and does nothing, though it may
 * talk with you.  Mannikins are used for townsfolk who have minimal
 * behavior.
 * @author will
 */
public class Mannikin extends Mobile  implements Serializable {
	private static final long serialVersionUID = 1L;

	// Instance Variables
	private String key;
	private Sprite sprite;
	private String table;
	private List<String> usedGreetings = new ArrayList<>(1);
	private List<String> greetings = new ArrayList<>(1);

	// Creation
	
	/** Creates the mobile and places it on the board. 
	 * @param key The name of the mobile
	 * @param sprite The displayed sprite.
	 * @param table The key of the strings table in the registry.
	 */
	public Mannikin(String key, Sprite sprite, String table) {
		// FIRST, initialize the super class.
		super();
		
		this.key = key;
		this.sprite = sprite;
		this.table = table;
		setGreetings(key + ".greeting*");
	}
	
	@Override public String name()   { 
		String name = string("name");
		
		return name != null ? name : key; 
	}
	
	@Override public Sprite sprite() { return sprite; }
	
	@Override public String description() { 
		String description = string("description");
		return description != null ? description : name(); 
	}
	
	/** Retrieves one of this mannikin's strings from its strings table.
	 * @param tag The tag, "key.tag"
	 * @return The string, or null if none.
	 */
	private String string(String tag) {
		return Strings.get(table, key + "." + tag);
	}
	
	/** Sets the mannikin's greeting to the string with the given tag
	 * in the strings table.
	 * @param tag The strings table tag.
	 */
	public final void setGreetingTag(String tag) {
		setGreeting(string(tag));
	}
	
	/** Sets the mannikin's greeting text, so that it can
	 * talk to the party.
	 * @param greeting The new greeting.
	 */
	public final void setGreeting(String greeting) {
		greetings.clear();
		greetings.add(greeting);
	}
	
	/** Sets the mannikin's greetings to a set strings from its strings table.
	 * 
	 * @param pattern A key, or a glob pattern matching a number of keys.
	 */
	public final void setGreetings(String pattern) {
		greetings.clear();
		StringsTable strings = Strings.table(table);
		
		for (String key : strings.keyList(pattern)) {
			greetings.add(strings.get(key));
		}
	}
	
	/** @return a greeting from the mannikin's store of greetings. */
	public final String getGreeting() {
		if (greetings.size() == 0) {
			for (String greeting : usedGreetings) {
				greetings.add(greeting);
			}
			usedGreetings.clear();
		}

		String greeting = random.takeFrom(greetings);
		usedGreetings.add(greeting);

		return greeting;
	}
	
	@Override
	public boolean canTalk() { return true; }
	
	

	//-------------------------------------------------------------------------
	// Required Mobile Behavior

	/** Mannikins don't do anything.
	 */
	@Override
	public boolean move(Script script) {
		return true;
	}

	/** Say your greeting to the PC.
	 */
	@Override
	public void talk(PlayerCharacter pc) {
		// Add some newlines to the greeting, so that extra whitespace
		// goes to the bottom.
		JOptionPane.showMessageDialog(Board.get(), 
				getGreeting() + "\n\n\n", 
				name(), 
				JOptionPane.PLAIN_MESSAGE, 
				bigIcon());

	}
}

