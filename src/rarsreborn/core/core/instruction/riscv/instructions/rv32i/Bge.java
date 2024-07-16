package rarsreborn.core.core.instruction.riscv.instructions.rv32i;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.ILinkableInstruction;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionB;
import rarsreborn.core.core.program.LinkRequest;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;
import rarsreborn.core.exceptions.linking.LinkingException;
import rarsreborn.core.exceptions.linking.TargetAddressTooLargeException;

public class Bge extends InstructionB implements ILinkableInstruction {
    public static final String NAME = "bge";
    public static final byte OPCODE = 0b1100011;
    public static final byte FUNCT_3 = 0x5;
    protected LinkRequest linkRequest;

    public Bge(InstructionBParams params) {
        super(new InstructionBData(OPCODE, params.imm(), FUNCT_3, params.rs1(), params.rs2()));
    }

    public void exec(IRegisterFile<Register32> registers, Register32 programCounter) throws IllegalRegisterException {
        if (registers.getRegisterByNumber(rs1).getValue() >= registers.getRegisterByNumber(rs2).getValue()) {
            programCounter.setValue(programCounter.getValue() + asNegative(imm, 13));
        }
    }

    @Override
    public void link(long offset) throws LinkingException {
        offset ^= offset & 0b1;
        try {
            imm = (short) truncateNegative(offset, 13);
        } catch (ImmediateTooLargeException e) {
            throw new TargetAddressTooLargeException(offset);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public LinkRequest getLinkRequest() {
        return linkRequest;
    }

    public static class Handler extends RiscV32InstructionHandler<Bge> {
        @Override
        public void handle(Bge instruction) throws IllegalRegisterException {
            instruction.exec(registerFile, programCounter);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Bge> {
        @Override
        public Bge parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 3, NAME);

            Register32 rs1 = castToRegister32(parseRegister(registers, split[0]));
            Register32 rs2 = castToRegister32(parseRegister(registers, split[1]));
            String label = split[2];

            Bge instruction = new Bge(new InstructionBParams(
                (byte) rs1.getNumber(),
                (byte) rs2.getNumber(),
                (short) 0
            ));
            instruction.linkRequest = new LinkRequest(label);
            return instruction;
        }
    }
}
