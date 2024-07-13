package rarsreborn.core.core.memory;

public class MemoryBlock {
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

    public void reset() {
        storage.clear();
    }

    public byte getByte(long address) {
        return storage.getByte(address - offset);
    }

    public void setByte(long address, byte value) {
        storage.setByte(address - offset, value);
    }

    public long getMultiple(long address, int bytes) {
        long value = 0;
        for (int i = 0; i < bytes; i++) {
            int shift = isLittleEndian ? i : bytes - i - 1;
            value |= Byte.toUnsignedLong(getByte(address + i)) << (shift * 8);
        }
        return value;
    }

    public void setMultiple(long address, long value, int size) {
        for (int i = 0; i < size; i++) {
            int shift = isLittleEndian ? i : size - i - 1;
            setByte(address + i, (byte) (value >> (shift * 8)));
        }
    }

    public byte[] readBytes(long address, int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = getByte(address + i);
        }
        return bytes;
    }

    public void writeBytes(long address, byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            setByte(address + i, bytes[i]);
        }
    }

    public long getSize() {
        return storage.getSize();
    }

    public boolean isLittleEndian() {
        return isLittleEndian;
    }

    public boolean contains(long address) {
        return offset <= address && address < offset + getSize();
    }
}
