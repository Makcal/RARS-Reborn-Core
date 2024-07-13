package rarsreborn.core.simulator.backstepper;

import rarsreborn.core.exceptions.execution.ExecutionException;

public interface IRevertible {
    void revert() throws ExecutionException;
}
