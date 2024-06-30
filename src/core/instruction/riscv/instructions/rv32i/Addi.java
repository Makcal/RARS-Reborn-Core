package core.instruction.riscv.instructions.rv32i;

import compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import core.instruction.ILinkableInstruction;
import core.instruction.riscv.RiscV32InstructionHandler;
import core.instruction.riscv.formats.InstructionI;
import core.program.LinkRequest;
import core.register.IRegisterFile;
import core.register.Register32;
import exceptions.compilation.CompilationException;
import exceptions.compilation.ImmediateTooLargeException;
import exceptions.compilation.UnknownRegisterException;

public class Addi extends InstructionI implements ILinkableInstruction {
    public static final String NAME = "addi";
    public static final byte OPCODE = 0b0010011;
    public static final byte FUNCT_3 = 0x0;

    protected LinkRequest linkRequest = null;

    public Addi(InstructionIParams data) {
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
    public void link(long address) {
        imm = (short) (address & 0b1111_1111_1111);
    }

    @Override
    public LinkRequest getLinkRequest() {
        return linkRequest;
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static class Handler extends RiscV32InstructionHandler<Addi> {
        @Override
        public void handle(Addi instruction) {
            instruction.exec(registerFile);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Addi> {
        @Override
        public Addi parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 3, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));
            Register32 rs1 = castToRegister32(parseRegister(registers, split[1]));
            short imm = parseShort(split[2]);

            try {
                return new Addi(new InstructionIParams((byte) rd.getNumber(), (byte) rs1.getNumber(), imm));
            } catch (IllegalArgumentException e) {
                throw new ImmediateTooLargeException(imm);
            }
        }
    }
}
