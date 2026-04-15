package __main.manager;

import __main.Main;
import combat_menu.action_panel.ActionPanel;
import lombok.*;
import lombok.experimental.*;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class InspirationManager {

    public static final InspirationManager MANAGER = new InspirationManager();

    static final int FREE_USES = 2;
    static final int BAR_MAX = 10;
    static final int EXCESS_DIE = 4;
    final List<Listener> listeners = new ArrayList<>();
    int usedCount = 0;
    int barTotal = 0;

    public void addListener(Listener l) {
        listeners.add(l);
    }

    public void useInspiration() {
        EncounterManager.getCurrentCombatant().useInspiration();

        usedCount++;
        fireCountChanged();
        if (usedCount > FREE_USES)
            fireExcessPanel();
        else
            getActionPanel().returnToButtons();

        Main.refreshUI();
    }

    private void fireCountChanged() {
        listeners.forEach(l -> l.onCountChanged(usedCount, FREE_USES));
    }

    private void fireExcessPanel() {
        getActionPanel().switchTo(ActionPanel.INSPIRATION_OPTION);
    }

    public void submitExcessRoll(int roll) {
        if (roll < 1 || roll > EXCESS_DIE)
            throw new IllegalArgumentException("Roll must be 1–" + EXCESS_DIE);

        barTotal += roll;
        fireBarChanged();
        fireExcessPanel();

        if (barTotal >= BAR_MAX) {
            barTotal = 0;
            fireBarReset();
            fireBarChanged();
        }

        getActionPanel().returnToButtons();
    }

    private void fireBarChanged() {
        listeners.forEach(l -> l.onBarChanged(barTotal, BAR_MAX));
    }

    private void fireBarReset() {
        listeners.forEach(Listener::onBarReset);
    }

    private ActionPanel getActionPanel() {
        return Main.getCombatMenu().getActionPanel();
    }

    public interface Listener {
        void onCountChanged(int used, int max);

        void onBarChanged(int total, int max);

        void onBarReset();
    }
}