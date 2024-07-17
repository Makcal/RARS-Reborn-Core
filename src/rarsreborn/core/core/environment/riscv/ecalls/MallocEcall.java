package rarsreborn.core.core.environment.riscv.ecalls;

import rarsreborn.core.core.environment.riscv.RiscVSystemCall;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;
import rarsreborn.core.exceptions.memory.MemoryAccessException;

public class MallocEcall extends RiscVSystemCall {
    @Override
    public void call() throws IllegalRegisterException, MemoryAccessException {
        setRegisterValue(10, (int) mmu.malloc(getRegisterValue(10)));
    }
}
