package rarsreborn.core.core.instruction.riscv.instructions.rv32i;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.ILinkableInstruction;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionU;
import rarsreborn.core.core.program.LinkRequest;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

public class Auipc extends InstructionU implements ILinkableInstruction {
    public static final String NAME = "auipc";
    public static final byte OPCODE = 0b0010111;
    protected LinkRequest linkRequest = null;

    public Auipc(InstructionUParams data) {
        super(new InstructionUData(OPCODE, data.rd(), data.imm()));
    }

    protected void exec(IRegisterFile<Register32> registerFile, Register32 programCounter)
            throws IllegalRegisterException {
        registerFile.getRegisterByNumber(rd).setValue(programCounter.getValue() + imm);
    }

    @Override
    public void link(long instructionPosition, long symbolAddress) {
        long offset = symbolAddress - instructionPosition;
        imm = splitImmediate((int) offset).high();
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
        public void handle(Auipc instruction) throws IllegalRegisterException {
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
