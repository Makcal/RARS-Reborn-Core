package rarsreborn.core.core.environment.riscv;

import rarsreborn.core.core.environment.ISystemCall;
import rarsreborn.core.core.memory.IMemory;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.compilation.UnknownRegisterException;

public abstract class RiscVSystemCall implements ISystemCall {
    protected Register32File registers;
    protected Register32 programCounter;
    protected IMemory memory;

    public RiscVSystemCall() {
        this(null, null, null);
    }

    public RiscVSystemCall(Register32File registers, IMemory memory, Register32 programCounter) {
        this.registers = registers;
        this.memory = memory;
        this.programCounter = programCounter;
    }

    public void setRegisters(Register32File registers) {
        this.registers = registers;
    }

    public void setMemory(IMemory memory) {
        this.memory = memory;
    }

    public void setProgramCounter(Register32 programCounter) {
        this.programCounter = programCounter;
    }

    protected int getRegisterValue(int number) {
        try {
            return registers.getRegisterByNumber(number).getValue();
        } catch (UnknownRegisterException e) {
            throw new RuntimeException(e);
        }
    }
}
