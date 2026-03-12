This program contains a menu to create campaigns, which is much more
user-friendly, but does admittedly take a while. If you don't want to
deal with that, this is how to manually type campaign code:

***
## Part 1: General Syntax
There are two sections to the code: Combatants and Scenarios.
These sections are designated with those names in brackets but do
not need to be closed by another one.
```text
<Combatants>
~combatants and such

<Scenarios>
~scenarios and such
```
Each individual combatant or scenario is also headed by ```.code```, with "code"
being different for each and being specified in those sections.

Individual parameters are written as ```key <= value```.

Note that everything is case-sensitive, and whitespace is necessary between the key, <=,
and value. I do not like this but every attempt to fix it has resulted in frankly baffling,
so I've decided to give up for now.

```
***

## Part 2: NPCs and Enemies

PCs have three parameters:
1. Name
2. Maximum HP
3. Armor Class

and uses the header code ```.npc```. So, a correct NPC will look like the 
following:

```text
.npc
name <= Elrond
hp <= 42/42
ac <= 14
```

Enemies are formatted identically to NPCs but with the header code ```.enemy```.
***

## Part 3: PCs

PCs have the same three parameters as NPCs as well as the following:
1. Level
2. Class
3. Main Field Stats
4. Weapons (Optional)
5. Spells (Optional)

Level and Class are written in the standard ```key <= value``` format, so 
```level <= 2``` and ```class <= bard``` as examples respectively.

### Main Field Stats
These are written like ```stats <= str12,dex10,con16,int16,wis12,cha10```
with that key. Stats are separated by ```,```. Each stat has its abbreviation and value
with no separation.

### Weapons and Spells
These are written in a list like ```weapons <= Dagger,Longsword``` or
```spells <= Eldritch Blast,Toll the Dead,Hex```, with lowercase and 
underscore-separated words, separated by ```,```. If a combatant has many,
they can be split to multiple lines for readability. For example,
```
weapons <= Dagger,Lance,Javelin
weapons <= Longsword,Crossbow
```
is also valid.

### Putting the PC Together
Finally, the pc uses the header code ```.party``` so a correct PC will look 
similar to the following:

```text
.party
name <= Gandalf the Grey
hp <= 48/62
ac <= 14
spellMod <= wis
stats <= str12,dex14,con14,int18,wis20,cha20
weapons <= Longsword,Staff
spells <= Beam of Light,Intimidate Bilbo
spells <= You Shall Not Pass,Speak Language of Mordor
```

## Scenarios
Scenarios allow you to combine created combatants into different encounters 
that you can easily call at runtime. They require only a name, list of 
included friendly NPCs ONLY and enemies. All party members are implicitly 
included in all scenarios (you will have an option to mark them absent at 
runtime), so they are not part of this. 

If you have a generic npc you would like to add multiple of, you can do so by
adding ```_num``` after the NPC's name, with "num" being the total number you
would like to add.

A correct scenario will look like this:

```text
.scenario
name <= Sacking of Coruscant
with <= Ven Zallow,Jedi_12
against <= Darth Malgus,Shae Vizla,Sith_20
```

## Putting it All Together
Below is a full correct example:
```text
<Combatants>

.party
name <= Frodo Baggins
hp <= 20/20
ac <= 18
class <= fighter
stats <= str10,dex12,con16,int13,wis16,cha14
weapons <= Sting

.party
name <= Samwise Gamgee
hp <= 20/20
ac <= 10
class <= paladin
stats <= str14,dex9,con16,int9,wis16,cha11
weapons <= Dagger,Pot

.npc
name <= Pippin Took
hp <= 20/20
ac <= 14

.npc
name <= Merry Brandybuck
hp <= 20/20
ac <= 12

.npc
name <= Boromir
hp <= 40/40
ac <= 16

.enemy
name <= Orc
hp <= 20/20
ac <= 16

.enemy
name <= Uruk-hai
hp <= 40/40
ac <= 17

.enemy
name <= Nazgul
hp <= 160/160
ac <= 19


<Scenarios>

.scenario
name <= Forest Chase
with <= Pippin Took,Merry Brandybuck
against <= Nazgul_4

.scenario
name <= Breaking of the Fellowship
with <= Pippin Took,Merry Brandybuck,Boromir
against <= Orc_50,Uruk-hai
```