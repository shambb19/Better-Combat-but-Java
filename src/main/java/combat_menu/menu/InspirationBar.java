package combat_menu.menu;

import _manager.InspirationManager;
import lombok.*;
import swing.ColorStyles;
import swing.custom.AnimatedBar;
import swing.builder.ComponentBuilder;
import swing.builder.PanelBuilder;
import util.Message;

import javax.swing.*;

import java.awt.*;

@NoArgsConstructor
public class InspirationBar extends JPanel implements InspirationManager.Listener {

    private final AnimatedBar bar = new AnimatedBar();

    {
        InspirationManager.MANAGER.addListener(this);

        JLabel label = ComponentBuilder.createLabel("Excess Inspiration 1d4 Points")
                .border(BorderFactory.createEmptyBorder(0, 0, 0, 4))
                .foreground(ColorStyles.FG_MUTED).build().get();

        PanelBuilder.modifyingPanel(this).layoutType(PanelBuilder.BORDER).hgap(10)
                .hostedComponent(label, BorderLayout.WEST)
                .hostedComponent(bar, BorderLayout.CENTER)
                .preferredSize(new Dimension(0, 40)).build().validated();
    }

    @Override public void onCountChanged(int used, int max) {
    }

    @Override public void onBarChanged(int total, int max) {
        SwingUtilities.invokeLater(() -> bar.animateTo((float) total / max));
    }

    @Override public void onBarReset() {
        SwingUtilities.invokeLater(() -> {
            bar.animateTo(1f, () -> bar.animateTo(0f));
            Message.showAsInfoMessage("Excess inspiration pool has reached 10. DM, do with this what you wish! Spooky");
        });
    }
}