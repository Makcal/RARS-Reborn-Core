package core.instruction;

import exceptions.execution.ExecutionException;

public interface IInstructionHandler<T extends IInstruction> {
    void handle(T instruction) throws ExecutionException;
}
