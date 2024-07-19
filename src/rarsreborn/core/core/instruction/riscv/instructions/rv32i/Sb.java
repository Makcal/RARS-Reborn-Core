package rarsreborn.core.core.instruction.riscv.instructions.rv32i;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionS;
import rarsreborn.core.core.memory.IMemory;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;
import rarsreborn.core.exceptions.memory.MemoryAccessException;

public class Sb extends InstructionS {
    public static final String NAME = "sb";
    public static final byte OPCODE = 0b0100011;
    public static final byte FUNCT_3 = 0x0;

    public Sb(InstructionSParams params) {
        super(new InstructionSData(OPCODE, params.imm(), FUNCT_3, params.rs1(), params.rs2()));
    }

    public void exec(IRegisterFile<Register32> registers, IMemory memory)
            throws MemoryAccessException, IllegalRegisterException {
        memory.setMultiple(
            registers.getRegisterByNumber(rs1).getValue() + asNegative(imm, 12),
            (byte) registers.getRegisterByNumber(rs2).getValue(),
            1
        );
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static class Handler extends RiscV32InstructionHandler<Sb> {
        @Override
        public void handle(Sb instruction) throws MemoryAccessException, IllegalRegisterException {
            instruction.exec(registerFile, memory);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Sb> {
        @Override
        public Sb parse(String line) throws CompilationException {
            LoadStoreFormatArguments args = parseLoadStoreFormat(line);

            Register32 rs2 = castToRegister32(parseRegister(registers, args.valueRegister()));
            Register32 rs1 = castToRegister32(parseRegister(registers, args.addressRegister()));
            short imm = (short) truncateNegative(args.offset(), 12);

            try {
                return new Sb(new InstructionSParams((byte) rs1.getNumber(), (byte) rs2.getNumber(), imm));
            } catch (IllegalArgumentException e) {
                throw new ImmediateTooLargeException(imm);
            }
        }
    }
}
