package me.mrfunny.elementalclient.event;

import java.util.function.Consumer;

public class RegisteredListener {
    private final EventLink link;
    private final Consumer<Event> executor;
    private final Object owner;

    public RegisteredListener(Object owner, EventLink link, Consumer<Event> executor) {
        this.link = link;
        this.executor = executor;
        this.owner = owner;
    }

    public EventLink getLink() {
        return link;
    }

    public Consumer<Event> getExecutor() {
        return executor;
    }

    public Object getOwner() {
        return owner;
    }
}
