package rarsreborn.core.core.instruction.riscv.instructions.rv32d;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.core.register.floatpoint.RegisterFloat64;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

public class Fclass_d extends InstructionR {
    public static final String NAME = "fclass.d";
    public static final byte OPCODE = 0b1010011;
    public static final byte FUNCT_3 = 0b001;
    public static final byte FUNCT_7 = 0b1110001;

    public Fclass_d(InstructionRParams data) {
        super(new InstructionRData(OPCODE, data.rd(), FUNCT_3, data.rs1(), (byte) 0, FUNCT_7));
    }

    public void exec(IRegisterFile<RegisterFloat64> floatRegisters, IRegisterFile<Register32> registers)
            throws IllegalRegisterException {
        int result;
        double value = floatRegisters.getRegisterByNumber(rs1).getDouble();
        if (value == Double.NEGATIVE_INFINITY) {
            result = 1;
        } else if (value < 0 && isSubnormal(value)) {
            result = 4;
        } else if (value < 0) {
            result = 2;
        } else if (Double.doubleToRawLongBits(value) == Double.doubleToRawLongBits(-0.0)) {
            result = 8;
        } else if (Double.doubleToRawLongBits(value) == Double.doubleToRawLongBits(+0.0)) {
            result = 16;
        } else if (value == Double.POSITIVE_INFINITY) {
            result = 128;
        } else if (value > 0 && isSubnormal(value)) {
            result = 32;
        } else if (value > 0) {
            result = 64;
        } else if (isSignalingNaN(value)) {
            result = 256;
        } else {
            // quite NaN
            result = 512;
        }
        registers.getRegisterByNumber(rd).setValue(result);
    }

    public static boolean isSubnormal(double value) {
        return value != 0.0f && Math.getExponent(value) < -1022;
    }

    public static boolean isSignalingNaN(double value) {
        if (!Double.isNaN(value)) {
            return false; // Not NaN
        }
        // Check if the highest-order bit of the fraction field (quiet bit) is not set
        return (Double.doubleToRawLongBits(value) & 0x0008000000000000L) == 0;
    }

        @Override
    public String getName() {
        return NAME;
    }

    public static class Handler extends RiscV32InstructionHandler<Fclass_d> {
        @Override
        public void handle(Fclass_d instruction) throws IllegalRegisterException {
            instruction.exec(floatRegisterFile, registerFile);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Fclass_d> {
        @Override
        public Fclass_d parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 2, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));
            RegisterFloat64 rs1 = castToRegisterFloat64(parseRegister(registers, split[1]));

            return new Fclass_d(
                new InstructionRParams(
                    (byte) rd.getNumber(),
                    (byte) rs1.getNumber(),
                    (byte) 0
                )
            );
        }
    }
}