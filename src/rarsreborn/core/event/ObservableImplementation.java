package rarsreborn.core.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ObservableImplementation implements IObservable {
    protected final Map<Class<?>, HashSet<IObserver<?>>> observers = new HashMap<>();

    @Override
    public <TEvent> void addObserver(Class<TEvent> eventClass, IObserver<TEvent> observer) {
        if (observers.containsKey(eventClass)) {
            observers.get(eventClass).add(observer);
        } else {
            HashSet<IObserver<?>> set = new HashSet<>();
            set.add(observer);
            observers.put(eventClass, set);
        }
    }

    @Override
    public <TEvent> void removeObserver(Class<TEvent> eventClass, IObserver<TEvent> observer) {
        if (observers.containsKey(eventClass))
            observers.get(eventClass).remove(observer);
    }

    @Override
    public <TEvent> void notifyObservers(TEvent event) {
        if (!observers.containsKey(event.getClass()))
            return;
        for (IObserver<?> observer : observers.get(event.getClass())) {
            //noinspection unchecked
            ((IObserver<TEvent>) observer).update(event);
        }
    }
}
