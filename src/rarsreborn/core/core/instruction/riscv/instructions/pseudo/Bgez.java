package rarsreborn.core.core.instruction.riscv.instructions.pseudo;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.ILinkableInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionB;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Bge;
import rarsreborn.core.core.program.LinkRequest;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.linking.TargetAddressTooLargeException;

public class Bgez implements IInstruction, ILinkableInstruction {
    public static final String NAME = "bgez";

    private final Bge base;
    protected LinkRequest linkRequest = null;

    public Bgez(byte rs, short imm) {
        base = new Bge(new InstructionB.InstructionBParams(rs, (byte) 0, imm));
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

    public static class Parser extends InstructionRegexParserRegisterBase<Bgez> {
        @Override
        public Bgez parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 2, NAME);

            Register32 rs = castToRegister32(parseRegister(registers, split[0]));
            String label = split[1];

            Bgez instruction = new Bgez((byte) rs.getNumber(), (short) 0);
            instruction.linkRequest = new LinkRequest(label);
            return instruction;
        }
    }
}
