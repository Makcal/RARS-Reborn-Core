package rarsreborn.core.core.instruction;

import rarsreborn.core.exceptions.execution.ExecutionException;

public interface IInstructionHandler<T extends IInstruction> {
    void handle(T instruction) throws ExecutionException;
}
