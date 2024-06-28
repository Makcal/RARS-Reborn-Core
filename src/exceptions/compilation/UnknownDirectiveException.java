package exceptions.compilation;

public class UnknownDirectiveException extends CompilationException {
    public UnknownDirectiveException(String directive) {
        super("Unknown directive: " + directive);
    }
}
