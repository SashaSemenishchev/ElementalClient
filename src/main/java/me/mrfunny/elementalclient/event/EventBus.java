package me.mrfunny.elementalclient.event;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Consumer;

/**
 * Java is better at reflection, so EventBus is written in Java, however it supports Kotlin listeners
 */
public class EventBus {
    private final Map<Class<?>, List<RegisteredListener>> eventListeners = new HashMap<>();
    private final Map<Object, List<Class<?>>> eventSubscribers = new HashMap<>();
    public void registerEvent(Class<? extends Event> clazz) {
        eventListeners.putIfAbsent(clazz, new ArrayList<>(3));
    }

    public void callEvent(Event event) {
        List<RegisteredListener> listeners = eventListeners.get(event.getClass());
        if(listeners == null) return;
        for(RegisteredListener listener : listeners) {
            listener.getExecutor().accept(event);
        }
    }

    public void unregisterListener(Object listener) {
        List<Class<?>> subscribedTo = eventSubscribers.remove(listener);
        if(subscribedTo == null) return;
        for(Class<?> aClass : subscribedTo) {
            List<RegisteredListener> registeredListeners = eventListeners.get(aClass);
            if(registeredListeners == null) continue;
            registeredListeners.removeIf(registered -> registered.getOwner() == listener);
        }
    }

    /**
     * Kotlin "object"s can't be listeners due to arbitrary static field generation. Only classes
     * @param listener The listening object with EventLinks
     */
    public void registerListener(Object listener) {
        Class<?> clazz = listener.getClass();
        System.out.println("Registering " + clazz + " as listener");
        boolean kotlinCitizen = false;
        for(Annotation annotation : clazz.getAnnotations()) {
            if(!annotation.annotationType().getName().equals("kotlin.Metadata")) continue;
            kotlinCitizen = true;
            break;
        }
        boolean illegal = false;
        for(Field declaredField : clazz.getDeclaredFields()) {
            declaredField.setAccessible(true);
            if(declaredField.getName().equals("INSTANCE")
                && Modifier.isStatic(declaredField.getModifiers())
                && kotlinCitizen && !illegal
            ) {
                illegal = true;
                continue;
            }

            EventLink annotation = declaredField.getAnnotation(EventLink.class);
            if(annotation == null) continue;
            if(illegal) {
                throw new IllegalArgumentException("Kotlin Objects are not supported as event listeners. Only classes: " + listener.getClass());
            }
            Type eventType = ((ParameterizedType) (declaredField.getGenericType())).getActualTypeArguments()[0];
            Class<?> eventClass;
            try {
                eventClass = Class.forName(eventType.getTypeName());
            } catch(ClassNotFoundException e) {
                throw new IllegalArgumentException("Event class specified in EventLink in " + clazz.getName()
                    + " is not found: " + eventType.getTypeName());
            }

            try {
                registerEvent(eventClass.asSubclass(Event.class));
            } catch(ClassCastException e) {
                throw new IllegalArgumentException(eventClass.getName() + " is not a valid event to be registered!");
            }
            eventSubscribers.computeIfAbsent(listener, e -> new ArrayList<>()).add(eventClass);
            try {
                Consumer<Event> executor = (Consumer<Event>) declaredField.get(listener);
                List<RegisteredListener> listeners =
                    eventListeners.computeIfAbsent(eventClass, e -> new ArrayList<>());
                listeners.add(new RegisteredListener(listener, annotation, executor));
                listeners.sort(Comparator.comparingInt(r -> r.getLink().priority().ordinal()));
            } catch(IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
