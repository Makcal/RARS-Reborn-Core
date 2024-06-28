package core.instruction.riscv;

import core.instruction.IInstruction;

public abstract class RiscVInstruction implements IInstruction {
    protected final byte opcode;

    public RiscVInstruction(byte opcode) {
        checkFieldSize(opcode, 7);
        this.opcode = opcode;
    }

    protected boolean fieldOutOfBounds(int field, int bitSize) {
        return (field & (-1 << bitSize)) != 0;
    }

    protected boolean fieldOutOfBounds(int field, int bitSize, int offset) {
        return (field & (-1 << (bitSize + offset))) != 0 || (field & ((1 << offset) - 1)) != 0;
    }

    protected void checkFieldSize(int field, int bitSize) {
        if (fieldOutOfBounds(field, bitSize)) {
            throw new IllegalArgumentException("Field 0x%x is too large (%d bits max)".formatted(field, bitSize));
        }
    }

    protected void checkFieldSize(int field, int bitSize, int offset) {
        if (fieldOutOfBounds(field, bitSize, offset)) {
            throw new IllegalArgumentException(
                "Field 0x%x is too large (%d bits max) or ".formatted(field, bitSize) +
                "contains non-zero bits in the lower %d bits".formatted(offset)
            );
        }
    }

    /**
     * @param value a 32bit integer to convert
     * @return the number split into bytes (big endian)
     */
    protected static byte[] intToBytes(int value) {
        return new byte[] {
            (byte) (value >> 24),
            (byte) (value >> 16),
            (byte) (value >> 8),
            (byte) value
        };
    }
}
