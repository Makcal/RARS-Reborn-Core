package core.memory;

import exceptions.memory.MemoryAccessException;

public interface IMemory {
    void reset();

    byte getByte(long address) throws MemoryAccessException;

    void setByte(long address, byte value) throws MemoryAccessException;

    /**
     * @param address The address to read from
     * @param size The size in bytes of the value to read (usually 1, 2, 4 or 8)
     * @return The read number
     */
    long getMultiple(long address, int size) throws MemoryAccessException;

    void setMultiple(long address, long value, int size) throws MemoryAccessException;

    byte[] readBytes(long address, int length) throws MemoryAccessException;

    void writeBytes(long address, byte[] bytes) throws MemoryAccessException;

    long getSize();

    boolean isLittleEndian();
}
