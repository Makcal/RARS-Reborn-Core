package rarsreborn.core.core.environment.riscv;

import rarsreborn.core.core.environment.ISystemCall;
import rarsreborn.core.core.environment.mmu.IMemoryManagementUnit;
import rarsreborn.core.core.memory.IMemory;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

public abstract class RiscVSystemCall implements ISystemCall {
    protected RiscV32ExecutionEnvironment executionEnvironment;
    protected Register32File registers;
    protected Register32 programCounter;
    protected IMemory memory;
    protected IMemoryManagementUnit mmu;

    public RiscVSystemCall() {
        this(null, null, null, null);
    }

    public RiscVSystemCall(
            RiscV32ExecutionEnvironment executionEnvironment,
            Register32File registers,
            IMemory memory,
            Register32 programCounter
    ) {
        this.executionEnvironment = executionEnvironment;
        this.registers = registers;
        this.memory = memory;
        this.programCounter = programCounter;
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

    public void setMmu(IMemoryManagementUnit mmu) {
        this.mmu = mmu;
    }

    protected int getRegisterValue(int number) throws IllegalRegisterException {
        return registers.getRegisterByNumber(number).getValue();
    }

    protected void setRegisterValue(int number, int value) throws IllegalRegisterException {
        registers.getRegisterByNumber(number).setValue(value);
    }
}
