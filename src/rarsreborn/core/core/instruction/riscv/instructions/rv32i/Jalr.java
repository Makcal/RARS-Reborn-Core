package rarsreborn.core.core.instruction.riscv.instructions.rv32i;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

public class Jalr extends InstructionI {
    public static final String NAME = "jalr";
    public static final byte OPCODE = 0b1100111;
    public static final byte FUNCT_3 = 0x0;

    public Jalr(InstructionIParams data) {
        super(new InstructionIData(OPCODE, data.rd(), FUNCT_3, data.rs1(), data.imm()));
    }

    protected void exec(IRegisterFile<Register32> registerFile, Register32 programCounter) throws IllegalRegisterException {
        registerFile.getRegisterByNumber(rd).setValue(programCounter.getValue() + 4);
        programCounter.setValue(registerFile.getRegisterByNumber(rs1).getValue() + asNegative(imm, 12));
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static class Handler extends RiscV32InstructionHandler<Jalr> {
        @Override
        public void handle(Jalr instruction) throws IllegalRegisterException {
            instruction.exec(registerFile, programCounter);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Jalr> {
        @Override
        public Jalr parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 3, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));
            Register32 rs1 = castToRegister32(parseRegister(registers, split[1]));
            short imm = parseShort(split[2]);

            return new Jalr(new InstructionIParams(
                (byte) rd.getNumber(),
                (byte) rs1.getNumber(),
                imm
            ));
        }
    }
}
