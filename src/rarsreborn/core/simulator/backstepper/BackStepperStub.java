package rarsreborn.core.simulator.backstepper;

import rarsreborn.core.exceptions.NoBackStepsLeftException;

public class BackStepperStub implements IBackStepper {
    @Override
    public void addStep() {}

    @Override
    public void addChange(IRevertible revertible) {}

    @Override
    public void revert() throws NoBackStepsLeftException {
        throw new NoBackStepsLeftException();
    }

    @Override
    public void reset() {}
}
