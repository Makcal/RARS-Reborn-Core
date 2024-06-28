package core.riscvprogram;

public record DataBlock(int size, byte alignment, byte[] value) implements IDataBlock {}
