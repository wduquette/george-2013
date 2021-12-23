# Sprites, Icons and Tiles #

This document explains how the application handles sprites, the images that are
used to build up the game world.  There are two kinds of sprite: icons and tiles.
These are defined in icon sets and tile sets.  All sprites are 40 pixels by 40 pixels.

## Icons ##

Icons are images used to depict Mobiles, Features, Items, and other GUI
elements.  All images that appear *on* the map, and most images that appear
elsewhere in the application, are icons.

Icons are defined in the `icon_images.pyxel` file, which is exported as 
`icon_images.png` for import into the application.  The icons are imported
by the Icon enumeration, which creates a symbolic name for each icon.

Icons can be added and re-ordered freely, provided that Icon.java is kept in sync 
with `icon_images.*`.

In the future, we might split `icon_images.pyxel` into several files, and have 
Icon.java load them in sequence. 

## Tiles ##
    
Tiles are images used to define Region maps.  There are two kinds.

*   Terrain tiles dictate the appearance of the map: walls, floor, buildings,
    and so forth.  A region will build the underlying map image out of the
    terrain tiles.

*   Marker tiles are used to indicate the location of features and mobiles
    on predefined tile maps.
    
## Tile Sets ##

Tiles are defined by tile sets.  This package contains (TBD) several tile sets
for general use, and particularly for use by regions that generate their maps
randomly.  However, individual regions may define their own tile sets.

The tile sets in this package are imported by classes with names like 
StandardTile.java.  Regions that define their own tiles will usually import 
them tiles using a nested class.

The "marker.pyxel" tile set is particularly important.  It is a set of
marker tiles for use in predefined tile maps.

## Predefined Tile Maps ##

Regions can either generate their maps algorithmically, or load a predefined
tile map edited in PyxelEdit.  Predefined tile maps are created and used as
follows:

1.  Copy marker.pyxel to the new Region, and rename it according to the
    region, i.e., sewers.pyxel.  This gives you the basic set of marker tiles.
    You can delete any marker tiles you're sure you won't need.
    
2.  Import or create the desired terrain tiles.  They should go after the
    marker tiles.   Create as many as you need.
    
3.  On Layer 0, paint your terrain using the terrain tiles.

4.  On Layer 1, add marker tiles for the needed features: entrances, doors,
    chests, and so forth.
    
5.  On Layer 2, add marker tiles for the needed mobiles.

6.  Save the .pyxel file, and export it as a text tile map, e.g., sewers.txt.
    
7.  Define a TerrainTile enum as a nested class in your Region's class.
    It must list all of the marker and terrain tiles found in your tile map's
    tile set, in the same order, and must assign a name and TerrainType to each.
    For your marker tiles, the terrain type can be UNKNOWN.  The names need
    not be unique; indeed, you might find you have several kinds of "stone wall".

## Marker Tile Set Details ##

The Marker tile set consists of tiles containing short text strings, e.g.,
"M1", "M2", and "M3".  These can be used to mark anything, but usually the 
"M" tiles are used for mobiles, the "C" tiles are used for chests, and so on.

PyxelEdit doesn't provide any tools for creating Marker tiles.  Hence, they were
produced as follows:

1.  The file `marker.psd` was created in PhotoShop Elements.  PSE allows you to
    create images with text objects which can be edited and moved around.  We
    used 30pt Arial with anti-aliasing turned off, as strings like "A99" will
    fit in one 40x40 tile.
    
2.  The file `marker.psd` was then exported as `marker.png`.

3.  The file `marker.png` was then imported into PyxelEdit to make `marker.pyxel`.
    You need to tell PyxelEdit that the tiles are 40x40, and to identify tiles.
    This gives you a PyxelEdit tile set that can be imported into other
    PyxelEdit files.
    
With luck I won't need to go through this again.