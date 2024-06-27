package exceptions.compilation;

public class ConstantTooLargeException extends CompilationException {
    public ConstantTooLargeException(long value) {
        super("Immediate " + value + " is too large");
    }
}
