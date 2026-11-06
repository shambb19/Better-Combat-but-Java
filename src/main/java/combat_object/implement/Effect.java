package combat_object.implement;

import combat_object.combatant.Combatant;
import lombok.*;
import lombok.experimental.*;
import swing.builder.ComponentBuilder;
import util.StringUtil;

import javax.swing.*;
import java.awt.*;

import static swing.builder.Components.*;
import static swing.builder.PanelBuilder.*;

@AllArgsConstructor
public enum Effect {

    // pre 4.3.1
    HEAL_BLOCK(Colors.purple("..target.. is unable to recover hit points until the effect expires")),
    POISON(Colors.green("..attacker.. has disadvantage on all attack rolls")),
    ILLUSION(Colors.purple("..target.. now believes an illusion of ..attacker..'s choice. Spooky")),
    ADVANTAGE(Colors.amber("..attacker.. can take advantage (unpunished) if they attack ..target.. again this turn")),
    BONUS_DAMAGE(Colors.amber("..target.. is taking bonus 1d6 damage from ..attacker.. for the rest of combat")),
    HALF_DAMAGE(Colors.blue("On a failed attack, this spell still deals half damage")),

    // released 4.4.0
    AUTO_HIT(Colors.blue("This spell bypasses AC and saving throws; ..target.. takes the damage automatically.")),
    BLIND(Colors.purple("..target.. cannot see and automatically fails any ability check that requires sight.")),
    DAMAGE_OVER_TIME(Colors.red("..target.. takes ongoing damage on subsequent turns.")), // todo implement
    DIFFICULT_TERRAIN(Colors.green("The area around ..target.. becomes difficult terrain, costing extra movement.")),
    ATTACK_DISADVANTAGE(Colors.amber("..target.. has disadvantage on their next attack roll.")),
    FORCED_MOVEMENT(Colors.red("..target.. is forced to move away or in a random direction.")),
    FRIGHTENED(Colors.purple("..target.. has disadvantage on all rolls while ..attacker.. can see them.")),
    LIFE_DRAIN(Colors.red("..attacker.. regains hit points equal to half the damage dealt to ..target..")),
    PENALTY_SAVE(Colors.purple("..target.. must subtract 1d4 from their next saving throw")),
    PRONE(Colors.amber("..target.. is knocked prone and must spend half their next turn to get up")),
    PULLED(Colors.blue("..target.. is pulled 10 feet towards ..attacker..")),
    RESTRAINED(Colors.green("..target.. cannot move, so attack rolls against them have advantage")),
    TRACKED(Colors.green("..attacker.. knows the exact location of ..target.., so ..target.. cannot hide")),
    FULL_HP_OPTION(Colors.blue("Deals a d12 instead of a d8 if ..target.. is missing any hit points")),

    // released 4.5.0
    BANISHED(Colors.purple("..target.. is banished to another plane of existence if hp <= 50")),
    PENALTY_ATTACK(Colors.amber("..target.. must subtract 1d6 from their attack rolls and ability checks")),
    RANDOM_ACTION(Colors.purple("..target.. suffers unpredictable effects each turn based on a d10 roll")),
    STAT_DROP(Colors.red("..target.. has their Intelligence and Charisma scores dropped to 1")),
    STUNNED(Colors.purple("..target.. is incapacitated, can't move, and automatically fails Str and Dex saves")),

    // released 5.0.0
    DISADVANTAGE(Colors.red("..attacker.. is injured and must roll with disadvantage on this attack.")),

    NONE(null);

    private final NoticeComponents noticeComponents;

    /**
     * Returns the notice JPanel associated with this Effect, which is displayed
     * in the CombatMenu action panel when its information is relevant to the user
     * @param on the Combatant object on which this effect is
     * @param by the Combatant object that dealt this effect
     * @return the notice JPanel
     */
    public NoticePanel noticePanel(Combatant on, Combatant by) {
        return new NoticePanel(this, on, by);
    }

    /**
     * The screen-appropriate name String for this effect, which is just the enum name
     * capitalized and with underscores replaced with spaces
     * @return the adjusted name String
     */
    public String getOfficialName() {
        String withSpaces = name().replace("_", " ");
        return StringUtil.capitalized(withSpaces);
    }

    @Override public String toString() {
        return name();
    }

    @ExtensionMethod(StringUtil.class)
    public static class NoticePanel extends JPanel {
        public NoticePanel(Effect effect, Combatant on, Combatant by) {
            if (effect.noticeComponents == null)
                throw new IllegalStateException("Effect " + effect.name() + " does not have an associated notice.");

            NoticeComponents components = effect.noticeComponents;

            String subtitle = components.subtitle.infoString(by, on);

            modifyBasicPanel(this, BORDER, 15, 0, BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 4, 0, 0, effect.noticeComponents.accent),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
            ));
            modifying(this).fixedHeight(60).alignmentX(LEFT_ALIGNMENT).build().validated();

            JPanel textCol = createPanel(VERTICAL_BOX).build().putIn(this, BorderLayout.CENTER);

            JLabel titleLabel = ComponentBuilder.createLabel(effect.getOfficialName())
                    .fontBuilder(FontBuilder.of(Font.BOLD, 12f, components.foreground))
                    .alignmentX(LEFT_ALIGNMENT).build().get();

            JLabel subLabel = ComponentBuilder.createLabel(subtitle)
                    .fontBuilder(FontBuilder.of(Font.PLAIN, 11f, components.foregroundDim))
                    .alignmentX(LEFT_ALIGNMENT).build().get();

            addConstraintlessComponentsTo(textCol,
                    glue(), titleLabel, spacer(0, 2), subLabel, glue()
            );
        }
    }

    @ExtensionMethod(StringUtil.class)
    static final class Colors {
        static final Color BG_PURPLE = new Color(0x22, 0x1E, 0x2E),
                FG_PURPLE = new Color(0xAF, 0xA9, 0xEC),
                FG_PURPLE_DIM = new Color(0x7F, 0x77, 0xDD),
                BORDER_PURPLE = new Color(0x53, 0x4A, 0xB7),

        BG_AMBER = new Color(0x26, 0x20, 0x14),
                FG_AMBER = new Color(0xEF, 0x9F, 0x27),
                FG_AMBER_DIM = new Color(0xBA, 0x75, 0x17),
                BORDER_AMBER = new Color(0xBA, 0x75, 0x17),

        BG_BLUE = new Color(0x1A, 0x22, 0x2E),
                FG_BLUE = new Color(0x94, 0xC9, 0xFF),
                FG_BLUE_DIM = new Color(0x60, 0x8A, 0xAF),
                BORDER_BLUE = new Color(0x3A, 0x86, 0xFF),

        BG_RED = new Color(0x2E, 0x1A, 0x1A),
                FG_RED = new Color(0xFF, 0x94, 0x94),
                FG_RED_DIM = new Color(0xAF, 0x60, 0x60),
                BORDER_RED = new Color(0xFF, 0x3A, 0x3A),

        BG_GREEN = new Color(0x1A, 0x2A, 0x1E),
                FG_GREEN = new Color(0x94, 0xFF, 0xAB),
                FG_GREEN_DIM = new Color(0x60, 0xAF, 0x70),
                BORDER_GREEN = new Color(0x3A, 0xFF, 0x55);

        static NoticeComponents purple(String subtitle) {
            return new NoticeComponents(BG_PURPLE, BORDER_PURPLE, FG_PURPLE, FG_PURPLE_DIM, subtitle);
        }

        static NoticeComponents amber(String subtitle) {
            return new NoticeComponents(BG_AMBER, BORDER_AMBER, FG_AMBER, FG_AMBER_DIM, subtitle);
        }

        static NoticeComponents blue(String subtitle) {
            return new NoticeComponents(BG_BLUE, BORDER_BLUE, FG_BLUE, FG_BLUE_DIM, subtitle);
        }

        static NoticeComponents red(String subtitle) {
            return new NoticeComponents(BG_RED, BORDER_RED, FG_RED, FG_RED_DIM, subtitle);
        }

        static NoticeComponents green(String subtitle) {
            return new NoticeComponents(BG_GREEN, BORDER_GREEN, FG_GREEN, FG_GREEN_DIM, subtitle);
        }
    }

    record NoticeComponents(
            Color background, Color accent, Color foreground, Color foregroundDim, String subtitle) {}
}