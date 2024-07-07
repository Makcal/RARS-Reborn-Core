package rarsreborn.core.core.instruction.riscv.instructions.rv32i;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.ILinkableInstruction;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.program.LinkRequest;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;
import rarsreborn.core.exceptions.compilation.UnknownRegisterException;

public class Srli extends InstructionI implements ILinkableInstruction {
    public static final String NAME = "srli";
    public static final byte OPCODE = 0b0010011;
    public static final byte FUNCT_3 = 0x5;

    public Srli(InstructionIParams data) {
        super(new InstructionIData(OPCODE, data.rd(), FUNCT_3, data.rs1(), data.imm()));
    }

    private void exec(IRegisterFile<Register32> registerFile) {
        try {
            registerFile.getRegisterByNumber(rd).setValue(registerFile.getRegisterByNumber(rs1).getValue() >> imm);
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
        return null;
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static class Handler extends RiscV32InstructionHandler<Srli> {
        @Override
        public void handle(Srli instruction) {
            instruction.exec(registerFile);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Srli> {
        @Override
        public Srli parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 3, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));
            Register32 rs1 = castToRegister32(parseRegister(registers, split[1]));
            short imm = parseShort(split[2]);

            try {
                return new Srli(new InstructionIParams((byte) rd.getNumber(), (byte) rs1.getNumber(), imm));
            } catch (IllegalArgumentException e) {
                throw new ImmediateTooLargeException(imm);
            }
        }
    }
}
