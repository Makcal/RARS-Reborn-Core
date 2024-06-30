package rarsreborn.core.exceptions.compilation;

public class UnexpectedDirectiveException extends CompilationException {
    public UnexpectedDirectiveException(String directive) {
        super("Unexpected directive: " + directive);
    }
}
