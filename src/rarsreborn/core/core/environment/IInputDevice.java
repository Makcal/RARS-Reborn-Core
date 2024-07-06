package rarsreborn.core.core.environment;

public interface IInputDevice {
    byte[] requestInput(int count);

    String requestString(int count);
}
