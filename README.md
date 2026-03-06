This program contains a menu to create campaigns, which is much more
user-friendly, but does admittedly take a while. If you don't want to
deal with that, this is how to manually type campaign code:

***
## Part 1: General Syntax
There are three sections to the code: Allies, Enemies, and Scenarios.
These sections are designated with those names in brackets, similar to 
a pom.xml document. As such, each section will look like this:
```text
<Allies>

~logic and such

<Allies>

```
with the header substituted for ```<Enemies>``` or ```<Scenarios>``` 
respectively.

Within each section, components should be enclosed by curly brackets
ON DIFFERENT LINES.

This is valid:
```text
{
~logic and such
}
{
~more logic
}
```
but this is NOT:
```text
{
logic and such
} {
~more logic that will cause an error
}
```
This isn't java and I read docs with less than 200 lines of code, so 
it will need to be a little picky. Also, in the Ally section, components
will need indicators next to their opening bracket. This will be explained
more in their respective sections.

Finally, individual parameters must be written without spaces and in all
lowercase, like ```key=value```.

### Note about Whitespace
Blank lines are allowed and tested to be so. Spaces are coded to work (and
do so in names for sure) but can throw random errors, and I have no idea
where they come from, so avoiding spaces when possible is ideal.

***

## Part 2: NPCs

PCs have three parameters:
1. Name
2. Maximum HP
3. Armor Class

and uses the header code ```npc```. So, a correct NPC will look like the 
following:

```text
{npc
name=Elrond
hp=42
ac=14
}
```
***

## Part 3: PCs

PCs have the same three parameters as NPCs as well as the following:
1. Level
2. Spell-casting Ability Modifier (Optional)
3. Main Field Stats
4. Weapons (Optional)
5. Spells (Optional)

Level is self-explanatory, but for the others:

### Spell-Casting Ability Modifier
This is written like ```spellMod=cha``` with that key and the stat in
question written as its three-letter abbreviation in lowercase.

### Main Field Stats
These are written like ```stats=str(12)/dex(10)/con(16+)/int(16+)/wis(12)/cha(10)```
with that key. Stats are separated by ```/```. Each stat has its abbreviation,
the value, and a plus if the combatant has proficiency in that stat.

### Weapons and Spells
These are written in a list like ```weapons=dagger/longsword``` or
```spells=eldritch_blast/toll_the_dead/hex```, with lowercase and 
underscore-separated words, separated by ```/```. If a combatant has many,
they can be split to multiple lines for readabiltiy. For example,
```
weapons=dagger/lance/javelin
weapons=longsword/crossbow
```
is also valid.

### Putting the PC Together
Finally, the pc uses the header code ```party``` so a correct PC will look 
similar to the following:

```text
{party
name=Gandalf the Grey
hp=62
ac=14
spellMod=wis
stats=str(12)/dex(14)/con(14)/int(18)/wis(20+)/cha(20+)
weapons=Longsword/Staff
spells=Beam of Light/Intimidate Bilbo
spells=You Shall Not Pass/Speak Language of Mordor
}
```
### Current HP
Both NPCs and PCs have the option to set a current health. This is completely
optional and, upon runs of the program, will be handled on the backend, but if
you want, it can be handled with ```hpCur=value```.

## Scenarios
Scenarios allow you to combine created combatants into different encounters 
that you can easily call at runtime. They require only a name, list of 
included friendly NPCS ONLY and enemies. All party members are implicitly 
included in all scenarios (you will have an option to mark them absent at 
runtime), so they are not part of this.

A correct scenario will look like this:

```text
{
name=Korriban Duel
with=Kao Cen Darach/Satele Shan
against=Darth Malgus/Darth Vitiate
}
```
Soon to be implemented is the ability to add quantities of a combatant.
This will be done with an underscore and the number, like
```against=Stormtrooper_20```

## Putting it All Together
Below is a full correct example:
```text
<Allies>

{party
name=Frodo Baggins
hp=20
ac=18
stats=str(10)/dex(12)/con(16+)/int(13)/wis(16+)/cha(14)
weapons=Sting
}

{party
name=Samwise Gamgee
hp=20
ac=10
stats=str(14)/dex(9)/con(16+)/int(9)/wis(16+)/cha(11)
weapons=Dagger/Pot
}

{npc
name=Pippin Took
hp=20
ac=14
}

{npc
name=Merry Brandybuck
hp=20
ac=12
}

<Allies>

<Enemies>

{
name=Orc
hp=20
ac=16
}

{
name=Uruk-hai
hp=40
ac=17
}

{
name=Nazgul
hp=160
ac=19
}

<Enemies>

<Scenarios>

{
name=Forest Chase
with=Pippin Took/Merry Brandybuck
against=Nazgul_2
}

{
name=Breaking of the Fellowship
with=Pippin Took/Merry Brandybuck
against=Orc_12/Uruk-hai
}

<Scenarios>
```