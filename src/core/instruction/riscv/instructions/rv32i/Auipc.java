package core.instruction.riscv.instructions.rv32i;

import compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import core.instruction.ILinkableInstruction;
import core.instruction.riscv.RiscV32InstructionHandler;
import core.instruction.riscv.formats.InstructionU;
import core.program.LinkRequest;
import core.register.IRegisterFile;
import core.register.Register32;
import exceptions.compilation.CompilationException;
import exceptions.compilation.UnknownRegisterException;

public class Auipc extends InstructionU implements ILinkableInstruction {
    public static final String NAME = "auipc";
    public static final byte OPCODE = 0b0010111;
    protected LinkRequest linkRequest = null;

    public Auipc(InstructionUParams data) {
        super(new InstructionUData(OPCODE, data.rd(), data.imm()));
    }

    protected void exec(IRegisterFile<Register32> registerFile, Register32 programCounter) {
        try {
            registerFile.getRegisterByNumber(rd).setValue(programCounter.getValue() - 4 + imm);
        } catch (UnknownRegisterException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void link(long address) {
        imm = (int) (address ^ (address & 0b1111_1111_1111));
    }

    @Override
    public LinkRequest getLinkRequest() {
        return linkRequest;
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static class Handler extends RiscV32InstructionHandler<Auipc> {
        @Override
        public void handle(Auipc instruction) {
            instruction.exec(registerFile, programCounter);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Auipc> {
        @Override
        public Auipc parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 2, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));

            String label = split[1];
            Auipc instruction = new Auipc(new InstructionUParams((byte) rd.getNumber(), 0));
            instruction.linkRequest = new LinkRequest(label);

            return instruction;
        }
    }
}
