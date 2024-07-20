package rarsreborn.core.core.instruction.riscv.instructions.pseudo;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Slt;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Sltu;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;

public class Sltz implements IInstruction {
    public static final String NAME = "sltz";

    private final Slt base;

    public Sltz(byte rd, byte rs) {
        base = new Slt(new InstructionR.InstructionRParams(rd, rs, (byte) 0));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] serialize() {
        return base.serialize();
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Sltz> {
        @Override
        public Sltz parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 2, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));
            Register32 rs = castToRegister32(parseRegister(registers, split[1]));

            return new Sltz((byte) rd.getNumber(), (byte) rs.getNumber());
        }
    }
}
