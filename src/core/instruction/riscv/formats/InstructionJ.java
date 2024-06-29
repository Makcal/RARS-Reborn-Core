package core.instruction.riscv.formats;

import core.instruction.riscv.RiscVInstruction;

public abstract class InstructionJ extends RiscVInstruction {
    protected final byte rd;
    protected int imm;

    public InstructionJ(InstructionJData data) {
        super(data.opcode);
        checkFieldSize(data.rd, 5);
        checkFieldSize(data.imm, 20, 1);
        this.rd = data.rd;
        this.imm = data.imm;
    }

    @Override
    public byte[] serialize() {
        int encoded = opcode & 0b111_1111
            | (rd & 0b1_1111) << 7
            | imm & 0b1111_1111_0000_0000_0000
            | (imm >> 11 & 0b1) << 20
            | (imm >> 1 & 0b11_1111_1111) << 21
            | (imm >> 20 & 0b1) << 31;
        return intToBytes(encoded);
    }

    public static InstructionJData deserialize(int encoded) {
        return new InstructionJData(
            (byte) (encoded & 0b111_1111),
            (byte) (encoded >> 7 & 0b1_1111),
            encoded & 0b1111_1111_0000_0000_0000
                | (encoded >> 20 & 0b1) << 11
                | (encoded >> 21 & 0b11_1111_1111) << 1
                | (encoded >> 31 & 0b1) << 20
        );
    }

    public record InstructionJData(byte opcode, byte rd, int imm) {}

    public record InstructionJParams(byte rd, int imm) {}
}