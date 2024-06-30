package core.instruction.riscv.instructions.rv32i;

import compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import core.instruction.ILinkableInstruction;
import core.instruction.riscv.RiscV32InstructionHandler;
import core.instruction.riscv.formats.InstructionJ;
import core.program.LinkRequest;
import core.register.IRegisterFile;
import core.register.Register32;
import exceptions.compilation.*;
import exceptions.linking.TargetAddressTooLargeException;

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
            programCounter.setValue(programCounter.getValue() - 4 + imm);
        } catch (UnknownRegisterException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void link(long address) throws TargetAddressTooLargeException {
        address ^= address & 0b1;
        if ((address ^ (address & 0b1111_1111_1111_1111_1110)) != 0) {
            throw new TargetAddressTooLargeException(linkRequest.label());
        }
        imm = (int) (address & 0b1111_1111_1111_1111_1110);
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
