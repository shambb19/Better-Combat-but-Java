package __main.manager;

import __main.Main;
import combat_menu.action_panel.ActionPanel;

import java.util.ArrayList;
import java.util.List;

public class InspirationManager {

    public static final InspirationManager MANAGER = new InspirationManager();
    public static ActionPanel ACTION_PANEL;

    private static final int FREE_USES = 2;
    private static final int BAR_MAX = 10;
    private static final int EXCESS_DIE = 4;
    private final List<Listener> listeners = new ArrayList<>();
    private int usedCount = 0;
    private int barTotal = 0;

    public static void init(ActionPanel actionPanel) {
        ACTION_PANEL = actionPanel;
    }

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
            ACTION_PANEL.returnToButtons();

        Main.logAction();
    }

    // ── Public actions ────────────────────────────────────────────────────────

    private void fireCountChanged() {
        listeners.forEach(l -> l.onCountChanged(usedCount, FREE_USES));
    }

    private void fireExcessPanel() {
        ACTION_PANEL.switchTo(ActionPanel.INSPIRATION_OPTION);
    }

    /**
     * Called when player clicks a 1d4 button (value 1–4).
     */
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

        ACTION_PANEL.returnToButtons();
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    private void fireBarChanged() {
        listeners.forEach(l -> l.onBarChanged(barTotal, BAR_MAX));
    }

    private void fireBarReset() {
        listeners.forEach(Listener::onBarReset);
    }

    public interface Listener {
        void onCountChanged(int used, int max);

        void onBarChanged(int total, int max);

        void onBarReset();
    }
}