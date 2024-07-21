package rarsreborn.core.core.instruction.riscv.instructions.rv32fd;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionS;
import rarsreborn.core.core.memory.IMemory;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.core.register.floatpoint.RegisterFloat64;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;
import rarsreborn.core.exceptions.memory.MemoryAccessException;

public class Fsd extends InstructionS {
    public static final String NAME = "fsd";
    public static final byte OPCODE = 0b0100111;
    public static final byte FUNCT_3 = 0x2;

    public Fsd(InstructionSParams params) {
        super(new InstructionSData(OPCODE, params.imm(), FUNCT_3, params.rs1(), params.rs2()));
    }

    public void exec(IRegisterFile<Register32> registers, IRegisterFile<RegisterFloat64> floatRegisters, IMemory memory)
            throws MemoryAccessException, IllegalRegisterException {
        memory.setMultiple(
            registers.getRegisterByNumber(rs1).getValue() + asNegative(imm, 12),
            floatRegisters.getRegisterByNumber(rs2).getLong(),
            8
        );
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static class Handler extends RiscV32InstructionHandler<Fsd> {
        @Override
        public void handle(Fsd instruction) throws MemoryAccessException, IllegalRegisterException {
            instruction.exec(registerFile, floatRegisterFile, memory);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Fsd> {
        @Override
        public Fsd parse(String line) throws CompilationException {
            LoadStoreFormatArguments args = parseLoadStoreFormat(line);

            RegisterFloat64 rs2 = castToRegisterFloat64(parseRegister(registers, args.valueRegister()));
            Register32 rs1 = castToRegister32(parseRegister(registers, args.addressRegister()));
            short imm = (short) truncateNegative(args.offset(), 12);

            try {
                return new Fsd(new InstructionSParams((byte) rs1.getNumber(), (byte) rs2.getNumber(), imm));
            } catch (IllegalArgumentException e) {
                throw new ImmediateTooLargeException(imm);
            }
        }
    }
}
