# The RPG #

This file will document the RPG statistics and rules used in _George's Saga_ 
(GS), as I work them out.

# Character Stats #

A "character" is a player character or other mobile that can participate in
combat.  (The NPCs in friendly towns and castles generally won't fight, and
so won't need stats as such.) 

## Basic Stats ##

A character has four basic stats:

- STR: Strength
- DEX: Dexterity
- WIS: Wisdom
- CON: Constitution

These are the six D&D/Angband stats, with INT folded into WIS and CHR dropped
because it is boring.  They also parallel the four GURPS stats ST, DX, IQ, and
HT.

An average adult human has a value of 10 for each stat.


## Character Points ##

GURPS has the useful notion of Character Points.  Building a character in GURPS
involves buying stats and other attributes for points.  Thus, a character's 
point score is a good measure of the general studliness of the character.  

This is a concept I will want to use, just as a neat way of ranking characters
and monsters.

For now, we'll assume with GURPS that each point of STR or CON costs 10 
character points; each point of DEX or INT costs 20 character points.

I will differ from GURPS in that GURPS assigns an average human (stats of 10
across the board) as having a point cost of 0: those initial 10 points are 
free.  Since my goal is to compare different creatures, some of whom will be
weaker than the average human, I'm going to start counting at zero.  In other
words, an average human will have a point cost of 500; a small humanoid might
have a point cost of 300, and a rat a point cost of 200.  Or something like
that.

On leveling up, the character will be given some number of points to allocate,
thus increasing his stats.

## Hit Points ##

In GURPS, HP = ST, that is, your hit points are the same as your strength.  For
a CRPG, this isn't reasonable; at least, I don't think it is.  (The result would
be a rather different CRPG than any I've played.)  Your typical
CRPG character sees significant HP growth as he levels up, and expects to
fight monsters with more HP as well.  Further, HP really ought to be a function
of health, i.e., CON, and of the player's level.

The Angband model, as described in ability.spo, seems to be more on target.  
What follows is a variant of it.

The player's initial MAXHP is his initial CON:

    HP(1) = CON(1)
    
At each level, he gets an additional 1 to CON(1) points.

Plus, he gets a bonus due to CON and level, as shown in the following
table:  The numbers are more or less from the Angband table, although
I don't intend to use 18/xx-style stats, so I've modified that.
  
| CON   | Extra HP/level |
|-------|----------------|
| 3     |     -2.5       |
| 4     |     -1.5       |
| 5     |     -1.0       |
| 6     |     -0.5       |
| 7-14  |        0       |
| 15-16 |      0.5       |
| 17    |      1.0       |
| 18-19 |      1.5       |
| 20-21 |      2.0       | 
| 22-23 |      2.5       |

Thus, your MAXHP = baseHP + modHP. 

## Will and Perception ##

GURPS defines secondary stats called "Will" and "Perception"; they are simply
equal to IQ.  (I suppose there might be power-ups and curses that affect only
one or the other.)  For now, we'll just use WIS.

## Speed and Evasiveness ##

GURPS defines Basic Speed = (HT + DX)/4.0.  This is used to determine order of
action in combat.  Basic Move is how far the player can move in one second;
GURPS defines it as int(Basic Speed).  

For GS, the definition of speed looks reasonable; Basic Move would correspond to
the character's movement points in combat mode.  floor(Basic Speed) seems low to
me, but we'll see how it works out.
Speed (SPD) is defined as follows: SPD = (DEX + CON)/4.0.

Movement Points (MP) is defined as follows: MP = round(SPD).  I might change 
it to something like MP = round(SPD) + 2.  This is the number of cells the 
player can  move in combat in one turn.

Attack Points (AP) the number of attacks a player can make in one turn.  By 
default, AP = 1.  Hasting a player might increase AP.

## Experience and Leveling Up ##

A PC can be awarded experience for a variety of actions, such as killing 
monsters.  The PC levels up when his experience exceeds the thresholds in
the following table.

|Exp.  |Experience|Exp.  |Experience|Exp.  |Experience
|Lvl.  |    Needed|Lvl.  |    Needed|Lvl.  |    Needed
|------|----------|------|----------|------|----------
|   1  |         0|  18  |     2,900|  35  |   350,000
|   2  |        10|  19  |     3,600|  36  |   450,000
|   3  |        25|  20  |     4,400|  37  |   550,000
|   4  |        45|  21  |     5,400|  38  |   700,000
|   5  |        70|  22  |     6,800|  39  |   850,000
|   6  |       100|  23  |     8,400|  40  | 1,000,000
|   7  |       140|  24  |    10,200|  41  | 1,250,000
|   8  |       200|  25  |    12,500|  42  | 1,500,000
|   9  |       280|  26  |    17,500|  43  | 1,800,000
|  10  |       380|  27  |    25,000|  44  | 2,100,000
|  11  |       500|  28  |    35,000|  45  | 2,400,000
|  12  |       650|  29  |    50,000|  46  | 2,700,000
|  13  |       850|  30  |    75,000|  47  | 3,000,000
|  14  |     1,100|  31  |   100,000|  48  | 3,500,000
|  15  |     1,400|  32  |   150,000|  49  | 4,000,000
|  16  |     1,800|  33  |   200,000|  50  | 4,500,000
|  17  |     2,300|  34  |   275,000


# Combat #

GURPS-style combat led to play that was absurdly tedious and slow, character
monster trading blows that did no damage over and over and over.  
Angband-style combat appears to be more useful.  The following is based on
the Angband spoilers "attack" and "mon-blow".

Note that the function of armor in Angband is to reduce the probability of
hitting; that is, all armor works like shields in GURPS.  Armor does not
absorb damage once the mobile is hit.

Topics for the future:

* Critical hits
* Elemental attacks
* Equipment and spell-based To hit and to damage modifiers
* Banes

## Armor Class ##

Monsters are assigned an armor class (AC) on creation.  Player characters have 
an armor class based on their stats and equipment.

Armor has an Armor Class [m,+n], where [m] is standard armor class for the 
kind of armor, and +n is any magical bonus it gives.  The higher the armor
class, the less likely the player is to be hit.  Armor might have other 
effects as well, e.g., blocking elemental damage.

A player's Armor Class (AC) is the sum of the armor class for all equipped
armor (and other items), plus a dexterity bonus.  The Angband bonus looks
like this:

|DEX    |+AC
|-------|---
|     3 | -4 
|     4 | -3
|     5 | -2 
|     6 | -1
|  7-14 |  0
| 15-17 |  1
| 18-19 |  2

It goes up from there, for various 18/xx ratings.

In practice, we aren't going to have any PCs with a DEX less than 10; and we
might well have DEX > 18 or 19.  Also, this is a fairly minimal effect,
considering that even a cloak adds 1 point to armor class.  I'm going to adopt
this rule:

The DEX modifier to AC = max(0, DEX - 12), i.e., 1 point for every point of 
DEX over 12.

## Weapons, Attacks, and Damage Dice ##

Every weapon that a player character can equip, and every monster attack, has
some number of damage dice (e.g., 1D6+3).  If the attack hits, roll the damage
dice and apply the damage.  Armor does not absorb damage.

Equipment can add "to damage" modifiers, which are simply added to the damage
dice.  

For now we will use a STR bonus of +1 per point of STR over 12.

## Other Equipment Effects ##

A PC's equipment can add a number of other effects not yet implemented:

* +/- to AC
* +/- "to hit"
* +/- to damage
* Elemental damage
* Banes of various sorts
* Extra attacks

## The "to-hit" Probability: Skill vs. Armor Class ##

The probability P of hitting another mobile is based on the attacker's skill 
(K) vs. the defender's armor class (AC).  The attacker always hits 5% of the
time and always misses 5% of the time; which is simply to say that P is
constrained to be between 5% and 95%, inclusive.

P is therefore computed as follows:

Let N = max(0, K - 0.75AC))

The probability of a hit, stated as a percentage,
is then 

    P = max(5, min(95, 100 x N/K)).

P is now a number between 5 and 95.

Then, roll R=1D100.  If R <= P, it's a hit; otherwise it's a miss.  

If it's a hit, roll and apply damage to the mobile based on the damage dice.

## Monster Attacks on Player Characters ##

A monster's skill depends on the nature of the attack, of which Angband has
many.  For now we're concerned with physical attacks; the monster's skill is
simply

    K = 60 + 3 x LEVEL

Plug this into the "to-hit" function to get the probability of a hit.

See the Angband "mon-blow" spoiler for other attack possibilities.

### Number of Monster Attacks ###

Angband allows monsters to have up to four attacks per turn, depending on
the kind of monster.  I might want to allow that; we'll see.

## Player Character Melee Attacks ##

A player character's melee attack skill K is a complicated beast in Angband:

* The PC's class and race gives him a BASE skill, and a bonus per level. 

Then we add "to hit" modifiers

* STR bonus; it doesn't even start until STR=19 or more.
* DEX bonus; we'll call it +1 for every point of DEX above 15.
* Armor encumbrance penalty
* Equipment to-hit bonuses
* Heavy weapon penalty
* Edged weapon penalty for priests
* Temporary spell bonuses
* Stunning penalty.

The PC's skill K is then defined as follows:

K = BASE + BONUS*LEVEL + 3 x "To Hit".

Of these, we'll start with the DEX bonus; eventually we'll want a number of the
other modifiers, especially "to hit" bonuses from other equipment and from
spells.

### Base Melee Skill ###

The classes in GS do not directly map to the Angband classes, and there are 
no racial modifiers.  I'll define the basic skill and the level bonus as 
follows:

* A Farmer is basically a ranger
* A Knight is basically a warrior
* A Friar is basically a priest
* A Wizard is basically a mage

But I've given Friars and Wizards better level bonuses.

|Class  |Base Skill |Level Bonus 
|-------|-----------|-------------
|Farmer | 56        | 4.5
|Knight | 68        | 4.5
|Friar  | 48        | 3.0
|Wizard | 34        | 2.5
    
## Ranged Attacks ##

For now, we'll handle ranged attacks just like melee attacks.



# Ideas for Later #

These are ideas from GURPS or other sources that I don't see an immediate need
for but which might be interesting at a later time.

## Reaction Rolls ##

In GURPS, when a PC encounters an NPC, the GM does a "reaction roll".  This
determines the reaction of the NPC to the PC, and helps the GM to role-play
the NPC.  (See pg 3 of the GURPS Lite book.)

If GS ever has a narrative with multiple paths, or even NPCs with multiple
threads of speech, reaction rolls might be interesting.  Appearance, status,
and reputation would come into this.


