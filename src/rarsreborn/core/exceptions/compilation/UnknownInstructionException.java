package rarsreborn.core.exceptions.compilation;

public class UnknownInstructionException extends CompilationException {
    public UnknownInstructionException(String instructionName) {
        super("Instruction " + instructionName + " is unknown");
    }
}
