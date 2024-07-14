package rarsreborn.core.core.instruction.riscv.instructions.rv32i;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;
import rarsreborn.core.exceptions.execution.IllegalInstructionException;

/**
 * Right shift with sign extension
 */
public class Srai extends InstructionI {
    public static final String NAME = "srai";
    public static final byte OPCODE = 0b0010011;
    public static final byte FUNCT_3 = 0x5;

    public Srai(InstructionIParams data) throws IllegalInstructionException {
        super(new InstructionIData(OPCODE, data.rd(), FUNCT_3, data.rs1(), data.imm()));
        if ((imm >> 10 & 0b1) == 0) {
            throw new IllegalInstructionException("\"srai\" instruction must have the 30th bit set");
        }
        checkFieldSize(imm ^ (imm & (1 << 10)), 5);
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
                return new Srai(new InstructionIParams((byte) rd.getNumber(), (byte) rs1.getNumber(), imm));
            } catch (IllegalArgumentException e) {
                throw new ImmediateTooLargeException(imm);
            } catch (IllegalInstructionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
