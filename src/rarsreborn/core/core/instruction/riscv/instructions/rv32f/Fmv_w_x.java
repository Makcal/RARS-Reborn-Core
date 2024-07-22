package rarsreborn.core.core.instruction.riscv.instructions.rv32f;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.core.register.floatpoint.RegisterFloat64;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

public class Fmv_w_x extends InstructionR {
    public static final String NAME = "fmv.w.x";
    public static final byte OPCODE = 0b1010011;
    public static final byte FUNCT_3 = 0b000;
    public static final byte FUNCT_7 = 0b1111000;

    public Fmv_w_x(InstructionRParams data) {
        super(new InstructionRData(OPCODE, data.rd(), FUNCT_3, data.rs1(), (byte) 0, FUNCT_7));
    }

    public Fmv_w_x(byte rd, byte rs1) {
        super(new InstructionRData(OPCODE, rd, FUNCT_3, rs1, (byte) 0, FUNCT_7));
    }

    public void exec(IRegisterFile<RegisterFloat64> floatRegisters, IRegisterFile<Register32> registers)
            throws IllegalRegisterException {
        floatRegisters.getRegisterByNumber(rd).setFloat(
            Float.intBitsToFloat(registers.getRegisterByNumber(rs1).getValue())
        );
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static class Handler extends RiscV32InstructionHandler<Fmv_w_x> {
        @Override
        public void handle(Fmv_w_x instruction) throws IllegalRegisterException {
            instruction.exec(floatRegisterFile, registerFile);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Fmv_w_x> {
        @Override
        public Fmv_w_x parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 2, NAME);

            RegisterFloat64 rd = castToRegisterFloat64(parseRegister(registers, split[0]));
            Register32 rs1 = castToRegister32(parseRegister(registers, split[1]));

            return new Fmv_w_x((byte) rd.getNumber(), (byte) rs1.getNumber());
        }
    }
}