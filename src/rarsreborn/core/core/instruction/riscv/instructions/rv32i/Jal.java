package rarsreborn.core.core.instruction.riscv.instructions.rv32i;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.ILinkableInstruction;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionJ;
import rarsreborn.core.core.program.LinkRequest;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.*;
import rarsreborn.core.exceptions.linking.TargetAddressTooLargeException;

public class Jal extends InstructionJ implements ILinkableInstruction {
    public static final String NAME = "jal";
    public static final byte OPCODE = 0b1101111;
    protected LinkRequest linkRequest = null;

    public Jal(InstructionJParams data) {
        super(new InstructionJData(OPCODE, data.rd(), data.imm()));
    }

    protected void exec(IRegisterFile<Register32> registerFile, Register32 programCounter) {
        try {
            registerFile.getRegisterByNumber(rd).setValue(programCounter.getValue());
            programCounter.setValue(programCounter.getValue() + imm);
        } catch (UnknownRegisterException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void link(long address) throws TargetAddressTooLargeException {
        address ^= address & 0b1;
        boolean negative = ((address >> 20) & 1) == 1;
        if (negative && (address | 0b1_1111_1111_1111_1111_1111) != -1) {
            throw new TargetAddressTooLargeException(address);
        }
        else if (negative) {
            address &= 0b1_1111_1111_1111_1111_1111;
        }
        if ((address ^ (address & 0b1_1111_1111_1111_1111_1110)) != 0) {
            throw new TargetAddressTooLargeException(address);
        }
        imm = (int) (address & 0b1_1111_1111_1111_1111_1110 | (negative ? -1 << 20 : 0));
    }

    @Override
    public LinkRequest getLinkRequest() {
        return linkRequest;
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static class Handler extends RiscV32InstructionHandler<Jal> {
        @Override
        public void handle(Jal instruction) {
            instruction.exec(registerFile, programCounter);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Jal> {
        @Override
        public Jal parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 2, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));

            String label = split[1];
            Jal instruction = new Jal(new InstructionJParams((byte) rd.getNumber(), 0));
            instruction.linkRequest = new LinkRequest(label);

            return instruction;
        }
    }
}
