package rarsreborn.core.core.environment.riscv.ecalls;

import rarsreborn.core.core.environment.events.ConsolePrintIntegerOctalEvent;
import rarsreborn.core.core.environment.riscv.RiscVSystemCall;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

public class PrintIntegerOctalEcall extends RiscVSystemCall {
    @Override
    public void call() throws IllegalRegisterException {
        executionEnvironment.notifyObservers(new ConsolePrintIntegerOctalEvent(getRegisterValue(10)));
    }
}
