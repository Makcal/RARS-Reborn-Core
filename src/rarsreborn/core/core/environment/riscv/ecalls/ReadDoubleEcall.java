package rarsreborn.core.core.environment.riscv.ecalls;

import rarsreborn.core.core.environment.riscv.RiscVSystemCall;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

public class ReadDoubleEcall extends RiscVSystemCall {
    @Override
    public void call() throws IllegalRegisterException {
        // 10 = a0
        getFloatRegister(10).setDouble(executionEnvironment.getConsoleReader().requestDouble());
    }
}
