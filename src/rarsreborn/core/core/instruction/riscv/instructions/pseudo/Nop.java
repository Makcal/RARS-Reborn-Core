package rarsreborn.core.core.instruction.riscv.instructions.pseudo;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Addi;
import rarsreborn.core.exceptions.compilation.CompilationException;

public class Nop implements IInstruction {
    public static final String NAME = "nop";
    public static final byte[] MACHINE_CODE
        = new Addi(new InstructionI.InstructionIParams((byte) 0, (byte) 0, (short) 0)).serialize();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] serialize() {
        return MACHINE_CODE;
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Nop> {
        @Override
        public Nop parse(String line) throws CompilationException {
            splitArguments(line, 0, NAME);

            return new Nop();
        }
    }
}
