package exceptions.compilation;

import exceptions.CoreException;

public abstract class CompilationException extends CoreException {
    public CompilationException() {}

    public CompilationException(String message) {
        super(message);
    }
}
