package combat_menu;

import __main.manager.InspirationManager;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;
import swing.custom_component.AnimatedBar;
import swing.swing_comp.SwingComp;
import swing.swing_comp.SwingPane;
import util.Message;

import javax.swing.*;
import java.awt.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@NoArgsConstructor(staticName = "newInstance", force = true)
public class InspirationBar extends JPanel implements InspirationManager.Listener {

    AnimatedBar bar = new AnimatedBar();

    {
        InspirationManager.MANAGER.addListener(this);

        SwingPane.fluent(this).arrangedAs(SwingPane.BORDER, 10, 0)
                .withPreferredSize(0, 40);

        SwingComp.label("Excess Inspiration 1d4 Points", ColorStyles.TEXT_MUTED).in(this, BorderLayout.WEST);

        add(bar, BorderLayout.CENTER);
    }

    @Override
    public void onCountChanged(int used, int max) {
    }

    @Override
    public void onBarChanged(int total, int max) {
        SwingUtilities.invokeLater(() -> bar.animateTo((float) total / max));
    }

    @Override
    public void onBarReset() {
        SwingUtilities.invokeLater(() -> {
            bar.animateTo(1f, () -> bar.animateTo(0f));
            Message.template("Excess inspiration pool has reached 10. DM, do with this what you wish! Spooky");
        });
    }


}