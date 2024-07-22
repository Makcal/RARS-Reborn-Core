package rarsreborn.core.core.instruction.riscv.instructions.pseudo;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.instruction.riscv.instructions.rv32d.Fsgnj_d;
import rarsreborn.core.core.register.floatpoint.RegisterFloat64;
import rarsreborn.core.exceptions.compilation.CompilationException;

public class Fmv_d implements IInstruction {
    public static final String NAME = "fmv.d";

    private final Fsgnj_d base;

    public Fmv_d(byte rd, byte rs) {
        base = new Fsgnj_d(new InstructionR.InstructionRParams(rd, rs, rs));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] serialize() {
        return base.serialize();
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Fmv_d> {
        @Override
        public Fmv_d parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 2, NAME);

            RegisterFloat64 rd = castToRegisterFloat64(parseRegister(registers, split[0]));
            RegisterFloat64 rs = castToRegisterFloat64(parseRegister(registers, split[1]));

            return new Fmv_d((byte) rd.getNumber(), (byte) rs.getNumber());
        }
    }
}
