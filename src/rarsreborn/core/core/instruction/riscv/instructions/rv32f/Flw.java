package rarsreborn.core.core.instruction.riscv.instructions.rv32f;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.memory.IMemory;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.core.register.floatpoint.RegisterFloat64;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;
import rarsreborn.core.exceptions.memory.MemoryAccessException;

public class Flw extends InstructionI {
    public static final String NAME = "flw";
    public static final byte OPCODE = 0b0000111;
    public static final byte FUNCT_3 = 0b10;

    public Flw(InstructionIParams params) {
        super(new InstructionIData(OPCODE, params.rd(), FUNCT_3, params.rs1(), params.imm()));
    }

    public void exec(IRegisterFile<RegisterFloat64> floatRegisters, IRegisterFile<Register32> registers, IMemory memory)
            throws MemoryAccessException, IllegalRegisterException {
        floatRegisters.getRegisterByNumber(rd).setInt(
            (int) memory.getMultiple(registers.getRegisterByNumber(rs1).getValue() + asNegative(imm, 12), 4)
        );
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static class Handler extends RiscV32InstructionHandler<Flw> {
        @Override
        public void handle(Flw instruction) throws MemoryAccessException, IllegalRegisterException {
            instruction.exec(floatRegisterFile, registerFile, memory);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Flw> {
        @Override
        public Flw parse(String line) throws CompilationException {
            LoadStoreFormatArguments args = parseLoadStoreFormat(line);

            RegisterFloat64 rd = castToRegisterFloat64(parseRegister(registers, args.valueRegister()));
            Register32 rs1 = castToRegister32(parseRegister(registers, args.addressRegister()));
            short imm = (short) truncateNegative(args.offset(), 12);

            try {
                return new Flw(new InstructionIParams((byte) rd.getNumber(), (byte) rs1.getNumber(), imm));
            } catch (IllegalArgumentException e) {
                throw new ImmediateTooLargeException(imm);
            }
        }
    }
}