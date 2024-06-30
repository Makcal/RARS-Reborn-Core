package core.instruction.riscv.instructions.pseudo;

import compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import core.instruction.IInstruction;
import core.instruction.riscv.formats.InstructionR;
import core.instruction.riscv.instructions.rv32i.Add;
import core.register.Register32;
import exceptions.compilation.CompilationException;

public class Mv implements IInstruction {
    public static String NAME = "mv";

    private final Add base;

    public Mv(byte rd, byte rs2) {
        base = new Add(new InstructionR.InstructionRParams(rd, (byte) 0, rs2));
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
            Register32 rs2 = castToRegister32(parseRegister(registers, split[0]));

            return new Mv((byte) rd.getNumber(), (byte) rs2.getNumber());
        }
    }
}
