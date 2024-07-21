package rarsreborn.core.core.instruction.riscv.instructions.rv32i;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;

public class Xori extends InstructionI {
    public static final String NAME = "xori";
    public static final byte OPCODE = 0b0010011;
    public static final byte FUNCT_3 = 0x4;

    public Xori(InstructionIParams data) {
        super(new InstructionIData(OPCODE, data.rd(), FUNCT_3, data.rs1(), data.imm()));
    }

    public void exec(IRegisterFile<Register32> registerFile) throws IllegalRegisterException {
        registerFile.getRegisterByNumber(rd).setValue(
            registerFile.getRegisterByNumber(rs1).getValue() ^ asNegative(imm, 12)
        );
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static class Handler extends RiscV32InstructionHandler<Xori> {
        @Override
        public void handle(Xori instruction) throws IllegalRegisterException {
            instruction.exec(registerFile);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Xori> {
        @Override
        public Xori parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 3, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));
            Register32 rs1 = castToRegister32(parseRegister(registers, split[1]));
            short imm = (short) truncateNegative(parseShort(split[2]), 12);

            try {
                return new Xori(new InstructionIParams((byte) rd.getNumber(), (byte) rs1.getNumber(), imm));
            } catch (IllegalArgumentException e) {
                throw new ImmediateTooLargeException(imm);
            }
        }
    }
}
