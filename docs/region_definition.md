# Region Maps and the Tiled Map Editor #

Tiled is a tile map editor available from www.mapeditor.org.  It appears
to be the standard tile map editor at the present time.  It has a great many 
features, and allows tile maps
to be annotated with a wide variety of metadata.

This document explains how Tiled's features are used to define Region maps
for George's Saga.

## Quick Reference ##

The Region class does most of the work of translating a Tiled tile map.  This
section is a quick reference to the conventions used.

* Placing a Sign

  Features object, type="Sign", name="<strings table name>".
  TBD: If there's a Feature tile, use its appearance.
  
* Placing a Narrative

  Features object, type="Narrative", name="<strings table name>".
  The narrative will pop up the first time the player steps on the
  tile, or any other that uses the same name in this region.
  
* Placing an invisible Exit

  Features object, type="Exit", name="<region>:<point>".  The terrain
  tile is all that will appear, but the player will go to the given 
  region and point.  Unless the object has an explicit "point" property,
  the "<region>" will be used to name the exit as a point of interest.
  
* Placing a visible Exit (e.g., a ladder down)

  Features tile with exit sprite (e.g., ladder down or tunnel entrance), 
  plus Features object, type="Exit", name="<region>:<point>".  The Exit
  object will use the tile's name and sprite.  Unless the object has an 
  explicit "point" property,
  the "<region>" will be used to name the exit as a point of interest.
  
* Placing a closed Door

  Features tile, name="Door".  Uses the sprites for the region's 
  "Door" and "Open Door" tiles.
  
* Placing an open Door

  Features tile, name="Open Door".  Uses the sprites for the region's
  "Door" and "Open Door" tiles.
  
* Placing a closed empty PlainChest

  Features tile, name="Chest".  Uses the sprites for the region's
  "Chest" and "Open Chest" tiles.
  
* Placing an open empty PlainChest

  Features tile, name="Open Chest".  Uses the sprites for the region's
  "Chest" and "Open Chest" tiles.
  
* Defining a point of interest

  Add a Features object, type="Point", name="<point>".
   
  Add a Features object with property "point"="<point>".
  
  Add an Exit with no "point" property; its "<region>" will be used
  as a point of interest name.

* Placing a Mannikin with a fixed greeting

  Add a Mobiles object, type="Mannikin", name="<name>" with property
  "sprite=<spriteConstant>".  The displayed sprite is Mobiles.<spriteConstant>.
  The displayed name and greeting are "<name>.name" "<name>.greeting" 
  from the strings table.  If there are multiple strings matching
  "<name>.greeting*", the mannikin will cycle through them randomly,
  over and over.


## File Format ##

Tiled produces .tmx files; it can also export tile maps in a number of
formats, including JSON.  A Java library exists to read .tmx files directly;
however, I intend to export to JSON and use Gson to read the .json files.
JSON is a human-readable text format, which means that if Tiled should be
abandoned, the map data is still in a form that I can manipulate.

Use map.tiled.TiledMap.read() to read a Tiled .json file.

## Tile Sets ##

The available tiles are defined by tile sets loaded into a Tiled map document.  
At present no metadata is required for the individual tiles; instead, the 
region will define an Enum corresponding to the tile set, as is done with the 
older PyxelEdit maps, and relate the meta data to the enum constants.  
This makes saving region maps easier, as the terrain tiles are serialized as 
enums.

Alternatively, the tile set could be a static array in the region class, and
the tile grid could simply use integers to identify tiles.  But that's a 
significant change, and has implications for saving the file.

We will continue to use PyxelEdit to edit the tile sets, at least for the
time being, though we might move to Pixen or some other tool as time goes on.

## Use of Layers ##

The Tiled map editor allows data to be defined in layers.  There are two kinds
of layers.  Tile layers contain tiles arranged on a grid.  Object groups 
contain arbitrary polygons with associated metadata.  Our regions will use
the following layers.

### "Terrain" Tile Layer ###

The terrain map is implemented as a tile layer called "Terrain".  Every
cell in the map should have a tile in this layer, corresponding to one
of the tiles in the region's MyTiles enumeration.  

*Note* that tiles in the TiledMap are indexed 1 to N; 0 is the absence of 
a tile.  TerrainTiles in a MyTile enumeration are index 0 to N-1.  So we have
to handle the offset properly.

### "Features" Tile Layer ###

Features can be placed on the map as tiles using a tile layer called 
"Features".  By default, all such tiles will create Furniture objects:
impassable features that are only present for looks.  (Furniture features
are used in place of wall tiles because the feature tile usually has 
transparent areas where the floor shows through.)

However, the region is free to capture particular feature tiles and do
something special with them.  Door tiles, for example, might create
standard Door objects, and Chests can create empty chests.

However, this requires that the relevant tiles are included in the 
region's tile set.  This is not always desirable.

### "Features" Object Group ###

More specific features, i.e., the chest containing the prize, traps, and
the like, can be defined as "objects" on an object layer called
"Features".  A Tiled object is a polygon on 
An object layer.  If an object covers only one tile, it appears in the
map editor as a small square around the upper left corner of the tile;
this is how features should be defined.

Objects can have attached metadata.  At present, each feature on the layer
should at least have a "name" associated with it; this will be used by
the region to place the actual object.  They will usually have a "type"
as well, which will sometimes be used.

Note that there can only be one feature at a given spot; if there is a
feature tile and a feature object, the feature object wins.

The follow types are handled specially:

* "Sign": the name should be an entry in the region's strings file.

TBD: Eventually, standard "Exit" objects should have the metadata required
to link them to other regions.  However, we need a "create region on demand"
infrastructure for that.

### "Mobiles" Tile Layer ###

Like Features, Mobiles can be placed on the map as tiles using a tile layer 
called "Mobiles".  This should be used sparingly, as it requires that the 
relevant tiles be included in the region-specific tile set.

NOTE: At present, we aren't use thing.

### "Mobiles" Object Group ###

Like Features, Mobiles can be placed on the map as objects in an object
group called "Mobiles".  Each such object should have a "name", which the
region will use to place the correct mobile.  This is the usual way to
pre-place mobiles on a tile map.

At present, all mobiles are placed by name by region-specific code.
Eventually, some mobile types might get handled automatically.

### "Areas" Object Layer ###

An object layer called "Areas" can be used to define larger object polygons
used by the region.  This is all TBD, but uses might include:

* Turf: an area a particular mobile will protect, and not leave.
* Paths: A list of points that a mobile will traverse over and over, like
  a cop walking a beat.
* Areas of heightened danger.  In the World region, for example, areas 
  might determine the kinds of monsters that can appear, if any.
  
## Specific Entities ##

### Signs ###

Signs are placed on the map as objects on the Features layer.  The type should
be "Sign" and the name should be the sign's name in the region's strings file.

### NPCs ###

NPCs are placed on the map as objects on the Mobiles layer.  The type should be
"NPC" and the name should be the NPC's root name in the region's strings file.
