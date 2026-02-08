package scenarios;

import combatants.Combatant;
import combatants.NPC;
import combatants.PC;

public enum Party {

    KARIS("Karis", 26, 14, false),
    BELLADONNA("Belladonna", 27, 16, false),
    BRAXTON("Braxton", 27, 16, false),
    DREXEN("Drexen", 27, 16, false),
    ROLLO("Rollo", 27, 16, false),
    EZEKIEL("Ezekiel", 27, 16, false),
    ENZA("Enza", 30, 18, false);

    private final String name;
    private final int hpMax;
    private final int armorClass;
    private final boolean isEnemy;

    Party(String name, int hpMax, int armorClass, boolean isEnemy) {
        this.name = name;
        this.hpMax = hpMax;
        this.armorClass = armorClass;
        this.isEnemy = isEnemy;
    }

    public Combatant get() {
        return new PC(name, hpMax, armorClass, isEnemy);
    }

    public Combatant getNPC() {
        return new NPC(name, hpMax, armorClass, isEnemy);
    }

}
