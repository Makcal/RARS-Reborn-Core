package rarsreborn.core;

import rarsreborn.core.compilation.compiler.ICompiler;
import rarsreborn.core.compilation.compiler.riscv.RegexCompiler;
import rarsreborn.core.compilation.decoder.riscv.RiscVDecoder;
import rarsreborn.core.compilation.linker.RiscVLinker;
import rarsreborn.core.core.environment.ITextInputDevice;
import rarsreborn.core.core.environment.mmu.LinearMemoryManagementUnit;
import rarsreborn.core.core.environment.riscv.RiscV32ExecutionEnvironment;
import rarsreborn.core.core.environment.riscv.ecalls.*;
import rarsreborn.core.core.instruction.riscv.instructions.pseudo.*;
import rarsreborn.core.core.instruction.riscv.instructions.rv32fd.*;
import rarsreborn.core.core.instruction.riscv.instructions.rv32i.*;
import rarsreborn.core.core.instruction.riscv.instructions.rv32m.*;
import rarsreborn.core.core.memory.Memory32;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.core.register.ZeroRegister32;
import rarsreborn.core.core.program.riscvprogram.RiscVObjectFile;
import rarsreborn.core.core.register.floatpoint.RegisterFloat64File;
import rarsreborn.core.event.ObservableImplementation;
import rarsreborn.core.simulator.SimulatorRiscV;
import rarsreborn.core.simulator.backstepper.BackStepper;

/**
 * @see <a href="https://github.com/jameslzhu/riscv-card/blob/master/riscv-card.pdf">Some instructions</a>
 */
public class Presets {
    public static SimulatorRiscV getClassicalRiscVSimulator(ITextInputDevice consoleReader) {
        try {
            Register32File registers = getRegisters();
            Register32 programCounter = new Register32(32, "pc");
            RegisterFloat64File floatRegisters = getFloatRegisters();

            ICompiler compiler = new RegexCompiler.RegexCompilerBuilder()
                .setProgramBuilder(new RiscVObjectFile.ProgramBuilder())
                .registerRegistersFromFile(registers)
                .registerRegistersFromFile(floatRegisters)
                // Arithmetic and logic
                .registerInstruction(Add.NAME, new Add.Parser())
                .registerInstruction(Sub.NAME, new Sub.Parser())
                .registerInstruction(Xor.NAME, new Xor.Parser())
                .registerInstruction(Or.NAME, new Or.Parser())
                .registerInstruction(And.NAME, new And.Parser())
                .registerInstruction(Sll.NAME, new Sll.Parser())
                .registerInstruction(Srl.NAME, new Srl.Parser())
                .registerInstruction(Sra.NAME, new Sra.Parser())
                .registerInstruction(Slt.NAME, new Slt.Parser())
                .registerInstruction(Sltu.NAME, new Sltu.Parser())
                // Immediate
                .registerInstruction(Addi.NAME, new Addi.Parser())
                .registerInstruction(Xori.NAME, new Xori.Parser())
                .registerInstruction(Ori.NAME, new Ori.Parser())
                .registerInstruction(Andi.NAME, new Andi.Parser())
                .registerInstruction(Slli.NAME, new Slli.Parser())
                .registerInstruction(Srli.NAME, new Srli.Parser())
                .registerInstruction(Srai.NAME, new Srai.Parser())
                .registerInstruction(Slti.NAME, new Slti.Parser())
                .registerInstruction(Sltiu.NAME, new Sltiu.Parser())
                // Load/store
                .registerInstruction(Lb.NAME, new Lb.Parser())
                .registerInstruction(Lh.NAME, new Lh.Parser())
                .registerInstruction(Lw.NAME, new Lw.Parser())
                .registerInstruction(Lbu.NAME, new Lbu.Parser())
                .registerInstruction(Lhu.NAME, new Lhu.Parser())
                .registerInstruction(Sb.NAME, new Sb.Parser())
                .registerInstruction(Sh.NAME, new Sh.Parser())
                .registerInstruction(Sw.NAME, new Sw.Parser())
                // Branches
                .registerInstruction(Beq.NAME, new Beq.Parser())
                .registerInstruction(Bne.NAME, new Bne.Parser())
                .registerInstruction(Blt.NAME, new Blt.Parser())
                .registerInstruction(Bge.NAME, new Bge.Parser())
                .registerInstruction(Bltu.NAME, new Bltu.Parser())
                .registerInstruction(Bgeu.NAME, new Bgeu.Parser())
                // Jumps
                .registerInstruction(Jal.NAME, new Jal.Parser())
                .registerInstruction(Jalr.NAME, new Jalr.Parser())
                .registerInstruction(Lui.NAME, new Lui.Parser())
                .registerInstruction(Auipc.NAME, new Auipc.Parser())
                // Other
                .registerInstruction(Ecall.NAME, new Ecall.Parser())
                .registerInstruction(Ebreak.NAME, new Ebreak.Parser())
                // RV32M
                .registerInstruction(Mul.NAME, new Mul.Parser())
                .registerInstruction(Mulh.NAME, new Mulh.Parser())
                .registerInstruction(Mulhsu.NAME, new Mulhsu.Parser())
                .registerInstruction(Mulhu.NAME, new Mulhu.Parser())
                .registerInstruction(Div.NAME, new Div.Parser())
                .registerInstruction(Divu.NAME, new Divu.Parser())
                .registerInstruction(Rem.NAME, new Rem.Parser())
                .registerInstruction(Remu.NAME, new Remu.Parser())
                // Pseudo
                .registerInstruction(La.NAME, new La.Parser())
                .registerInstruction(Nop.NAME, new Nop.Parser())
                .registerInstruction(Li.NAME, new Li.Parser())
                .registerInstruction(Mv.NAME, new Mv.Parser())
                .registerInstruction(Not.NAME, new Not.Parser())
                .registerInstruction(Neg.NAME, new Neg.Parser())
                .registerInstruction(Seqz.NAME, new Seqz.Parser())
                .registerInstruction(Snez.NAME, new Snez.Parser())
                .registerInstruction(Sltz.NAME, new Sltz.Parser())
                .registerInstruction(Sgtz.NAME, new Sgtz.Parser())
                .registerInstruction(Sgt.NAME, new Sgt.Parser())
                .registerInstruction(Sgtu.NAME, new Sgt.Parser())

                .registerInstruction(Beqz.NAME, new Beqz.Parser())
                .registerInstruction(Bnez.NAME, new Bnez.Parser())
                .registerInstruction(Blez.NAME, new Blez.Parser())
                .registerInstruction(Bgez.NAME, new Bgez.Parser())
                .registerInstruction(Bltz.NAME, new Bltz.Parser())
                .registerInstruction(Bgtz.NAME, new Bgtz.Parser())

                .registerInstruction(Bgt.NAME, new Bgt.Parser())
                .registerInstruction(Ble.NAME, new Ble.Parser())
                .registerInstruction(Bgtu.NAME, new Bgtu.Parser())
                .registerInstruction(Bleu.NAME, new Bleu.Parser())

                .registerInstruction(J.NAME, new J.Parser())
                .registerInstruction(Jr.NAME, new Jr.Parser())
                .registerInstruction(Ret.NAME, new Ret.Parser())
                .registerInstruction(Call.NAME, new Call.Parser())
                .registerInstruction(Tail.NAME, new Tail.Parser())
                // RV32FD
                .registerInstruction(Flw.NAME, new Flw.Parser())
                .registerInstruction(Fld.NAME, new Fld.Parser())
                .registerInstruction(Fsw.NAME, new Fsw.Parser())
                .registerInstruction(Fsd.NAME, new Fsd.Parser())
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
                .registerRInstruction(Slt.OPCODE, Slt.FUNCT_3, Slt.FUNCT_7, Slt.class)
                .registerRInstruction(Sltu.OPCODE, Sltu.FUNCT_3, Sltu.FUNCT_7, Sltu.class)
                // Immediate
                .registerIInstruction(Addi.OPCODE, Addi.FUNCT_3, Addi.class)
                .registerIInstruction(Xori.OPCODE, Xori.FUNCT_3, Xori.class)
                .registerIInstruction(Ori.OPCODE, Ori.FUNCT_3, Ori.class)
                .registerIInstruction(Andi.OPCODE, Andi.FUNCT_3, Andi.class)
                .registerIInstruction(Slli.OPCODE, Slli.FUNCT_3, Slli.class)
                .registerIInstruction(ShiftRightImm.OPCODE, ShiftRightImm.FUNCT_3, ShiftRightImm.class)
                .registerIInstruction(Slti.OPCODE, Slti.FUNCT_3, Slti.class)
                .registerIInstruction(Sltiu.OPCODE, Sltiu.FUNCT_3, Sltiu.class)
                // Load/store
                .registerIInstruction(Lb.OPCODE, Lb.FUNCT_3, Lb.class)
                .registerIInstruction(Lh.OPCODE, Lh.FUNCT_3, Lh.class)
                .registerIInstruction(Lw.OPCODE, Lw.FUNCT_3, Lw.class)
                .registerIInstruction(Lbu.OPCODE, Lbu.FUNCT_3, Lbu.class)
                .registerIInstruction(Lhu.OPCODE, Lhu.FUNCT_3, Lhu.class)
                .registerSInstruction(Sb.OPCODE, Sb.FUNCT_3, Sb.class)
                .registerSInstruction(Sh.OPCODE, Sh.FUNCT_3, Sh.class)
                .registerSInstruction(Sw.OPCODE, Sw.FUNCT_3, Sw.class)
                // Branches
                .registerBInstruction(Beq.OPCODE, Beq.FUNCT_3, Beq.class)
                .registerBInstruction(Bne.OPCODE, Bne.FUNCT_3, Bne.class)
                .registerBInstruction(Blt.OPCODE, Blt.FUNCT_3, Blt.class)
                .registerBInstruction(Bge.OPCODE, Bge.FUNCT_3, Bge.class)
                .registerBInstruction(Bltu.OPCODE, Bltu.FUNCT_3, Bltu.class)
                .registerBInstruction(Bgeu.OPCODE, Bgeu.FUNCT_3, Bgeu.class)
                // Jumps
                .registerJInstruction(Jal.OPCODE, Jal.class)
                .registerIInstruction(Jalr.OPCODE, Jalr.FUNCT_3, Jalr.class)
                .registerUInstruction(Lui.OPCODE, Lui.class)
                .registerUInstruction(Auipc.OPCODE, Auipc.class)
                // Other
                .registerIInstruction(EcallEbreakImpl.OPCODE, EcallEbreakImpl.FUNCT_3, EcallEbreakImpl.class)
                // RV32M
                .registerRInstruction(Mul.OPCODE, Mul.FUNCT_3, Mul.FUNCT_7, Mul.class)
                .registerRInstruction(Mulh.OPCODE, Mulh.FUNCT_3, Mulh.FUNCT_7, Mulh.class)
                .registerRInstruction(Mulhsu.OPCODE, Mulhsu.FUNCT_3, Mulhsu.FUNCT_7, Mulhsu.class)
                .registerRInstruction(Mulhu.OPCODE, Mulhu.FUNCT_3, Mulhu.FUNCT_7, Mulhu.class)
                .registerRInstruction(Div.OPCODE, Div.FUNCT_3, Div.FUNCT_7, Div.class)
                .registerRInstruction(Divu.OPCODE, Divu.FUNCT_3, Divu.FUNCT_7, Divu.class)
                .registerRInstruction(Rem.OPCODE, Rem.FUNCT_3, Rem.FUNCT_7, Rem.class)
                .registerRInstruction(Remu.OPCODE, Remu.FUNCT_3, Remu.FUNCT_7, Remu.class)
                // RV32FD
                .registerIInstruction(Flw.OPCODE, Flw.FUNCT_3, Flw.class)
                .registerIInstruction(Fld.OPCODE, Fld.FUNCT_3, Fld.class)
                .registerSInstruction(Fsw.OPCODE, Fsw.FUNCT_3, Fsw.class)
                .registerSInstruction(Fsd.OPCODE, Fsd.FUNCT_3, Fsd.class)
                .build();

            RiscVLinker linker = new RiscVLinker(decoder, Memory32.DATA_SECTION_START, Memory32.TEXT_SECTION_START);

            Memory32 memory = new Memory32();

            RiscV32ExecutionEnvironment executionEnvironment = new RiscV32ExecutionEnvironment.Builder()
                .setRegisters(registers)
                .setProgramCounter(programCounter)
                .setMemory(memory)
                .setFloatRegisters(floatRegisters)
                .setObservableImplementation(new ObservableImplementation())
                .setConsoleReader(consoleReader)
                .setMmu(new LinearMemoryManagementUnit(memory, Memory32.HEAP_SECTION_START, Memory32.HEAP_SECTION_SIZE))
                .addHandler(1, new PrintIntegerEcall())
                .addHandler(2, new PrintFloatEcall())
                .addHandler(3, new PrintDoubleEcall())
                .addHandler(4, new PrintStringEcall())
                .addHandler(5, new ReadIntegerEcall())
                .addHandler(6, new ReadFloatEcall())
                .addHandler(7, new ReadDoubleEcall())
                .addHandler(8, new ReadStringEcall())
                .addHandler(9, new MallocEcall())
                .addHandler(10, new ExitEcall())
                .addHandler(11, new PrintCharEcall())
                .addHandler(12, new ReadCharEcall())
                .addHandler(13, new MallocEcall())
                .addHandler(14, new FreeEcall())
                .addHandler(34, new PrintIntegerHexEcall())
                .addHandler(35, new PrintIntegerBinaryEcall())
                .addHandler(36, new PrintIntegerUnsignedEcall())
                .addHandler(37, new PrintIntegerOctalEcall())
                .build();

            return new SimulatorRiscV(
                compiler,
                linker,
                decoder,
                new BackStepper(200),
                registers,
                programCounter,
                memory,
                executionEnvironment,
                floatRegisters
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
                .registerHandler(Slt.class, new Slt.Handler())
                .registerHandler(Sltu.class, new Sltu.Handler())
                // Immediate
                .registerHandler(Addi.class, new Addi.Handler())
                .registerHandler(Xori.class, new Xori.Handler())
                .registerHandler(Ori.class, new Ori.Handler())
                .registerHandler(Andi.class, new Andi.Handler())
                .registerHandler(Slli.class, new Slli.Handler())
                .registerHandler(ShiftRightImm.class, new ShiftRightImm.Handler())
                .registerHandler(Slti.class, new Slti.Handler())
                .registerHandler(Sltiu.class, new Sltiu.Handler())
                // Load/store
                .registerHandler(Lb.class, new Lb.Handler())
                .registerHandler(Lh.class, new Lh.Handler())
                .registerHandler(Lw.class, new Lw.Handler())
                .registerHandler(Lbu.class, new Lbu.Handler())
                .registerHandler(Lhu.class, new Lhu.Handler())
                .registerHandler(Sb.class, new Sb.Handler())
                .registerHandler(Sh.class, new Sh.Handler())
                .registerHandler(Sw.class, new Sw.Handler())
                // Branches
                .registerHandler(Beq.class, new Beq.Handler())
                .registerHandler(Bne.class, new Bne.Handler())
                .registerHandler(Blt.class, new Blt.Handler())
                .registerHandler(Bge.class, new Bge.Handler())
                .registerHandler(Bltu.class, new Bltu.Handler())
                .registerHandler(Bgeu.class, new Bgeu.Handler())
                // Jumps
                .registerHandler(Jal.class, new Jal.Handler())
                .registerHandler(Jalr.class, new Jalr.Handler())
                .registerHandler(Lui.class, new Lui.Handler())
                .registerHandler(Auipc.class, new Auipc.Handler())
                // Other
                .registerHandler(EcallEbreakImpl.class, new EcallEbreakImpl.Handler())
                // RV32M
                .registerHandler(Mul.class, new Mul.Handler())
                .registerHandler(Mulh.class, new Mulh.Handler())
                .registerHandler(Mulhsu.class, new Mulhsu.Handler())
                .registerHandler(Mulhu.class, new Mulhu.Handler())
                .registerHandler(Div.class, new Div.Handler())
                .registerHandler(Divu.class, new Divu.Handler())
                .registerHandler(Rem.class, new Rem.Handler())
                .registerHandler(Remu.class, new Remu.Handler())
                // RV32FD
                .registerHandler(Flw.class, new Flw.Handler())
                .registerHandler(Fld.class, new Fld.Handler())
                .registerHandler(Fsw.class, new Fsw.Handler())
                .registerHandler(Fsd.class, new Fsd.Handler())
            ;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Register32File getRegisters() {
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
        return registers;
    }

    private static RegisterFloat64File getFloatRegisters() {
        String[] floatRegisterNames = new String[] {
            "ft0", "ft1", "ft2", "ft3", "ft4", "ft5", "ft6", "ft7",
            "fs0", "fs1",
            "fa0", "fa1", "fa2", "fa3", "fa4", "fa5", "fa6", "fa7",
            "fs2", "fs3", "fs4", "fs5", "fs6", "fs7", "fs8", "fs9", "fs10", "fs11",
            "ft8", "ft9", "ft10", "ft11"
        };
        RegisterFloat64File floatRegisters = new RegisterFloat64File();
        floatRegisters.createRegistersFromNames(floatRegisterNames);
        return floatRegisters;
    }
}
