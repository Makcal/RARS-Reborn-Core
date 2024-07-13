package rarsreborn.core.core.register;

import rarsreborn.core.event.IObservable;
import rarsreborn.core.event.IObserver;
import rarsreborn.core.event.ObservableImplementation;

public class Register32 implements IIntegerRegister, IObservable {
    private final int number;
    private final String name;
    private int value;

    private final ObservableImplementation observableImplementation = new ObservableImplementation();

    public Register32(int number, String name) {
        this(number, name, 0);
    }

    public Register32(int number, String name, int value) {
        this.number = number;
        this.name = name;
        this.value = value;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        int oldValue = this.value;
        setValueSilently(value);
        notifyObservers(new Register32ChangeEvent(this, oldValue, value));
    }

    public void setValueSilently(int value) {
        this.value = value;
    }

    @Override
    public void reset() {
        setValue(0);
    }

    @Override
    public <TEvent> void addObserver(Class<TEvent> eventClass, IObserver<TEvent> observer) {
        observableImplementation.addObserver(eventClass, observer);
    }

    @Override
    public <TEvent> void removeObserver(Class<TEvent> eventClass, IObserver<TEvent> observer) {
        observableImplementation.removeObserver(eventClass, observer);
    }

    @Override
    public <TEvent> void notifyObservers(TEvent event) {
        observableImplementation.notifyObservers(event);
    }
}
