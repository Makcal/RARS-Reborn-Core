package rarsreborn.core.core.environment.riscv.ecalls;

import rarsreborn.core.core.environment.riscv.RiscVSystemCall;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;
import rarsreborn.core.exceptions.memory.MemoryAccessException;

public class ReadEcall extends RiscVSystemCall {
    @Override
    public void call() throws MemoryAccessException, IllegalRegisterException {
        long address = getRegisterValue(10); // a0
        int count = getRegisterValue(11); // a1
        String input = executionEnvironment.getConsoleReader().requestString(count);
        for (int i = 0; i < input.length(); i++) {
            memory.setByte(address + i, (byte) input.charAt(i));
        }
    }
}
