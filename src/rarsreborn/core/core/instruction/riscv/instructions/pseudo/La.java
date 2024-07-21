package rarsreborn.core.core.instruction.riscv.instructions.pseudo;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.ILinkableInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.instruction.riscv.formats.InstructionU;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Addi;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Auipc;
import rarsreborn.core.core.program.LinkRequest;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;

import static rarsreborn.core.core.instruction.riscv.RiscVInstruction.*;

public class La implements IInstruction, ILinkableInstruction {
    public static final String NAME = "la";

    private final Auipc firstBase;
    private final Addi secondBase;
    protected LinkRequest linkRequest = null;

    public La(byte rd, int imm) {
        ImmediateSignedSplit split = splitImmediate(imm);
        firstBase = new Auipc(new InstructionU.InstructionUParams(rd, split.high()));
        secondBase = new Addi(new InstructionI.InstructionIParams(rd, rd, split.low()));
    }

    @Override
    public void link(long instructionPosition, long symbolAddress) {
        firstBase.link(instructionPosition, symbolAddress);
        secondBase.link(instructionPosition, symbolAddress);
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
        return concatArrays(firstBase.serialize(), secondBase.serialize());
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
