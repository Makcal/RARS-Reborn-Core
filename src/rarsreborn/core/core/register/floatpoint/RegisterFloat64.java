package rarsreborn.core.core.register.floatpoint;

import rarsreborn.core.core.register.IRegister;
import rarsreborn.core.event.IObservable;
import rarsreborn.core.event.IObserver;
import rarsreborn.core.event.ObservableImplementation;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class RegisterFloat64 implements IRegister, IObservable {
    private final int number;
    private final String name;
    private final ByteBuffer buffer;

    private final ObservableImplementation observableImplementation = new ObservableImplementation();

    public RegisterFloat64(int number, String name) {
        this.number = number;
        this.name = name;
        buffer = ByteBuffer.allocate(8);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    public RegisterFloat64(int number, String name, float value) {
        this(number, name);
        buffer.putFloat(0, value);
    }

    public RegisterFloat64(int number, String name, double value) {
        this(number, name);
        buffer.putDouble(0, value);
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNumericName() {
        return "f" + getNumber();
    }

    public float getFloat() {
        return buffer.getFloat(0);
    }

    public double getDouble() {
        return buffer.getDouble(0);
    }

    public int getInt() {
        return buffer.getInt(0);
    }

    public long getLong() {
        return buffer.getLong(0);
    }

    public void setFloat(float value) {
        float oldFloatValue = getFloat();
        double oldDoubleValue = getDouble();
        setFloatSilently(value);
        notifyObservers(new RegisterFloat64ChangeEvent(this, oldFloatValue, oldDoubleValue, value, getDouble()));
    }

    public void setFloatSilently(float value) {
        buffer.putFloat(0, value);
    }

    public void setDouble(double value) {
        float oldFloatValue = getFloat();
        double oldDoubleValue = getDouble();
        setDoubleSilently(value);
        notifyObservers(new RegisterFloat64ChangeEvent(this, oldFloatValue, oldDoubleValue, getFloat(), value));
    }

    public void setDoubleSilently(double value) {
        buffer.putDouble(0, value);
    }

    public void setInt(int value) {
        float oldFloatValue = getFloat();
        double oldDoubleValue = getDouble();
        setIntSilently(value);
        notifyObservers(new RegisterFloat64ChangeEvent(this, oldFloatValue, oldDoubleValue, getFloat(), value));
    }

    public void setIntSilently(int value) {
        buffer.putInt(0, value);
    }

    public void setLong(long value) {
        float oldFloatValue = getFloat();
        double oldDoubleValue = getDouble();
        setLongSilently(value);
        notifyObservers(new RegisterFloat64ChangeEvent(this, oldFloatValue, oldDoubleValue, getFloat(), value));
    }

    public void setLongSilently(long value) {
        buffer.putLong(0, value);
    }

    @Override
    public void reset() {
        buffer.putDouble(0);
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
