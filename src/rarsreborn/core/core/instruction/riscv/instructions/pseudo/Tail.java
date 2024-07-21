package rarsreborn.core.core.instruction.riscv.instructions.pseudo;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.ILinkableInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.instruction.riscv.formats.InstructionU;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Auipc;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Jalr;
import rarsreborn.core.core.program.LinkRequest;
import rarsreborn.core.exceptions.compilation.CompilationException;

import static rarsreborn.core.core.instruction.riscv.RiscVInstruction.*;

public class Tail implements IInstruction, ILinkableInstruction {
    public static final String NAME = "tail";

    private final Auipc firstBase;
    private final Jalr secondBase;
    protected LinkRequest linkRequest = null;

    public Tail(int imm) {
        ImmediateSignedSplit split = splitImmediate(imm);
        firstBase = new Auipc(new InstructionU.InstructionUParams((byte) 6, split.high()));
        secondBase = new Jalr(new InstructionI.InstructionIParams((byte) 0, (byte) 6, split.low()));
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

    public static class Parser extends InstructionRegexParserRegisterBase<Tail> {
        @Override
        public Tail parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 1, NAME);

            String label = split[0];
            Tail instruction = new Tail((short) 0);
            instruction.linkRequest = new LinkRequest(label, (byte) 4);

            return instruction;
        }
    }
}
