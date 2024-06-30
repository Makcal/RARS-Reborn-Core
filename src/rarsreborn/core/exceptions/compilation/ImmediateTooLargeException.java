package rarsreborn.core.exceptions.compilation;

public class ImmediateTooLargeException extends CompilationException {
    public ImmediateTooLargeException(long value) {
        super("Immediate " + value + " is too large");
    }
}
