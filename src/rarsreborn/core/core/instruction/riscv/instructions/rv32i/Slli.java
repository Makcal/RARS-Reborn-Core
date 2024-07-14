package rarsreborn.core.core.instruction.riscv.instructions.rv32i;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

public class Slli extends InstructionI {
    public static final String NAME = "slli";
    public static final byte OPCODE = 0b0010011;
    public static final byte FUNCT_3 = 0x1;

    public Slli(InstructionIParams data) {
        super(new InstructionIData(OPCODE, data.rd(), FUNCT_3, data.rs1(), data.imm()));
        checkFieldSize(imm, 5);
    }

    private void exec(IRegisterFile<Register32> registerFile) throws IllegalRegisterException {
        registerFile.getRegisterByNumber(rd).setValue(
            registerFile.getRegisterByNumber(rs1).getValue() << (imm & 0b1_1111)
        );
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static class Handler extends RiscV32InstructionHandler<Slli> {
        @Override
        public void handle(Slli instruction) throws IllegalRegisterException {
            instruction.exec(registerFile);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Slli> {
        @Override
        public Slli parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 3, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));
            Register32 rs1 = castToRegister32(parseRegister(registers, split[1]));
            short imm = parseShort(split[2]);

            try {
                checkFieldSize(imm, 5);
                return new Slli(new InstructionIParams((byte) rd.getNumber(), (byte) rs1.getNumber(), imm));
            } catch (IllegalArgumentException e) {
                throw new ImmediateTooLargeException(imm);
            }
        }
    }
}
