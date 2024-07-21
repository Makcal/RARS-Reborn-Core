package rarsreborn.core.core.environment.riscv.ecalls;

import rarsreborn.core.core.environment.events.ConsolePrintDoubleEvent;
import rarsreborn.core.core.environment.riscv.RiscVSystemCall;
import rarsreborn.core.exceptions.execution.ExecutionException;

public class PrintDoubleEcall extends RiscVSystemCall {
    @Override
    public void call() throws ExecutionException {
        double value = getFloatRegister(10).getDouble();
        executionEnvironment.notifyObservers(new ConsolePrintDoubleEvent(value));
    }
}
