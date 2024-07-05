package rarsreborn.core.exceptions;

public class CoreException extends Exception {
    public CoreException() {}

    public CoreException(String message) {
        super(message);
    }

    public CoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
