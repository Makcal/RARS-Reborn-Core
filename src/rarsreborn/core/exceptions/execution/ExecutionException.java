package rarsreborn.core.exceptions.execution;

import rarsreborn.core.exceptions.CoreException;

public abstract class ExecutionException extends CoreException {
    public ExecutionException() {}

    public ExecutionException(String message) {
        super(message);
    }
}
