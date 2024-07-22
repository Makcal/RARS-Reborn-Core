package rarsreborn.core.core.instruction.riscv.instructions.pseudo;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.instruction.riscv.instructions.rv32f.Flt_s;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.core.register.floatpoint.RegisterFloat64;
import rarsreborn.core.exceptions.compilation.CompilationException;

public class Fgt_s implements IInstruction {
    public static final String NAME = "fgt.s";

    private final Flt_s base;

    public Fgt_s(byte rd, byte rs1, byte rs2) {
        base = new Flt_s(new InstructionR.InstructionRParams(rd, rs2, rs1));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] serialize() {
        return base.serialize();
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Fgt_s> {
        @Override
        public Fgt_s parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 3, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));
            RegisterFloat64 rs1 = castToRegisterFloat64(parseRegister(registers, split[1]));
            RegisterFloat64 rs2 = castToRegisterFloat64(parseRegister(registers, split[2]));

            return new Fgt_s((byte) rd.getNumber(), (byte) rs1.getNumber(), (byte) rs2.getNumber());
        }
    }
}
