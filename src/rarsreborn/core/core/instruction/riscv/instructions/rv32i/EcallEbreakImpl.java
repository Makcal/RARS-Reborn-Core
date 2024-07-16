package rarsreborn.core.core.instruction.riscv.instructions.rv32i;

import rarsreborn.core.core.environment.IExecutionEnvironment;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.execution.ExecutionException;

/**
 * A general class for `ecall` and `ebreak` instructions. Used to avoid one more instruction format.
 */
public class EcallEbreakImpl extends InstructionI {
    public static final byte OPCODE = 0b1110011;
    public static final byte FUNCT_3 = 0x0;

    public EcallEbreakImpl(InstructionIParams data) {
        super(new InstructionIData(OPCODE, data.rd(), FUNCT_3, data.rs1(), data.imm()));
        checkFieldSize(imm, 1);
    }

    @Override
    public String getName() {
        return imm == 0 ? "ecall" : "ebreak";
    }

    @Override
    public String toString() {
        return getName();
    }

    public void exec(IExecutionEnvironment environment, Register32 programCounter) throws ExecutionException {
        if (imm == 0) new Ecall().exec(environment);
        else new Ebreak().exec(programCounter);
    }

    public static class Handler extends RiscV32InstructionHandler<EcallEbreakImpl> {
        @Override
        public void handle(EcallEbreakImpl instruction) throws ExecutionException {
            instruction.exec(executionEnvironment, programCounter);
        }
    }
}
