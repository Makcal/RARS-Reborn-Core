package rarsreborn.core.core.memory;

import rarsreborn.core.event.IObservable;
import rarsreborn.core.event.IObserver;
import rarsreborn.core.event.ObservableImplementation;

import java.util.List;

public class Memory32 implements IMemory, IObservable {
    private final static int SIZE = 0x7fffffff;

    // Derived from RARS
    public static final int TEXT_SECTION_START = 0x00400000;
    public static final int DATA_SECTION_START = 0x10010000;
    public static final int HEAP_SECTION_START = 0x10040000;
    public static final int STACK_SECTION_START = 0x7fdfffff;

    // In bytes
    public static final int TEXT_SECTION_SIZE = 0x100000; // 1 MB
    public static final int DATA_SECTION_SIZE = 0x30000; // 192 KB
    public static final int HEAP_SECTION_SIZE = 0x30000; // 192 KB
    public static final int STACK_SECTION_SIZE = 0x100000; // 1 MB
    public static final int INITIAL_STACK_POINTER = STACK_SECTION_START + STACK_SECTION_SIZE - 4;

    protected MemoryBlock dataSection, textSection, stackSection, heapSection, other;
    protected List<MemoryBlock> sections;

    protected final ObservableImplementation observableImplementation = new ObservableImplementation();

    public Memory32() {
        reset();
    }

    @Override
    public void reset() {
        textSection = new MemoryBlock(TEXT_SECTION_START, new ArrayBlockStorage(TEXT_SECTION_SIZE));
        dataSection = new MemoryBlock(DATA_SECTION_START, new ArrayBlockStorage(DATA_SECTION_SIZE));
        heapSection = new MemoryBlock(HEAP_SECTION_START, new ArrayBlockStorage(HEAP_SECTION_SIZE));
        stackSection = new MemoryBlock(STACK_SECTION_START, new ArrayBlockStorage(STACK_SECTION_SIZE));
        other = new MemoryBlock(0, new HashMapBlockStorage(SIZE));
        sections = List.of(dataSection, textSection, stackSection, heapSection, other);
    }

    protected MemoryBlock getSection(long address) {
        for (MemoryBlock section : sections) {
            if (section.contains(address)) {
                return section;
            }
        }
        throw new IndexOutOfBoundsException(address);
    }

    @Override
    public byte getByte(long address) {
        return getSection(address).getByte(address);
    }

    @Override
    public void setByte(long address, byte value) {
        MemoryBlock section = getSection(address);
        byte oldValue = section.getByte(address);
        section.setByte(address, value);
        notifyObservers(new Memory32ChangeEvent(address, new byte[] {oldValue}, new byte[] {value}));
    }

    @Override
    public long getMultiple(long address, int bytes) {
        return getSection(address).getMultiple(address, bytes);
    }

    @Override
    public void setMultiple(long address, long value, int size) {
        MemoryBlock section = getSection(address);
        byte[] oldValue = section.readBytes(address, size);
        section.setMultiple(address, value, size);
        notifyObservers(new Memory32ChangeEvent(address, oldValue, section.readBytes(address, size)));
    }

    @Override
    public byte[] readBytes(long address, int length) {
        return getSection(address).readBytes(address, length);
    }

    @Override
    public void writeBytes(long address, byte[] bytes) {
        MemoryBlock section = getSection(address);
        byte[] oldBytes = section.readBytes(address, bytes.length);
        section.writeBytes(address, bytes);
        notifyObservers(new Memory32ChangeEvent(address, oldBytes, section.readBytes(address, bytes.length)));
    }

    @Override
    public long getSize() {
        return SIZE;
    }

    @Override
    public boolean isLittleEndian() {
        return true;
    }

    @Override
    public <TEvent> void addObserver(Class<TEvent> eventClass, IObserver<TEvent> observer) {
        observableImplementation.addObserver(eventClass, observer);
    }

    @Override
    public <TEvent> void removeObserver(Class<TEvent> eventClass, IObserver<TEvent> observer) {
        observableImplementation.removeObserver(eventClass, observer);
    }

    @Override
    public <TEvent> void notifyObservers(TEvent event) {
        observableImplementation.notifyObservers(event);
    }
}

