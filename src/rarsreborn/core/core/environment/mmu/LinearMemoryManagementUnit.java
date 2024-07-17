package rarsreborn.core.core.environment.mmu;

import rarsreborn.core.core.memory.IMemory;
import rarsreborn.core.exceptions.memory.IllegalFreeException;
import rarsreborn.core.exceptions.memory.OutOfMemoryException;

import java.util.LinkedList;
import java.util.ListIterator;

public class LinearMemoryManagementUnit implements IMemoryManagementUnit {
    protected final IMemory memory;
    private final long heapStartAddress;
    private final long heapSize;
    protected final LinkedList<HeapBlock> blocks = new LinkedList<>();

    public LinearMemoryManagementUnit(IMemory memory, long heapStartAddress, long heapSize) {
        this.memory = memory;
        this.heapStartAddress = heapStartAddress;
        this.heapSize = heapSize;
        freeAll();
    }

    @Override
    public long malloc(long size) throws OutOfMemoryException {
        for (ListIterator<HeapBlock> iterator = blocks.listIterator(); iterator.hasNext(); ) {
            HeapBlock block = iterator.next();
            if (!block.isFree || block.size < size) continue;
            if (block.size == size) {
                block.isFree = false;
            }
            else {
                iterator.remove();
                iterator.add(new HeapBlock(block.address, size, false));
                iterator.add(new HeapBlock(block.address + size, block.size - size, true));
            }
            return block.address;
        }
        throw new OutOfMemoryException();
    }

    @Override
    public void free(long address) throws IllegalFreeException {
        if (address == 0) return;
        HeapBlock lastBlock;
        HeapBlock block = null;
        for (ListIterator<HeapBlock> iterator = blocks.listIterator(); iterator.hasNext(); ) {
            lastBlock = block;
            block = iterator.next();
            if (block.address < address) continue;
            if (block.address > address || block.isFree) throw new IllegalFreeException();

            HeapBlock nextBlock = null;
            if (iterator.hasNext()) {
                nextBlock = iterator.next();
                iterator.previous();
                iterator.previous();
                iterator.next();
            }

            if (!isBlockFree(lastBlock) && !isBlockFree(nextBlock)) {
                block.isFree = true;
                return;
            } else if (isBlockFree(lastBlock) && !isBlockFree(nextBlock)) {
                lastBlock.size += block.size;
                iterator.remove();
                return;
            } else if (!isBlockFree(lastBlock) && isBlockFree(nextBlock)) {
                block.size += nextBlock.size;
                iterator.next();
                iterator.remove();
                return;
            } else if (isBlockFree(lastBlock) && isBlockFree(nextBlock)) {
                lastBlock.size += block.size + nextBlock.size;
                iterator.remove();
                iterator.next();
                iterator.remove();
                return;
            }
        }
        throw new IllegalFreeException();
    }

    protected final boolean isBlockFree(HeapBlock block) {
        return block != null && block.isFree;
    }

    @Override
    public void freeAll() {
        blocks.clear();
        blocks.add(new HeapBlock(heapStartAddress, heapSize));
    }

    protected static class HeapBlock {
        public final long address;
        public long size;
        public boolean isFree;

        public HeapBlock(long address, long size, boolean isFree) {
            this.address = address;
            this.size = size;
            this.isFree = isFree;
        }

        public HeapBlock(long address, long size) {
            this(address, size, true);
        }
    }
}
