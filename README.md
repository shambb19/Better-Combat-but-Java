# Contents
1. Tutorial for using the .txt input system
2. Copy-able text blocks for the party and allies of the current Kyreun campaign

## Input .txt Tutorial

This program requires .txt files to run. 

### Part 1: Writing PCs

PCs are more complicated in that they require stats, weapons, and spells. 
Below is an example of a correctly formatted PC:

```text
{
name=Frodo Baggins
hp=20
ac=14
prof=2
stats=str(12)/dex(9)/con(14+)/int(11)/wis(16+)/cha(12)
weapons=longsword/dagger
}
```

This is relatively self-explanatory, except the stat block. Values for each stat are put in parentheses. 
Plus values represent proficiency in that particular stat. Note that these values can be in any order, 
but proficiency bonus MUST ALWAYS come before the stat block. I tried and failed to code around this issue, 
so you'll have to be aware of it.

Note that for readability, weapons and spells may be split into multiple lines and will be read correctly. 
For example:

```text
weapons=longsword
weapons=dagger
```

is also valid.

Spell names tend to be multiple words, and these should be separated with underscores:

```text
spells=chill_touch/poison_spray/shocking_grasp/ray_of_sickness
```

### Part 2: Writing NPCs

This is far simpler than the above. Here is a correct example:
```text
{
name=Glorfindel
hp=40
ac=16
}
```

### Part 3: Putting it All Together

Chained combinations of NPCs and PCs make up a battle scenario. Any PCs must be uploaded in a separate 
.txt file, which will look something like the following:

```text
{
name=Frodo Baggins
hp=20
ac=14
prof=2
stats=str(12)/dex(9)/con(14+)/int(11)/wis(16+)/cha(12)
weapons=longsword/dagger
}
{
name=Gandalf
hp=20
ac=12
prof=2
spellMod=wis
stats=str(16)/dex(14)/con(15+)/int(14+)/wis(18+)/cha(15)
weapons=longsword
spells=hex/bright_sun_thing/hellish_rebuke
spells=you_shall_not_pass
}
```

This will be entered in the "Party" prompt in the upload screen. Note that no headers are required, 
though the brackets absolutely are. The rest of the information will be uploaded in the other prompt.

```text
<Allies>
party=Frodo Baggins/Gandalf
{
name=Samwise Gamgee
hp=12
ac=10
}
{
name=Pippin
hp=12
ac=10
}
<Allies>
<Enemies>
{
name=Saruman
hp=100
ac=18
}
{
name=Balrog
hp=200
ac=20
}
<Enemies>
```
The Allies and Enemies tags are required at the beginning and end of their respective team lists.
Party members are written as listed above. The only allowed party members are those present in 
your uploaded Party .txt document

Let me know if you have any questions, comments, or concerns!

## Kyreun Starter Code

### Party Code
This contains only the party members for whom I have received adequate character information. 
Required information to be included in the party code is:
1. Maximum HP
2. Armor Class
3. STR, DEX, CON, INT, WIS, and CHA stat values and if your character has proficiency.

Information that is optional, but recommended (if your character has them) for easier interaction 
with the program is:
1. Spell Modifier
2. A weapon list
3. A spell list

Current Party Code:
```text
{
name=Karis
hp=24
ac=14
prof=2
stats=str(7)/dex(12+)/con(11)/int(16+)/wis(14)/cha(15)
weapons=crossbow/dagger
}
{
name=Belladonna
hp=30
ac=14
prof=2
spellMod=cha
stats=str(11)/dex(13+)/con(14)/int(16+)/wis(12)/cha(10)
weapons=dagger
spells=chill_touch/poison_spray/shocking_grasp/ray_of_sickness
spells=thunder_wave/hellish_rebuke/phantasmal_force
}
{
name=Drexen
hp=30
ac=12
prof=2
spellMod=cha
stats=str(10)/dex(10)/con(10)/int(10)/wis(10)/cha(10)
spells=burning_hands/hex/scorching_ray/chill_touch
spells=eldritch_blast
}
```
NOTES: Drexen has placeholder hp, ac, stat values

### Allies Starter Code (NEEDS ENEMIES-WILL NOT RUN)
This is code with the rest of the party added as NPCs. Note, as above, that this 
code requires an Enemy block and will not run in the program on its own.

Battle Starter code:
```text
<Allies>
party=Karis/Belladonna/Drexen
{
name=Braxton
hp=30
ac=16
}
{
name=Ezekiel
hp=18
ac=10
}
{
name=Rollo
hp=30
ac=16
}
<Allies>
```