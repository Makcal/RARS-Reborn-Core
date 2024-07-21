package rarsreborn.core.core.environment.riscv.ecalls;

import rarsreborn.core.core.environment.events.ConsolePrintFloatEvent;
import rarsreborn.core.core.environment.riscv.RiscVSystemCall;
import rarsreborn.core.exceptions.execution.ExecutionException;

public class PrintFloatEcall extends RiscVSystemCall {
    @Override
    public void call() throws ExecutionException {
        float value = getFloatRegister(10).getFloat();
        executionEnvironment.notifyObservers(new ConsolePrintFloatEvent(value));
    }
}
