package core.instruction.riscv.instructions.pseudo;

import compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import core.instruction.IInstruction;
import core.instruction.riscv.formats.InstructionI;
import core.instruction.riscv.instructions.rv32i.Addi;
import core.register.Register32;
import exceptions.compilation.CompilationException;
import exceptions.compilation.ImmediateTooLargeException;

public class Li implements IInstruction {
    public static String NAME = "li";

    private final Addi base;

    public Li(byte rd, short imm) {
        base = new Addi(new InstructionI.InstructionIParams(rd, (byte) 0, imm));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] serialize() {
        return base.serialize();
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Li> {
        @Override
        public Li parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 2, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));
            short imm = parseShort(split[1]);

            try {
                return new Li((byte) rd.getNumber(), imm);
            } catch (IllegalArgumentException e) {
                throw new ImmediateTooLargeException(imm);
            }
        }
    }
}
