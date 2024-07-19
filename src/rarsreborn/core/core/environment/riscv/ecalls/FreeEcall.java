package rarsreborn.core.core.environment.riscv.ecalls;

import rarsreborn.core.core.environment.riscv.RiscVSystemCall;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;
import rarsreborn.core.exceptions.memory.MemoryAccessException;

public class FreeEcall extends RiscVSystemCall {
    @Override
    public void call() throws IllegalRegisterException, MemoryAccessException {
        mmu.free(getRegisterValue(10));
    }
}
