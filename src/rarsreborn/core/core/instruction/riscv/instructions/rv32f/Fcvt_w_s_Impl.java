package rarsreborn.core.core.instruction.riscv.instructions.rv32f;

import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.core.register.floatpoint.RegisterFloat64;
import rarsreborn.core.exceptions.execution.ExecutionException;
import rarsreborn.core.exceptions.execution.IllegalInstructionException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

public class Fcvt_w_s_Impl extends InstructionR {
    public static final byte OPCODE = 0b1010011;
    public static final byte FUNCT_3 = 0b0;
    public static final byte FUNCT_7 = 0b1100000;

    public Fcvt_w_s_Impl(InstructionRParams data) {
        super(new InstructionRData(OPCODE, data.rd(), FUNCT_3, data.rs1(), data.rs2(), FUNCT_7));
        checkFieldSize(rs2, 1);
    }

    public void exec(IRegisterFile<RegisterFloat64> floatRegisters, IRegisterFile<Register32> registers)
            throws IllegalRegisterException, IllegalInstructionException {
        if (isUnsigned())
            new Fcvt_wu_s(rd, rs1).exec(floatRegisters, registers);
        else
            new Fcvt_w_s(rd, rs1).exec(floatRegisters, registers);
    }

    public boolean isUnsigned() {
        return rs2 == 1;
    }

    @Override
    public String getName() {
        return isUnsigned() ? Fcvt_wu_s.NAME : Fcvt_w_s.NAME;
    }

    @Override
    public String toString() {
        return "%s x%d, f%d".formatted(getName(), rd, rs1);
    }

    public static class Handler extends RiscV32InstructionHandler<Fcvt_w_s_Impl> {
        @Override
        public void handle(Fcvt_w_s_Impl instruction) throws ExecutionException {
            instruction.exec(floatRegisterFile, registerFile);
        }
    }
}