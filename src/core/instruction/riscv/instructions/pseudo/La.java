package core.instruction.riscv.instructions.pseudo;

import compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import core.instruction.IInstruction;
import core.instruction.ILinkableInstruction;
import core.instruction.riscv.formats.InstructionI;
import core.instruction.riscv.formats.InstructionU;
import core.instruction.riscv.instructions.rv32i.Addi;
import core.instruction.riscv.instructions.rv32i.Auipc;
import core.program.LinkRequest;
import core.register.Register32;
import exceptions.compilation.CompilationException;

public class La implements IInstruction, ILinkableInstruction {
    public static String NAME = "la";

    private final Auipc firstBase;
    private final Addi secondBase;
    protected LinkRequest linkRequest = null;

    public La(byte rd, int imm) {
        firstBase = new Auipc(new InstructionU.InstructionUParams(rd, imm ^ (imm & 0b1111_1111_1111)));
        secondBase = new Addi(new InstructionI.InstructionIParams(rd, rd, (short) (imm & 0b1111_1111_1111)));
    }

    @Override
    public void link(long address) {
        throw new RuntimeException("Should not have been come here");
    }

    @Override
    public LinkRequest getLinkRequest() {
        return linkRequest;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] serialize() {
        byte[] part1 = firstBase.serialize();
        byte[] part2 = secondBase.serialize();
        byte[] res = new byte[part1.length + part2.length];
        int i = 0;
        for (; i < part1.length; i++) {
            res[i] = part1[i];
        }
        for (int j = 0; j < part2.length; j++, i++) {
            res[i] = part2[j];
        }
        return res;
    }

    public static class Parser extends InstructionRegexParserRegisterBase<La> {
        @Override
        public La parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 2, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));

            String label = split[1];
            La instruction = new La((byte) rd.getNumber(), (short) 0);
            instruction.linkRequest = new LinkRequest(label, (byte) 4);

            return instruction;
        }
    }
}
