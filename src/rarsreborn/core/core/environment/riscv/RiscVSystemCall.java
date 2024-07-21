package rarsreborn.core.core.environment.riscv;

import rarsreborn.core.core.environment.ISystemCall;
import rarsreborn.core.core.environment.mmu.IMemoryManagementUnit;
import rarsreborn.core.core.memory.IMemory;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.core.register.floatpoint.RegisterFloat64;
import rarsreborn.core.core.register.floatpoint.RegisterFloat64File;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

public abstract class RiscVSystemCall implements ISystemCall {
    protected RiscV32ExecutionEnvironment executionEnvironment;
    protected Register32File registers;
    protected Register32 programCounter;
    protected IMemory memory;
    protected RegisterFloat64File floatRegisters;
    protected IMemoryManagementUnit mmu;

    public RiscVSystemCall() {
        this(null, null, null, null, null);
    }

    public RiscVSystemCall(
            RiscV32ExecutionEnvironment executionEnvironment,
            Register32File registers,
            IMemory memory,
            Register32 programCounter,
            RegisterFloat64File floatRegisters
    ) {
        this.executionEnvironment = executionEnvironment;
        this.registers = registers;
        this.memory = memory;
        this.programCounter = programCounter;
        this.floatRegisters = floatRegisters;
    }

    public void setExecutionEnvironment(RiscV32ExecutionEnvironment executionEnvironment) {
        this.executionEnvironment = executionEnvironment;
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

    public void setFloatRegisters(RegisterFloat64File floatRegisters) {
        this.floatRegisters = floatRegisters;
    }

    public void setMmu(IMemoryManagementUnit mmu) {
        this.mmu = mmu;
    }

    protected int getRegisterValue(int number) throws IllegalRegisterException {
        return registers.getRegisterByNumber(number).getValue();
    }

    protected void setRegisterValue(int number, int value) throws IllegalRegisterException {
        registers.getRegisterByNumber(number).setValue(value);
    }

    protected RegisterFloat64 getFloatRegister(int number) throws IllegalRegisterException {
        return floatRegisters.getRegisterByNumber(number);
    }
}
