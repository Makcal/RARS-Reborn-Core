package rarsreborn.core.exceptions.execution;

public class IllegalRegisterException extends ExecutionException {
    public IllegalRegisterException(int register) {
        super("Register not found: " + register);
    }

    public IllegalRegisterException(String register) {
        super("Register not found: " + register);
    }
}
