/**
 * 
 */
package app;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import util.StringsTable;

/** This class serves as a registry for StringsTable objects, so that Entities
 * can have a serializable reference to a StringsTable without having to
 * make the StringsTable itself (a static collection of data) part of the
 * game's save data.
 *  
 * @author will
 *
 */
public class Strings {
	// Can't instantiate
	private Strings() {}
	
	/** The mapping from keys to strings tables. */
	private static Map<Serializable,StringsTable> map = new HashMap<>();
	
	/** Registers a strings table with the registry for later access.
	 * 
	 * @param tableKey  The key.  Any serializable object can be used, but
	 * it will usually be a String.
	 * @param table The StringsTable itself.
	 */
	public static void register(String tableKey, StringsTable table) {
		map.put(tableKey, table);
	}
	
	/** Loads and registers a strings table with the registry for later access.
	 * 
	 * @param tableKey  The key.  Any serializable object can be used, but
	 * it will usually be a String.
	 * @param cls The class that owns the resource
	 * @param resource The resource name of the .strings file.
	 * @return the strings table
	 */
	public static StringsTable register(String tableKey, Class<?> cls, String resource) {
		StringsTable table = new StringsTable(cls,resource); 
		register(tableKey, table);
		
		return table;
	}
	
	/** Retrieves a strings table given its key; the table must exist.
	 * @param tableKey The table's key.
	 * @return a StringsTable.
	 */
	public static StringsTable table(Serializable tableKey) {
		StringsTable table = map.get(tableKey);
		
		if (table == null) {
			throw new IllegalArgumentException("No registered strings table:" + tableKey);
		}
		
		return table;
	}
	
	/** Retrieves a string from a table.  Both the table and the string must
	 * exist.
	 * @param tableKey The table's key in the map.
	 * @param stringKey The string's key in the table.
	 * @return the string
	 */
	public static String get(Serializable tableKey, String stringKey) {
		String string = table(tableKey).get(stringKey);
		
		if (string == null) {
			throw new IllegalArgumentException("No such string in table " 
					+ tableKey + ": " + stringKey);
		}
		
		return string;
		
	}
}
