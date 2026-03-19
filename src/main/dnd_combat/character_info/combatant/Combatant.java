package character_info.combatant;

import character_info.AbilityModifier;
import character_info.DealtEffectsList;
import character_info.LifeStatus;
import damage_implements.Effect;
import damage_implements.Weapon;
import format.ColorStyle;
import format.SwingStyles;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.ArrayList;

public class Combatant {

    protected final LifeStatus lifeStatus = new LifeStatus();
    protected final DealtEffectsList dealtEffects = new DealtEffectsList(this);
    protected final ArrayList<Combatant> hexedByList = new ArrayList<>();
    protected String name;
    protected int armorClass;
    protected int hpMax, hpCurrent;
    private JProgressBar healthBar;
    protected int initiative, inspiration;
    protected boolean isHealBlocked, isPoisoned;

    /**
     * NPC constructor
     */
    public Combatant(String name, int hpMax, int armorClass) {
        this.name = name;
        this.hpMax = hpMax;
        this.armorClass = armorClass;

        hpCurrent = hpMax;
        inspiration = 0;
    }

    /**
     * Increments the number of inspirations used
     *
     * @return true if the combatant has used more than two inspirations
     */
    public boolean useInspirationAndCheckExcess() {
        inspiration++;
        return inspiration > 2;
    }

    /**
     * Deals the param damage. Logs the combatant as unconscious if hp is 0.
     *
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

    /**
     * @return black if combatant is unconscious, green for safe hp, yellow for low hp, red for critical hp
     */
    public Color getHealthBarColor() {
        if (!lifeStatus.isConscious()) {
            return Color.BLACK;
        }
        if (isEnemy()) {
            return ColorStyle.ENEMY.getColor();
        }
        return ColorStyle.getPercentColor(hpCurrent, hpMax);
    }

    public String getHealthBarString() {
        return switch (lifeStatus.status()) {
            case ALIVE -> {
                if (hpCurrent == 0) {
                    yield "Alive but down for the count";
                } else if (isEnemy()) {
                    yield "?";
                }
                yield String.format("%d/%d", hpCurrent, hpMax);
            }
            case UNCONSCIOUS -> lifeStatus.toString();
            case DEAD -> "Dead :((";
        };
    }

    public void setHealth(int newHealth) {
        hpCurrent = newHealth;
    }

    public void setHealthBar(JProgressBar healthBar) {
        this.healthBar = healthBar;
    }

    public int initiative() {
        return initiative;
    }

    public void setInitiative(int initiative) {
        this.initiative = initiative;
    }

    public LifeStatus lifeStatus() {
        return lifeStatus;
    }

    public boolean isEnemy() {
        return false;
    }

    public int ac() {
        return armorClass;
    }

    public int mod(AbilityModifier stat) {
        return 0;
    }

    public int attackBonus(Weapon weapon) {
        return 0;
    }

    public int spellAttackBonus() {
        return 0;
    }

    public int saveDc() {
        return 10;
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
     *
     * @param target      the combatant to whom the effect is dealt
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

    public JPanel getEffectListPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        if (isPoisoned) {
            JLabel poisoned = new JLabel("\uD83D\uDC80");
            poisoned.putClientProperty("FlatLaf.style", "font: $h2.font");
            panel.add(poisoned);
        }
        if (!hexedByList.isEmpty()) {
            JLabel hexed = new JLabel("\uD83C\uDF00");
            hexed.putClientProperty("FlatLaf.style", "font: $h2.font");
            panel.add(hexed);
        }

        panel.setVisible(panel.getComponents().length > 0);
        panel.setBorder(new EmptyBorder(4, 4, 4, 4));

        return panel;
    }

    @NotNull
    private JLabel getOptionLabel() {
        JLabel optionLabel = new JLabel();
        optionLabel.putClientProperty("FlatLaf.style", "font: $h2.font");
        optionLabel.setBorder(new EmptyBorder(4, 4, 4, 4));

        optionLabel.setText("Inspirations Used: " + inspiration + "/2");
        if (!lifeStatus.isConscious()) {
            optionLabel.setText("Not in Fighting Condition");
            optionLabel.setForeground(ColorStyle.ORANGE_ISH_RED.getColor());
        } else if (inspiration > 2) {
            optionLabel.setForeground(ColorStyle.ORANGE_ISH_RED.getColor());
        }

        optionLabel.setVisible(!isEnemy());
        return optionLabel;
    }

    @Override
    public String toString() {
        return name;
    }

    public JPanel toPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        Color accentColor = switch (this) {
            case PC ignored -> ColorStyle.PARTY.getColor();
            case NPC npc when npc.isEnemy() -> ColorStyle.ENEMY.getColor();
            default -> ColorStyle.NPC.getColor();
        };
        panel.setBorder(new CompoundBorder(
                new MatteBorder(0, 5, 0, 0, accentColor),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel nameLabel = new JLabel(name);
        nameLabel.putClientProperty("FlatLaf.style", "font: $h1.font");
        nameLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel initiativeLabel = new JLabel("Initiative: " + initiative);
        initiativeLabel.putClientProperty("FlatLaf.style", "font: $h2.font");
        initiativeLabel.setBorder(new EmptyBorder(4, 4, 4, 4));

        JLabel optionLabel = getOptionLabel();

        JPanel effectList = getEffectListPanel();

        JProgressBar healthBarCopy = SwingStyles.cloneComponent(healthBar);
        assert healthBarCopy != null;
        healthBarCopy.setStringPainted(false);
        healthBarCopy.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(nameLabel);
        panel.add(initiativeLabel);
        panel.add(optionLabel);
        panel.add(effectList);
        panel.add(healthBarCopy);

        return panel;
    }

    public ArrayList<String> toTxt() {
        return null;
    }

    public String name() {
        return name;
    }

}