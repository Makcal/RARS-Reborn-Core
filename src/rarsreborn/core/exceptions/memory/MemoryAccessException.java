package rarsreborn.core.exceptions.memory;

import rarsreborn.core.exceptions.CoreException;

public abstract class MemoryAccessException extends CoreException {
    public MemoryAccessException() {
        super();
    }

    public MemoryAccessException(String message) {
        super(message);
    }
}
