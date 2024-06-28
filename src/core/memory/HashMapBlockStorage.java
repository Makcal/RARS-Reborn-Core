package core.memory;

import java.util.HashMap;

public class HashMapBlockStorage implements IMemoryBlockStorage {
    private final HashMap<Long, Byte> data;
    private final long length;

    public HashMapBlockStorage(long length) {
        this.length = length;
        this.data = new HashMap<>();
    }

    @Override
    public void clear() {
        data.clear();
    }

    protected boolean outOfBounds(long address) {
        return address < 0 || length <= address;
    }

    @Override
    public byte getByte(long address) {
        if (outOfBounds(address))
            throw new IndexOutOfBoundsException(address);
        return data.getOrDefault(address, (byte) 0);
    }

    @Override
    public void setByte(long address, byte value) {
        if (outOfBounds(address))
            throw new IndexOutOfBoundsException(address);
        data.put(address, value);
    }

    @Override
    public long getSize() {
        return length;
    }
}
