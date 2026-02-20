package combatants;

import damage.Effect;
import damage.Spell;
import damage.Weapon;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Combatant {

    private String name;
    private boolean isEnemy;
    private final boolean isNPC;

    private int initiative, inspiration, inspirationRolls;

    private final DealtEffectsList dealtEffects = new DealtEffectsList(this);
    private final ArrayList<Combatant> hexedByList = new ArrayList<>();
    private boolean isHealBlocked, isPoisoned;

    private int armorClass;
    private int level;
    private Stats stats;

    private int hpMax, hpCurrent;
    private final LifeStatus lifeStatus = new LifeStatus();
    private JProgressBar healthBar;

    private ArrayList<Weapon> weapons = null;
    private ArrayList<Spell> spells = null;

    private int totalDamageDealt, totalHealsGiven, totalHealsReceived;
    private int totalAttackSuccesses, totalAttackFails;

    public Combatant(String name, int hpMax, int armorClass, boolean isEnemy) {
        defaultConstructor(name, hpMax, armorClass, isEnemy);
        isNPC = true;
    }

    public Combatant(
            String name, int hpMax, int armorClass, boolean isEnemy,
            Stats stats, ArrayList<Weapon> weapons, ArrayList<Spell> spells
    ) {
        defaultConstructor(name, hpMax, armorClass, isEnemy);

        this.stats = stats;

        this.weapons = weapons;
        this.spells = spells;

        isNPC = false;
    }

    private void defaultConstructor(String name, int hpMax, int armorClass, boolean isEnemy) {
        this.name = name;
        this.hpMax = hpMax;
        this.armorClass = armorClass;
        this.isEnemy = isEnemy;

        hpCurrent = hpMax;
        inspiration = 0;

        totalDamageDealt = 0;
        totalHealsGiven = 0;
        totalHealsReceived = 0;

        totalAttackSuccesses = 0;
        totalAttackFails = 0;
    }

    public boolean useInspirationAndCheckExcess() {
        inspiration++;
        return inspiration > 2;
    }

    public void damage(int damage) {
        hpCurrent = Math.max(0, hpCurrent - damage);
        if (hpCurrent == 0) {
            lifeStatus.setUnconscious();
        }
    }

    public void heal(int healthRegained) {
        hpCurrent = Math.min(hpMax, hpCurrent + healthRegained);
        totalHealsReceived += healthRegained;
    }

    public int hp() {
        return hpCurrent;
    }

    public int maxHp() {
        return hpMax;
    }

    public double getHealthPercent() {
        return (double) hpCurrent / hpMax;
    }

    public Color getHealthBarColor() {
        if (!lifeStatus.isConscious()) {
            return Color.BLACK;
        }

        if (getHealthPercent() > 0.6) {
            return Color.GREEN;
        } else if (getHealthPercent() > 0.25) {
            return Color.YELLOW;
        } else {
            return Color.RED;
        }
    }

    public String getHealthString() {
        return hpCurrent + "/" + hpMax;
    }

    public void setHealth(int newHealth) {
        hpCurrent = newHealth;
    }

    public JProgressBar getHealthBar() {
        return healthBar;
    }

    public void setHealthBar(JProgressBar healthBar) {
        this.healthBar = healthBar;
    }

    public int getInitiative() {
        return initiative;
    }

    public void setInitiative(int initiative) {
        this.initiative = initiative;
    }

    public LifeStatus lifeStatus() {
        return lifeStatus;
    }

    public boolean isEnemy() {
        return isEnemy;
    }

    public boolean isNPC() {
        return isNPC;
    }

    public int ac() {
        return armorClass;
    }

    public boolean hasWeapons() {
        return weapons != null;
    }

    public ArrayList<Weapon> weapons() {
        return weapons;
    }

    public boolean hasSpells() {
        return spells != null;
    }

    public ArrayList<Spell> spells() {
        return spells;
    }

    public Stats stats() {
        return stats;
    }

    public boolean canHeal() {
        return !isHealBlocked;
    }

    public void setCanHeal(boolean canHeal) {
        isHealBlocked = !canHeal;
    }

    public boolean isPoisoned() {
        return isPoisoned;
    }

    public void setPoisoned(boolean isPoisoned) {
        this.isPoisoned = isPoisoned;
    }

    public void putEffect(Combatant target, Effect dealtEffect) {
        dealtEffects.put(target, dealtEffect);
    }

    public void endDealtEffects() {
        dealtEffects.clear();
    }

    public boolean isHexedBy(Combatant hexer) {
        return hexedByList.contains(hexer);
    }

    public void setHexedBy(Combatant hexer) {
        hexedByList.add(hexer);
    }

    public void logDamageDealt(int dmgAmount) {
        totalDamageDealt += dmgAmount;
    }

    public int getTotalDamageDealt() {
        return totalDamageDealt;
    }

    public void logHealGiven(int healAmount) {
        totalHealsGiven += healAmount;
    }

    public int getTotalHealsGiven() {
        return totalHealsGiven;
    }

    public int getTotalHealsReceived() {
        return totalHealsReceived;
    }

    public void logHit() {
        totalAttackSuccesses++;
    }

    public int getTotalAttackSuccesses() {
        return totalAttackSuccesses;
    }

    public void logMiss() {
        totalAttackFails++;
    }

    public int getTotalAttackFails() {
        return totalAttackFails;
    }

    public void logInspirationRoll(int inspirationRoll) {
        this.inspirationRolls += inspirationRoll;
    }

    public int getInspirationRolls() {
        return inspirationRolls;
    }

    public void levelUp() {
        level++;
    }

    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder(name + "\n");
        toString.append("Initiative: ").append(initiative).append("\n")
                .append("Inspirations Used: ").append(inspiration).append("/2\n");

        if (isPoisoned) {
            toString.append("Poisoned\n");
        }
        if (isHealBlocked) {
            toString.append("Healing Disabled\n");
        }
        hexedByList.forEach(hexer -> toString.append("Hexed by ").append(hexer.name()));

        return toString.toString();
    }

    public ArrayList<String> toTxt() {
        ArrayList<String> txt = new ArrayList<>();
        txt.add("{");
        txt.add("name=" + name);
        txt.add("hp=" + hpMax);
        txt.add("hpCur=" + hpCurrent);
        txt.add("ac=" + armorClass);

        if (!isNPC) {
            txt.add("level=" + level);
            if (stats.spellMod() != null) {
                txt.add("spellMod=" + stats.spellModStr());
            }
            txt.add(stats.toString());
            if (weapons != null) {
                StringBuilder weaponStr = new StringBuilder("weapons=");
                weapons.forEach(weapon -> weaponStr.append(weapon.getName()).append("/"));
                txt.add(weaponStr.toString());
            }
            if (spells != null) {
                StringBuilder spellStr = new StringBuilder("spells=");
                spells.forEach(spell -> spellStr.append(spell.getNameRoot()).append("/"));
                txt.add(spellStr.toString());
            }
        }

        txt.add("}");
        return txt;
    }

    public String name() {
        return name;
    }

    static class DealtEffectsList {

        private final Combatant parentCombatant;

        private final ArrayList<Combatant> poisonedCombatants = new ArrayList<>();
        private final ArrayList<Combatant> healBlockedCombatants = new ArrayList<>();

        public DealtEffectsList(Combatant parentCombatant) {
            this.parentCombatant = parentCombatant;
        }

        public void put(Combatant target, Effect effect) {
            if (effect == null) {
                return;
            }
            switch (effect) {
                case Effect.POISON -> {
                    poisonedCombatants.add(target);
                    target.setPoisoned(true);
                }
                case Effect.HEAL_BLOCK -> {
                    healBlockedCombatants.add(target);
                    target.setCanHeal(false);
                }
                case Effect.BONUS_DAMAGE -> target.setHexedBy(parentCombatant);
            }
        }

        public void clear() {
            poisonedCombatants.forEach(combatant -> combatant.setPoisoned(false));
            healBlockedCombatants.forEach(combatant -> combatant.setCanHeal(true));

            poisonedCombatants.clear();
            healBlockedCombatants.clear();
        }

    }

}