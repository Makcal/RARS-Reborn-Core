package core.instruction.riscv.instructions.rv32i;

import compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import core.instruction.riscv.RiscV32InstructionHandler;
import core.instruction.riscv.formats.InstructionR;
import core.register.IRegisterFile;
import exceptions.compilation.*;
import core.register.Register32;

public class Or extends InstructionR {
    public static final String NAME = "or";
    public static final byte OPCODE = 0b0110011;
    public static final byte FUNCT_3 = 0x06;
    public static final byte FUNCT_7 = 0x0;

    public Or(InstructionRParams data) {
        super(new InstructionRData(OPCODE, data.rd(), FUNCT_3, data.rs1(), data.rs2(), FUNCT_7));
    }

    private void exec(IRegisterFile<Register32> registerFile) {
        try {
            registerFile.getRegisterByNumber(rd).setValue(
                registerFile.getRegisterByNumber(rs1).getValue()
                | registerFile.getRegisterByNumber(rs2).getValue()
            );
        } catch (UnknownRegisterException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static class Handler extends RiscV32InstructionHandler<Or> {
        @Override
        public void handle(Or instruction) {
            instruction.exec(registerFile);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Or> {
        @Override
        public Or parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 3, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));
            Register32 rs1 = castToRegister32(parseRegister(registers, split[1]));
            Register32 rs2 = castToRegister32(parseRegister(registers, split[2]));

            return new Or(
                new InstructionRParams(
                    (byte) rd.getNumber(),
                    (byte) rs1.getNumber(),
                    (byte) rs2.getNumber()
                )
            );
        }
    }
}