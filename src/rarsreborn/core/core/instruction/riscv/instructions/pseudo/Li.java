package rarsreborn.core.core.instruction.riscv.instructions.pseudo;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionU;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Addi;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Lui;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;

public class Li implements IInstruction {
    public static final String NAME = "li";

    protected final byte rd;
    protected final int imm;

    public Li(byte rd, int imm) {
        this.rd = rd;
        this.imm = imm;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] serialize() {
        byte[] result;
        final int ADDI_MAX = (1 << (12 - 1)) - 1;
        if (~ADDI_MAX <= imm && imm <= ADDI_MAX) {
            result = new Addi(new Addi.InstructionIParams(rd, (byte) 0, (short) (imm & 0b1111_1111_1111))).serialize();
        } else if ((imm & 0b1111_1111_1111) == 0) {
            result = new Lui(new InstructionU.InstructionUParams(rd, imm)).serialize();
        } else {
            result = new byte[8];
            int luiExtra = (imm & 0b1000_0000_0000) != 0 ? 0b1_0000_0000_0000 : 0;
            byte[] lui = new Lui(new InstructionU.InstructionUParams(rd, (imm >> 12 << 12) + luiExtra)).serialize();
            byte[] addi = new Addi(
                new Addi.InstructionIParams(rd, rd, (short) (imm & 0b1111_1111_1111))
            ).serialize();
            System.arraycopy(lui, 0, result, 0, 4);
            System.arraycopy(addi, 0, result, 4, 4);
        }
        return result;
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Li> {
        @Override
        public Li parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 2, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));
            int imm = parseInt(split[1]);

            try {
                return new Li((byte) rd.getNumber(), imm);
            } catch (IllegalArgumentException e) {
                throw new ImmediateTooLargeException(imm);
            }
        }
    }
}
