package combat_menu.popup;

import __main.Main;
import __main.manager.EncounterManager;
import combat_object.combatant.PC;
import format.ColorStyles;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import txt_input.CampaignWriter;
import util.Message;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;

import static util.Message.confirmIf;
import static util.Message.template;

public class CombatEndPopup extends JDialog {

    public static void run(boolean isVictory) {
        Main.getCombatMenu().dispose();
        new CombatEndPopup(isVictory).setVisible(true);
    }

    private CombatEndPopup(boolean isVictory) {
        setTitle(isVictory ? "Victory" : "Defeat");
        setModal(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(Main.getAppIcon().getImage());
        getContentPane().setBackground(ColorStyles.BACKGROUND);
        setLayout(new BorderLayout());
        getRootPane().setBorder(BorderFactory.createLineBorder(ColorStyles.TRACK, 1));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        topBar.setBackground(ColorStyles.BG_DARK);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ColorStyles.TRACK));
        add(topBar, BorderLayout.NORTH);

        JLabel titleLabel = new JLabel(isVictory ? "VICTORY" : "DEFEAT");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 16f));
        titleLabel.setForeground(isVictory ? ColorStyles.HEALTHY : ColorStyles.CRITICAL);
        topBar.add(titleLabel);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBackground(ColorStyles.BACKGROUND);
        center.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(center, BorderLayout.CENTER);

        JLabel messageLabel = getVictoryMessage(isVictory);
        center.add(messageLabel);

        center.add(Box.createRigidArea(new Dimension(0, 24)));

        JLabel optionsLabel = new JLabel("OPTIONS");
        optionsLabel.setFont(optionsLabel.getFont().deriveFont(Font.BOLD, 10f));
        optionsLabel.setForeground(ColorStyles.TEXT_MUTED);
        optionsLabel.setBorder(new EmptyBorder(0, 0, 8, 0));
        optionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(optionsLabel);

        JButton btnLevelUp = createActionButton("Level Up the Party", ColorStyles.SUCCESS);
        btnLevelUp.addActionListener(e -> levelUp(btnLevelUp));

        JButton btnDownload = createActionButton("Download Updated .txt File", ColorStyles.TRACK);
        btnDownload.addActionListener(e -> download());

        JButton btnQuit = createActionButton("Quit Program", ColorStyles.CRITICAL);
        btnQuit.addActionListener(e -> quit("quit"));

        center.add(btnLevelUp);
        center.add(Box.createRigidArea(new Dimension(0, 10)));
        center.add(btnDownload);
        center.add(Box.createRigidArea(new Dimension(0, 10)));
        center.add(btnQuit);

        pack();
        setLocationRelativeTo(null);
    }

    @NotNull
    private static JLabel getVictoryMessage(boolean isVictory) {
        String msg = isVictory ? "Victory! You have won this combat." :
                "You have been defeated. You were " + EncounterManager.getEncounter().percentToVictory() + " of the way to victory.";

        JLabel messageLabel = new JLabel("<html><p style='width: 250px;'>" + msg + "</p></html>");
        messageLabel.setFont(messageLabel.getFont().deriveFont(Font.PLAIN, 14f));
        messageLabel.setForeground(ColorStyles.TEXT_PRIMARY);
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return messageLabel;
    }

    private JButton createActionButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setFont(button.getFont().deriveFont(Font.PLAIN, 13f));
        button.setBackground(bg);
        button.setForeground(ColorStyles.TEXT_PRIMARY);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return button;
    }

    private void levelUp(JButton button) {
        EncounterManager.getParty().forEach(PC::levelUp);

        String message = "Level up successful! As of " + Main.VERSION + ", only proficiency bonuses and hp are handled internally. " +
                "All other changes (stats, etc.) need to be manually entered in the Campaign Creator " +
                "for now. If you buy Braden a Red Bull he might fix that :P";
        template(message);

        button.setEnabled(false);
        button.setText("Party Level Increased");
        button.setBackground(ColorStyles.TRACK);
        button.setForeground(ColorStyles.TEXT_MUTED);
    }

    private void download() {
        URL savedFile = CampaignWriter.ofFullCampaign().getUrl("Campaign Post Encounter", true);

        if (savedFile != null)
            template("Successfully saved to Downloads");
        else
            System.err.println("Failed to save the campaign file.");
    }

    public static void quit(@MagicConstant(stringValues = {"quit", "restart"}) String mode) {
        if (confirmIf(mode + " and lose all progress") == JOptionPane.OK_OPTION) {
            Message.template("Goodbye! Thanks for playing :)");

            if (mode.equals("quit"))
                System.exit(0);
            else
                Main.clearAllAndShowUploadMenu();
        }
    }
}