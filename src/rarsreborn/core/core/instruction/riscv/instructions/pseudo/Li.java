package rarsreborn.core.core.instruction.riscv.instructions.pseudo;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Addi;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;

import static rarsreborn.core.core.instruction.riscv.RiscVInstruction.truncateNegative;

public class Li implements IInstruction {
    public static final String NAME = "li";

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
            short imm = (short) truncateNegative(parseShort(split[1]), 12);

            try {
                return new Li((byte) rd.getNumber(), imm);
            } catch (IllegalArgumentException e) {
                throw new ImmediateTooLargeException(imm);
            }
        }
    }
}
