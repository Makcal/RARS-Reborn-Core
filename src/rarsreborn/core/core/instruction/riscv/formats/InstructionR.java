package rarsreborn.core.core.instruction.riscv.formats;

import rarsreborn.core.core.instruction.riscv.RiscVInstruction;

public abstract class InstructionR extends RiscVInstruction {
    protected final byte rd, rs1, rs2;
    protected final byte funct3, funct7;

    public InstructionR(InstructionRData data) {
        super(data.opcode);
        checkFieldSize(data.funct3, 3);
        checkFieldSize(data.funct7, 7);
        checkFieldSize(data.rd, 5);
        checkFieldSize(data.rs1, 5);
        checkFieldSize(data.rs2, 5);
        this.funct3 = data.funct3;
        this.funct7 = data.funct7;
        this.rd = data.rd;
        this.rs1 = data.rs1;
        this.rs2 = data.rs2;
    }

    @Override
    public byte[] serialize() {
        int encoded = opcode & 0b111_1111
            | (rd & 0b1_1111) << 7
            | (funct3 & 0b111) << 12
            | (rs1 & 0b1_1111) << 15
            | (rs2 & 0b1_1111) << 20
            | (funct7 & 0b111_1111) << 25;
        return intToBytes(encoded);
    }

    public static InstructionRData deserialize(int encoded) {
        return new InstructionRData(
            (byte) (encoded & 0b111_1111),
            (byte) (encoded >> 7 & 0b1_1111),
            (byte) (encoded >> 12 & 0b111),
            (byte) (encoded >> 15 & 0b1_1111),
            (byte) (encoded >> 20 & 0b1_1111),
            (byte) (encoded >> 25 & 0b111_1111)
        );
    }

    public record InstructionRData(byte opcode, byte rd, byte funct3, byte rs1, byte rs2, byte funct7) {}

    public record InstructionRParams(byte rd, byte rs1, byte rs2) {}
}