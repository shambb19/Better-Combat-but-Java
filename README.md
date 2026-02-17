This program requires .txt files to run. 

**Part 1: Writing PCs**

PCs are more complicated in that they require stats, weapons, and spells. Below is an example of a correctly formatted PC:

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

This is relatively self-explanatory, except the stat block. Values for each stat are put in parentheses. Plus values represent proficiency in that particular stat.
Note that these values can be in any order, but proficiency bonus MUST ALWAYS come before the stat block. I tried and failed to code around this issue, so you'll have to be aware of it.

Note that for readability, weapons and spells may be split into multiple lines and will be read correctly. For example:

```text
weapons=longsword
weapons=dagger
```

is also valid.

Spell names tend to be multiple words, and these should be separated with underscores:

```text
spells=chill_touch/poison_spray/shocking_grasp/ray_of_sickness
```

**Part 2: Writing NPCs**

This is far simpler than the above. Here is a correct example:
```text
{
name=Glorfindel
hp=40
ac=16
}
```

**Part 3: Putting it All Together**

Chained combinations of NPCs and PCs make up a battle scenario. Any PCs must be uploaded in a separate .txt file, which will look something like the following:

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

This will be entered in the "Party" prompt in the upload screen. Note that no headers are required, though the brackets absolutely are. 
The rest of the information will be uploaded in the other prompt.

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
Party members are written as listed above. The only allowed party members are those present in your uploaded Party .txt document

Let me know if you have any questions, comments, or concerns!
