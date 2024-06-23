package exceptions.execution;

import exceptions.CoreException;

public abstract class ExecutionException extends CoreException {
    public ExecutionException() {}

    public ExecutionException(String message) {
        super(message);
    }
}
