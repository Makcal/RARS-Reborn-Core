package rarsreborn.core.core.program.riscvprogram;

import rarsreborn.core.core.program.IExecutable;

public record RiscVExecutable(byte[] data, byte[] text) implements IExecutable {
    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public byte[] getText() {
        return text;
    }
}
