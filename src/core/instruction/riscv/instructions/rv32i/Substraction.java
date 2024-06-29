package core.instruction.riscv.instructions.rv32i;

import compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import core.instruction.IInstructionHandler;
import core.instruction.riscv.formats.InstructionR;
import core.register.IRegisterFile;
import exceptions.compilation.*;
import core.register.Register32;

public class Substraction extends InstructionR {
    public static final String NAME = "sub";
    public static final byte OPCODE = 0b0110011;
    public static final byte FUNCT_3 = 0x0;
    public static final byte FUNCT_7 = 0x20;

    public Substraction(InstructionRParams data) {
        super(new InstructionRData(OPCODE, data.rd(), FUNCT_3, data.rs1(), data.rs2(), FUNCT_7));
    }

    private void exec(IRegisterFile<Register32> registerFile) {
        try {
            registerFile.getRegisterByNumber(rd).setValue(
                registerFile.getRegisterByNumber(rs1).getValue()
                - registerFile.getRegisterByNumber(rs2).getValue()
            );
        } catch (UnknownRegisterException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static class Handler implements IInstructionHandler<Substraction> {
        final IRegisterFile<Register32> registerFile;

        public Handler(IRegisterFile<Register32> registerFile) {
            this.registerFile = registerFile;
        }

        @Override
        public void handle(Substraction instruction) {
            instruction.exec(registerFile);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Substraction> {
        @Override
        public Substraction parse(String line) throws CompilationException {
            String[] split = line.split(",");
            if (split.length != 3) {
                throw new WrongNumberOfArgumentsException(NAME, split.length, 3);
            }

            Register32 rd;
            try {
                rd = (Register32) registers.get(split[0]);
            } catch (ClassCastException e) {
                throw new WrongRegisterTypeException(Register32.class, registers.get(split[0]).getClass());
            }
            if (rd == null) {
                throw new UnknownRegisterException(split[0]);
            }

            Register32 rs1;
            try {
                rs1 = (Register32) registers.get(split[1]);
            } catch (ClassCastException e) {
                throw new WrongRegisterTypeException(Register32.class, registers.get(split[1]).getClass());
            }
            if (rs1 == null) {
                throw new UnknownRegisterException(split[1]);
            }

            Register32 rs2;
            try {
                rs2 = (Register32) registers.get(split[2]);
            } catch (ClassCastException e) {
                throw new WrongRegisterTypeException(Register32.class, registers.get(split[1]).getClass());
            }
            if (rs2 == null) {
                throw new UnknownRegisterException(split[2]);
            }

            return new Substraction(
                new InstructionRParams(
                    (byte) rd.getNumber(),
                    (byte) rs1.getNumber(),
                    (byte) rs2.getNumber()
                )
            );
        }
    }
}