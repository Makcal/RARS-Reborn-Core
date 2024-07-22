package rarsreborn.core.core.instruction.riscv.instructions.rv32i;

import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

/**
 * A general class for `srli` and `shiftRightImm` instructions. Used to avoid one more instruction format.
 */
public class ShiftRightImm extends InstructionI {
    public static final byte OPCODE = 0b0010011;
    public static final byte FUNCT_3 = 0x5;

    public ShiftRightImm(InstructionIParams data) {
        super(new InstructionIData(OPCODE, data.rd(), FUNCT_3, data.rs1(), data.imm()));
        checkFieldSize(imm ^ (imm & (1 << 10)), 5);
    }

    public void exec(IRegisterFile<Register32> registerFile) throws IllegalRegisterException {
        if (isArithmetic())
            new Srai(rd, rs1, (short) (imm ^ 1 << 10)).exec(registerFile);
        else
            new Srli(new InstructionIParams(rd, rs1, imm)).exec(registerFile);
    }

    @Override
    public String getName() {
        return isArithmetic() ? Srai.NAME : Srli.NAME;
    }

    @Override
    public String toString() {
        return "%s x%d, x%d, %d".formatted(getName(), rd, rs1, imm & 0b1_1111);
    }

    public boolean isArithmetic() {
        return (imm & (1 << 10)) != 0;
    }

    public static class Handler extends RiscV32InstructionHandler<ShiftRightImm> {
        @Override
        public void handle(ShiftRightImm instruction) throws IllegalRegisterException {
            instruction.exec(registerFile);
        }
    }
}
