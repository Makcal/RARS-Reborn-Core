package rarsreborn.core.simulator.backstepper;

import rarsreborn.core.core.memory.IMemory;
import rarsreborn.core.core.memory.MemoryChangeEvent;
import rarsreborn.core.exceptions.memory.MemoryAccessException;

public class MemoryChange implements IRevertible {
    protected final IMemory memory;
    protected final MemoryChangeEvent event;

    public MemoryChange(IMemory memory, MemoryChangeEvent event) {
        this.memory = memory;
        this.event = event;
    }

    @Override
    public void revert() throws MemoryAccessException {
        memory.writeBytes(event.address(), event.oldSpan());
    }
}
