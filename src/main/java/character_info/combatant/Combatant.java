package character_info.combatant;

import __main.Main;
import character_info.AbilityModifier;
import character_info.DealtEffectsList;
import character_info.LifeStatus;
import damage_implements.Effect;
import damage_implements.Weapon;
import format.ColorStyle;
import format.swing_comp.SwingComp;
import format.swing_comp.SwingPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public abstract class Combatant {

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
        Main.logAction();
        return inspiration > 2;
    }

    /**
     * Deals the param damage. Logs the combatant as unconscious if hp is 0.
     *
     * @param damage The input amount of damage
     */
    public void damage(int damage) {
        hpCurrent = Math.max(0, hpCurrent - damage);
        if (hpCurrent == 0)
            lifeStatus.setUnconscious();
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
        if (!lifeStatus.isConscious())
            return Color.BLACK;
        if (isEnemy())
            return ColorStyle.ENEMY.getColor();

        return ColorStyle.getPercentColor(hpCurrent, hpMax);
    }

    public String getHealthBarString() {
        return switch (lifeStatus.status()) {
            case ALIVE -> {
                if (hpCurrent == 0)
                    yield "Alive but down for the count";
                else if (isEnemy())
                    yield "?";
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

    public JProgressBar getHealthBarOrSafeVersion() {
        JProgressBar safeVersion = SwingComp.progressBar(0, hpMax, hpCurrent, SwingConstants.HORIZONTAL)
                .round()
                .build();

        return Objects.requireNonNullElse(healthBar, safeVersion);
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
        JPanel panel = SwingPane.panel()
                .collectIf(isPoisoned, SwingComp.label("\uD83D\uDC80").withFont(SwingComp.SUB_HEADER))
                .collectIf(!hexedByList.isEmpty(), SwingComp.label("\uD83C\uDF00").withFont(SwingComp.SUB_HEADER))
                .build();

        panel.setVisible(panel.getComponents().length > 0);
        return panel;
    }

    @NotNull
    private JLabel getOptionLabel() {
        String text = "Inspiration Used: " + inspiration + "/2";
        if (!lifeStatus.isConscious())
            text = "Not in Fighting Condition";

        Color foreground = new Color(163, 163, 163);
        if (!lifeStatus.isConscious() || inspiration > 2)
            foreground = ColorStyle.ORANGE_ISH_RED.getColor();

        return SwingComp.label(text)
                .withFont(SwingComp.SUB_HEADER)
                .withForeground(foreground)
                .withEmptyBorder(4)
                .visibleIf(!isEnemy())
                .build();
    }

    @Override
    public String toString() {
        return name;
    }

    public JPanel toPanel() {
        Color accentColor = switch (this) {
            case PC ignored -> ColorStyle.PARTY.getColor();
            case NPC npc when npc.isEnemy() -> ColorStyle.ENEMY.getColor();
            default -> ColorStyle.NPC.getColor();
        };

        JPanel panel = SwingPane.panel()
                .withLayout(SwingPane.VERTICAL_BOX)
                .withHighlight(accentColor, SwingComp.LEFT)
                .build();

        SwingComp.label(name)
                .withFont(SwingComp.BOLD, SwingComp.TITLE)
                .withEmptyBorder(10)
                .in(panel);

        SwingComp.label("Initiative: " + initiative)
                .withFont(SwingComp.SUB_HEADER)
                .in(panel);

        SwingComp.modifiable(getOptionLabel()).in(panel);
        SwingComp.modifiable(getEffectListPanel()).in(panel);

        SwingComp.cloned(getHealthBarOrSafeVersion())
                .withEmptyBorder(10)
                .applied(b -> b.setStringPainted(false))
                .in(panel);

        return panel;
    }

    public ArrayList<String> toTxt() {
        return null;
    }

    public String name() {
        return name;
    }

}