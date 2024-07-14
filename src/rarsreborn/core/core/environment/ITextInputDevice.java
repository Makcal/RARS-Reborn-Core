package rarsreborn.core.core.environment;

import java.nio.charset.StandardCharsets;

public interface ITextInputDevice extends IInputDevice {
    @Override
    default byte[] requestInput(int count) {
        return requestString(count).getBytes(StandardCharsets.UTF_8);
    }

    String requestString(int count);

    int requestInt();

    byte requestChar();
}
