package rarsreborn.core.core.instruction.riscv;

import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;

public abstract class RiscV32InstructionHandler<TInstruction extends IInstruction>
        extends RiscVInstructionHandler<TInstruction> {
    protected IRegisterFile<Register32> registerFile;
    protected Register32 programCounter;

    public void attachRegisters(IRegisterFile<Register32> registerFile) {
        this.registerFile = registerFile;
    }

    public void attachProgramCounter(Register32 programCounter) {
        this.programCounter = programCounter;
    }
}
