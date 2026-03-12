package combat_menu.listener;

import campaign_creator.ColoredTxtDisplay;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class FieldEditListener extends KeyAdapter {

    private final ColoredTxtDisplay root;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledTask;

    public FieldEditListener(ColoredTxtDisplay root) {
        this.root = root;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        resetTimer();
    }

    private synchronized void resetTimer() {
        if (scheduledTask != null && !scheduledTask.isDone()) {
            scheduledTask.cancel(false);
        }
        scheduledTask = scheduler.schedule(this::performAction, 1, TimeUnit.SECONDS);
    }

    private void performAction() {
        SwingUtilities.invokeLater(root::addLines);
    }

    // Call this when the window/app closes to prevent memory leaks
    public void cleanup() {
        scheduler.shutdownNow();
    }
}