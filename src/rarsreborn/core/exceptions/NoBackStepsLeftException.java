package rarsreborn.core.exceptions;

public class NoBackStepsLeftException extends CoreException {
    public NoBackStepsLeftException() {
        super("Can not revert more steps");
    }
}
