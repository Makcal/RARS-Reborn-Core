package rarsreborn.core.core.instruction.riscv.instructions.pseudo;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Addi;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;

public class Mv implements IInstruction {
    public static final String NAME = "mv";

    private final Addi base;

    public Mv(byte rd, byte rs2) {
        base = new Addi(new InstructionI.InstructionIParams(rd, rs2, (short) 0));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] serialize() {
        return base.serialize();
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Mv> {
        @Override
        public Mv parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 2, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));
            Register32 rs2 = castToRegister32(parseRegister(registers, split[1]));

            return new Mv((byte) rd.getNumber(), (byte) rs2.getNumber());
        }
    }
}
