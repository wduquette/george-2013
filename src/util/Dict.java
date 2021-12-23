/**
 * 
 */
package util;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** A Dict is simply a HashMap<String,String>, packaged for convenience.
 * @author will
 *
 */
public class Dict implements Map<String, String>, Serializable {
	private static final long serialVersionUID = 1L;

	private HashMap<String,String> map = new HashMap<>();

	/**
	 * @param o
	 * @return
	 * @see java.util.AbstractMap#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		return this.map.equals(o);
	}

	/**
	 * @return
	 * @see java.util.HashMap#size()
	 */
	public int size() {
		return this.map.size();
	}

	/**
	 * @return
	 * @see java.util.HashMap#isEmpty()
	 */
	public boolean isEmpty() {
		return this.map.isEmpty();
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.HashMap#get(java.lang.Object)
	 */
	public String get(Object key) {
		return this.map.get(key);
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.HashMap#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		return this.map.containsKey(key);
	}

	/**
	 * @return
	 * @see java.util.AbstractMap#hashCode()
	 */
	public int hashCode() {
		return this.map.hashCode();
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	public String put(String key, String value) {
		return this.map.put(key, value);
	}

	/**
	 * @return
	 * @see java.util.AbstractMap#toString()
	 */
	public String toString() {
		return this.map.toString();
	}

	/**
	 * @param m
	 * @see java.util.HashMap#putAll(java.util.Map)
	 */
	public void putAll(Map<? extends String, ? extends String> m) {
		this.map.putAll(m);
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.HashMap#remove(java.lang.Object)
	 */
	public String remove(Object key) {
		return this.map.remove(key);
	}

	/**
	 * 
	 * @see java.util.HashMap#clear()
	 */
	public void clear() {
		this.map.clear();
	}

	/**
	 * @param value
	 * @return
	 * @see java.util.HashMap#containsValue(java.lang.Object)
	 */
	public boolean containsValue(Object value) {
		return this.map.containsValue(value);
	}

	/**
	 * @return
	 * @see java.util.HashMap#clone()
	 */
	public Object clone() {
		return this.map.clone();
	}

	/**
	 * @return
	 * @see java.util.HashMap#keySet()
	 */
	public Set<String> keySet() {
		return this.map.keySet();
	}

	/**
	 * @return
	 * @see java.util.HashMap#values()
	 */
	public Collection<String> values() {
		return this.map.values();
	}

	/**
	 * @return
	 * @see java.util.HashMap#entrySet()
	 */
	public Set<java.util.Map.Entry<String, String>> entrySet() {
		return this.map.entrySet();
	}
	

}
