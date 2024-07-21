package rarsreborn.core.core.instruction.riscv.instructions.pseudo;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Jalr;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;

public class Jr implements IInstruction {
    public static final String NAME = "jr";

    private final Jalr base;

    public Jr(byte rs) {
        base = new Jalr(new InstructionI.InstructionIParams((byte) 0, rs, (short) 0));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] serialize() {
        return base.serialize();
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Jr> {
        @Override
        public Jr parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 1, NAME);

            Register32 rs = castToRegister32(parseRegister(registers, split[0]));

            return new Jr((byte) rs.getNumber());
        }
    }
}
