package rarsreborn.core.core.instruction.riscv.instructions.pseudo;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.ILinkableInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionB;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Bltu;
import rarsreborn.core.core.program.LinkRequest;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.linking.TargetAddressTooLargeException;

public class Bgtu implements IInstruction, ILinkableInstruction {
    public static final String NAME = "bgt";

    private final Bltu base;
    protected LinkRequest linkRequest = null;

    public Bgtu(byte rs1, byte rs2, short imm) {
        base = new Bltu(new InstructionB.InstructionBParams(rs2, rs1, imm));
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

    public static class Parser extends InstructionRegexParserRegisterBase<Bgtu> {
        @Override
        public Bgtu parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 3, NAME);

            Register32 rs1 = castToRegister32(parseRegister(registers, split[0]));
            Register32 rs2 = castToRegister32(parseRegister(registers, split[1]));
            String label = split[2];

            Bgtu instruction = new Bgtu((byte) rs1.getNumber(), (byte) rs2.getValue(), (short) 0);
            instruction.linkRequest = new LinkRequest(label);
            return instruction;
        }
    }
}
