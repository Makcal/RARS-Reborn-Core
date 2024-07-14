package rarsreborn.core.core.environment.riscv.ecalls;

import rarsreborn.core.core.environment.events.ConsolePrintIntegerUnsignedEvent;
import rarsreborn.core.core.environment.riscv.RiscVSystemCall;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

public class PrintIntegerUnsignedEcall extends RiscVSystemCall {
    @Override
    public void call() throws IllegalRegisterException {
        executionEnvironment.notifyObservers(new ConsolePrintIntegerUnsignedEvent(getRegisterValue(10)));
    }
}
