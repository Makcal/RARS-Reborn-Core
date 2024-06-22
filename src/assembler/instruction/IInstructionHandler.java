package assembler.instruction;

public interface IInstructionHandler<T extends IInstruction> {
    void handle(T instruction);
}
