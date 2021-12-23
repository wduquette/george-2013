/**
 * 
 */
package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/** This class will load a Strings Table resource from disk, returning the
 * resource as a Map from keys to strings.  The file's contents is line
 * oriented:<p>
 * 
 * <code><pre>
 * # Lines beginning with "#" outside of a string are comments.
 * # Blank lines outside of a string are ignored.
 * 
 * # Strings begin with the %string <key> directive.  Keys should
 * # contain no whitespace, but there are no other constraints.
 * %string ThisIsTheKey
 * This is text in the string.
 * 
 * This is more text in the string.
 * %end
 * # Strings end with the %end directive.
 * </pre></code>
 * 
 * Strings may contain most anything.  Leading and trailing whitespace
 * is trimmed.<p>
 * 
 * @author will
 *
 */
public final class StringsTable {
	// Instance variables
	Map<String,String> table;

	/** Creates a Strings table using the strings in the named resource.
	 * @param cls  The class that owns the resource.
	 * @param resource The resource name..
	 */
	public StringsTable(Class<?> cls, String resource) {
		table = load(cls,resource);
	}
	
	//-------------------------------------------------------------------------
	// Getters
	
	/** Retrieves a string from the table given its key
	 * 
	 * @param key The key
	 * @return The string
	 */
	public String get(String key) {
		return table.get(key);
	}
	
	/** @return a list of the keys. */
	public List<String> keyList() {
		List<String> keys = new ArrayList<>();
		keys.addAll(table.keySet());
		return keys;
	}

	/** Return a list of the keys that match a glob pattern
	 * @param pattern The glob pattern 
	 * @return the list.
	 */
	public List<String> keyList(String pattern) {
		List<String> keys = new ArrayList<>();
		
		for (String key : table.keySet()) {
			if (StringUtil.matches(pattern, key))
				keys.add(key);
		}

		return keys;
	}

	/** Return a list of the strings whose keys match a glob pattern
	 * @param pattern The glob pattern 
	 * @return the list.
	 */
	public List<String> strings(String pattern) {
		List<String> strings = new ArrayList<>();
		
		for (String key : table.keySet()) {
			if (StringUtil.matches(pattern, key))
				strings.add(table.get(key));
		}

		return strings;
	}

	//-------------------------------------------------------------------------
	// Static API
	
	/** Loads a StringsTable resource from disk.  Because this is intended
	 * for loading resources, it will halt if the resource can't be
	 * loaded successfully.
	 * 
	 * 
	 * @param cls  The class that owns the resource.
	 * @param resource The resource name..
	 * @return The strings table.
	 */
	private final static Map<String,String> load(Class<?> cls, String resource) {
		Map<String,String> table = null;
		
		try {
			table = parse(readFile(cls.getResourceAsStream(resource)));
		} catch (Exception e) {
			System.err.println("Could not read strings table resource: " +
					resource + ": " + e);
			System.exit(1);
		}
		
		return table;
	}
	
	/** Parses a StringsTable resource into a map.
	 * 
	 * @param lines A table resource, already split into lines; see the class 
	 * documentation for the syntax.
	 * @return A map of the keys and strings in the table.
	 * @throws SyntaxException on error.
	 */
	private static Map<String,String> parse(List<String> lines) {
		Map<String,String> map = new HashMap<>();
		
		boolean inString = false;
		String key = null;
		StringBuilder buffer = new StringBuilder();
		
		for (String line : lines) {
			Scanner scanner = new Scanner(line);
			
			// FIRST, if we're in the string either we've got another
			// line or we are at the end of the string.
			if (inString) {
				if (scanner.hasNext("%end")) {
					map.put(key, buffer.toString().trim());
					buffer.delete(0, buffer.length());
					inString = false;
				} else if (scanner.hasNext("%string")) {
					scanner.close();
					throw new InvalidFormatException("no %end for %string " + key);
				} else {
					buffer.append(line);
					buffer.append("\n");
				}
				
				// Go on to the next line.
				continue;
			}
			
			// NEXT, we're waiting for the next string.
			// Ignore anything but %string.
			if (scanner.hasNext("%string")) {
				scanner.next();  // Skip the keyword
				key = scanner.next();
				inString = true;
			}
			
			scanner.close();
		}
		
		if (inString) {
			throw new InvalidFormatException("no %end for %string " + key);
		}
		
		return map;
	}
	
	private static List<String> readFile(InputStream istream) 
	throws IOException {
		List<String> lines = new ArrayList<>();
		BufferedReader inputStream = null;
		
		try {
			inputStream = new BufferedReader(new InputStreamReader(istream));
		
			String line;
			while ((line = inputStream.readLine()) != null) 
				lines.add(line);
		} finally {
			if (inputStream != null)
				inputStream.close();
		}
		return lines;
	}
	
	/** This exception is thrown when a problem is found with the content
	 * of the strings file.  This is a RuntimeException because 
	 * strings tables are usually read at start-up, and any problem is
	 * effectively a problem with the source code.
	 * @author will
	 *
	 */
	@SuppressWarnings("serial")
	public static class InvalidFormatException extends RuntimeException {
		/** Creates a new exception.
		 * 
		 * @param message The error message. 
		 */
		public InvalidFormatException(String message) {
			super(message);
		}
	}
	
	//-------------------------------------------------------------------------
	// Test code
	
	/** Main routine
	 * @param args Unused
	 */
	public static void main(String[] args) {
		Map<String,String> table = StringsTable.load(StringsTable.class,"test.strings");
		
		System.out.println("Table size: " + table.size());
		for (String key : table.keySet()) {
			System.out.println("Key<" + key + "> {\n" + table.get(key) + "}\n");
		}
	}
}
