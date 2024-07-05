package rarsreborn.core.exceptions.execution;

public class UnknownSystemCallException extends ExecutionException {
    public UnknownSystemCallException(int number) {
        super("Unknown system call number: " + number);
    }
}
