package character_info;

import combat_menu.listener.DieRollListener;
import damage_implements.Effect;
import damage_implements.Spell;
import damage_implements.Weapon;
import admin.Admin;
import util.Message;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Combatant {

    private String name;
    private boolean isEnemy;
    private final boolean isNPC;

    private int initiative, inspiration;

    private final DealtEffectsList dealtEffects = new DealtEffectsList(this);
    private final ArrayList<Combatant> hexedByList = new ArrayList<>();
    private boolean isHealBlocked, isPoisoned;

    private int armorClass;
    private int level;
    private Stats stats;

    private int hpMax, hpCurrent;
    private final LifeStatus lifeStatus = new LifeStatus();
    private JProgressBar healthBar;

    private ArrayList<Weapon> weapons;
    private ArrayList<Spell> spells;

    /**
     * NPC constructor
     */
    public Combatant(String name, int hpMax, int armorClass, boolean isEnemy) {
        defaultConstructor(name, hpMax, armorClass, isEnemy, null, null);
        isNPC = true;
    }

    /**
     * PC Constructor; defaults isEnemy to false
     */
    public Combatant(
            String name, int hpMax, int armorClass,
            Stats stats, ArrayList<Weapon> weapons, ArrayList<Spell> spells
    ) {
        defaultConstructor(name, hpMax, armorClass, false, weapons, spells);

        this.stats = stats;

        isNPC = false;
    }

    /**
     * Separated from constructors to avoid code redundancy. Assigns default values.
     */
    private void defaultConstructor(String name, int hpMax, int armorClass, boolean isEnemy,
                                    ArrayList<Weapon> weapons, ArrayList<Spell> spells) {
        this.name = name;
        this.hpMax = hpMax;
        this.armorClass = armorClass;
        this.isEnemy = isEnemy;

        hpCurrent = hpMax;
        inspiration = 0;

        this.weapons = new ArrayList<>();
        if (weapons != null) {
            this.weapons.addAll(weapons);
        }

        this.spells = new ArrayList<>();
        if (spells != null) {
            this.spells.addAll(spells);
        }
    }

    public void manualAdjust(String key, String value) {
        if (key.equals(Admin.NAME_EDIT_CODE)) {
            name = value;
            return;
        } else if (key.equals(Admin.SPELL_MOD_EDIT_CODE) || key.equals(Admin.STAT_EDIT_CODE)) {
            stats.manualAdjust(value);
            return;
        }

        int valInt = Integer.parseInt(value);
        switch (key) {
            case Admin.HP_EDIT_CODE -> hpMax = valInt;
            case Admin.HP_CUR_EDIT_CODE -> {
                if (valInt < hpCurrent) {
                    damage(hpCurrent - valInt);
                } else {
                    heal(valInt - hpCurrent);
                }
            }
            case Admin.AC_EDIT_CODE -> armorClass = valInt;
        }
    }

    /**
     * Increments the number of inspirations used
     * @return true if the combatant has used more than two inspirations
     */
    public boolean useInspirationAndCheckExcess() {
        inspiration++;
        return inspiration > 2;
    }

    /**
     * Deals the param damage. Logs the combatant as unconscious if hp is 0.
     * @param damage The input amount of damage
     */
    public void damage(int damage) {
        hpCurrent = Math.max(0, hpCurrent - damage);
        if (hpCurrent == 0) {
            lifeStatus.setUnconscious();
        }
    }

    /**
     * Heals the combatant for the param amount without over-healing
     */
    public void heal(int healthRegained) {
        hpCurrent = Math.min(hpMax, hpCurrent + healthRegained);
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

    /**
     * @return black if combatant is unconscious, green for safe hp, yellow for low hp, red for critical hp
     */
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

    /**
     * @return a string of the current and max hp's as an unsimplified fraction
     */
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

    public ArrayList<Weapon> weapons() {
        return weapons;
    }

    public ArrayList<Spell> spells() {
        return spells;
    }

    public Stats stats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
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

    /**
     * Logs an effect dealt to the target param by the root combatant. Because most effects end
     * on the attacker's next turn, these are handles in the static DealtEffectsList class in
     * Combatant.
     * @param target the combatant to whom the effect is dealt
     * @param dealtEffect the effect dealt
     */
    public void putEffect(Combatant target, Effect dealtEffect) {
        dealtEffects.put(target, dealtEffect);
    }

    /**
     * Ends all effects dealt by the root combatant (outer method)
     */
    public void endDealtEffects() {
        dealtEffects.clear();
    }

    public boolean isHexedBy(Combatant hexer) {
        return hexedByList.contains(hexer);
    }

    public void setHexedBy(Combatant hexer) {
        hexedByList.add(hexer);
    }

    public int level() {
        return level;
    }

    public void levelUp() {
        if (isNPC) {
            return;
        }
        hpMax = Message.getWithLoopUntilInt(
                name + "'s health increase after level up?",
                "We love level ups!"
        );
        level++;
    }

    public JPanel getCombatantPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 3));

        JLabel label = new JLabel(name);

        JTextField initiativeField = new JTextField();
        initiativeField.addKeyListener(new DieRollListener(1, 20, initiativeField));

        JCheckBox absentBox = new JCheckBox();
        absentBox.addActionListener(e -> initiativeField.setEnabled(!absentBox.isSelected()));

        panel.add(label);
        panel.add(initiativeField);
        if (isEnemy || isNPC) {
            panel.add(new JLabel());
        } else {
            panel.add(absentBox);
        }

        return panel;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * @return the string used to display the combatant's stats in the ActionPanel JPanel.
     */
    public String actionList() {
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

    /**
     * @return the lines of text for this combatant to be logged in a .txt file for its party.
     */
    public ArrayList<String> toTxt() {
        ArrayList<String> txt = new ArrayList<>();
        String header = "{";
        if (!isEnemy) {
            if (isNPC) {
                header += "npc";
            } else {
                header += "party";
            }
        }
        txt.add(header);
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
            if (weapons != null && !weapons.isEmpty()) {
                System.out.println(weapons);
                StringBuilder weaponStr = new StringBuilder("weapons=");
                weapons.forEach(weapon -> weaponStr.append(weapon.name()).append("/"));
                txt.add(weaponStr.toString());
            }
            if (spells != null && !spells.isEmpty()) {
                StringBuilder spellStr = new StringBuilder("spells=");
                spells.forEach(spell -> spellStr.append(spell.name()).append("/"));
                txt.add(spellStr.toString());
            }
        }

        txt.add("}");
        txt.add("");
        return txt;
    }

    public String name() {
        return name;
    }

    static class DealtEffectsList {

        private final Combatant parentCombatant;

        private final ArrayList<Combatant> poisonedCombatants = new ArrayList<>();
        private final ArrayList<Combatant> healBlockedCombatants = new ArrayList<>();

        /**
         * Creates a list of combatants poisoned and heal blocked by the root
         * combatant. Other effects added later will also be logged here.
         * @param parentCombatant the root combatant
         */
        public DealtEffectsList(Combatant parentCombatant) {
            this.parentCombatant = parentCombatant;
        }

        /**
         * Logs the effect param as dealt to the target param.
         */
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

        /**
         * Ends all effects dealt by the root combatant (inner method)
         */
        public void clear() {
            poisonedCombatants.forEach(combatant -> combatant.setPoisoned(false));
            healBlockedCombatants.forEach(combatant -> combatant.setCanHeal(true));

            poisonedCombatants.clear();
            healBlockedCombatants.clear();
        }

    }

}