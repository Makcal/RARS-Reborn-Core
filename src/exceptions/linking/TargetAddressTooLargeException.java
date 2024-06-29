package exceptions.linking;

public class TargetAddressTooLargeException extends LinkingException {
    public TargetAddressTooLargeException(String label) {
        super("Target address for label \"" + label + "\" is too large");
    }
}
