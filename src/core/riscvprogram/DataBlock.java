package core.riscvprogram;

import core.program.IDataBlock;

public record DataBlock(int size, byte alignment, byte[] value, String label) implements IDataBlock {
    @Override
    public int getSize() {
        return size;
    }

    @Override
    public byte getAlignment() {
        return alignment;
    }

    @Override
    public byte[] getValue() {
        return value;
    }
}
