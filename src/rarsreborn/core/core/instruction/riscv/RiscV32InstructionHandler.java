package rarsreborn.core.core.instruction.riscv;

import rarsreborn.core.core.environment.IExecutionEnvironment;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;

public abstract class RiscV32InstructionHandler<TInstruction extends IInstruction>
        extends RiscVInstructionHandler<TInstruction> {
    protected IRegisterFile<Register32> registerFile;
    protected Register32 programCounter;
    protected IExecutionEnvironment executionEnvironment;

    public void attachRegisters(IRegisterFile<Register32> registerFile) {
        this.registerFile = registerFile;
    }

    public void attachProgramCounter(Register32 programCounter) {
        this.programCounter = programCounter;
    }

    public void attachExecutionEnvironment(IExecutionEnvironment executionEnvironment) {
        this.executionEnvironment = executionEnvironment;
    }
}