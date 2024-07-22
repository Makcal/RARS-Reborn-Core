package rarsreborn.core.core.instruction.riscv.instructions.rv32d;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.core.register.floatpoint.RegisterFloat64;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

public class Fcvt_wu_d extends InstructionR {
    public static final String NAME = "fcvt.wu.d";
    public static final byte OPCODE = 0b1010011;
    public static final byte FUNCT_3 = 0b0;
    public static final byte FUNCT_7 = 0b1100001;

    public Fcvt_wu_d(InstructionRParams data) {
        super(new InstructionRData(OPCODE, data.rd(), FUNCT_3, data.rs1(), (byte) 1, FUNCT_7));
    }

    public Fcvt_wu_d(byte rd, byte rs1) {
        super(new InstructionRData(OPCODE, rd, FUNCT_3, rs1, (byte) 1, FUNCT_7));
    }

    public void exec(IRegisterFile<RegisterFloat64> floatRegisters, IRegisterFile<Register32> registers)
            throws IllegalRegisterException {
        double value = floatRegisters.getRegisterByNumber(rs1).getDouble();
        int result;
        if (Double.isNaN(value) || value == Double.POSITIVE_INFINITY) {
            result = -1;
        } else if (value == Double.NEGATIVE_INFINITY) {
            result = 0;
        } else if (value < 0) {
            result = 0;
        } else if ((1L << 32) - 1 < value) {
            result = -1;
        } else {
            result = (int) (long) value;
        }
        registers.getRegisterByNumber(rd).setValue(result);
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static class Handler extends RiscV32InstructionHandler<Fcvt_wu_d> {
        @Override
        public void handle(Fcvt_wu_d instruction) throws IllegalRegisterException {
            instruction.exec(floatRegisterFile, registerFile);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Fcvt_wu_d> {
        @Override
        public Fcvt_wu_d parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 2, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));
            RegisterFloat64 rs1 = castToRegisterFloat64(parseRegister(registers, split[1]));

            return new Fcvt_wu_d((byte) rd.getNumber(), (byte) rs1.getNumber());
        }
    }
}