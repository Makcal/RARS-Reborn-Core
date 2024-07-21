package rarsreborn.core.core.instruction.riscv.instructions.pseudo;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Slt;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;

public class Sgtz implements IInstruction {
    public static final String NAME = "sgtz";

    private final Slt base;

    public Sgtz(byte rd, byte rs) {
        base = new Slt(new InstructionR.InstructionRParams(rd, (byte) 0, rs));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] serialize() {
        return base.serialize();
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Sgtz> {
        @Override
        public Sgtz parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 2, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));
            Register32 rs = castToRegister32(parseRegister(registers, split[1]));

            return new Sgtz((byte) rd.getNumber(), (byte) rs.getNumber());
        }
    }
}
