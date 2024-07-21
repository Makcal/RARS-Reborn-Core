package rarsreborn.core.core.instruction.riscv.instructions.pseudo;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Jalr;
import rarsreborn.core.exceptions.compilation.CompilationException;

public class Ret implements IInstruction {
    public static final String NAME = "ret";

    private final Jalr base;

    public Ret() {
        base = new Jalr(new InstructionI.InstructionIParams((byte) 0, (byte) 1, (short) 0));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] serialize() {
        return base.serialize();
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Ret> {
        @Override
        public Ret parse(String line) throws CompilationException {
            splitArguments(line, 0, NAME);

            return new Ret();
        }
    }
}
