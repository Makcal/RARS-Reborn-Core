package rarsreborn.core.core.instruction.riscv;

import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.IInstructionHandler;
import rarsreborn.core.core.memory.IMemory;

public abstract class RiscVInstructionHandler<TInstruction extends IInstruction>
        implements IInstructionHandler<TInstruction> {
    protected IMemory memory;

    public void attachMemory(IMemory memory) {
        this.memory = memory;
    }
}
