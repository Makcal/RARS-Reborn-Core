package rarsreborn.core.core.instruction.riscv;

import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;

public abstract class RiscVInstruction implements IInstruction {
    protected final byte opcode;

    public RiscVInstruction(byte opcode) {
        checkFieldSize(opcode, 7);
        this.opcode = opcode;
    }

    public static boolean fieldOutOfBounds(int field, int bitSize) {
        return (field & (-1 << bitSize)) != 0;
    }

    public static boolean fieldOutOfBounds(int field, int bitSize, int offset) {
        return (field & (-1 << (bitSize + offset))) != 0 && bitSize + offset < 32
            || (field & ((1 << offset) - 1)) != 0;
    }

    public static void checkFieldSize(int field, int bitSize) {
        if (fieldOutOfBounds(field, bitSize)) {
            throw new IllegalArgumentException("Field 0x%x is too large (%d bits max)".formatted(field, bitSize));
        }
    }

    public static void checkFieldSize(int field, int bitSize, int offset) {
        if (fieldOutOfBounds(field, bitSize, offset)) {
            throw new IllegalArgumentException(
                "Field 0x%x is too large (%d bits max) or ".formatted(field, bitSize) +
                "contains non-zero bits in the lower %d bits".formatted(offset)
            );
        }
    }

    /**
     * Check the sign bit and extend the sign if necessary
     */
    public static int asNegative(int value, int bitSize) {
        if ((value & (1 << (bitSize - 1))) != 0) {
            return value | (-1 << bitSize);
        }
        else {
            return value;
        }
    }

    /**
     * Removes leading bits compressing them to the sign bit.
     * <p>
     * E.g. -1 (0xffff_ffff) in 12 bits is 0xfff with leading zeros.
     * @param value a number to truncate
     * @param bitSize the number of significant bits including the right-most sign bit
     * @return the number with insignificant sign bits removed
     * @throws ImmediateTooLargeException if the number can not be fit in <code>value</code> bits
     */
    public static long truncateNegative(long value, int bitSize) throws ImmediateTooLargeException {
        final int MAX = (1 << (bitSize - 1)) - 1;
        if (value < ~MAX || MAX < value) {
            throw new ImmediateTooLargeException(value);
        }
        return value & ((1L << bitSize) - 1);
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
