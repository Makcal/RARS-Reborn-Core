package rarsreborn.core.exceptions.linking;

public class LabelNotFoundException extends LinkingException {
    public LabelNotFoundException(String label) {
        super("Label \"" + label + "\" is not found");
    }
}
