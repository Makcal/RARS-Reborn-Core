package rarsreborn.core.core.memory;

public class MemoryBlock implements IMemory {
    private final IMemoryBlockStorage storage;
    private final boolean isLittleEndian;
    private final long offset;

    public MemoryBlock(long offset, IMemoryBlockStorage storage) {
        this(offset, storage, true);
    }

    public MemoryBlock(long offset, IMemoryBlockStorage storage, boolean isLittleEndian) {
        this.offset = offset;
        this.storage = storage;
        this.isLittleEndian = isLittleEndian;
    }

    @Override
    public void reset() {
        storage.clear();
    }

    @Override
    public byte getByte(long address) {
        return storage.getByte(address - offset);
    }

    @Override
    public void setByte(long address, byte value) {
        storage.setByte(address - offset, value);
    }

    @Override
    public long getMultiple(long address, int bytes) {
        long value = 0;
        for (int i = 0; i < bytes; i++) {
            int shift = isLittleEndian ? i : bytes - i - 1;
            value |= Byte.toUnsignedLong(getByte(address + i)) << (shift * 8);
        }
        return value;
    }

    @Override
    public void setMultiple(long address, long value, int size) {
        for (int i = 0; i < size; i++) {
            int shift = isLittleEndian ? i : size - i - 1;
            setByte(address + i, (byte) (value >> (shift * 8)));
        }
    }

    @Override
    public byte[] readBytes(long address, int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = getByte(address + i);
        }
        return bytes;
    }

    @Override
    public void writeBytes(long address, byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            setByte(address + i, bytes[i]);
        }
    }

    @Override
    public long getSize() {
        return storage.getSize();
    }

    @Override
    public boolean isLittleEndian() {
        return isLittleEndian;
    }

    public boolean contains(long address) {
        return offset <= address && address < offset + getSize();
    }
}
