package rarsreborn.core.event;

public interface IObserver<TEvent> {
    void update(TEvent event);
}
