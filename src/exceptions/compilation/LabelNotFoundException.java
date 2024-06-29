package exceptions.compilation;

public class LabelNotFoundException extends CompilationException {
    public LabelNotFoundException(String label) {
        super("Label \"" + label + "\" is not found");
    }
}
