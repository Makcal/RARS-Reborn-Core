package rarsreborn.core.core.memory;

import rarsreborn.core.event.IObserver;

public record MemoryBlockWrapper(MemoryBlock block) implements IMemory {
    @Override
    public void reset() {
        block.reset();
    }

    @Override
    public byte getByte(long address) {
        return block.getByte(address);
    }

    @Override
    public void setByte(long address, byte value) {
        setByteSilently(address, value);
    }

    @Override
    public void setByteSilently(long address, byte value) {
        block.setByte(address, value);
    }

    @Override
    public long getMultiple(long address, int bytes) {
        return block.getMultiple(address, bytes);
    }

    @Override
    public void setMultiple(long address, long value, int size) {
        setMultipleSilently(address, value, size);
    }

    @Override
    public void setMultipleSilently(long address, long value, int size) {
        block.setMultiple(address, value, size);
    }

    @Override
    public byte[] readBytes(long address, int length) {
        return block.readBytes(address, length);
    }

    @Override
    public void writeBytes(long address, byte[] bytes) {
        writeBytesSilently(address, bytes);
    }

    @Override
    public void writeBytesSilently(long address, byte[] bytes) {
        block.writeBytes(address, bytes);
    }

    @Override
    public long getSize() {
        return block.getSize();
    }

    @Override
    public boolean isLittleEndian() {
        return block.isLittleEndian();
    }

    @Override
    public <TEvent> void addObserver(Class<TEvent> eventClass, IObserver<TEvent> observer) {}

    @Override
    public <TEvent> void removeObserver(Class<TEvent> eventClass, IObserver<TEvent> observer) {}

    @Override
    public <TEvent> void notifyObservers(TEvent event) {}
}
