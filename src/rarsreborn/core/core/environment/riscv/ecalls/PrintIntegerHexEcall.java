package rarsreborn.core.core.environment.riscv.ecalls;

import rarsreborn.core.core.environment.events.ConsolePrintIntegerHexEvent;
import rarsreborn.core.core.environment.riscv.RiscVSystemCall;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

public class PrintIntegerHexEcall extends RiscVSystemCall {
    @Override
    public void call() throws IllegalRegisterException {
        executionEnvironment.notifyObservers(new ConsolePrintIntegerHexEvent(getRegisterValue(10)));
    }
}
