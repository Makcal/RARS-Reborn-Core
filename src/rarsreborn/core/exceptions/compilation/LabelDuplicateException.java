package rarsreborn.core.exceptions.compilation;

public class LabelDuplicateException extends CompilationException {
    public LabelDuplicateException(String label) {
        super("Duplicate label: " + label);
    }
}
