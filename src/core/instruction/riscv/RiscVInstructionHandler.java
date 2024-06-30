package core.instruction.riscv;

import core.instruction.IInstruction;
import core.instruction.IInstructionHandler;
import core.memory.IMemory;

public abstract class RiscVInstructionHandler<TInstruction extends IInstruction>
        implements IInstructionHandler<TInstruction> {
    protected IMemory memory;

    public void attachMemory(IMemory memory) {
        this.memory = memory;
    }
}
