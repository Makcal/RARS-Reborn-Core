package rarsreborn.core.core.instruction.riscv.instructions.rv32i;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;
import rarsreborn.core.exceptions.execution.IllegalInstructionException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

/**
 * Right shift with sign extension
 */
public class Srai extends InstructionI {
    public static final String NAME = "srai";
    public static final byte OPCODE = 0b0010011;
    public static final byte FUNCT_3 = 0x5;

    // Required for decoder
    @SuppressWarnings("unused")
    public Srai(InstructionIParams data) throws IllegalInstructionException {
        super(new InstructionIData(OPCODE, data.rd(), FUNCT_3, data.rs1(), data.imm()));
        if ((imm >> 10 & 0b1) == 0) {
            throw new IllegalInstructionException("\"srai\" instruction must have the 30th bit set");
        }
        checkFieldSize(imm ^ (imm & (1 << 10)), 5);
    }

    public Srai(byte rd, byte rs1, short imm) {
        super(new InstructionIData(OPCODE, rd, FUNCT_3, rs1, (short) (imm | 1 << 10)));
        checkFieldSize(this.imm ^ (this.imm & 1 << 10), 5);
    }

    public void exec(IRegisterFile<Register32> registerFile) throws IllegalRegisterException {
        registerFile.getRegisterByNumber(rd).setValue(
            registerFile.getRegisterByNumber(rs1).getValue() >> (imm & 0b1_1111)
        );
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] serialize() {
        byte[] serialized = super.serialize();
        serialized[0] |= (byte) (1 << 6);
        return serialized;
    }

    public static class Handler extends RiscV32InstructionHandler<Srai> {
        @Override
        public void handle(Srai instruction) throws IllegalRegisterException {
            instruction.exec(registerFile);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Srai> {
        @Override
        public Srai parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 3, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));
            Register32 rs1 = castToRegister32(parseRegister(registers, split[1]));
            short imm = parseShort(split[2]);

            try {
                checkFieldSize(imm, 5);
                imm |= 1 << 10;
                return new Srai((byte) rd.getNumber(), (byte) rs1.getNumber(), imm);
            } catch (IllegalArgumentException e) {
                throw new ImmediateTooLargeException(imm);
            }
        }
    }
}
