package rarsreborn.core.core.instruction.riscv.instructions.rv32i;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.memory.IMemory;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;
import rarsreborn.core.exceptions.execution.IllegalRegisterException;
import rarsreborn.core.exceptions.memory.MemoryAccessException;

public class Lh extends InstructionI {
    public static final String NAME = "lh";
    public static final byte OPCODE = 0b0000011;
    public static final byte FUNCT_3 = 0x1;

    public Lh(InstructionIParams params) {
        super(new InstructionIData(OPCODE, params.rd(), FUNCT_3, params.rs1(), params.imm()));
    }

    public void exec(IRegisterFile<Register32> registers, IMemory memory)
            throws MemoryAccessException, IllegalRegisterException {
        registers.getRegisterByNumber(rd).setValue(
            (short) memory.getMultiple(
                registers.getRegisterByNumber(rs1).getValue() + asNegative(imm, 12),
                2
            )
        );
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static class Handler extends RiscV32InstructionHandler<Lh> {
        @Override
        public void handle(Lh instruction) throws MemoryAccessException, IllegalRegisterException {
            instruction.exec(registerFile, memory);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Lh> {
        @Override
        public Lh parse(String line) throws CompilationException {
            LoadStoreFormatArguments args = parseLoadStoreFormat(line);

            Register32 rd = castToRegister32(parseRegister(registers, args.valueRegister()));
            Register32 rs1 = castToRegister32(parseRegister(registers, args.addressRegister()));
            short imm = (short) truncateNegative(args.offset(), 12);

            try {
                return new Lh(new InstructionIParams((byte) rd.getNumber(), (byte) rs1.getNumber(), imm));
            } catch (IllegalArgumentException e) {
                throw new ImmediateTooLargeException(imm);
            }
        }
    }
}
