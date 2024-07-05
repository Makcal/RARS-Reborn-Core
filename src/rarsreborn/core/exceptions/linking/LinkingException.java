package rarsreborn.core.exceptions.linking;

import rarsreborn.core.exceptions.CoreException;

public class LinkingException extends CoreException {
    public LinkingException() {}

    public LinkingException(String message) {
        super(message);
    }

    public LinkingException(String message, Throwable cause) {
        super(message, cause);
    }
}
