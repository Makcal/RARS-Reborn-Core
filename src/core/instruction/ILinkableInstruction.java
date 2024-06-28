package core.instruction;

public interface ILinkableInstruction extends IInstruction {
    void link(String label, long address);
}
