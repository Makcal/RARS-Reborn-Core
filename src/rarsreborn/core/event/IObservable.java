package rarsreborn.core.event;

public interface IObservable {
    <TEvent> void addObserver(Class<TEvent> eventClass, IObserver<TEvent> observer);

    <TEvent> void removeObserver(Class<TEvent> eventClass, IObserver<TEvent> observer);

    <TEvent> void notifyObservers(TEvent event);
}