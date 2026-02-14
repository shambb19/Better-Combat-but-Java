package scenarios;

import combatants.Combatant;
import damage.Spell;
import combatants.Stats;
import damage.Weapon;

import java.util.ArrayList;
import java.util.List;

public enum Party {

    KARIS("Karis", 24, 14, false,
            new Stats(
                    7, false,
                    12, true,
                    11, false,
                    16, true,
                    14, false,
                    15, false,
                    2,
                    null
            ),
            List.of(
                    Weapon.CROSSBOW,
                    Weapon.DAGGER
            ),
            null
    ),
    BELLADONNA("Belladonna", 30, 14, false,
            new Stats(
                    11, false,
                    13, false,
                    14, false,
                    16, false,
                    12, false,
                    10, false,
                    2,
                    Stats.stat.CHA
            ),
            List.of(
                    Weapon.DAGGER
            ),
            List.of(
                    Spell.CHILL_TOUCH,
                    Spell.POISON_SPRAY,
                    Spell.SHOCKING_GRASP,
                    Spell.RAY_OF_SICKNESS,
                    Spell.THUNDER_WAVE,
                    Spell.HELLISH_REBUKE,
                    Spell.PHANTASMAL_FORCE
            )
    ),
    BRAXTON("Braxton", 27, 16, false,
            new Stats(
                    10, false,
                    10, false,
                    10, false,
                    10, false,
                    10, false,
                    10, false,
                    2,
                    Stats.stat.CHA
            ),
            null,
            List.of(
                    Spell.BURNING_HANDS,
                    Spell.HEX,
                    Spell.SCORCHING_RAY,
                    Spell.CHILL_TOUCH,
                    Spell.ELDRITCH_BLAST
            )
    ),
    DREXEN("Drexen", 27, 16, false),
    ROLLO("Rollo", 27, 16, false),
    EZEKIEL("Ezekiel", 27, 16, false),
    ENZA("Enza", 30, 18, false);

    private final String name;
    private final int hpMax;
    private final int armorClass;
    private final boolean isEnemy;
    private Stats stats = null;
    private List<Weapon> weapons;
    private List<Spell> spells;

    Party(String name, int hpMax, int armorClass, boolean isEnemy) {
        this.name = name;
        this.hpMax = hpMax;
        this.armorClass = armorClass;
        this.isEnemy = isEnemy;
    }

    Party(String name, int hpMax, int armorClass, boolean isEnemy,
          Stats stats, List<Weapon> weapons, List<Spell> spells
    ) {
        this.name = name;
        this.hpMax = hpMax;
        this.armorClass = armorClass;
        this.isEnemy = isEnemy;
        this.stats = stats;

        this.weapons = weapons;
        this.spells = spells;
    }

    public Combatant get() {
        if (weapons != null || spells != null) {
            ArrayList<Weapon> weaponsList = (weapons != null) ? new ArrayList<>(weapons) : null;
            ArrayList<Spell> spellsList = (spells != null) ? new ArrayList<>(spells) : null;

            return new Combatant(name, hpMax, armorClass, isEnemy, stats, weaponsList, spellsList);
        }
        if (stats != null) {
            return new Combatant(name, hpMax, armorClass, isEnemy, stats);
        }
        return new Combatant(name, hpMax, armorClass, isEnemy);
    }

}