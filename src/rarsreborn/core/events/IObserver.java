package rarsreborn.core.events;

public interface IObserver<TEvent> {
    void update(TEvent event);
}
