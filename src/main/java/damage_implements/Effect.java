package damage_implements;

import __main.EncounterInfo;
import character_info.combatant.Combatant;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public enum Effect {

    HEAL_BLOCK(Colors.purpleSet("Heal Block", "..target.. is unable to recover hit points until the effect expires")),
    POISON(Colors.purpleSet("Poisoned", "..target.. is afflicted by toxins and will take periodic damage")),
    ILLUSION(Colors.purpleSet("Illusion", "..target.. now believes an illusion of ..attacker..'s choice. spooky")),
    ADVANTAGE_SOON(Colors.amberSet("Advantage", "..attacker.. can take advantage (unpunished) if they attack ..target.. again this turn")),
    BONUS_DAMAGE(Colors.amberSet("Hex", "..target.. is taking bonus 1d6 damage from ..attacker.. for the rest of combat (handled internally)")),

    HALF_DAMAGE,
    SPLIT_ATTACK,
    NONE;

    private final NoticeComponents noticeComponents;

    Effect() {
        this.noticeComponents = null;
    }

    Effect(NoticeComponents noticeComponents) {
        this.noticeComponents = noticeComponents;
    }

    public JPanel noticePanel(Combatant target) {
        if (noticeComponents == null) {
            throw new IllegalStateException("Effect " + name() + " does not have an associated notice.");
        }

        noticeComponents.updateSubtitle(EncounterInfo.getCurrentCombatant(), target);
        return buildNoticeBanner();
    }

    private JPanel buildNoticeBanner() {
        JPanel notice = new JPanel(new BorderLayout(15, 0));
        notice.setBackground(noticeComponents.background());
        notice.setOpaque(true);
        notice.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, noticeComponents.accent()),
                new EmptyBorder(10, 12, 10, 12)));
        notice.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        notice.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel textCol = new JPanel();
        textCol.setLayout(new BoxLayout(textCol, BoxLayout.Y_AXIS));
        textCol.setOpaque(false);

        JLabel titleLabel = new JLabel(noticeComponents.title());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 12f));
        titleLabel.setForeground(noticeComponents.foreground());

        JLabel subLabel = new JLabel(noticeComponents.subtitle().toString());
        subLabel.setFont(subLabel.getFont().deriveFont(Font.PLAIN, 11f));
        subLabel.setForeground(noticeComponents.foregroundDim());

        textCol.add(Box.createVerticalGlue());
        textCol.add(titleLabel);
        textCol.add(Box.createRigidArea(new Dimension(0, 2)));
        textCol.add(subLabel);
        textCol.add(Box.createVerticalGlue());

        notice.add(textCol, BorderLayout.CENTER);
        return notice;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}

class Colors {
    public static final Color BG_PURPLE = new Color(0x22, 0x1E, 0x2E);
    public static final Color FG_PURPLE = new Color(0xAF, 0xA9, 0xEC);
    public static final Color FG_PURPLE_DIM = new Color(0x7F, 0x77, 0xDD);
    public static final Color BORDER_PURPLE = new Color(0x53, 0x4A, 0xB7);

    public static final Color BG_AMBER = new Color(0x26, 0x20, 0x14);
    public static final Color FG_AMBER = new Color(0xEF, 0x9F, 0x27);
    public static final Color FG_AMBER_DIM = new Color(0xBA, 0x75, 0x17);
    public static final Color BORDER_AMBER = new Color(0xBA, 0x75, 0x17);

    static NoticeComponents purpleSet(String title, String subtitle) {
        return new NoticeComponents(BG_PURPLE, BORDER_PURPLE, FG_PURPLE, FG_PURPLE_DIM, title, new StringBuilder(subtitle));
    }

    static NoticeComponents amberSet(String title, String subtitle) {
        return new NoticeComponents(BG_AMBER, BORDER_AMBER, FG_AMBER, FG_AMBER_DIM, title, new StringBuilder(subtitle));
    }
}

record NoticeComponents(Color background, Color accent, Color foreground, Color foregroundDim, String title,
                        StringBuilder subtitle) {
    void updateSubtitle(Combatant attacker, Combatant target) {
        final String ATTACKER = "..attacker..";
        final String TARGET = "..target..";

        replaceKey(ATTACKER, attacker);
        replaceKey(TARGET, target);
    }

    void replaceKey(final String key, Combatant combatant) {
        while (subtitle.toString().contains(key)) {
            int idxKey = subtitle.indexOf(key);
            int idxKeyEnd = idxKey + key.length();

            subtitle.replace(idxKey, idxKeyEnd, combatant.name());
        }
    }
}