package rarsreborn.core.core.instruction.riscv.instructions.rv32i;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.environment.IExecutionEnvironment;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.execution.ExecutionException;

public class Ecall extends InstructionI {
    public static final String NAME = "ecall";
    public static final byte OPCODE = 0b1110011;
    public static final byte FUNCT_3 = 0x0;

    public Ecall() {
        super(new InstructionIData(OPCODE, (byte) 0, FUNCT_3, (byte) 0, (short) 0));
    }

    // Required for parsing
    @SuppressWarnings("unused")
    public Ecall(InstructionIParams data) {
        super(new InstructionIData(OPCODE, data.rd(), FUNCT_3, data.rs1(), data.imm()));
        checkFieldSize(rd, 0);
        checkFieldSize(rs1, 0);
        checkFieldSize(imm, 0);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String toString() {
        return getName();
    }

    public void exec(IExecutionEnvironment environment) throws ExecutionException {
        environment.call();
    }

    public static class Handler extends RiscV32InstructionHandler<Ecall> {
        @Override
        public void handle(Ecall instruction) throws ExecutionException {
            instruction.exec(executionEnvironment);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Ecall> {
        @Override
        public Ecall parse(String line) throws CompilationException {
            splitArguments(line, 0, NAME);

            return new Ecall();
        }
    }
}
