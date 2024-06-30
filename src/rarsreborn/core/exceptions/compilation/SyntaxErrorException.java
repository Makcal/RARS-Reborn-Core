package rarsreborn.core.exceptions.compilation;

public class SyntaxErrorException extends CompilationException {
    public SyntaxErrorException(String line) {
        super("Syntax error at: " + line);
    }
}
