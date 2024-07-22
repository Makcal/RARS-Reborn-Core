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
import rarsreborn.core.core.instruction.riscv.instructions.rv32f.*;
import rarsreborn.core.core.instruction.riscv.instructions.rv32d.*;
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
                // Pseudo float
                .registerInstruction(Fmv_s.NAME, new Fmv_s.Parser())
                .registerInstruction(Fmv_d.NAME, new Fmv_d.Parser())
                // RV32F
                .registerInstruction(Flw.NAME, new Flw.Parser())
                .registerInstruction(Fsw.NAME, new Fsw.Parser())
                .registerInstruction(Fadd_s.NAME, new Fadd_s.Parser())
                .registerInstruction(Fsub_s.NAME, new Fsub_s.Parser())
                .registerInstruction(Fmul_s.NAME, new Fmul_s.Parser())
                .registerInstruction(Fdiv_s.NAME, new Fdiv_s.Parser())
                .registerInstruction(Fsgnj_s.NAME, new Fsgnj_s.Parser())
                .registerInstruction(Fsgnjn_s.NAME, new Fsgnjn_s.Parser())
                .registerInstruction(Fsgnjx_s.NAME, new Fsgnjx_s.Parser())
                .registerInstruction(Fmin_s.NAME, new Fmin_s.Parser())
                .registerInstruction(Fmax_s.NAME, new Fmax_s.Parser())
                .registerInstruction(Fsqrt_s.NAME, new Fsqrt_s.Parser())
                .registerInstruction(Fcvt_s_w.NAME, new Fcvt_s_w.Parser())
                .registerInstruction(Fcvt_s_wu.NAME, new Fcvt_s_wu.Parser())
                .registerInstruction(Fcvt_w_s.NAME, new Fcvt_w_s.Parser())
                .registerInstruction(Fcvt_wu_s.NAME, new Fcvt_wu_s.Parser())
                // RV32D
                .registerInstruction(Fld.NAME, new Fld.Parser())
                .registerInstruction(Fsd.NAME, new Fsd.Parser())
                .registerInstruction(Fadd_d.NAME, new Fadd_d.Parser())
                .registerInstruction(Fsub_d.NAME, new Fsub_d.Parser())
                .registerInstruction(Fmul_d.NAME, new Fmul_d.Parser())
                .registerInstruction(Fdiv_d.NAME, new Fdiv_d.Parser())
                .registerInstruction(Fsgnj_d.NAME, new Fsgnj_d.Parser())
                .registerInstruction(Fsgnjn_d.NAME, new Fsgnjn_d.Parser())
                .registerInstruction(Fsgnjx_d.NAME, new Fsgnjx_d.Parser())
                .registerInstruction(Fmin_d.NAME, new Fmin_d.Parser())
                .registerInstruction(Fmax_d.NAME, new Fmax_d.Parser())
                .registerInstruction(Fsqrt_d.NAME, new Fsqrt_d.Parser())
                .registerInstruction(Fcvt_d_w.NAME, new Fcvt_d_w.Parser())
                .registerInstruction(Fcvt_d_wu.NAME, new Fcvt_d_wu.Parser())
                .registerInstruction(Fcvt_w_d.NAME, new Fcvt_w_d.Parser())
                .registerInstruction(Fcvt_wu_d.NAME, new Fcvt_wu_d.Parser())
                .registerInstruction(Fcvt_d_s.NAME, new Fcvt_d_s.Parser())
                .registerInstruction(Fcvt_s_d.NAME, new Fcvt_s_d.Parser())
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
                // RV32F
                .registerIInstruction(Flw.OPCODE, Flw.FUNCT_3, Flw.class)
                .registerSInstruction(Fsw.OPCODE, Fsw.FUNCT_3, Fsw.class)
                .registerRInstruction(Fadd_s.OPCODE, Fadd_s.FUNCT_3, Fadd_s.FUNCT_7, Fadd_s.class)
                .registerRInstruction(Fsub_s.OPCODE, Fsub_s.FUNCT_3, Fsub_s.FUNCT_7, Fsub_s.class)
                .registerRInstruction(Fmul_s.OPCODE, Fmul_s.FUNCT_3, Fmul_s.FUNCT_7, Fmul_s.class)
                .registerRInstruction(Fdiv_s.OPCODE, Fdiv_s.FUNCT_3, Fdiv_s.FUNCT_7, Fdiv_s.class)
                .registerRInstruction(Fsgnj_s.OPCODE, Fsgnj_s.FUNCT_3, Fsgnj_s.FUNCT_7, Fsgnj_s.class)
                .registerRInstruction(Fsgnjn_s.OPCODE, Fsgnjn_s.FUNCT_3, Fsgnjn_s.FUNCT_7, Fsgnjn_s.class)
                .registerRInstruction(Fsgnjx_s.OPCODE, Fsgnjx_s.FUNCT_3, Fsgnjx_s.FUNCT_7, Fsgnjx_s.class)
                .registerRInstruction(Fmin_s.OPCODE, Fmin_s.FUNCT_3, Fmin_s.FUNCT_7, Fmin_s.class)
                .registerRInstruction(Fmax_s.OPCODE, Fmax_s.FUNCT_3, Fmax_s.FUNCT_7, Fmax_s.class)
                .registerRInstruction(Fsqrt_s.OPCODE, Fsqrt_s.FUNCT_3, Fsqrt_s.FUNCT_7, Fsqrt_s.class)
                .registerRInstruction(
                    Fcvt_s_w_Impl.OPCODE, Fcvt_s_w_Impl.FUNCT_3, Fcvt_s_w_Impl.FUNCT_7, Fcvt_s_w_Impl.class
                )
                .registerRInstruction(
                    Fcvt_w_s_Impl.OPCODE, Fcvt_w_s_Impl.FUNCT_3, Fcvt_w_s_Impl.FUNCT_7, Fcvt_w_s_Impl.class
                )
                // RV32D
                .registerIInstruction(Fld.OPCODE, Fld.FUNCT_3, Fld.class)
                .registerSInstruction(Fsd.OPCODE, Fsd.FUNCT_3, Fsd.class)
                .registerRInstruction(Fadd_d.OPCODE, Fadd_d.FUNCT_3, Fadd_d.FUNCT_7, Fadd_d.class)
                .registerRInstruction(Fsub_d.OPCODE, Fsub_d.FUNCT_3, Fsub_d.FUNCT_7, Fsub_d.class)
                .registerRInstruction(Fmul_d.OPCODE, Fmul_d.FUNCT_3, Fmul_d.FUNCT_7, Fmul_d.class)
                .registerRInstruction(Fdiv_d.OPCODE, Fdiv_d.FUNCT_3, Fdiv_d.FUNCT_7, Fdiv_d.class)
                .registerRInstruction(Fsgnj_d.OPCODE, Fsgnj_d.FUNCT_3, Fsgnj_d.FUNCT_7, Fsgnj_d.class)
                .registerRInstruction(Fsgnjn_d.OPCODE, Fsgnjn_d.FUNCT_3, Fsgnjn_d.FUNCT_7, Fsgnjn_d.class)
                .registerRInstruction(Fsgnjx_d.OPCODE, Fsgnjx_d.FUNCT_3, Fsgnjx_d.FUNCT_7, Fsgnjx_d.class)
                .registerRInstruction(Fmin_d.OPCODE, Fmin_d.FUNCT_3, Fmin_d.FUNCT_7, Fmin_d.class)
                .registerRInstruction(Fmax_d.OPCODE, Fmax_d.FUNCT_3, Fmax_d.FUNCT_7, Fmax_d.class)
                .registerRInstruction(Fsqrt_d.OPCODE, Fsqrt_d.FUNCT_3, Fsqrt_d.FUNCT_7, Fsqrt_d.class)
                .registerRInstruction(
                    Fcvt_d_w_Impl.OPCODE, Fcvt_d_w_Impl.FUNCT_3, Fcvt_d_w_Impl.FUNCT_7, Fcvt_d_w_Impl.class
                )
                .registerRInstruction(
                    Fcvt_w_d_Impl.OPCODE, Fcvt_w_d_Impl.FUNCT_3, Fcvt_w_d_Impl.FUNCT_7, Fcvt_w_d_Impl.class
                )
                .registerRInstruction(Fcvt_d_s.OPCODE, Fcvt_d_s.FUNCT_3, Fcvt_d_s.FUNCT_7, Fcvt_d_s.class)
                .registerRInstruction(Fcvt_s_d.OPCODE, Fcvt_s_d.FUNCT_3, Fcvt_s_d.FUNCT_7, Fcvt_s_d.class)
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
                // RV32F
                .registerHandler(Flw.class, new Flw.Handler())
                .registerHandler(Fsw.class, new Fsw.Handler())
                .registerHandler(Fadd_s.class, new Fadd_s.Handler())
                .registerHandler(Fsub_s.class, new Fsub_s.Handler())
                .registerHandler(Fmul_s.class, new Fmul_s.Handler())
                .registerHandler(Fdiv_s.class, new Fdiv_s.Handler())
                .registerHandler(Fsgnj_s.class, new Fsgnj_s.Handler())
                .registerHandler(Fsgnjn_s.class, new Fsgnjn_s.Handler())
                .registerHandler(Fsgnjx_s.class, new Fsgnjx_s.Handler())
                .registerHandler(Fmin_s.class, new Fmin_s.Handler())
                .registerHandler(Fmax_s.class, new Fmax_s.Handler())
                .registerHandler(Fsqrt_s.class, new Fsqrt_s.Handler())
                .registerHandler(Fcvt_s_w_Impl.class, new Fcvt_s_w_Impl.Handler())
                .registerHandler(Fcvt_w_s_Impl.class, new Fcvt_w_s_Impl.Handler())
                // RV32D
                .registerHandler(Fld.class, new Fld.Handler())
                .registerHandler(Fsd.class, new Fsd.Handler())
                .registerHandler(Fadd_d.class, new Fadd_d.Handler())
                .registerHandler(Fsub_d.class, new Fsub_d.Handler())
                .registerHandler(Fmul_d.class, new Fmul_d.Handler())
                .registerHandler(Fdiv_d.class, new Fdiv_d.Handler())
                .registerHandler(Fsgnj_d.class, new Fsgnj_d.Handler())
                .registerHandler(Fsgnjn_d.class, new Fsgnjn_d.Handler())
                .registerHandler(Fsgnjx_d.class, new Fsgnjx_d.Handler())
                .registerHandler(Fmin_d.class, new Fmin_d.Handler())
                .registerHandler(Fmax_d.class, new Fmax_d.Handler())
                .registerHandler(Fsqrt_d.class, new Fsqrt_d.Handler())
                .registerHandler(Fcvt_d_w_Impl.class, new Fcvt_d_w_Impl.Handler())
                .registerHandler(Fcvt_w_d_Impl.class, new Fcvt_w_d_Impl.Handler())
                .registerHandler(Fcvt_d_s.class, new Fcvt_d_s.Handler())
                .registerHandler(Fcvt_s_d.class, new Fcvt_s_d.Handler())
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
