package core.instruction.riscv.instructions.rv32i;

import compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import core.instruction.IInstructionHandler;
import core.instruction.ILinkableInstruction;
import core.instruction.riscv.RiscV32InstructionHandler;
import core.instruction.riscv.formats.InstructionJ;
import core.program.LinkRequest;
import core.register.IRegisterFile;
import core.register.Register32;
import exceptions.compilation.*;
import exceptions.linking.TargetAddressTooLargeException;

public class JumpAndLink extends InstructionJ implements ILinkableInstruction {
    public static final String NAME = "jal";
    public static final byte OPCODE = 0b1101111;
    protected LinkRequest linkRequest = null;

    public JumpAndLink(InstructionJParams data) {
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

    public static class Handler extends RiscV32InstructionHandler<JumpAndLink> {
        @Override
        public void handle(JumpAndLink instruction) {
            instruction.exec(registerFile, programCounter);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<JumpAndLink> {
        @Override
        public JumpAndLink parse(String line) throws CompilationException {
            String[] split = line.split(",");
            if (split.length != 2) {
                throw new WrongNumberOfArgumentsException(NAME, split.length, 2);
            }

            Register32 rd;
            try {
                rd = (Register32) registers.get(split[0]);
            } catch (ClassCastException e) {
                throw new WrongRegisterTypeException(Register32.class, registers.get(split[0]).getClass());
            }
            if (rd == null) {
                throw new UnknownRegisterException(split[0]);
            }

            String label = split[1];
            JumpAndLink instruction = new JumpAndLink(new InstructionJParams((byte) rd.getNumber(), 0));
            instruction.linkRequest = new LinkRequest(label);

            return instruction;
        }
    }
}
