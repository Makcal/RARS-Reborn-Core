package rarsreborn.core.exceptions.compilation;

public class SyntaxErrorException extends CompilationException {
    public SyntaxErrorException(String line) {
        super("Syntax error at: " + line);
    }

    public SyntaxErrorException(String line, Throwable cause) {
        super("Syntax error at: " + line, cause);
    }
}
