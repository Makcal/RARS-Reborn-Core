package rarsreborn.core.core.environment;

import java.nio.charset.StandardCharsets;

public abstract class StringInputDevice implements IInputDevice {
    @Override
    public final byte[] requestInput(int count) {
        return requestString(count).getBytes(StandardCharsets.UTF_8);
    }
}
