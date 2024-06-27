package core.memory;

public interface IMemoryBlockStorage {
    void clear();

    byte getByte(long address);

    void setByte(long address, byte value);

    long getSize();
}
