package rarsreborn.core.core.environment.riscv.ecalls;

import rarsreborn.core.core.environment.ConsolePrintIntegerEvent;
import rarsreborn.core.core.environment.riscv.RiscVSystemCall;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

public class PrintIntegerEcall extends RiscVSystemCall {
    @Override
    public void call() throws IllegalRegisterException {
        executionEnvironment.notifyObservers(new ConsolePrintIntegerEvent(getRegisterValue(10)));
    }
}
