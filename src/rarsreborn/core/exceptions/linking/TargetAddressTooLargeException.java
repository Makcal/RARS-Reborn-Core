package rarsreborn.core.exceptions.linking;

public class TargetAddressTooLargeException extends LinkingException {
    public TargetAddressTooLargeException(String label) {
        super("Target address for label \"" + label + "\" is too large");
    }

    public TargetAddressTooLargeException(long address) {
        super("Target address 0x%x is too large".formatted(address));
    }

    public TargetAddressTooLargeException(String label, Throwable cause) {
        super("Target address for label \"" + label + "\" is too large", cause);
    }
}
