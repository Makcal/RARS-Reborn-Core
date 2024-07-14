package rarsreborn.core.simulator.backstepper;

import rarsreborn.core.exceptions.NoBackStepsLeftException;
import rarsreborn.core.exceptions.execution.ExecutionException;

public interface IBackStepper {
    void addStep();

    void addChange(IRevertible revertible);

    void revert() throws NoBackStepsLeftException, ExecutionException;

    void reset();
}
