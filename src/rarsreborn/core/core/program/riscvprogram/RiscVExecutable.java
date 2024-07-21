package rarsreborn.core.core.program.riscvprogram;

import rarsreborn.core.core.program.IExecutable;

public record RiscVExecutable(byte[] data, byte[] text, long entryPoint) implements IExecutable {
    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public byte[] getText() {
        return text;
    }

    @Override
    public long getEntryPointOffset() {
        return entryPoint;
    }
}
