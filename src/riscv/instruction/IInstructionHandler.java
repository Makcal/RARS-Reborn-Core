package riscv.instruction;

public interface IInstructionHandler<T extends IInstruction> {
    void handle(T instruction);
}
