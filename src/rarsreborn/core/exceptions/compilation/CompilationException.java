package rarsreborn.core.exceptions.compilation;

import rarsreborn.core.exceptions.CoreException;

public abstract class CompilationException extends CoreException {
    public CompilationException() {}

    public CompilationException(String message) {
        super(message);
    }
}
