package export;

import compilation.compiler.ICompiler;
import compilation.compiler.riscv.RegexCompiler;
import compilation.decoder.riscv.RiscVDecoder;
import compilation.linker.RiscVLinker;
import core.instruction.riscv.instructions.pseudo.La;
import core.instruction.riscv.instructions.pseudo.Li;
import core.instruction.riscv.instructions.pseudo.Mv;
import core.instruction.riscv.instructions.rv32i.*;
import core.instruction.riscv.instructions.rv32m.Div;
import core.instruction.riscv.instructions.rv32m.Mul;
import core.memory.Memory32;
import core.register.Register32;
import core.register.Register32File;
import core.register.ZeroRegister32;
import core.riscvprogram.RiscVObjectFile;
import simulator.Simulator32;

public class Presets {
    public static Simulator32 classical = null;

    static {
        initRiscVSimulator();
    }

    private static void initRiscVSimulator() {
        try {
            String[] registerNames = new String[] {
                "ra", "sp", "gp", "tp",
                "t0", "t1", "t2",
                "s0", "s1",
                "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7",
                "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11",
                "t3", "t4", "t5", "t6"
            };
            Register32File registers = new Register32File();
            registers.addRegister(new ZeroRegister32(0, "zero"));
            registers.createRegistersFromNames(registerNames);
            registers.getRegisterByName("sp").setValue(Memory32.INITIAL_STACK_POINTER);

            ICompiler compiler = new RegexCompiler.RegexCompilerBuilder()
                .setProgramBuilder(new RiscVObjectFile.ProgramBuilder())
                .registerRegistersFromFile(registers)
                .registerInstruction(Addi.NAME, new Addi.Parser())
                .registerInstruction(Jal.NAME, new Jal.Parser())
                .registerInstruction(Auipc.NAME, new Auipc.Parser())
                .registerInstruction(Li.NAME, new Li.Parser())
                .registerInstruction(La.NAME, new La.Parser())
                .registerInstruction(Mv.NAME, new Mv.Parser())
                .registerInstruction(Add.NAME, new Add.Parser())
                .registerInstruction(Sub.NAME, new Sub.Parser())
                .registerInstruction(Mul.NAME, new Mul.Parser())
                .registerInstruction(Div.NAME, new Div.Parser())
                .registerInstruction(And.NAME, new And.Parser())
                .registerInstruction(Or.NAME, new Or.Parser())
                .registerInstruction(Xor.NAME, new Xor.Parser())
                .build();

            RiscVDecoder decoder = new RiscVDecoder.RiscVDecoderBuilder()
                .registerRInstruction(Add.OPCODE, Add.FUNCT_3, Add.FUNCT_7, Add.class)
                .registerRInstruction(Sub.OPCODE, Sub.FUNCT_3, Sub.FUNCT_7, Sub.class)
                .registerRInstruction(Mul.OPCODE, Mul.FUNCT_3, Mul.FUNCT_7, Mul.class)
                .registerRInstruction(Div.OPCODE, Div.FUNCT_3, Div.FUNCT_7, Div.class)
                .registerRInstruction(And.OPCODE, And.FUNCT_3, And.FUNCT_7, And.class)
                .registerRInstruction(Or.OPCODE, Or.FUNCT_3, Or.FUNCT_7, Or.class)
                .registerRInstruction(Xor.OPCODE, Xor.FUNCT_3, Xor.FUNCT_7, Xor.class)
                .registerIInstruction(Addi.OPCODE, Addi.FUNCT_3, Addi.class)
                .registerUInstruction(Auipc.OPCODE, Auipc.class)
                .registerJInstruction(Jal.OPCODE, Jal.class)
                .build();

            RiscVLinker linker = new RiscVLinker(decoder);

            Memory32 memory = new Memory32();

            Register32 programCounter = new Register32(32, "pc", Memory32.TEXT_SECTION_START);

            classical = new Simulator32(compiler, linker, decoder, registers, programCounter, memory)
                .registerHandler(Add.class, new Add.Handler())
                .registerHandler(Sub.class, new Sub.Handler())
                .registerHandler(Mul.class, new Mul.Handler())
                .registerHandler(Div.class, new Div.Handler())
                .registerHandler(And.class, new And.Handler())
                .registerHandler(Or.class, new Or.Handler())
                .registerHandler(Xor.class, new Xor.Handler())
                .registerHandler(Addi.class, new Addi.Handler())
                .registerHandler(Jal.class, new Jal.Handler())
                .registerHandler(Auipc.class, new Auipc.Handler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
