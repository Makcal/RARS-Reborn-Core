package core.instruction.riscv;

import core.instruction.IInstruction;
import core.instruction.IInstructionHandler;
import core.memory.IMemory;
import core.register.IRegisterFile;
import core.register.Register32;

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
