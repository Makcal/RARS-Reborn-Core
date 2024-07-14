package rarsreborn.core.core.environment.riscv.ecalls;

import rarsreborn.core.core.environment.riscv.RiscVSystemCall;
import rarsreborn.core.core.environment.ConsolePrintStringEvent;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;
import rarsreborn.core.exceptions.memory.MemoryAccessException;

public class PrintStringEcall extends RiscVSystemCall {
    @Override
    public void call() throws MemoryAccessException, IllegalRegisterException {
        StringBuilder stringBuilder = new StringBuilder();
        long address = getRegisterValue(10); // a0
        while (true) {
            byte b = memory.getByte(address++);
            if (b == 0) {
                break;
            }
            stringBuilder.append((char) b);
        }
        executionEnvironment.notifyObservers(new ConsolePrintStringEvent(stringBuilder.toString()));
    }
}