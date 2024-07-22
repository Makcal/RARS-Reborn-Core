package rarsreborn.core.core.instruction.riscv.instructions.pseudo;

import rarsreborn.core.compilation.compiler.riscv.InstructionRegexParserRegisterBase;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.riscv.RiscVInstruction;
import rarsreborn.core.core.instruction.riscv.formats.InstructionI;
import rarsreborn.core.core.instruction.riscv.formats.InstructionR;
import rarsreborn.core.core.instruction.riscv.instructions.rv32f.Feq_s;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.Xori;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.core.register.floatpoint.RegisterFloat64;
import rarsreborn.core.exceptions.compilation.CompilationException;

public class Fne_s implements IInstruction {
    public static final String NAME = "fne.s";

    private final Feq_s firstPart;
    private final Xori secondPart;

    public Fne_s(byte rd, byte rs1, byte rs2) {
        firstPart = new Feq_s(new InstructionR.InstructionRParams(rd, rs1, rs2));
        secondPart = new Xori(new InstructionI.InstructionIParams(rd, rd, (short) 1));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public byte[] serialize() {
        return RiscVInstruction.concatArrays(firstPart.serialize(), secondPart.serialize());
    }

    public static class Parser extends InstructionRegexParserRegisterBase<Fne_s> {
        @Override
        public Fne_s parse(String line) throws CompilationException {
            String[] split = splitArguments(line, 3, NAME);

            Register32 rd = castToRegister32(parseRegister(registers, split[0]));
            RegisterFloat64 rs1 = castToRegisterFloat64(parseRegister(registers, split[1]));
            RegisterFloat64 rs2 = castToRegisterFloat64(parseRegister(registers, split[2]));

            return new Fne_s((byte) rd.getNumber(), (byte) rs1.getNumber(), (byte) rs2.getNumber());
        }
    }
}
