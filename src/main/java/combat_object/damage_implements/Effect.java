package combat_object.damage_implements;

import __main.manager.EncounterManager;
import combat_object.combatant.Combatant;
import lombok.*;
import swing.swing_comp.SwingComp;
import swing.swing_comp.SwingPane;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

@AllArgsConstructor
public enum Effect {

    // pre 4.3.1
    HEAL_BLOCK(Colors.purple("Heal Block",
            "..target.. is unable to recover hit points until the effect expires")),
    POISON(Colors.green("Poisoned",
            "..attacker.. has disadvantage on all attack rolls")),
    ILLUSION(Colors.purple("Illusion",
            "..target.. now believes an illusion of ..attacker..'s choice. Spooky")),
    ADVANTAGE_SOON(Colors.amber("Advantage",
            "..attacker.. can take advantage (unpunished) if they attack ..target.. again this turn")),
    BONUS_DAMAGE(Colors.amber("Hex / Mark",
            "..target.. is taking bonus 1d6 damage from ..attacker.. for the rest of combat")),
    HALF_DAMAGE(Colors.blue("Half Damage",
            "This attack deals half damage on a failed attack/save (handled internally; enter full roll value)")),

    // released 4.4.0
    AUTO_HIT(Colors.blue("Auto Hit",
            "This spell bypasses armor class and saving throws. ..target.. takes the damage automatically.")),
    BLIND(Colors.purple("Blinded",
            "..target.. cannot see and automatically fails any ability check that requires sight.")),
    DAMAGE_OVER_TIME(Colors.red("Damage Over Time",
            "..target.. takes ongoing damage on subsequent turns.")),
    DIFFICULT_TERRAIN(Colors.green("Difficult Terrain",
            "The area around ..target.. becomes difficult terrain, costing extra movement.")),
    DISADVANTAGE_ATTACK(Colors.amber("Disadvantage",
            "..target.. has disadvantage on their next attack roll.")),
    FORCED_MOVE(Colors.red("Forced Movement",
            "..target.. is forced to move away or in a random direction.")),
    FRIGHTEN(Colors.purple("Frightened",
            "..target.. has disadvantage on ability checks and attack rolls while ..attacker.. is within line of sight.")),
    HEAL_SELF(Colors.red("Life Drain",
            "..attacker.. regains hit points equal to half the damage dealt to ..target..")),
    PENALTY_SAVE(Colors.purple("Mind Weakened",
            "..target.. must subtract 1d4 from their next saving throw")),
    PRONE(Colors.amber("Prone",
            "..target.. is knocked prone and must spend half their next turn to get up")),
    PULL(Colors.blue("Pulled",
            "..target.. is pulled 10 feet towards ..attacker..")),
    RESTRAIN(Colors.green("Restrained",
            "..target.. cannot move, so attack rolls against them have advantage")),
    TRACKING(Colors.green("Tracked",
            "..attacker.. knows the exact location of ..target.., so ..target.. cannot hide")),
    FULL_HP_OPTION(Colors.blue("Toll the Dead",
            "Deals a d12 instead of a d8 if ..target.. is missing any hit points")),

    // released 4.5.0
    BANISH(Colors.purple("Banished",
            "..target.. is banished to another plane of existence if reduced to 50 hit points or fewer")),
    CHARMED(Colors.purple("Charmed",
            "..target.. is magically compelled by ..attacker.. and cannot disobey. Kinky")),
    PENALTY_ATTACK(Colors.amber("Muddled Mind",
            "..target.. must subtract 1d6 from their attack rolls and ability checks")),
    RANDOM_ACTION(Colors.purple("Reality Broken",
            "..target.. suffers unpredictable effects each turn based on a d10 roll")),
    STAT_DROP(Colors.red("Mind Shattered",
            "..target.. has their Intelligence and Charisma scores dropped to 1")),
    STUNNED(Colors.purple("Stunned",
            "..target.. is incapacitated, can't move, and automatically fails Str and Dex saves")),

    NONE(null);

    private final NoticeComponents noticeComponents;

    public NoticePanel noticePanel(Combatant target) {
        return new NoticePanel(this, target);
    }

    public String getOfficialName() {
        return noticeComponents.title;
    }

    public static class NoticePanel extends JPanel {
        public NoticePanel(Effect effect, Combatant target) {
            if (effect.noticeComponents == null)
                throw new IllegalStateException("Effect " + effect.name() + " does not have an associated notice.");

            String subtitle = effect.noticeComponents.getSubtitle(EncounterManager.getCurrentCombatant(), target);

            SwingPane.fluent(this).arrangedAs(SwingPane.BORDER, 15, 0)
                    .withBackground(effect.noticeComponents.getBackground())
                    .withPaddedBorder(new MatteBorder(0, 4, 0, 0, effect.noticeComponents.getAccent()),
                            10, 12, 10, 12)
                    .withMaximumSize(Integer.MAX_VALUE, 60)
                    .onLeft();

            JPanel textCol = SwingPane.panelIn(this, BorderLayout.CENTER)
                    .arrangedAs(SwingPane.VERTICAL_BOX).transparent().component();

            JLabel titleLabel =
                    SwingComp.label(effect.noticeComponents.getTitle(), Font.BOLD, 12f, effect.noticeComponents.getForeground())
                            .component();

            JLabel subLabel = SwingComp.label(subtitle, Font.PLAIN, 11f, effect.noticeComponents.getForegroundDim())
                    .component();

            SwingPane.fluent(textCol).collect(
                    Box.createVerticalGlue(), titleLabel, SwingComp.spacer(0, 2),
                    subLabel, Box.createVerticalGlue()
            );
        }
    }

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

        static NoticeComponents purple(String title, String subtitle) {
            return new NoticeComponents(BG_PURPLE, BORDER_PURPLE, FG_PURPLE, FG_PURPLE_DIM, title, subtitle);
        }

        static NoticeComponents amber(String title, String subtitle) {
            return new NoticeComponents(BG_AMBER, BORDER_AMBER, FG_AMBER, FG_AMBER_DIM, title, subtitle);
        }

        static NoticeComponents blue(String title, String subtitle) {
            return new NoticeComponents(BG_BLUE, BORDER_BLUE, FG_BLUE, FG_BLUE_DIM, title, subtitle);
        }

        static NoticeComponents red(String title, String subtitle) {
            return new NoticeComponents(BG_RED, BORDER_RED, FG_RED, FG_RED_DIM, title, subtitle);
        }

        static NoticeComponents green(String title, String subtitle) {
            return new NoticeComponents(BG_GREEN, BORDER_GREEN, FG_GREEN, FG_GREEN_DIM, title, subtitle);
        }
    }

    @Value @RequiredArgsConstructor static class NoticeComponents {
        Color background, accent, foreground, foregroundDim;
        String title;
        String subtitle;

        String getSubtitle(Combatant attacker, Combatant target) {
            final String ATTACKER = "..attacker..";
            final String TARGET = "..target..";

            StringBuilder builder = new StringBuilder(subtitle);

            class Replacer {
                void replaceKey(final String key, Combatant combatant) {
                    while (builder.toString().contains(key)) {
                        int idxKey = subtitle.indexOf(key);
                        int idxKeyEnd = idxKey + key.length();
                        builder.replace(idxKey, idxKeyEnd, combatant.getName());
                    }
                }
            }
            Replacer r = new Replacer();

            r.replaceKey(ATTACKER, attacker);
            r.replaceKey(TARGET, target);

            return builder.toString();
        }
    }
}