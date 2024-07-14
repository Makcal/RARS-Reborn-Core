package rarsreborn.core.core.environment.riscv.ecalls;

import rarsreborn.core.core.environment.riscv.RiscVSystemCall;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

public class ReadIntegerEcall extends RiscVSystemCall {
    @Override
    public void call() throws IllegalRegisterException {
        // 10 = a0
        registers.getRegisterByNumber(10).setValue(executionEnvironment.getConsoleReader().requestInt());
    }
}
