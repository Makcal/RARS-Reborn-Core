import compilation.compiler.riscv.RegexCompiler;
import compilation.decoder.riscv.RiscVDecoder;
import compilation.linker.RiscVLinker;
import core.instruction.riscv.instructions.rv32i.AddImmediate;
import core.instruction.riscv.instructions.rv32i.JumpAndLink;
import core.memory.Memory32;
import core.register.Register32;
import core.register.Register32File;
import core.riscvprogram.RiscVObjectFile;
import exceptions.compilation.UnknownRegisterException;
import simulator.Simulator32;

import java.util.Arrays;

public class Example {
    public static void main(String[] args) {
        var regs = new Register32File(Arrays.asList("t0", "t1", "t2"));
        var compiler = new RegexCompiler.RegexCompilerBuilder()
            .setProgramBuilder(new RiscVObjectFile.ProgramBuilder())
            .registerRegistersFromFile(regs)
            .registerInstruction(AddImmediate.NAME, new AddImmediate.Parser())
            .registerInstruction(JumpAndLink.NAME, new JumpAndLink.Parser())
            .build();
        var decoder = new RiscVDecoder.RiscVDecoderBuilder()
            .registerIInstruction(AddImmediate.OPCODE, AddImmediate.FUNCT_3, AddImmediate.class)
            .registerJInstruction(JumpAndLink.OPCODE, JumpAndLink.class)
            .build();
        var linker = new RiscVLinker(decoder);
        var memory = new Memory32();
        var pc = new Register32(32, "pc", Memory32.TEXT_SECTION_START);
        var sim = new Simulator32(compiler, linker, decoder, regs, pc, memory)
            .registerHandler(AddImmediate.class, new AddImmediate.Handler())
            .registerHandler(JumpAndLink.class, new JumpAndLink.Handler());
        try {
            sim.compile("""
                       .data
                            h: .string "123"
                       .text
                           jal t2, test
                           addi t0, t0, 1
                       test:
                           addi t1, t0, 2
                    """);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        sim.run();
        try {
            System.out.printf("t0: %d\n", regs.getRegisterByName("t0").getValue());
            System.out.printf("t1: %d\n", regs.getRegisterByName("t1").getValue());
            System.out.printf("t2: %d\n", regs.getRegisterByName("t2").getValue());
            System.out.printf(Arrays.toString(memory.readBytes(Memory32.DATA_SECTION_START, 4)));
        } catch (UnknownRegisterException e) {
            throw new RuntimeException(e);
        }
    }
}

