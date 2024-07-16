package rarsreborn.core.core.instruction.riscv.instructions.rv32i;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.execution.ExecutionBreakException;
import rarsreborn.core.exceptions.execution.ExecutionException;
import rarsreborn.core.exceptions.execution.IllegalInstructionException;

public class Ebreak extends InstructionI {
    public static final String NAME = "ebreak";
    public static final byte OPCODE = 0b1110011;
    public static final byte FUNCT_3 = 0x0;

    public Ebreak() {
        super(new InstructionIData(OPCODE, (byte) 0, FUNCT_3, (byte) 0, (short) 1));
    }

    // Required for parsing
    @SuppressWarnings("unused")
    public Ebreak(InstructionIParams data) throws IllegalInstructionException {
        super(new InstructionIData(OPCODE, data.rd(), FUNCT_3, data.rs1(), data.imm()));
        checkFieldSize(rd, 0);
        checkFieldSize(rs1, 0);
        if (imm != 0x1) {
            throw new IllegalInstructionException("\"ebreak\" instruction must have imm equal to 0x1");
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String toString() {
        return getName();
    }

    public void exec(Register32 programCounter) throws ExecutionException {
        programCounter.setValue(programCounter.getValue() + 4);
        throw new ExecutionBreakException();
    }

    public static class Handler extends RiscV32InstructionHandler<Ebreak> {
        @Override
        public void handle(Ebreak instruction) throws ExecutionException {
            instruction.exec(programCounter);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Ebreak> {
        @Override
        public Ebreak parse(String line) throws CompilationException {
            splitArguments(line, 0, NAME);

            return new Ebreak();
        }
    }
}
