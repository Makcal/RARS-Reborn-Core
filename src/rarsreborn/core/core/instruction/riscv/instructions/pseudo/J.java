package rarsreborn.core.core.instruction.riscv.instructions.pseudo;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.ILinkableInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionJ;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Jal;
import rarsreborn.core.core.program.LinkRequest;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.linking.TargetAddressTooLargeException;

public class J implements IInstruction, ILinkableInstruction {
    public static final String NAME = "j";

    private final Jal base;
    protected LinkRequest linkRequest = null;

    public J(int imm) {
        base = new Jal(new InstructionJ.InstructionJParams((byte) 0, imm));
    }

    @Override
    public void link(long instructionPosition, long symbolAddress) throws TargetAddressTooLargeException {
        base.link(instructionPosition, symbolAddress);
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
        return base.serialize();
    }

    public static class Parser extends InstructionRegexParserRegisterBase<J> {
        @Override
        public J parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 1, NAME);

            String label = split[0];

            J instruction = new J(0);
            instruction.linkRequest = new LinkRequest(label);
            return instruction;
        }
    }
}
