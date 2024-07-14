package rarsreborn.core.core.environment.riscv.ecalls;

import rarsreborn.core.core.environment.events.ConsolePrintIntegerBinaryEvent;
import rarsreborn.core.core.environment.riscv.RiscVSystemCall;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

public class PrintIntegerBinaryEcall extends RiscVSystemCall {
    @Override
    public void call() throws IllegalRegisterException {
        executionEnvironment.notifyObservers(new ConsolePrintIntegerBinaryEvent(getRegisterValue(10)));
    }
}
