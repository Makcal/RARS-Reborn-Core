package rarsreborn.core.core.instruction.riscv.instructions.rv32f;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.core.register.floatpoint.RegisterFloat64;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

public class Fmv_x_w extends InstructionR {
    public static final String NAME = "fmv.x.w";
    public static final byte OPCODE = 0b1010011;
    public static final byte FUNCT_3 = 0b000;
    public static final byte FUNCT_7 = 0b1110000;

    public Fmv_x_w(InstructionRParams data) {
        super(new InstructionRData(OPCODE, data.rd(), FUNCT_3, data.rs1(), (byte) 0, FUNCT_7));
    }

    public Fmv_x_w(byte rd, byte rs1) {
        super(new InstructionRData(OPCODE, rd, FUNCT_3, rs1, (byte) 0, FUNCT_7));
    }

    public void exec(IRegisterFile<RegisterFloat64> floatRegisters, IRegisterFile<Register32> registers)
            throws IllegalRegisterException {
        registers.getRegisterByNumber(rd).setValue(
            Float.floatToRawIntBits(floatRegisters.getRegisterByNumber(rs1).getFloat())
        );
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static class Handler extends RiscV32InstructionHandler<Fmv_x_w> {
        @Override
        public void handle(Fmv_x_w instruction) throws IllegalRegisterException {
            instruction.exec(floatRegisterFile, registerFile);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Fmv_x_w> {
        @Override
        public Fmv_x_w parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 2, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));
            RegisterFloat64 rs1 = castToRegisterFloat64(parseRegister(registers, split[1]));

            return new Fmv_x_w((byte) rd.getNumber(), (byte) rs1.getNumber());
        }
    }
}