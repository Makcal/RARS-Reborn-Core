package exceptions.compilation;

public class UnknownRegisterException extends CompilationException {
    public UnknownRegisterException(String registerName) {
        super("Unknown register " + registerName);
    }
}
