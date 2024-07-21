package rarsreborn.core.core.instruction.riscv.instructions.pseudo;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Sltiu;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;

public class Seqz implements IInstruction {
    public static final String NAME = "seqz";

    private final Sltiu base;

    public Seqz(byte rd, byte rs) {
        base = new Sltiu(new InstructionI.InstructionIParams(rd, rs, (short) 1));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] serialize() {
        return base.serialize();
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Seqz> {
        @Override
        public Seqz parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 2, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));
            Register32 rs = castToRegister32(parseRegister(registers, split[1]));

            return new Seqz((byte) rd.getNumber(), (byte) rs.getNumber());
        }
    }
}
