package core.instruction.riscv.formats;

import core.instruction.riscv.RiscVInstruction;

public abstract class InstructionU extends RiscVInstruction {
    protected final byte rd;
    protected final int imm;

    public InstructionU(InstructionUData data) {
        super(data.opcode);
        checkFieldSize(data.rd, 5);
        checkFieldSize(data.imm, 20, 12);
        this.rd = data.rd;
        this.imm = data.imm;
    }

    @Override
    public byte[] serialize() {
        int encoded = opcode & 0b111_1111
            | (rd & 0b1_1111) << 7
            | imm & 0b1111_1111_1111_1111_1111_0000_0000_0000;
        return intToBytes(encoded);
    }

    public static InstructionUData deserialize(int encoded) {
        return new InstructionUData(
            (byte) (encoded & 0b111_1111),
            (byte) (encoded >> 7 & 0b1_1111),
            encoded & 0b1111_1111_1111_1111_1111_0000_0000_0000
        );
    }

    public record InstructionUData(byte opcode, byte rd, int imm) {}

    public record InstructionUParams(byte rd, int imm) {}
}