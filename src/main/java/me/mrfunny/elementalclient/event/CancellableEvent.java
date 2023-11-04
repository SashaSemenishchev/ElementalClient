package me.mrfunny.elementalclient.event;

public abstract class CancellableEvent extends Event {
    private boolean isCancelled = false;

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public boolean isCancelled() {
        return isCancelled;
    }
}
