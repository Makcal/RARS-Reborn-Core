package rarsreborn.core;

import rarsreborn.core.compilation.compiler.ICompiler;
import rarsreborn.core.compilation.compiler.riscv.RegexCompiler;
import rarsreborn.core.compilation.decoder.riscv.RiscVDecoder;
import rarsreborn.core.compilation.linker.RiscVLinker;
import rarsreborn.core.core.environment.IInputDevice;
import rarsreborn.core.core.environment.riscv.RiscV32ExecutionEnvironment;
import rarsreborn.core.core.environment.riscv.ecalls.PrintEcall;
import rarsreborn.core.core.environment.riscv.ecalls.ReadEcall;
import rarsreborn.core.core.instruction.riscv.instructions.pseudo.La;
import rarsreborn.core.core.instruction.riscv.instructions.pseudo.Li;
import rarsreborn.core.core.instruction.riscv.instructions.pseudo.Mv;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.*;
import rarsreborn.core.core.instruction.riscv.instructions.rv32m.Div;
import rarsreborn.core.core.instruction.riscv.instructions.rv32m.Mul;
import rarsreborn.core.core.memory.Memory32;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.core.register.ZeroRegister32;
import rarsreborn.core.core.riscvprogram.RiscVObjectFile;
import rarsreborn.core.events.ObservableImplementation;
import rarsreborn.core.simulator.Simulator32;

public class Presets {
    public static Simulator32 getClassicalRiscVSimulator(IInputDevice consoleReader) {
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

            Register32 programCounter = new Register32(32, "pc");

            ICompiler compiler = new RegexCompiler.RegexCompilerBuilder()
                .setProgramBuilder(new RiscVObjectFile.ProgramBuilder())
                .registerRegistersFromFile(registers)
                // Arithmetic and logic
                .registerInstruction(Add.NAME, new Add.Parser())
                .registerInstruction(Sub.NAME, new Sub.Parser())
                .registerInstruction(Xor.NAME, new Xor.Parser())
                .registerInstruction(Or.NAME, new Or.Parser())
                .registerInstruction(And.NAME, new And.Parser())
                .registerInstruction(Sll.NAME, new Sll.Parser())
                .registerInstruction(Srl.NAME, new Srl.Parser())
                .registerInstruction(Sra.NAME, new Sra.Parser())
                // Immediate
                .registerInstruction(Addi.NAME, new Addi.Parser())
                .registerInstruction(Xori.NAME, new Xori.Parser())
                .registerInstruction(Ori.NAME, new Ori.Parser())
                .registerInstruction(Andi.NAME, new Andi.Parser())
                .registerInstruction(Slli.NAME, new Slli.Parser())
                .registerInstruction(Srli.NAME, new Srli.Parser())
                .registerInstruction(Srai.NAME, new Srai.Parser())
                // Load/store

                // Branches

                // Jumps
                .registerInstruction(Jal.NAME, new Jal.Parser())
                .registerInstruction(Auipc.NAME, new Auipc.Parser())
                // Other
                .registerInstruction(Ecall.NAME, new Ecall.Parser())
                // RV32M
                .registerInstruction(Mul.NAME, new Mul.Parser())
                .registerInstruction(Div.NAME, new Div.Parser())
                // Pseudo
                .registerInstruction(Li.NAME, new Li.Parser())
                .registerInstruction(La.NAME, new La.Parser())
                .registerInstruction(Mv.NAME, new Mv.Parser())
                .build();

            RiscVDecoder decoder = new RiscVDecoder.RiscVDecoderBuilder()
                // Arithmetic and logic
                .registerRInstruction(Add.OPCODE, Add.FUNCT_3, Add.FUNCT_7, Add.class)
                .registerRInstruction(Sub.OPCODE, Sub.FUNCT_3, Sub.FUNCT_7, Sub.class)
                .registerRInstruction(Xor.OPCODE, Xor.FUNCT_3, Xor.FUNCT_7, Xor.class)
                .registerRInstruction(Or.OPCODE, Or.FUNCT_3, Or.FUNCT_7, Or.class)
                .registerRInstruction(And.OPCODE, And.FUNCT_3, And.FUNCT_7, And.class)
                .registerRInstruction(Sll.OPCODE, Sll.FUNCT_3, Sll.FUNCT_7, Sll.class)
                .registerRInstruction(Srl.OPCODE, Srl.FUNCT_3, Srl.FUNCT_7, Srl.class)
                .registerRInstruction(Sra.OPCODE, Sra.FUNCT_3, Sra.FUNCT_7, Sra.class)
                // Immediate
                .registerIInstruction(Addi.OPCODE, Addi.FUNCT_3, Addi.class)
                .registerIInstruction(Xori.OPCODE, Xori.FUNCT_3, Xori.class)
                .registerIInstruction(Ori.OPCODE, Ori.FUNCT_3, Ori.class)
                .registerIInstruction(Andi.OPCODE, Andi.FUNCT_3, Andi.class)
                .registerIInstruction(Slli.OPCODE, Slli.FUNCT_3, Slli.class)
                .registerIInstruction(Srli.OPCODE, Srli.FUNCT_3, Srli.class)
                .registerIInstruction(Srai.OPCODE, Srai.FUNCT_3, Srai.class)
                // Load/store

                // Branches

                // Jumps
                .registerJInstruction(Jal.OPCODE, Jal.class)
                .registerUInstruction(Auipc.OPCODE, Auipc.class)
                // Other
                .registerIInstruction(Ecall.OPCODE, Ecall.FUNCT3, Ecall.class)
                // RV32M
                .registerRInstruction(Mul.OPCODE, Mul.FUNCT_3, Mul.FUNCT_7, Mul.class)
                .registerRInstruction(Div.OPCODE, Div.FUNCT_3, Div.FUNCT_7, Div.class)
                .build();

            RiscVLinker linker = new RiscVLinker(decoder, Memory32.DATA_SECTION_START, Memory32.TEXT_SECTION_START);

            Memory32 memory = new Memory32();

            RiscV32ExecutionEnvironment executionEnvironment = new RiscV32ExecutionEnvironment.Builder()
                .setRegisters(registers)
                .setProgramCounter(programCounter)
                .setMemory(memory)
                .setObservableImplementation(new ObservableImplementation())
                .setConsoleReader(consoleReader)
                .addHandler(0, new PrintEcall())
                .addHandler(1, new ReadEcall())
                .build();

            return new Simulator32(
                    compiler, linker, decoder, registers, programCounter, memory, executionEnvironment
            )
                // Arithmetic and logic
                .registerHandler(Add.class, new Add.Handler())
                .registerHandler(Sub.class, new Sub.Handler())
                .registerHandler(Xor.class, new Xor.Handler())
                .registerHandler(Or.class, new Or.Handler())
                .registerHandler(And.class, new And.Handler())
                .registerHandler(Sll.class, new Sll.Handler())
                .registerHandler(Srl.class, new Srl.Handler())
                .registerHandler(Sra.class, new Sra.Handler())
                // Immediate
                .registerHandler(Addi.class, new Addi.Handler())
                .registerHandler(Xori.class, new Xori.Handler())
                .registerHandler(Ori.class, new Ori.Handler())
                .registerHandler(Andi.class, new Andi.Handler())
                .registerHandler(Slli.class, new Slli.Handler())
                .registerHandler(Srli.class, new Srli.Handler())
                .registerHandler(Srai.class, new Srai.Handler())
                // Load/store

                // Branches

                // Jumps
                .registerHandler(Jal.class, new Jal.Handler())
                .registerHandler(Auipc.class, new Auipc.Handler())
                // Other
                .registerHandler(Ecall.class, new Ecall.Handler())
                // RV32M
                .registerHandler(Mul.class, new Mul.Handler())
                .registerHandler(Div.class, new Div.Handler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
