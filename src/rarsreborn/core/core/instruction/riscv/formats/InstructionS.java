package rarsreborn.core.core.instruction.riscv.formats;

import rarsreborn.core.core.instruction.riscv.RiscVInstruction;

public abstract class InstructionS extends RiscVInstruction {
    protected final byte rs1;
    protected final byte funct3;
    protected final short imm;
    protected final byte rs2;

    public InstructionS(InstructionSData data) {
        super(data.opcode);
        checkFieldSize(data.funct3, 3);
        checkFieldSize(data.rs1, 5);
        checkFieldSize(data.rs2, 5);
        checkFieldSize(data.imm, 12);
        this.funct3 = data.funct3;
        this.rs1 = data.rs1;
        this.rs2 = data.rs2;
        this.imm = data.imm;
    }

    @Override
    public byte[] serialize() {
        int encoded = opcode & 0b111_1111
            | (imm & 0b1_1111) << 7
            | (funct3 & 0b111) << 12
            | (rs1 & 0b1_1111) << 15
            | (rs2 & 0b1_1111) << 20
            | (imm >> 5 & 0b111_1111) << 25;
        return intToBytes(encoded);
    }

    public static InstructionSData deserialize(int encoded) {
        return new InstructionSData(
            (byte) (encoded & 0b111_1111),
            (short) ((encoded >> 7 & 0b1_1111)
                | (encoded >> 25 & 0b111_1111) << 5),
            (byte) (encoded >> 12 & 0b111),
            (byte) (encoded >> 15 & 0b1_1111),
            (byte) (encoded >> 20 & 0b1_1111)
        );
    }

    public record InstructionSData(byte opcode, short imm, byte funct3, byte rs1, byte rs2) {}

    public record InstructionSParams(byte rs1, byte rs2, short imm) {}
}