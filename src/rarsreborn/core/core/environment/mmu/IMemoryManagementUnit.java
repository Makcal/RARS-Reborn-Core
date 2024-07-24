package rarsreborn.core.core.environment.mmu;

import rarsreborn.core.exceptions.memory.MemoryAccessException;

public interface IMemoryManagementUnit {
    long malloc(long size) throws MemoryAccessException;

    void free(long address) throws MemoryAccessException;

    void freeAll();

    void reset();
}
