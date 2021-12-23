/**
 * 
 */
package example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import com.google.gson.Gson;

/** An example of how to read in a Tiled JSON file using Gson.
 * @author will
 *
 */
public class Example {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// FIRST, get the JSON
		URL url = Example.class.getResource("small.json");
		StringBuilder jsonText = new StringBuilder();
		
		try {
			BufferedReader in = new BufferedReader(
				new InputStreamReader(url.openStream()));

			String inputLine;
			while ((inputLine = in.readLine()) != null)
				jsonText.append(inputLine);
			in.close();
		} catch (Exception e) {
			System.err.println("Error reading tile map " + e);
		}
		
		// NEXT, parse the JSON.
		Gson gson = new Gson();
		
		TiledTileMap map = gson.fromJson(jsonText.toString(), TiledTileMap.class);

		map.dump();
	}

}
