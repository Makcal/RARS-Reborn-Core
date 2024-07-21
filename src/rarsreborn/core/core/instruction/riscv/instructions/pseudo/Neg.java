package rarsreborn.core.core.instruction.riscv.instructions.pseudo;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Sub;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;

public class Neg implements IInstruction {
    public static final String NAME = "neg";

    private final Sub base;

    public Neg(byte rd, byte rs) {
        base = new Sub(new InstructionR.InstructionRParams(rd, (byte) 0, rs));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] serialize() {
        return base.serialize();
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Neg> {
        @Override
        public Neg parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 2, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));
            Register32 rs = castToRegister32(parseRegister(registers, split[1]));

            return new Neg((byte) rd.getNumber(), (byte) rs.getNumber());
        }
    }
}
