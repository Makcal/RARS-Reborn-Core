package rarsreborn.core.core.instruction.riscv.instructions.rv32m;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.exceptions.compilation.*;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

public class Rem extends InstructionR {
    public static final String NAME = "rem";
    public static final byte OPCODE = 0b0110011;
    public static final byte FUNCT_3 = 0x6;
    public static final byte FUNCT_7 = 0x01;

    public Rem(InstructionRParams data) {
        super(new InstructionRData(OPCODE, data.rd(), FUNCT_3, data.rs1(), data.rs2(), FUNCT_7));
    }

    private void exec(IRegisterFile<Register32> registerFile) throws IllegalRegisterException {
        registerFile.getRegisterByNumber(rd).setValue(
            registerFile.getRegisterByNumber(rs1).getValue()
            % registerFile.getRegisterByNumber(rs2).getValue()
        );
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static class Handler extends RiscV32InstructionHandler<Rem> {
        @Override
        public void handle(Rem instruction) throws IllegalRegisterException {
            instruction.exec(registerFile);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Rem> {
        @Override
        public Rem parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 3, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));
            Register32 rs1 = castToRegister32(parseRegister(registers, split[1]));
            Register32 rs2 = castToRegister32(parseRegister(registers, split[2]));

            return new Rem(
                new InstructionRParams((byte) rd.getNumber(), (byte) rs1.getNumber(), (byte) rs2.getNumber())
            );
        }
    }
}