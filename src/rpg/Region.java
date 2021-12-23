/**
 * 
 */
package rpg;

import graphics.Features;
import graphics.Mobiles;
import graphics.Sprite;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import map.Cell;
import map.MovementAssessor;
import map.TerrainGrid;
import map.TerrainMap;
import map.TerrainTile;
import map.tiled.TiledMap;
import rpg.feature.Door;
import rpg.feature.Exit;
import rpg.feature.Furniture;
import rpg.feature.ItemStack;
import rpg.feature.Narrative;
import rpg.feature.PlainChest;
import rpg.feature.Sign;
import rpg.monster.Monster;
import rpg.pc.PlayerCharacter;
import util.RandomPlus;
import util.StringsTable;
import app.Board;
import app.Strings;

/** A Region consists of a map, features located on it, mobiles that move
 * about it, and player characters that explore it under the guidance of the
 * user.  It may have any number of named "points of interest".
 * @author will
 */
public abstract class Region implements TerrainMap, Serializable {
	private static final long serialVersionUID = 1L;

	// Instance Variables
	protected final RandomPlus random = new RandomPlus();
	private final String name;
	private final int level;
	private TerrainGrid map;
	private Map<Cell,Feature> features = new HashMap<>();
	private List<Mobile> mobiles = new ArrayList<>();
	private List<Monster> monsters = new ArrayList<>();
	private List<Mobile> npcs = new ArrayList<>();
	private Map<String,Cell> points = new HashMap<>();
	private boolean seen[][];
	
	// Construction
	
	/** Initializes the AbstractRegion.  A region has a level;
	 * if 0, then the region is safe and no combat will be
	 * allowed there (combat mode is disabled).  Otherwise, combat mode is 
	 * allowed, and monsters are likely.
	 * @param name The region's unique name.
	 * @param level The region's level.
	 */
	public Region(String name, int level) {
		this.name = name;
		this.level = level;
	}
	
	/** @return the region's unique name. */
	public String name() {
		return name;
	}
	
	/** @return the board(). */
	protected final Board board() {
		return Board.get();
	}
	
	/** @return the region's human-readable title */
	abstract public String title();
		
	/** Gives the region its map.  This is usually done in
	 * the subclass constructor.  Then the "seen" array is
	 * created.
	 * @param map The map
	 */
	protected final void setMap(TerrainGrid map) {
		this.map = map;
		
		seen = new boolean[map.rows()][];
		
		for (int r = 0; r < map.rows(); r++) {
			seen[r] = new boolean[map.cols()];
			
			for (int c = 0; c < map.cols(); c++) {
				seen[r][c] = false;
			}
		}
	}
	
	/** Add a point of interest for the region.
	 * 
	 * @param id The identifier for the point of interest.
	 * @param cell
	 */
	public final void addPointOfInterest(String id, Cell cell) {
		println("addPointOfInterest("+id+","+cell+")");
		points.put(id, cell);
	}
	
	/** Returns the location of a point of interest,
	 * or null if there's no such point.
	 * @param id The ID of the point of interest.
	 * @return The location.
	 */
	public final Cell getPointOfInterest(String id) {
		return points.get(id);
	}

	/** Add a feature to the board, updating the various lists.
	 * @param cell The cell it occupies.
	 * @param f The feature
	 */
	protected final void addFeature(Cell cell, Feature f) {
		f.place(this, cell);
		features.put(cell, f);
	}
	
	/** Remove a feature from the region.
	 * 
	 * @param f The feature to remove.
	 */
	public final void removeFeature(Feature f) {
		features.remove(f.place());
	}

	/** Add a monster to the various lists.
	 * @param cell The cell at which the monster appears
	 * @param monster The monster to add.
	 */
	protected final void addMonster(Cell cell, Monster monster) {
		monster.place(this,cell);
		mobiles.add(monster);
		monsters.add(monster);
	}
	
	/** Add an NPC to the various lists.
	 * @param cell The cell at which the NPC appears.
	 * @param npc The NPC to add.
	 */
	protected final void addNPC(Cell cell, Mobile npc) {
		npc.place(this,cell);
		mobiles.add(npc);
		npcs.add(npc);
	}
	
	/** Remove an NPC from the region. 
	 * @param npc The NPC to remove
	 */
	public final void removeNPC(Mobile npc) {
		mobiles.remove(npc);
		npcs.remove(npc);
	}
	

	/** Player characters enter the region.  We always enter
	 * in normal mode, so all PCs enter at the same point.
	 * 
	 * @param party the entering player characters.
	 * @param point the point of interest at which they appear
	 */
	public void enter(List<PlayerCharacter> party, String point) {
		println("Enter region at (" + point + ")");
		
		// FIRST, get the point of interest.
		Cell cell = getPointOfInterest(point);
		if (cell == null) {
			throw new IllegalArgumentException("Unknown point of interest: " + 
					point);
		}

		// NEXT, position them in the region.
		for (int i = 0; i < party.size(); i++) {
			party.get(i).place(this, cell);
		}

		// NEXT, add them to the region's list of PCs.
		for (PlayerCharacter pc : party) {
			mobiles.add(pc);
		}
	}
	
	/** The party exits the region.
	 * @param toRegion The ID of the region they are exiting to
	 * @param point The point of interest in that region.
	 */
	public void exit(String toRegion, String point) {
		mobiles.removeAll(board().party.allMembers());
		board().enter(toRegion, point);
	}

	/** Removes dead mobiles from the region. 
	 * @return a list of the dead mobiles. */
	public List<Mobile> removeDeadMobiles() {
		List<Mobile> dead = new ArrayList<>();
		
		for (Mobile m : mobiles) {
			if (!m.isAlive())
				dead.add(m);
		}
		
		mobiles.removeAll(dead);
		monsters.removeAll(dead);
		
		return dead;
	}
	
	//-------------------------------------------------------------------------
	// Terrain Map Functions
	
	/** @return the number of rows in the region's map. */
	@Override
	public int rows() {
		return map.rows();
	}

	/** @return the number of columns in the region's map. */
	@Override
	public int cols() {
		return map.cols();
	}

	/** Determines whether the cell has been seen by the party or not.
	 * 
	 * @param cell  The cell
	 * @return true if it has, and false otherwise.
	 */
	public boolean hasBeenSeen(Cell cell) {
		return hasBeenSeen(cell.row, cell.col);
	}
	
	/** Determines whether the cell has been seen by the party or not.
	 * 
	 * @param row The cell's row
	 * @param col The cell's column
	 * @return true if it has, and false otherwise.
	 */
	public boolean hasBeenSeen(int row, int col) {
		return seen[row][col];
	}
	
	/** Marks a cell as having been seen by the party.  To be
	 * seen, it must be line of sight.  Since opaque cells block
	 * sight, mark all cells adjacent to this cell as seen as well;
	 * otherwise the opaque cells will never be visible.
	 * 
	 * @param cell The cell
	 */
	public void markSeen(Cell cell) {
		markSeen(cell, 1);
	}
	
	/** Marks an area around a cell as having been seen by the party.
	 * 
	 * @param cell  The center point
	 * @param radius a radius in cells
	 */
	public void markSeen(Cell cell, int radius) {
		int r0 = Math.max(0, cell.row - radius);
		int r1 = Math.min(map.rows() - 1, cell.row + radius);
		int c0 = Math.max(0, cell.col - radius);
		int c1 = Math.min(map.cols() - 1, cell.col + radius);
	
		for (int r = r0; r <= r1; r++) {
			for (int c = c0; c <= c1; c++) {
				seen[r][c] = true;
			}
		}
	}

	/** Marks the entire region as having been seen by the party.
	 */
	public void markSeen() {
		for (int r = 0; r < map.rows(); r++)
			for (int c = 0; c < map.cols(); c++)
				seen[r][c] = true;
	}
	
	/** Returns the terrain at a given cell in the map.
	 * 
	 * @param cell The cell of interest.
	 * @return The terrain.
	 */
	@Override
	public TerrainTile terrainAt(Cell cell) {
		return map.terrainAt(cell);
	}
	
	/* (non-Javadoc)
	 * @see map.TerrainMap#findAStarRoute(map.Cell, map.Cell, map.MovementAssessor)
	 */
	@Override
	public List<Cell> findAStarRoute(Cell start, Cell goal, MovementAssessor assessor) {
		return map.findAStarRoute(start, goal, assessor);
	}

	/* (non-Javadoc)
	 * @see map.TerrainMap#neighborsOf(map.Cell, map.Cell, map.MovementAssessor)
	 */
	@Override
	public List<Cell> neighborsOf(Cell cell, Cell goal, MovementAssessor assessor) {
		return map.neighborsOf(cell, goal, assessor);
	}

	/* (non-Javadoc)
	 * @see map.TerrainMap#travelDistance(map.Cell, map.Cell, map.MovementAssessor)
	 */
	@Override
	public int travelDistance(Cell start, Cell goal, MovementAssessor assessor) {
		return map.travelDistance(start, goal, assessor);
	}

	/* (non-Javadoc)
	 * @see map.TerrainMap#toBufferedImage()
	 * TBD: Consider drawing the features and mobiles to.
	 */
	@Override
	public BufferedImage toBufferedImage() {
		return map.toBufferedImage();
	}


	//-------------------------------------------------------------------------
	// Queries
	
	/** @return the level of this region. */
	public final int level() {
		return level;
	}

	/** @return true if we're in combat, and false otherwise. */
	public final boolean inCombat() {
		return board().inCombat();
	}
	
	/** Determines whether the cell's terrain is passable to a 
	 * given movement type, disregarding avoidances.
	 * @param type Movement type
	 * @param cell The cell in question
	 * @return True if it is passable, and false otherwise.
	 */
	public final boolean isPassableBy(MovementType type, Cell cell) {
		switch (type) {
		case WALKING:
			return map.terrainAt(cell).isWalkable();
		case FLYING:
			return map.terrainAt(cell).isFlyable();
		default:
			board().println("Error, unknown movement type: " + type);
			return false;
		}
	}
	
	/** A cell is opaque if the terrain is opaque or if
	 * there's an opaque feature.  
	 * @param cell The cell in question
	 * @return True if the cell blocks visibility, and false otherwise.
	 */
	public final boolean isOpaque(Cell cell) {
		if (map.terrainAt(cell).isOpaque())
			return true;
		
		Feature f = features.get(cell);
		
		if (f != null)
			return f.isOpaque();
		else
			return false;
	}
	
	/** Determines whether cell b is in line-of-sight of cell
	 * a, taking opacity of terrain and features into account.
	 * @param a The first cell
	 * @param b The second cell
	 * @return True if a has a line-of-sight to b, and false otherwise.
	 */
	public final boolean inLOS(Cell a, Cell b) {
		List<Cell> path = TerrainGrid.getLine(a, b);
		
		for (int i = 0; i < path.size(); i++) {
			if (isOpaque(path.get(i)))
				return false;
		}
		
		return true;
	}
	
	/** Determines whether the cell is in LOS of some member of the
	 * party.
	 * @param cell
	 * @return true if it is, and false otherwise.
	 */
	public final boolean inLOS(Cell cell) {
		// FIRST, in normal mode we only need to check the leader.
		if (!board().inCombat()) {
			return inLOS(board().party.leader().place(), cell);
		}
		
		// OTHERWISE, we need to check each PC
		for (PlayerCharacter pc : board().party.members()) {
			if (inLOS(pc.place(), cell)) {
				return true;
			}
		}
		
		return false;
	}
	
	/** Determines the minimum diagonal distance from any member of
	 * the party to the given cell.
	 * @param cell The cell in question
	 * @return The distance
	 */
	public final int diagonal(Cell cell) {
		int min = board().party.leader().place().diagonal(cell);
		
		if (!board().inCombat()) {
			return min;
		}
		
		int dist = min;
		
		for (Mobile m : board().party.members()) {
			dist = m.place().diagonal(cell);
			if (dist < min)
				min = dist;
		}
		
		return min;
	}

	/** Returns a list of all of the features in the region.
	 * TBD: Consider defining an immutable interface, so that
	 * we can return that and not make a copy.
	 * @return The list of features.
	 */
	public final List<Feature> getFeatures() {
		return new ArrayList<>(features.values());
	}

	/** Returns a list of all of the mobiles in the region.
	 * TBD: Consider defining an immutable interface, so that
	 * we can return that and not make a copy.
	 * @return The list of player characters.
	 */
	public final List<Mobile> getMobiles() {
		return Collections.unmodifiableList(mobiles);
	}

	/** Returns a list of all of the non-player character
	 * mobiles in the region.
	 * 
	 * @return the list of NPCs.
	 */
	public final List<Monster> getMonsters() {
		return Collections.unmodifiableList(monsters);
	}
		
	/** @return a list of all non-party mobiles, both NPCs and monsters. */
	public final List<Mobile> getNonPartyMobiles() {
		List<Mobile> list = new ArrayList<>();
		
		list.addAll(npcs);
		list.addAll(monsters);
		
		return list;
	}

	/** Returns a list of all of the non-player character
	 * mobiles in the region.
	 * 
	 * @return the list of NPCs.
	 */
	public final List<Mobile> getNPCs() {
		return Collections.unmodifiableList(npcs);
	}

	/** Determines whether there's something to interact with
	 * (i.e., poke or attack) at the given cell.
	 * @param cell The cell of interest.
	 * @return True if there is, and false otherwise.
	 */
	public final boolean canInteractAt(Cell cell) {
		Entity e = entityAt(cell);
		
		if (e != null)
			return e.canInteract();
		else
			return false;
	}

	/** Returns the entity at the given cell, if any.
	 * If the cell contains both a mobile and a feature,
	 * returns the mobile.
	 * @param cell The cell in question
	 * @return The entity, or null if none.
	 */
	public final Entity entityAt(Cell cell) {
		Mobile m = mobileAt(cell);
		
		if (m != null)
			return m;
		else
			return featureAt(cell);
	}

	/** Returns the mobile at the given cell, if any.
	 * 
	 * @param cell The cell in question
	 * @return the mobile, or null if none.
	 */
	public final Mobile mobileAt(Cell cell) {
		for (Mobile m : mobiles) {
			if (m.place().equals(cell)) {
				return m;
			}
		}
		
		return null;
	}
	
	/** Returns the entity at the given cell, if any.
	 * If the cell contains both a mobile and a feature,
	 * returns the mobile.
	 * @param cell The cell in question
	 * @return The entity, or null if none.
	 */
	public final Entity visibleEntityAt(Cell cell) {
		for (Mobile m : mobiles) {
			println("Checking " + m);
			if (m.isVisible() && m.place().equals(cell)) {
				return m;
			}
		}
		
		Feature f = featureAt(cell);
		
		if (f != null && f.isVisible()) {
			return f;
		}
		
		return null;
	}



	/** Returns the monster at the given cell, if any.
	 * 
	 * @param cell The cell in question
	 * @return the monster, or null if none.
	 */
	public final Monster monsterAt(Cell cell) {
		for (Monster m : monsters) {
			if (m.place().equals(cell)) {
				return m;
			}
		}
		
		return null;
	}

	/** Returns the feature at the given cell, if any.
	 * 
	 * @param cell The cell in question
	 * @return the feature, or null if none.
	 */
	public final Feature featureAt(Cell cell) {
		return features.get(cell);
	}

	/** @return a list of cells with walkable terrain
	 * that contain no entities.
	 */
	public final List<Cell> getOpenCells() {
		// FIRST, get the cells with walkable terrain.
		List<Cell> open = new ArrayList<>(0);
		
		for (int r = 0; r < map.rows(); r++) {
			for (int c = 0; c < map.cols(); c++) {
				Cell cell = new Cell(r,c);
				if (isPassableBy(MovementType.WALKING, cell)) {
					open.add(cell);
				}
			}
		}	
		
		// NEXT, remove any cells containing features or mobiles.
		open.removeAll(features.keySet());

		for (Mobile m : mobiles)
			open.remove(m.place());
		
		return open;
	}
	
	/** Find a list of open cells of with a given terrain tile.
	 * 
	 * @param tile The terrain tile
	 * @return a list of cells with the given tile that contain no entities.
	 */
	public final List<Cell> getOpenCellsWith(TerrainTile tile) {
		// FIRST, get the cells with walkable terrain.
		List<Cell> open = new ArrayList<>(0);
		
		for (int r = 0; r < map.rows(); r++) {
			for (int c = 0; c < map.cols(); c++) {
				Cell cell = new Cell(r,c);
				if (map.terrainAt(cell).equals(tile))
					open.add(cell);
			}
		}	
		
		// NEXT, remove any cells containing features or mobiles.
		open.removeAll(features.keySet());

		for (Mobile m : mobiles)
			open.remove(m.place());
		
		return open;
	}
	
	/** Attempts to drop an item at or near the given cell.
	 * The region will create a new ItemStack if necessary.
	 * TBD: Should check the indicated cell, first, and not go searching
	 * if it doesn't have to.
	 * @param cell The cell
	 * @param item The item
	 * @return True if the item could be dropped, and false otherwise.
	 */
	public final boolean dropItem(Cell cell, Item item) {
		// FIRST, get the valid places to drop items near cell.
		List<Cell> open = new ArrayList<>();
		
		// NEXT, all cells with travel distance <= dist will be within the
		// square of radius dist.  Get the bounds of that square.
		// TBD: Package this rect thing up.
		int dist = 3;
		int r0 = Math.max(0, cell.row - dist);
		int r1 = Math.min(map.rows() - 1, cell.row + dist);
		int c0 = Math.max(0, cell.col - dist);
		int c1 = Math.min(map.cols() - 1, cell.col + dist);
	
		// NEXT, find the ones that have a travelDist <= dist.
		DropAssessor da = new DropAssessor();
		
		for (int r = r0; r <= r1; r++) {
			for (int c = c0; c <= c1; c++) {
				Cell site = new Cell(r,c);
				
				if (!da.isPassable(site))
					continue;

				// NEXT, see if the travel distance is near enough.
				if (map.travelDistance(cell, site, da) <= dist)
					open.add(site);
			}
		}			
		
		// NEXT, sort by distance.
		cell.sortByDiagonal(open);
		
		// NEXT, find the first one that's either empty or has an 
		// ItemStack with an open slot.
		while (open.size() > 0) {
			Cell candidate = open.remove(0);
			Feature f = featureAt(candidate);
			ItemStack stack;
			
			if (f == null) {
				addFeature(candidate, stack = new ItemStack());
				stack.putItem(item);
				return true;
			} else if (f instanceof ItemStack) {
				stack = (ItemStack)f;
				
				if (!stack.isFull()) {
					stack.putItem(item);
					
					return true;
				}
			}
		}
		
		return false;
	}

	/** This class is used by dropItem() to find open spots
	 * one could in principle drop items at.  Which is to say,
	 * walkable cells where an ItemStack could be placed, or cells
	 * where there's already an ItemStack.
	 * @author will
	 */
	private final class DropAssessor implements MovementAssessor {
		public boolean isPassable(Cell cell) {
			Feature f = featureAt(cell);
			
			if (f != null)
				return (f instanceof ItemStack);
			else
				return isPassableBy(MovementType.WALKING, cell);
		}
	}
	
	

	
	/** Returns a list of open cells passable by the given movement type
	 * near a given cell. There will be a path to each cell.
	 * @param start The cell to find cells near
	 * @param mtype The movement type
	 * @param dist The maximum travel distance to the found cells.
	 * @return A list of the found cells.
	 */
	public final List<Cell> getOpenCellsNear(Cell start, MovementType mtype, int dist) {
		List<Cell> result = new ArrayList<>();
		
		// FIRST, all cells with travel distance <= dist will be within the
		// square of radius dist.  Get the bounds of that square.
		int r0 = Math.max(0, start.row - dist);
		int r1 = Math.min(map.rows() - 1, start.row + dist);
		int c0 = Math.max(0, start.col - dist);
		int c1 = Math.min(map.cols() - 1, start.col + dist);
	
		// NEXT, find the ones that have a travelDist <= dist.
		OpenAssessor ma = new OpenAssessor(mtype);
		
		for (int r = r0; r <= r1; r++) {
			for (int c = c0; c <= c1; c++) {
				Cell cell = new Cell(r,c);
				
				// FIRST, Skip the impassable ones; travelDistance
				// finds distances to goals, which might not be passable
				// themselves.  But here, the cell has to be passable
				// itself or there's no point.
				if (!ma.isPassable(cell))
					continue;

				// NEXT, see if the travel distance is near enough.
				if (map.travelDistance(start, cell, ma) <= dist)
					result.add(cell);
			}
		}			
				
		return result;
	}
	
	/** This class is used by getOpenCellsNear() to find open spots
	 * one could in principle put new mobiles or features at.
	 * TBD: Consider putting this somewhere else and making it
	 * public.
	 * @author will
	 */
	private final class OpenAssessor implements MovementAssessor {
		private MovementType mtype;
		
		public OpenAssessor(MovementType mtype) {
			this.mtype = mtype;
		}
		
		public boolean isPassable(Cell cell) {
			if (mobileAt(cell) != null)
				return false;
			
			Feature f = featureAt(cell);
			
			if (f != null)
				return f.isPassableBy(mtype);
			
			return isPassableBy(mtype, cell);
		}
	}
	
	//------------------------------------------------------------------------
	// TiledMap-based Region Initialization
	
	// Names of the TiledMap layers.
	// TBD: Possibly, these should be in TiledMap.
	
	private static String TERRAIN_LAYER = "Terrain";
	private static String FEATURES_LAYER = "Features";
	private static String MOBILES_LAYER = "Mobiles";
	
	
	/** Initializes the region's map, features, and so forth, given a
	 * TiledMap object and an array of terrain tiles.  Some things are
	 * left to the region.  See tiled_maps.md for details of how to set
	 * up a TiledMap and what things the region will handle automatically.
	 * 
	 * @param tm The tiled map, a tile map created using the Tiled map editor.
	 * @param tiles The terrain tile constants for this region.
	 * @param tableKey The ID of the region's strings table in the Strings
	 * registry.
	 */
	public final void setupRegion(TiledMap tm, TerrainTile[] tiles, String tableKey) {
		// FIRST, create and populate the terrain grid.
		setupTerrainGrid(tm, tiles);
		
		// NEXT, get features defined as tiles.
		setupTileFeatures(tm, tiles);
		
		// NEXT, get features defined as objects in the Features object
		// group.
		setupObjectFeatures(tm, tableKey);
		
		// NEXT, get mobiles defines as objects in the Mobiles object
		// group.
		setupMobiles(tm, tableKey);
	}

	/** Create the region's map from the TiledMap we read in.
	 * @param tm The TiledMap read from disk.
	 * @param tiles The array of terrain tiles used by this region.
	 */
	private void setupTerrainGrid(TiledMap tm, TerrainTile[] tiles) {
		TerrainGrid grid = new TerrainGrid(tm.height, tm.width);
		
		// FIRST, find the tm's Terrain Layer
		TiledMap.Layer terrain = tm.getTileLayer(TERRAIN_LAYER);
		assert terrain != null;
		
		// NEXT, look up the terrain objects that match the tile GIDs.
		for (int r = 0; r < tm.height; r++) {
			for (int c = 0; c < tm.width; c++) {
				int ndx = r*tm.width + c;
				int tile = terrain.data[ndx];
				
				if (tile > 0) {
					// Our tiles start at 0; Tiled's GIDs start at 1.
					grid.set(r, c, tiles[tile-1]);
				}
			}
		}
		
		setMap(grid);
	}
	
	/** Get features defined on the Features tile layer.  These features
	 * are defined as feature tiles; they create default features, e.g.,
	 * doors, chests, and furniture.<p>
	 *
	 * @param tm  The tile map
	 * @param tiles The terrain tiles used in this region.
	 */
	private void setupTileFeatures(TiledMap tm, TerrainTile[] tiles) {
		// FIRST, retrieve the layer.
		TiledMap.Layer features = tm.getTileLayer(FEATURES_LAYER);
		assert features != null;
		
		// NEXT, iterate over the defined features.
		for (int r = 0; r < tm.height; r++) {
			for (int c = 0; c < tm.width; c++) {
				Cell cell = new Cell(r,c);
				int ndx = r*tm.width + c;
				int tile = features.data[ndx];
				
				if (tile == 0) {
					continue;
				}
				
				// Our tiles start at 0; Tiled's GIDs start at 1.

				TerrainTile f = tiles[tile - 1];
				switch (f.getName()) {
				case "Chest": // Empty Closed Chest
					setupChest(tiles, cell, f, null);
					break;
				case "Open Chest": // Empty Open Chest
					setupChest(tiles, cell, null, f);
					break;
				case "Door":
					setupDoor(tiles, cell, f, null);
					break;
				case "Open Door":
					setupDoor(tiles, cell, null, f);
					break;
				default:
					addFeature(cell, new Furniture(f.getName(), f));
					break;
				}
			}
		}
	}
	
	/** Sets up a default chest feature, based on its name.  It can be
	 * open or closed, depend on the tile used; the tile used for the other
	 * state will be found in the set of terrain tiles, also by name, or
	 * the standard chest sprite will be used.
	 * @param tiles The array of terrain tiles.
	 * @param cell The cell for this chest.
	 * @param closed The sprite for when it is closed, or null.
	 * @param open The sprite for when it is open, or null.
	 */
	private void setupChest(TerrainTile[] tiles, Cell cell, Sprite closed, Sprite open) {
		// FIRST, if the closed sprite is null, find it.
		if (closed == null) {
			closed = findTile(tiles, "Chest", Features.CHEST);
		}
		
		if (open == null) {
			open = findTile(tiles, "Open Chest", Features.OPEN_CHEST);
		}
		
		PlainChest chest = new PlainChest();
		chest.setSprites(closed, open);
		addFeature(cell, chest);
	}

	/** Sets up a default door feature, based on its name.  It can be
	 * open or closed, depend on the tile used; the tile used for the other
	 * state will be found in the set of terrain tiles, also by name, or
	 * the standard door sprite will be used.
	 * @param tiles The array of terrain tiles.
	 * @param cell The cell for this feature.
	 * @param closed The sprite for when it is closed, or null.
	 * @param open The sprite for when it is open, or null.
	 */
	private void setupDoor(TerrainTile[] tiles, Cell cell, Sprite closed, Sprite open) {
		// FIRST, if the closed sprite is null, find it.
		if (closed == null) {
			closed = findTile(tiles, "Door", Features.CLOSED_DOOR);
		}
		
		if (open == null) {
			open = findTile(tiles, "Open Door", Features.OPEN_DOOR);
		}
		
		Door door = new Door(open, closed);
		addFeature(cell, door);
	}
	

	/** Find a tile by name in the set of terrain tiles.
	 * 
	 * @param tiles  The array of terrain tiles
	 * @param name The name to look for.
	 * @param defSprite The sprite to use if the name is not found.
	 * @return Returns either the terrain tile sprite or the default sprite.
	 */
	private Sprite findTile(TerrainTile[] tiles, String name, Sprite defSprite) {
		for (TerrainTile t : tiles) {
			if (t.getName().equals(name)) {
				return t;
			}
		}
		
		return defSprite;
	}
	
	/** Retrieves features defined as objects in the Tiled map's
	 * Features object group.  Unrecognized features are passed back
	 * to the subclass.
	 * @param tm The Tiled map.
	 * @param tableKey The ID of the region's StringsTable in the registry
	 */
	private void setupObjectFeatures(TiledMap tm, String tableKey) {
		// FIRST, retrieve the layer and the strings table.
		TiledMap.Layer flayer = tm.getObjectGroup(FEATURES_LAYER);
		StringsTable strings = Strings.table(tableKey);
		
		if (flayer == null) {
			return;
		}
		
		String point[];
		
		for (TiledMap.MapObject o : flayer.objects) {
			Cell cell = tm.getObjectCell(o);
			Feature f = features.get(cell);
			
			
			// FIRST, if the object has a point property defined, define
			// the point of interest.
			if (o.properties.point != null) {
				addPointOfInterest(o.properties.point, cell);
			}
			
			switch (o.type) {
			case "Exit":
				// FIRST, get the link information.
				point = o.name.split(":");
				assert point.length == 2;
				
				if (o.properties.point == null) {
					addPointOfInterest(point[0], cell);
				}
				
				// NEXT, create exit given the data we have.  If there's
				// already a feature here, copy its name and sprite; otherwise
				// the exit will be invisible and the terrain will show through.
				Exit e;
				
				if (f != null) {
					e = new Exit(f.name(), f.sprite(), point[0], point[1]);
				} else {
					e = new Exit(point[0], point[1]);
				}

				addFeature(cell, e);
				break;
			case "Narrative":
				addFeature(cell, new Narrative(name() + "." + o.name, 
						                       strings.get(o.name)));
				break;
			case "Point":
				addPointOfInterest(o.name, cell);
				break;
			case "Sign":
				addFeature(cell, new Sign(strings.get(o.name)));
				break;
			default:
				setupObjectFeature(cell, o);
				break;
			}
		}
	}
	
	/** Subclasses should override this to handle object features the
	 * region itself is not prepared to handle.
	 * @param cell The cell at which the feature appears
	 * @param o The map object itself.
	 */
	protected void setupObjectFeature(Cell cell, TiledMap.MapObject o) {
		println("Unsupported object feature at cell "
				+ cell + ": " + o.type + ":" + o.name);
	}

	/** Retrieves mobiles defined as objects in the Tiled map's
	 * Mobiles object group.  Unrecognized Mobiles are passed back
	 * to the subclass.
	 * @param tm The Tiled map.
	 * @param tableKey The ID of the region's StringsTable.
	 */
	private void setupMobiles(TiledMap tm, String tableKey) {
		// FIRST, retrieve the layer.
		TiledMap.Layer mobiles = tm.getObjectGroup(MOBILES_LAYER);
		if (mobiles == null) {
			return;
		}
		
		Mannikin man;
		
		for (TiledMap.MapObject o : mobiles.objects) {
			Cell cell = tm.getObjectCell(o);
			
			// FIRST, if the object has a point property defined, define
			// the point of interest.
			if (o.properties.point != null) {
				addPointOfInterest(o.properties.point, cell);
			}
			
			switch (o.type) {
			case "Mannikin":
				Sprite s;
				
				if (o.properties.sprite != null) {
					s = Mobiles.valueOf(o.properties.sprite.toUpperCase());
				} else {
					s = Mobiles.PEASANT1;
				}
				
				// NOTE: The "name" from the TiledMap is the mannikin's
				// root key into its strings table.
				man = new Mannikin(o.name, s, tableKey);
				addNPC(cell, man);
				break;
			default:
				setupMobile(cell, o);
				break;
			}
		}
	}
	
	/** Subclasses should override this to handle mobiles the
	 * region itself is not prepared to handle.
	 * @param cell The cell at which the mobile appears
	 * @param o The map object itself.
	 */
	protected void setupMobile(Cell cell, TiledMap.MapObject o) {
		println("Unsupported mobile at cell "
				+ cell + ": " + o.type + ":" + o.name);
	}

	//------------------------------------------------------------------------
	// Utilities
	
	/** The region writes a debugging message.
	 * 
	 * @param message The message.
	 */
	protected final void println(String message) {
		board().println(message);
	}
	
	/** The region writes a message to the user.
	 * @param message The message.
	 */
	protected final void log(String message) {
		board().log(message);
	}
}
