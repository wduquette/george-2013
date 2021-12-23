/**
 * 
 */
package rpg;

import graphics.Sprite;

import java.util.ArrayList;
import java.util.List;

import rpg.pc.PlayerCharacter;

import app.Board;

/** This class defines the interface required by shopkeeper mobiles.
 * @author will
 *
 */
abstract public class Shopkeeper extends Mannikin {
	private static final long serialVersionUID = 1L;

	// Constants
	
	/** Maximum number of items in the shopkeeper's stock. */
	public static final int MAX_STOCK = 32;
	
	// Instance variables
	
	private List<Class<? extends Item>> stocks = new ArrayList<>(); 
	private List<Class<? extends Item>> buys = new ArrayList<>(); 
	
	// Creation

	/** Creates a new shopkeeper
	 * @param key The name of the shopkeeper
	 * @param sprite The shopkeeper's sprite image.
	 * @param tableKey The ID of the shopkeeper's strings table.
	 */
	public Shopkeeper(String key, Sprite sprite, String tableKey) {
		super(key, sprite, tableKey);
	}
	
	/** Add a kind of item that the shopkeeper stocks.  Note that
	 * the shopkeeper is willing to buy back whatever he stocks.
	 * 
	 * @param cls The item class.
	 */
	protected final void stocks(Class<? extends Item> cls) {
		stocks.add(cls);
		buys.add(cls);
		
		assert stocks.size() < MAX_STOCK;
	}
	
	/** Add a kind of item that the shopkeeper will buy that he doesn't
	 * normally stock.
	 * TBD: Eventually, the shopkeeper should stock any specific
	 * item that he buys, at least for a little while.
	 * 
	 * @param cls The item class.
	 */
	protected final void buys(Class<? extends Item> cls) {
		buys.add(cls);
	}
	
	// Methods to be overridden by subclasses.
	
	/** @return the name of the Shopkeeper's shop. */
	abstract public String getShopName();
	
	/** @return the shopkeeper's dialog on selling an item, 
	 * for display in the shop. */
	public String getDialogOnSelling() {
		return "Thanks for your business!";
	}

	/** @return the shopkeeper's dialog on buying an item, 
	 * for display in the shop. */
	public String getDialogOnBuying() {
		return "Thanks for your business!";
	}

	/** Determine the price at which this shopkeeper will buy this
	 * item (if they will buy it at all).
	 * TBD: for now, it will be the same markdown for all shopkeepers.
	 * Later, we'll generalize this a bit.
	 * @param item The item in question
	 * @return The price
	 */
	public int buyFor(Item item) {
		if (buys.contains(item.getClass()))
			return item.value() / 2;
		else
			return 0;
	}

	/** Determine the price at which a PC can buy this item from this
	 * shopkeeper.
	 * TBD: for now, it will be the same markup for all shopkeepers.
	 * Later, we'll generalize this a bit.
	 * @param item The item in question
	 * @return The price
	 */
	public int sellFor(Item item) {
		if (stocks.contains(item.getClass()))
			return item.value();
		else
			return 0;
	}
	
	/** @return the items the shopkeeper has in stock. */
	public List<Item> getStock() {
		List<Item> stock = new ArrayList<>();
		
		for (Class<? extends Item> cls : stocks) {
			try {
				stock.add(cls.newInstance());
			} catch (Exception e) {
				Board.get().println("Could not create stock item: " +
						e.getMessage());
			}
		}
		
		return stock;
	}
	
	/** Go shopping when you talk to the shopkeeper. */
	@Override
	public void talk(PlayerCharacter pc) {
		Board.get().goShopping(this);
	}

}
