package rarsreborn.core.core.instruction.riscv.instructions.pseudo;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Sltu;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;

public class Snez implements IInstruction {
    public static final String NAME = "snez";

    private final Sltu base;

    public Snez(byte rd, byte rs) {
        base = new Sltu(new InstructionR.InstructionRParams(rd, (byte) 0, rs));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] serialize() {
        return base.serialize();
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Snez> {
        @Override
        public Snez parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 2, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));
            Register32 rs = castToRegister32(parseRegister(registers, split[1]));

            return new Snez((byte) rd.getNumber(), (byte) rs.getNumber());
        }
    }
}
