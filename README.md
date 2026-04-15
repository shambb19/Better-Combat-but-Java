This program contains a menu to create campaigns, which is much more
user-friendly, but does admittedly take a while. If you don't want to
deal with that, this is how to manually type campaign code:

***

# Section 1: Manual Code Writing

## 1) General Syntax

All items follow the syntax

```text
.type
key: value
key: value
// and so on
```

Values that are in a list follow the syntax

```text
key: [value1, value2, value3, value4]
```

## 2) Type Examples

Note that all of the following should be included in one .txt document:

### NPC (Friendly NPCs and all Enemies)
```text
.npc
name: Faramir
hp: 22/30
ac: 14
```

or

```text
.enemy
name: Nazgul
hp: 44/44
ac: 18
```

### PC (Party Members only)

```text
.party
name: Frodo Baggins
hp: 16/20
ac: 14
level: 3
class: paladin
stats: [STR: 12, DEX: 12, CON: 18, INT: 17, WIS: 16, CHA: 15]
weapons: [Shortsword]
```

or
```text
.party
name: Gandalf the Grey
hp: 32/44
ac: 18
level: 20
class: wizard
stats: [STR: 12, DEX: 14, CON: 18, INT: 20, WIS: 20, CHA: 20]
weapons: [Longsword, Staff]
spells: [You Shall Not Pass, Light Beacon]
```

### Scenarios (specific, pre-programmed encounters)
```text
.scenario
name: Flight to the Ford
with: [Samwise Gamgee, Peregrin Took, Meriadoc Brandybuck, Boromir]
against: [Orc_22, Uruk-hai]
```

Note here that the underscored number represents the quantity of that
particular NPC present.

Also note that all PCs in the file will automatically be included in any
created scenarios. There is an option in the app to mark them absent, either
if the player is gone or if the character is not participating in the battle.
***

# Section 2: Weapons and Spells

As of v4.4.0, the game has all simple and martial weapons found in the PHB,
as well as what I *think* is all the spells in the PHB. I intend to add the
ability to put custom weapons and spells in your campaign .txt, but this
functionality does not as yet exist.