package rarsreborn.core.core.instruction.riscv.instructions.rv32d;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.core.register.floatpoint.RegisterFloat64;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

public class Fcvt_d_wu extends InstructionR {
    public static final String NAME = "fcvt.d.wu";
    public static final byte OPCODE = 0b1010011;
    public static final byte FUNCT_3 = 0b0;
    public static final byte FUNCT_7 = 0b1101001;

    public Fcvt_d_wu(InstructionRParams data) {
        super(new InstructionRData(OPCODE, data.rd(), FUNCT_3, data.rs1(), (byte) 1, FUNCT_7));
    }

    public Fcvt_d_wu(byte rd, byte rs1) {
        super(new InstructionRData(OPCODE, rd, FUNCT_3, rs1, (byte) 1, FUNCT_7));
    }

    public void exec(IRegisterFile<RegisterFloat64> floatRegisters, IRegisterFile<Register32> registers)
            throws IllegalRegisterException {
        floatRegisters.getRegisterByNumber(rd).setDouble(
            (double) Integer.toUnsignedLong(registers.getRegisterByNumber(rs1).getValue())
        );
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static class Handler extends RiscV32InstructionHandler<Fcvt_d_wu> {
        @Override
        public void handle(Fcvt_d_wu instruction) throws IllegalRegisterException {
            instruction.exec(floatRegisterFile, registerFile);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Fcvt_d_wu> {
        @Override
        public Fcvt_d_wu parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 2, NAME);

            RegisterFloat64 rd = castToRegisterFloat64(parseRegister(registers, split[0]));
            Register32 rs1 = castToRegister32(parseRegister(registers, split[1]));

            return new Fcvt_d_wu((byte) rd.getNumber(), (byte) rs1.getNumber());
        }
    }
}