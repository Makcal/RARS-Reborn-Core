package rarsreborn.core.core.program;

/*
 * Use `extra` to specify the offset of the second instruction if needed
 */
public record LinkRequest(String label, Byte extra) {
    public LinkRequest(String label) {
        this(label, null);
    }
}
