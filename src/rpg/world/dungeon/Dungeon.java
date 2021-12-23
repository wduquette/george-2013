/**
 * 
 */
package rpg.world.dungeon;

import graphics.StandardTile;

import java.util.ArrayList;
import java.util.List;

import map.Cell;
import map.TerrainGrid;
import map.maze.ClassicMaze;
import rpg.Gold;
import rpg.Region;
import rpg.feature.Door;
import rpg.feature.ItemStack;
import rpg.feature.Exit;
import rpg.feature.StairsUp;
import rpg.item.ScrollOfMagicMapping;
import rpg.item.Skull;
import rpg.item.VialOfHealing;
import rpg.monster.Goblin;
import rpg.monster.LadyBug;
import rpg.monster.ManlyBug;
import rpg.monster.Monster;
import rpg.monster.Rat;
import rpg.monster.Roach;
import rpg.world.World;

/** This region models a random dungeon level.<p>
 * 
 * TBD: We're going to need a model of region networks that supports having 
 * multiple instances of a particular Region subclass.
 * @author will
 *
 */
public final class Dungeon extends Region {
	private static final long serialVersionUID = 1L;

	/** This region's ID */
	public static final String ID = "dungeon";
	
	// Points of Interest
	/** The stairs up. */
	public static final String ORIGIN = "origin";
			
	// Other Constants
	/** Maze to Map scale */
	public static final int SCALE = 8;
	
	// Constructor
	
	/** Creates the region.	 
	 */
	public Dungeon() {
		super(ID, 1);
		
		// FIRST, create the map.
		ClassicMaze cm = getMazeA(20,20);
		
		// NEXT, get a list of the rooms in the map.
		List<Cell> candidates = cm.rooms();
		
		// NEXT, choose some rooms to expand.
		List<Cell> rooms = new ArrayList<>();
		
		int numRooms = random.roll(40,60);

		for (int i = 0; i < numRooms; i++) {
			rooms.add(random.takeFrom(candidates));
		}

		// NEXT, create the map
		TerrainGrid map = cm.toGrid(StandardTile.STONE_WALL, 
									StandardTile.GRAVEL, SCALE, 1);

		// NEXT, add the rooms.
		for (Cell cell : rooms) {
			// FIRST, convert the maze coordinate to a grid coordinate.
			int r = cell.row + SCALE*(cell.row + 1);
			int c = cell.col + SCALE*(cell.col + 1);
			
			int r0 = random.roll(r - 5, r - 2);
			int r1 = random.roll(r + 2, r + 5);
			int c0 = random.roll(c - 5, c - 2);
			int c1 = random.roll(c + 2, c + 5);
			
			addRoom(map, r0, c0, r1, c1);
		}
		
		// NEXT, give the map to the region.
		setMap(map);

		// NEXT, get a new list of the open cells, so we can position some
		// things.
		List<Cell> openCells = getOpenCells();
				
		// NEXT, add the stairs out.
		Cell exit = random.takeFrom(openCells);
		
		addPointOfInterest(ORIGIN, exit);
		Exit re = new StairsUp(World.ID, World.TEST);
		re.setConfirm(true);
		addFeature(exit, re);
		
		// NEXT, as a test, add an ItemStack containing a health potion
		// near the stairs.
		
		for (Cell cell : openCells) {
			if (cell.diagonal(exit) < 3) {
				ItemStack stack = new ItemStack();
				stack.putItem(new VialOfHealing());
				stack.putItem(new ScrollOfMagicMapping());
				stack.putItem(new Gold(15));
				stack.putItem(new Skull());
				addFeature(cell, stack);
				break;
			}
		}
		
		// NEXT, add other features
		
		// NEXT, add monsters
		List<Class<? extends Monster>> mClasses = new ArrayList<>();
		mClasses.add(Rat.class);
		mClasses.add(LadyBug.class);
		mClasses.add(ManlyBug.class);
		mClasses.add(Goblin.class);
		mClasses.add(Roach.class);
		
		int numMonsters = random.roll(30,60);
		
		for (int i = 0; i < numMonsters; i++) {
			try {
				Monster m = random.pickFrom(mClasses).newInstance();
				addMonster(random.takeFrom(openCells), m);
			} catch (Exception e) {
				System.err.println("Error creating monster: " + e);
			}
		}
	}
	
	@Override public String title() { return "Dungeon"; }


	private void addRoom(TerrainGrid map, int r0, int c0, int r1, int c1) {
		for (int r = r0; r <= r1; r++) {
			for (int c = c0; c <= c1; c++) {
				if (r == r0 || r == r1 || c == c0 || c == c1) {
					if (map.get(r,c).equals(StandardTile.GRAVEL)) {
						map.set(r,c, StandardTile.TILE_FLOOR);
						
						if (random.roll(1,100) < 60) {
							addFeature(new Cell(r,c), new Door());
						}
					} else {
						map.set(r,c, StandardTile.BLOCK_WALL);
					}
					
					continue;
				}
				
				map.set(r,c, StandardTile.TILE_FLOOR);
			}
		}
	}


	private ClassicMaze getMazeA(int rows, int cols) {
		return new ClassicMaze(rows,cols)
			.recursiveBacktracker(true)
			.connectify(10)
			.sparsify(5,50)
			.connectify(50);
	}

	//-------------------------------------------------------------------------
	// Level Inner Classes
	

	
}
