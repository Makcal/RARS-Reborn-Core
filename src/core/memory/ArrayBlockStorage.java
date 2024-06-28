package core.memory;

public class ArrayBlockStorage implements IMemoryBlockStorage {
    private byte[] data;

    public ArrayBlockStorage(long length) {
        this.data = new byte[(int) length];
    }

    @Override
    public void clear() {
        data = new byte[data.length];
    }

    @Override
    public byte getByte(long address) {
        return data[(int) address];
    }

    @Override
    public void setByte(long address, byte value) {
        data[(int) address] = value;
    }

    @Override
    public long getSize() {
        return data.length;
    }
}
