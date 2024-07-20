package rarsreborn.core.core.instruction.riscv.instructions.rv32m;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

/**
 * Multiplies a sign-extended number (first) with a zero-extended one. Returns higher 32 bits.
 */
public class Mulhsu extends InstructionR {
    public static final String NAME = "mulhsu";
    public static final byte OPCODE = 0b0110011;
    public static final byte FUNCT_3 = 0x2;
    public static final byte FUNCT_7 = 0x01;

    public Mulhsu(InstructionRParams data) {
        super(new InstructionRData(OPCODE, data.rd(), FUNCT_3, data.rs1(), data.rs2(), FUNCT_7));
    }

    public void exec(IRegisterFile<Register32> registerFile) throws IllegalRegisterException {
        long val1 = registerFile.getRegisterByNumber(rs1).getValue();
        long val2 = Integer.toUnsignedLong(registerFile.getRegisterByNumber(rs2).getValue());
        registerFile.getRegisterByNumber(rd).setValue((int) (val1 * val2 >> 32));
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static class Handler extends RiscV32InstructionHandler<Mulhsu> {
        @Override
        public void handle(Mulhsu instruction) throws IllegalRegisterException {
            instruction.exec(registerFile);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Mulhsu> {
        @Override
        public Mulhsu parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 3, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));
            Register32 rs1 = castToRegister32(parseRegister(registers, split[1]));
            Register32 rs2 = castToRegister32(parseRegister(registers, split[2]));

            return new Mulhsu(
                new InstructionRParams((byte) rd.getNumber(), (byte) rs1.getNumber(), (byte) rs2.getNumber())
            );
        }
    }
}