/**
 * 
 */
package app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.Board.SaveSet;

/** Metadata for the save files in the save slots.
 * @author will
 */

class SaveMetadata implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final int MIN = 0;
	public static final int MAX = 7;

	private String dirName = System.getProperty("user.home") + "/.george";
	private String metaName = dirName + "/saveslots.dat";
			
	private List<String> slotList = new ArrayList<>(6);
	
	/** Initialize the slot list. */
	public SaveMetadata() {
		for (int i = MIN; i <= MAX; i++) {
			slotList.add(null);
		}
	}
	
	/** Returns a file object for the given slot.
	 * 
	 * @param slot The slot number
	 * @return A File for the slot's save file.
	 */
	public File slotFile(int slot) {
		String fileName = dirName + "/save" + slot + ".dat";
		return new File(fileName);
	}
	
	/** Set the metadata string associated with the slot.
	 * 
	 * @param slot The slot number
	 * @param set The save set
	 */
	public void set(int slot, SaveSet set) {
		Format formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
		String dateText = formatter.format(new Date());
		String text = dateText + ", " + set.region.title() +
				" (" + Board.get().time() + " steps)";
		
		slotList.set(slot,text);
	}
	
	/** Get the metadata string associated with the slot.
	 * 
	 * @param slot The slot number.
	 * @return The string, or null
	 */
	public String getSlotText(int slot) {
		String text = slotList.get(slot);
		
		return (text != null) ? text : "Empty Slot --------------------------";
	}
	
	/** Is there file data in the slot?
	 * 
	 * @param slot The slot number
	 * @return True if so, false otherwise.
	 */
	public boolean hasData(int slot) {
		return slotList.get(slot) != null;
	}

	/** Serialize the current meta data to disk */
	public void save() {
		try {
			// FIRST, make sure the save directory exists.
			File dir = new File(dirName);
			dir.mkdir();

			// NEXT, save the file in the directory
			File saveFile = new File(metaName);
			ObjectOutput out = 
					new ObjectOutputStream(new FileOutputStream(saveFile));
			out.writeObject(this);
			out.close();
		} catch (Exception e) {
			Board.get().log("Error, could not save metadata: " + e);
			return;
		}
	}
	
	/** Load the current metadata from disk, coincidentally creating
	 * the player's save directory.
	 * If there's a slot for which we have data but no save file,
	 * make it null.
	 */
	public void load() {
		// FIRST, make sure that the save directory exists.
		File dir = new File(dirName);
		dir.mkdir();

		File f = new File(metaName);
		SaveMetadata meta = null;
		
		if (f.exists()) {
			try {
				ObjectInputStream os = new ObjectInputStream(new FileInputStream(f));
				meta = (SaveMetadata)os.readObject();
				os.close();
			} catch (Exception e) {
				Board.get().println("Error loading saved game metadata: " 
						+ e.getMessage());
				return;
			}
		}
		
		if (meta == null) {
			meta = new SaveMetadata();
		}

		// FIRST, Keep the loaded meta data.
		slotList = meta.slotList;
			
		// NEXT, make sure the occupied slots have matching files.
		// If not, clear the slot data for that slot.
		for (int i = MIN; i <= MAX; i++) {
			if (slotList.get(i) != null) {
				File sf = slotFile(i);
				if (!sf.exists()) {
					slotList.set(i,null);
				}
			}
		}
	}
}
