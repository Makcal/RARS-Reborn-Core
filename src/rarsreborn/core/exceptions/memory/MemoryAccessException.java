package rarsreborn.core.exceptions.memory;

import rarsreborn.core.exceptions.execution.ExecutionException;

public abstract class MemoryAccessException extends ExecutionException {
    public MemoryAccessException() {
        super();
    }

    public MemoryAccessException(String message) {
        super(message);
    }
}
