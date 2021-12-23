= Saving the Game =

This document has notes on how to save the game in George's Saga.

The problem is essentially one of checkpointing and restoring the state of a 
single object, where that object contains all of the significant game state,
directly or indirectly.

This is going to be annoying.  The big question is, what constraints do I want
to live with in order to make saving and restoring easy and error free?  What's
the right architecture?

== Data to be saved ==

What data needs to be saved?

* The party and its members, including their current locations and inventories

* The existing regions and their contents, including the map, features, 
  and mobiles, and any instance variables

* The state of the various quests

This is to say, the Board's party, quest, and regions objects.

== Requirements ==

At the very least, we need the following:

* A given version of GS needs to be able to save its state and load it again.

* Subsequent versions need to be able to reject save files from previous 
  versions. 
  
Ideally, we'd like a scheme where save files can be loaded into later versions
as much as possible.

== Observations ==

It will be simpler if the objects in question have minimal state.  It occurs to
me that my objects have more state than they need.  For example, every entity
has a name variable; but in fact, the names are (at present) one-for-one with
classes in almost every case.  The Mannikin class is an exception, because you
can have many Mannikins with different names.  So it needs a name variable.  
But in every other case, a name() method that returns the name would do the
job perfectly well.  And then that's a value that need not be saved.

Similarly, every region has a map; but if that map is read from disk and never
changes, it need not be part of the region's save state.  It could, in fact, 
be a static member variable.

== Possible Mechanisms ==

There are three obvious mechanisms:

* Java Serialization via the Serializable interface

* JSON serialization via the Gson library

* Custom save format

=== Java Serialization ===

I've read the Effective Java info on Serializable, and it's  
scary.  In particular, it's clear that using Serializable with inheritance
hierarchies is difficult.  It may be doable, but it's clear that the overhead
of doing it right is significant.

For example, I'd usually want to have explicit readObject and writeObject
methods in every class (ugh!).  Given the vast number of leaf classes in 
George's Saga, that's an awful lot of ugly boilerplate code.  Also, I'd need to
remember to put "transient" on almost all instance variables.

Oh, and inner classes can't be serializable.  That's a big problem, because I
use a lot of them...and they make sense.

=== JSON Serialization ===

The Gson library relies on a set of classes that directly mirror the JSON
entities, and although it can write out data from classes that use the 
Collections library I seem to recall that it can't read it back in without
help.  What I'm likely to end up with here is a set of proxy classes; to save,
I create the tree of proxy objects representing the state, and save that as
JSON.  On restore, I create a new such tree, and use it to initialize the 
game.

Of course, I can do the same thing with Java Serialization.

Gson seems like it might be easier to use, and it means that the save files
are to some extent human readable, which might be nice.  On the other hand,
it means that the save files are to some extent human readable, which is a
problem if I ever release the game publicly.

However, Gson/JSON doesn't seem like it's going to cope nicely with lists of
polymorphic objects.  Java Serializations is a better match for that.

=== Custom Save Format ===

This approach is attractive, because it seems like it might be simpler than 
the previous two approaches.  I think in practice, though, it might be just
as bad, i.e., it's likely to have problems with the same things.  And the others
might do some things for me that I'd have to do for myself otherwise.

== Solution Concept A ==

Here's an idea.  We don't attempt to serialize the actual region, mobile,
feature, and item objects that make up the game.  Instead, we create a parallel 
structure and serialize that.  On load, we load the parallel structure and use
it explicitly to rebuild the game objects.  Here are some ideas:

* Every object is capable of producing a proxy that contains enough information
  to rebuild the object.  The proxy object has no behavior.

* Every object has a unique string name, assigned when it was created.

* When an object is asked to save itself, it registers its name, its class,
  and its proxy with a SaveFile object.  (Alternatively, the proxy might be
  a builder object that knows how to recreate its original.  This would work
  well for singletons, which should be created at application start-up; the
  proxy just returns the original singleton.)
  
* When an object that owns other objects (as a PC owns items) is asked to
  save itself, lists and maps of objects replace the objects with their names.
  Thus, a PC's proxy has a list of the names of the items in its inventory.
  
* When it is time to save, the Board asks all of the relevant top-level 
  objects (i.e., the quest manager, the PCs, and the regions) to save 
  themselves.
  
* Then, the SaveFile object serializes itself to disk in some way.  Gson
  is a likely candidate.

* When the save file is loaded, each object is recreated from its proxy; and 
  objects that own other objects ask the SaveFile object for the proxy and
  use it to build the new instance.

There are some things to do to prepare for this.

* I need to flatten the inheritance tree, insofar as instance variables are
  concerned, i.e., I need to push instance variables up the tree as far as
  possible, so that the code to create proxies can be done once for 
  entire families of objects.  
  
* Alternatively, I can break inheritance trees apart using interfaces, 
  possibly joined with composition and delegation.  Do all Items really need
  to belong to one class?  It might be easier if Armor were one base class,
  Potion was another, and so on.  The Entity class can probably be an
  interface;  there is some commonality between Mobiles and Features, but 
  it's not clear that there's enough to make it a class.

* In every case, I want to segregate the information that needs to be saved
  into some data structure that can easily be copied into a proxy, so as to
  make maintenance trivially easy.
  
* And if the proxies can be designed so that later versions of the code can
  cope with earlier proxies, so much the better.

=== Items ===

Every item instance has a unique string name