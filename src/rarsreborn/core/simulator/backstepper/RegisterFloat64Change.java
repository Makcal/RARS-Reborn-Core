package rarsreborn.core.simulator.backstepper;

import rarsreborn.core.core.register.floatpoint.RegisterFloat64ChangeEvent;

public class RegisterFloat64Change implements IRevertible {
    protected final RegisterFloat64ChangeEvent event;

    public RegisterFloat64Change(RegisterFloat64ChangeEvent event) {
        this.event = event;
    }

    @Override
    public void revert() {
        event.register().setDoubleSilently(event.oldDoubleValue());
    }
}
