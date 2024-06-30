package rarsreborn.core.core.instruction.riscv.formats;

import rarsreborn.core.core.instruction.riscv.RiscVInstruction;

public abstract class InstructionI extends RiscVInstruction {
    protected final byte rd, rs1;
    protected short imm;
    protected final byte funct3;

    public InstructionI(InstructionIData data) {
        super(data.opcode);
        checkFieldSize(data.funct3, 3);
        checkFieldSize(data.rd, 5);
        checkFieldSize(data.rs1, 5);
        checkFieldSize(data.imm, 12);
        this.funct3 = data.funct3;
        this.rd = data.rd;
        this.rs1 = data.rs1;
        this.imm = data.imm;
    }

    @Override
    public byte[] serialize() {
        int encoded = opcode & 0b111_1111
            | (rd & 0b1_1111) << 7
            | (funct3 & 0b111) << 12
            | (rs1 & 0b1_1111) << 15
            | (imm & 0b1111_1111_1111) << 20;
        return intToBytes(encoded);
    }

    public static InstructionIData deserialize(int encoded) {
        return new InstructionIData(
            (byte) (encoded & 0b111_1111),
            (byte) (encoded >> 7 & 0b1_1111),
            (byte) (encoded >> 12 & 0b111),
            (byte) (encoded >> 15 & 0b1_1111),
            (short) (encoded >> 20 & 0b1111_1111_1111)
        );
    }

    public record InstructionIData(byte opcode, byte rd, byte funct3, byte rs1, short imm) {}

    public record InstructionIParams(byte rd, byte rs1, short imm) {}
}