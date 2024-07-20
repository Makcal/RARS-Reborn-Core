package rarsreborn.core.core.program;

public interface IExecutable {
    byte[] getData();

    byte[] getText();

    /**
     * @return The address of the entry point of the program relative to the start of the text segment.
     */
    long getEntryPointOffset();
}
