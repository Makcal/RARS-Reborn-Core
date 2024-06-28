import compilation.compiler.riscv.RegexCompiler;
import compilation.decoder.riscv.RiscVDecoder;
import core.instruction.riscv.instructions.rv32i.AddImmediate;
import core.instruction.riscv.formats.RiscVInstructionFormat;
import core.memory.Memory32;
import core.register.Register32File;
import core.riscvprogram.RiscVProgram;
import exceptions.compilation.CompilationException;
import exceptions.compilation.UnknownRegisterException;
import simulator.Simulator32;

import java.util.Arrays;

public class Example {
    public static void main(String[] args) {
        var regs = new Register32File(Arrays.asList("t0", "t1"));
        var compiler = new RegexCompiler.RegexCompilerBuilder()
            .setProgramBuilder(new RiscVProgram.Builder())
            .registerRegistersFromFile(regs)
            .registerInstruction(AddImmediate.NAME, new AddImmediate.Parser())
            .build();
        var decoder = new RiscVDecoder.RiscVDecoderBuilder()
            .registerIInstruction(
                AddImmediate.OPCODE, AddImmediate.FUNCT_3, RiscVInstructionFormat.I, AddImmediate.class
            )
            .build();
        var memory = new Memory32();
        var sim = new Simulator32(compiler, decoder, regs, memory)
            .registerHandler(AddImmediate.class, new AddImmediate.Handler(regs));
        try {
            sim.compile("""
                       .data
                            h: .string "123"
                       .text
                           addi t0, t0, 1
    
                           addi t1, t0, 2
                    """);
        } catch (CompilationException e) {
            throw new RuntimeException(e);
        }
        sim.run();
        try {
            System.out.printf("t0: %d\n", regs.getRegisterByName("t0").getValue());
            System.out.printf("t0: %d\n", regs.getRegisterByName("t1").getValue());
            System.out.printf(Arrays.toString(memory.readBytes(Memory32.DATA_SECTION_START, 4)));
        } catch (UnknownRegisterException e) {
            throw new RuntimeException(e);
        }
    }
}

