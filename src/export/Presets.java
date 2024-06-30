package export;

import compilation.compiler.ICompiler;
import compilation.compiler.riscv.RegexCompiler;
import compilation.decoder.riscv.RiscVDecoder;
import compilation.linker.RiscVLinker;
import core.instruction.riscv.instructions.rv32i.Addi;
import core.instruction.riscv.instructions.rv32i.Jal;
import core.memory.Memory32;
import core.register.Register32;
import core.register.Register32File;
import core.riscvprogram.RiscVObjectFile;
import simulator.Simulator32;

import java.util.Arrays;

public class Presets {
    public static Simulator32 classical = null;

    static {
        initRiscVSimulator();
    }

    private static void initRiscVSimulator() {
        try {
            String[] registerNames = new String[] {
                "zero", "ra", "sp", "gp", "tp",
                "t0", "t1", "t2",
                "s0", "s1",
                "a0", "a1", "a2", "a3", "a4", "a5", "a6", "a7",
                "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11",
                "t3", "t4", "t5", "t6"
            };
            Register32File registers = new Register32File(Arrays.asList(registerNames));
            registers.getRegisterByName("sp").setValue(Memory32.INITIAL_STACK_POINTER);

            ICompiler compiler = new RegexCompiler.RegexCompilerBuilder()
                .setProgramBuilder(new RiscVObjectFile.ProgramBuilder())
                .registerRegistersFromFile(registers)
                .registerInstruction(Addi.NAME, new Addi.Parser())
                .registerInstruction(Jal.NAME, new Jal.Parser())
                .build();

            RiscVDecoder decoder = new RiscVDecoder.RiscVDecoderBuilder()
                .registerIInstruction(Addi.OPCODE, Addi.FUNCT_3, Addi.class)
                .registerJInstruction(Jal.OPCODE, Jal.class)
                .build();

            RiscVLinker linker = new RiscVLinker(decoder);

            Memory32 memory = new Memory32();

            Register32 programCounter = new Register32(32, "pc", Memory32.TEXT_SECTION_START);

            classical = new Simulator32(compiler, linker, decoder, registers, programCounter, memory)
                .registerHandler(Addi.class, new Addi.Handler())
                .registerHandler(Jal.class, new Jal.Handler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
