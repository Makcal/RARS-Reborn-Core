package rarsreborn.core.core.instruction.riscv.instructions.rv32i;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.instruction.riscv.formats.InstructionS;
import rarsreborn.core.core.memory.IMemory;
import rarsreborn.core.core.register.IRegisterFile;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.exceptions.compilation.CompilationException;
import rarsreborn.core.exceptions.compilation.ImmediateTooLargeException;
import rarsreborn.core.exceptions.compilation.UnknownRegisterException;
import rarsreborn.core.exceptions.memory.MemoryAccessException;

public class Sw extends InstructionS{
    public static final String NAME = "sw";
    public static final byte OPCODE = 0b0100011;
    public static final byte FUNCT3 = 0x2;

    public Sw(InstructionSParams params) {
        super(new InstructionSData(OPCODE, params.imm(), FUNCT3, params.rs1(), params.rs2()));
    }

    public void exec(IRegisterFile<Register32> registers, IMemory memory) throws MemoryAccessException {
        try {
                memory.setMultiple(registers.getRegisterByNumber(rs1).getValue() + asNegative(imm, 12), 
                registers.getRegisterByNumber(rs2).getValue(), 
                4);
        } catch (UnknownRegisterException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    public static class Handler extends RiscV32InstructionHandler<Sw> {
        @Override
        public void handle(Sw instruction) throws MemoryAccessException {
            instruction.exec(registerFile, memory);
        }
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Sw> {
        @Override
        public Sw parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 3, NAME);

            Register32 rs1 = castToRegister32(parseRegister(registers, split[0]));
            Register32 rs2 = castToRegister32(parseRegister(registers, split[1]));
            short imm = (short) truncateNegative(parseShort(split[2]), 12);

            Sw instruction = new Sw(new InstructionSParams(
                (byte) rs1.getNumber(),
                (byte) rs2.getNumber(),
                (short) imm
            ));
            try {
                return instruction;
            } catch (IllegalArgumentException e) {
                throw new ImmediateTooLargeException(imm);
            }
        }
    }
}
