package rarsreborn.core.exceptions.execution;

public class IllegalInstructionException extends ExecutionException {
    public IllegalInstructionException(long address) {
        super("Could not decode an instruction at 0x%x".formatted(address));
    }

    public IllegalInstructionException(String message) {
        super(message);
    }
}
