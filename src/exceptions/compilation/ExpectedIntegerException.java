package exceptions.compilation;

public class ExpectedIntegerException extends CompilationException {
    public ExpectedIntegerException(String argument) {
        super("Expected integer literal. Given: " + argument);
    }
}
