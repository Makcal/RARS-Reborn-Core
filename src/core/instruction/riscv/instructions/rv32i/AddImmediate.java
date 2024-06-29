package core.instruction.riscv.instructions.rv32i;

import compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import core.instruction.IInstructionHandler;
import core.instruction.riscv.formats.InstructionI;
import core.register.IRegisterFile;
import core.register.Register32;
import exceptions.compilation.*;

public class AddImmediate extends InstructionI {
    public static final String NAME = "addi";
    public static final byte OPCODE = 0b0010011;
    public static final byte FUNCT_3 = 0x0;

    public AddImmediate(InstructionIParams data) {
        super(new InstructionIData(OPCODE, data.rd(), FUNCT_3, data.rs1(), data.imm()));
    }

    private void exec(IRegisterFile<Register32> registerFile) {
        try {
            registerFile.getRegisterByNumber(rd).setValue(registerFile.getRegisterByNumber(rs1).getValue() + imm);
        } catch (UnknownRegisterException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static class Handler implements IInstructionHandler<AddImmediate> {
        final IRegisterFile<Register32> registerFile;

        public Handler(IRegisterFile<Register32> registerFile) {
            this.registerFile = registerFile;
        }

        @Override
        public void handle(AddImmediate instruction) {
            instruction.exec(registerFile);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<AddImmediate> {
        @Override
        public AddImmediate parse(String line) throws CompilationException {
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

            short imm;
            try {
                imm = Short.parseShort(split[2]);
            } catch (NumberFormatException e) {
                throw new ExpectedIntegerException(split[2]);
            }

            try {
                return new AddImmediate(new InstructionIParams((byte) rd.getNumber(), (byte) rs1.getNumber(), imm));
            } catch (IllegalArgumentException e) {
                throw new ImmediateTooLargeException(imm);
            }
        }
    }
}
