package me.mrfunny.elementalclient.event;

public interface EventListener<T> {
    void call(T event);
}
