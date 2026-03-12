This program contains a menu to create campaigns, which is much more
user-friendly, but does admittedly take a while. If you don't want to
deal with that, this is how to manually type campaign code:

***
## Part 1: General Syntax
Each individual combatant or scenario is headed by ```.code```, with "code"
being different for each and being specified in those sections.

Individual parameters are written as ```key: value```.

As of my testing only the names should be case-sensitive, but as a general
rule leave things lowercase unless otherwise specified because the reader
likes to pull errors out of its ass sometimes.

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
name: Elrond
hp: 42/42
ac: 14
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

Level and Class are written in the standard ```key: value``` format, so 
```level: 2``` and ```class: bard``` as examples respectively.

### Main Field Stats
These are written like
```stats: [STR: 10, DEX: 12, CON: 9, INT: 12, WIS: 14, CHA: 10]```. 
Stats are separated by ```,```. Each stat has its abbreviation and value
with colon and space separation. Note the enclosing brackets as well.

### Weapons and Spells
These are written in a list like ```weapons: [Dagger, Longsword]``` or
```spells: [Eldritch Blast, Toll the Dead, Hex]```, with lowercase and 
underscore-separated words, separated by comma and space. Note the enclosing
brackets here as well.

### Putting the PC Together
Finally, the pc uses the header code ```.party``` so a correct PC will look 
similar to the following:

```text
.party
name: Gandalf the Grey
hp: 48/62
ac: 14
level: 100
class: wizard
stats: [STR: 12, DEX: 14, CON: 14, INT: 18, WIS: 20, CHA: 20]
weapons: [Longsword, Staff]
spells: [Intimidate Bilbo, You Shall Not Pass, Beam of Light]
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
name: Sacking of Coruscant
with: Ven Zallow,Jedi_12
against: Darth Malgus,Shae Vizla,Sith_20
```

## Putting it All Together
Below is a full correct example:
```text
.party
name: Frodo Baggins
hp: 20/20
ac: 18
class: fighter
stats: [STR: 10, DEX: 12, CON: 16, INT: 13, WIS: 16, CHA: 14]
weapons: [Sting]

.party
name: Samwise Gamgee
hp: 20/20
ac: 10
class: paladin
stats: [STR: 14, DEX: 9, CON: 16, INT: 9, WIS: 16, CHA: 11]
weapons: [Dagger, Pot]

.npc
name: Pippin Took
hp: 20/20
ac: 14

.npc
name: Merry Brandybuck
hp: 20/20
ac: 12

.npc
name: Boromir
hp: 40/40
ac: 16

.enemy
name: Orc
hp: 20/20
ac: 16

.enemy
name: Uruk-hai
hp: 40/40
ac: 17

.enemy
name: Nazgul
hp: 160/160
ac: 19

.scenario
name: Forest Chase
with: Pippin Took,Merry Brandybuck
against: Nazgul_4

.scenario
name: Breaking of the Fellowship
with: Pippin Took,Merry Brandybuck,Boromir
against: Orc_50,Uruk-hai
```