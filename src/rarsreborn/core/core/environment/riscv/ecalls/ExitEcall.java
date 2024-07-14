package rarsreborn.core.core.environment.riscv.ecalls;

import rarsreborn.core.core.environment.riscv.RiscVSystemCall;
import rarsreborn.core.exceptions.execution.EndOfExecutionException;
import rarsreborn.core.exceptions.execution.ExecutionException;

public class ExitEcall extends RiscVSystemCall {
    @Override
    public void call() throws ExecutionException {
        throw new EndOfExecutionException();
    }
}
