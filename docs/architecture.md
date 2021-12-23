# Architectural Notes #

This file contains notes on various aspects of the architecture of the RPG,
as they seem called for.

## Primary Classes ##

The main game entity is the Board.  It manages all interactions with the
user, and contains the GUI, including the inventory display and the
map.  It manages the party of player characters, and owns all of the regions.

The Board manages a dictionary of Regions, which represent "levels" in the
classic sense.

Regions contain a map of TerrainTiles, represented as a TerrainGrid.
Each Cell on the Map may contain at most one Feature and one Mobile.

Features are things that appear on the map and do not move, but which
can be interacted with: chests, doors, stairs, traps, etc.

Mobiles are things that appear on the map and can move: player characters,
monsters, and so forth.

Each mobile has a MovementType, e.g., WALKING or FLYING.  Terrain and Features
can blocks or enable movement of different types.

PlayerCharacters are a special kind of Mobile; the set of active 
player characters is called the "party".  Player Characters have
an inventory in which they can store Items.  Some items, such
as weapons and armor, can be equipped and will contribute to 
attack and defense.

## Regions and the Region Network ##

Each region is defined by a class; e.g., the over-world region is defined
by the World class.  Creating an instance creates the region's map and 
populates it with Mobiles and Features.  Regions can be predefined,
using PyxelEdit tile maps, in which case you'd probably only ever have
one instance of the region; or algorithmically generated, in which case
you might have many.  

Each region has a unique string that identifies it.  For pre-defined 
singleton regions, this is the class's static ID variable, e.g., 
World.ID.

Further, each region's map can have one more "points of interest",
each with its own name.  When entering another region, you specify
the region's ID and the name of a point of interest.

The party enters a region by calling the Board's enter() method;
or, more typically, by calling the current region's exit() method,
which causes it to enter the new region.

## User Interactions ##

A Mobile can interact with the features and mobiles on the board.

*   A mobile can attack another mobile.
*   A player character mobile can talk to a non-enemy mobile.
*   A player character can open doors and chests, walk up or down stairs,
    pick up items, and so forth.
    
If a cell contains both a Mobile and a Feature, then other Mobiles cannot
interact with the Feature, though they can interact with the Mobile.

In general, Mobile A will interact with Mobile B by fighting with it if it's
an enemy and by trying to talk with it otherwise.

Mobile A will usually interact with a feature by "poking" it, at which point
it will do whatever it does.  Doors will open, Chests will open, and so forth.

When Mobile A walks on a Feature, something may happen: the Mobile might get
moved somewhere else, or blown up, or what have you. 
