package rarsreborn.core.core.instruction.riscv.instructions.rv32d;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.floatpoint.RegisterFloat64;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

public class Fsgnjx_d extends InstructionR {
    public static final String NAME = "fsgnjx.d";
    public static final byte OPCODE = 0b1010011;
    public static final byte FUNCT_3 = 0b010;
    public static final byte FUNCT_7 = 0b0010001;

    public Fsgnjx_d(InstructionRParams data) {
        super(new InstructionRData(OPCODE, data.rd(), FUNCT_3, data.rs1(), data.rs2(), FUNCT_7));
    }

    public void exec(IRegisterFile<RegisterFloat64> registers) throws IllegalRegisterException {
        registers.getRegisterByNumber(rd).setDouble(
            Math.abs(registers.getRegisterByNumber(rs1).getDouble())
            * Math.copySign(1, registers.getRegisterByNumber(rs1).getDouble())
            * Math.copySign(1, registers.getRegisterByNumber(rs2).getDouble())
        );
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static class Handler extends RiscV32InstructionHandler<Fsgnjx_d> {
        @Override
        public void handle(Fsgnjx_d instruction) throws IllegalRegisterException {
            instruction.exec(floatRegisterFile);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Fsgnjx_d> {
        @Override
        public Fsgnjx_d parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 3, NAME);

            RegisterFloat64 rd = castToRegisterFloat64(parseRegister(registers, split[0]));
            RegisterFloat64 rs1 = castToRegisterFloat64(parseRegister(registers, split[1]));
            RegisterFloat64 rs2 = castToRegisterFloat64(parseRegister(registers, split[2]));

            return new Fsgnjx_d(
                new InstructionRParams(
                    (byte) rd.getNumber(),
                    (byte) rs1.getNumber(),
                    (byte) rs2.getNumber()
                )
            );
        }
    }
}