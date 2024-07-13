package rarsreborn.core.event;

public class ObservableImplementationStub implements IObservable {
    @Override
    public <TEvent> void addObserver(Class<TEvent> eventClass, IObserver<TEvent> observer) {}

    @Override
    public <TEvent> void removeObserver(Class<TEvent> eventClass, IObserver<TEvent> observer) {}

    @Override
    public <TEvent> void notifyObservers(TEvent event) {}
}