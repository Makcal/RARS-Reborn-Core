package rarsreborn.core.core.instruction.riscv.instructions.rv32i;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.ILinkableInstruction;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.program.LinkRequest;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;
import rarsreborn.core.exceptions.compilation.UnknownRegisterException;
import rarsreborn.core.exceptions.linking.LinkingException;
import rarsreborn.core.exceptions.linking.TargetAddressTooLargeException;

public class Jalr extends InstructionI implements ILinkableInstruction {
    public static final String NAME = "jalr";
    public static final byte OPCODE = 0b1100111;
    public static final byte FUNCT3 = 0x0;
    protected LinkRequest linkRequest;

    public Jalr(InstructionIParams data) {
        super(new InstructionIData(OPCODE, data.rd(), FUNCT3, data.rs1(), data.imm()));
    }

    protected void exec(IRegisterFile<Register32> registerFile, Register32 programCounter) {
        try {
            registerFile.getRegisterByNumber(rd).setValue(programCounter.getValue() + 4);
            programCounter.setValue(registerFile.getRegisterByNumber(rs1).getValue() + asNegative(imm, 12));
        } catch (UnknownRegisterException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void link(long offset) throws LinkingException {
        offset ^= offset & 0b1;
        try {
            imm = (short) truncateNegative(offset, 12);
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

    public static class Handler extends RiscV32InstructionHandler<Jalr> {
        @Override
        public void handle(Jalr instruction) {
            instruction.exec(registerFile, programCounter);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Jalr> {
        @Override
        public Jalr parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 3, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));
            Register32 rs1 = castToRegister32(parseRegister(registers, split[1]));
            String label = split[2];

            Jalr instruction = new Jalr(new InstructionIParams(
                (byte) rd.getNumber(),
                (byte) rs1.getNumber(),
                (short) 0
            ));
            instruction.linkRequest = new LinkRequest(label);
            return instruction;
        }
    }
}
