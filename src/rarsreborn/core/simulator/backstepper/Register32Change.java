package rarsreborn.core.simulator.backstepper;

import rarsreborn.core.core.register.Register32ChangeEvent;

public class Register32Change implements IRevertible {
    protected final Register32ChangeEvent event;

    public Register32Change(Register32ChangeEvent event) {
        this.event = event;
    }

    @Override
    public void revert() {
        event.register().setValueSilently(event.oldValue());
    }
}
