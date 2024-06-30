package rarsreborn.core.compilation.decoder.riscv;

import rarsreborn.core.compilation.decoder.DecodingResult;
import rarsreborn.core.compilation.decoder.IBufferedDecoder;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.riscv.formats.*;
import rarsreborn.core.core.memory.IMemory;
import rarsreborn.core.exceptions.execution.IllegalInstructionException;
import rarsreborn.core.exceptions.memory.MemoryAccessException;

import java.util.HashMap;
import java.util.Map;

public class RiscVDecoder implements IBufferedDecoder {
    private Map<Byte, RiscVInstructionFormat> opcodesToFormats = new HashMap<>();
    private InstructionMapping<DoubleFunctInfo, InstructionR> rInstructions = new InstructionMapping<>();
    private InstructionMapping<SingleFunctInfo, InstructionI> iInstructions = new InstructionMapping<>();
    private InstructionMapping<SingleFunctInfo, InstructionS> sInstructions = new InstructionMapping<>();
    private InstructionMapping<SingleFunctInfo, InstructionB> bInstructions = new InstructionMapping<>();
    private InstructionMapping<OpcodeInfo, InstructionU> uInstructions = new InstructionMapping<>();
    private InstructionMapping<OpcodeInfo, InstructionJ> jInstructions = new InstructionMapping<>();

    private RiscVDecoder() {}

    @Override
    public int getBufferSize() {
        return 4;
    }

    @Override
    public DecodingResult decodeNextInstruction(IMemory memory, long address)
            throws MemoryAccessException, IllegalInstructionException {
        int encoded = Byte.toUnsignedInt(memory.getByte(address)) << 24
            | Byte.toUnsignedInt(memory.getByte(address + 1)) << 16
            | Byte.toUnsignedInt(memory.getByte(address + 2)) << 8
            | Byte.toUnsignedInt(memory.getByte(address + 3));

        byte opcode = (byte) (encoded & 0b111_1111);
        IInstruction instruction = null;
        try {
            switch (opcodesToFormats.get(opcode)) {
                case R -> {
                    InstructionR.InstructionRData instructionRData = InstructionR.deserialize(encoded);
                    Class<? extends InstructionR> instructionClass = rInstructions.get(new DoubleFunctInfo(
                        instructionRData.opcode(),
                        instructionRData.funct3(),
                        instructionRData.funct7()
                    ));
                    instruction = instructionClass
                        .getConstructor(InstructionR.InstructionRParams.class)
                        .newInstance(new InstructionR.InstructionRParams(
                            instructionRData.rd(),
                            instructionRData.rs1(),
                            instructionRData.rs2()
                        ));
                }
                case I -> {
                    InstructionI.InstructionIData instructionIData = InstructionI.deserialize(encoded);
                    Class<? extends InstructionI> instructionClass = iInstructions.get(new SingleFunctInfo(
                        instructionIData.opcode(),
                        instructionIData.funct3()
                    ));
                    instruction = instructionClass
                        .getConstructor(InstructionI.InstructionIParams.class)
                        .newInstance(new InstructionI.InstructionIParams(
                            instructionIData.rd(),
                            instructionIData.rs1(),
                            instructionIData.imm()
                        ));
                }
                case S -> {
                    InstructionS.InstructionSData instructionSData = InstructionS.deserialize(encoded);
                    Class<? extends InstructionS> instructionClass = sInstructions.get(new SingleFunctInfo(
                        instructionSData.opcode(),
                        instructionSData.funct3()
                    ));
                    instruction = instructionClass
                        .getConstructor(InstructionS.InstructionSParams.class)
                        .newInstance(new InstructionS.InstructionSParams(
                            instructionSData.rs1(),
                            instructionSData.rs2(),
                            instructionSData.imm()
                        ));
                }
                case B -> {
                    InstructionB.InstructionBData instructionBData = InstructionB.deserialize(encoded);
                    Class<? extends InstructionB> instructionClass = bInstructions.get(new SingleFunctInfo(
                        instructionBData.opcode(),
                        instructionBData.funct3()
                    ));
                    instruction = instructionClass
                        .getConstructor(InstructionB.InstructionBParams.class)
                        .newInstance(new InstructionB.InstructionBParams(
                            instructionBData.rs1(),
                            instructionBData.rs2(),
                            instructionBData.imm()
                        ));
                }
                case U -> {
                    InstructionU.InstructionUData instructionUData = InstructionU.deserialize(encoded);
                    Class<? extends InstructionU> instructionClass = uInstructions.get(new OpcodeInfo(instructionUData.opcode()));
                    instruction = instructionClass
                        .getConstructor(InstructionU.InstructionUParams.class)
                        .newInstance(new InstructionU.InstructionUParams(
                            instructionUData.rd(),
                            instructionUData.imm()
                        ));
                }
                case J -> {
                    InstructionJ.InstructionJData instructionJData = InstructionJ.deserialize(encoded);
                    Class<? extends InstructionJ> instructionClass = jInstructions.get(new OpcodeInfo(instructionJData.opcode()));
                    instruction = instructionClass
                        .getConstructor(InstructionJ.InstructionJParams.class)
                        .newInstance(new InstructionJ.InstructionJParams(
                            instructionJData.rd(),
                            instructionJData.imm()
                        ));
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalInstructionException(address);
        }
        return new DecodingResult(instruction, (byte) 4);
    }

    private record DoubleFunctInfo(byte opcode, byte funct3, byte funct7) {}

    private record SingleFunctInfo(byte opcode, byte funct3) {}

    private record OpcodeInfo(byte opcode) {}

    private static class InstructionMapping<TInfo, TInstruction> {
        private final HashMap<TInfo, Class<? extends TInstruction>> instructions;

        public InstructionMapping() {
            instructions = new HashMap<>();
        }

        public InstructionMapping(InstructionMapping<TInfo, TInstruction> other) {
            this.instructions = other.instructions;
        }

        public void put(TInfo info, Class<? extends TInstruction> instructionClass) {
            instructions.put(info, instructionClass);
        }

        public Class<? extends TInstruction> get(TInfo info) throws IllegalArgumentException {
            Class<? extends TInstruction> type = instructions.get(info);
            if (type == null) {
                throw new IllegalArgumentException();
            }
            return type;
        }
    }

    public static class RiscVDecoderBuilder {
        private final Map<Byte, RiscVInstructionFormat> opcodesToFormats = new HashMap<>();
        private final InstructionMapping<DoubleFunctInfo, InstructionR> rInstructions = new InstructionMapping<>();
        private final InstructionMapping<SingleFunctInfo, InstructionI> iInstructions = new InstructionMapping<>();
        private final InstructionMapping<SingleFunctInfo, InstructionS> sInstructions = new InstructionMapping<>();
        private final InstructionMapping<SingleFunctInfo, InstructionB> bInstructions = new InstructionMapping<>();
        private final InstructionMapping<OpcodeInfo, InstructionU> uInstructions = new InstructionMapping<>();
        private final InstructionMapping<OpcodeInfo, InstructionJ> jInstructions = new InstructionMapping<>();final

        public RiscVDecoder build() {
            RiscVDecoder decoder = new RiscVDecoder();
            decoder.opcodesToFormats = new HashMap<>(opcodesToFormats);
            decoder.rInstructions = new InstructionMapping<>(rInstructions);
            decoder.iInstructions = new InstructionMapping<>(iInstructions);
            decoder.sInstructions = new InstructionMapping<>(sInstructions);
            decoder.bInstructions = new InstructionMapping<>(bInstructions);
            decoder.uInstructions = new InstructionMapping<>(uInstructions);
            decoder.jInstructions = new InstructionMapping<>(jInstructions);
            return decoder;
        }

        public RiscVDecoderBuilder registerRInstruction(
            byte opcode,
            byte funct3,
            byte funct7,
            Class<? extends InstructionR> instructionClass
        ) {
            opcodesToFormats.put(opcode, RiscVInstructionFormat.R);
            rInstructions.put(new DoubleFunctInfo(opcode, funct3, funct7), instructionClass);
            return this;
        }

        public RiscVDecoderBuilder registerIInstruction(
            byte opcode,
            byte funct3,
            Class<? extends InstructionI> instructionClass
        ) {
            opcodesToFormats.put(opcode, RiscVInstructionFormat.I);
            iInstructions.put(new SingleFunctInfo(opcode, funct3), instructionClass);
            return this;
        }

        public RiscVDecoderBuilder registerSInstruction(
            byte opcode,
            byte funct3,
            Class<? extends InstructionS> instructionClass
        ) {
            opcodesToFormats.put(opcode, RiscVInstructionFormat.S);
            sInstructions.put(new SingleFunctInfo(opcode, funct3), instructionClass);
            return this;
        }

        public RiscVDecoderBuilder registerBInstruction(
            byte opcode,
            byte funct3,
            Class<? extends InstructionB> instructionClass
        ) {
            opcodesToFormats.put(opcode, RiscVInstructionFormat.B);
            bInstructions.put(new SingleFunctInfo(opcode, funct3), instructionClass);
            return this;
        }

        public RiscVDecoderBuilder registerUInstruction(
            byte opcode,
            Class<? extends InstructionU> instructionClass
        ) {
            opcodesToFormats.put(opcode, RiscVInstructionFormat.U);
            uInstructions.put(new OpcodeInfo(opcode), instructionClass);
            return this;
        }

        public RiscVDecoderBuilder registerJInstruction(
            byte opcode,
            Class<? extends InstructionJ> instructionClass
        ) {
            opcodesToFormats.put(opcode, RiscVInstructionFormat.J);
            jInstructions.put(new OpcodeInfo(opcode), instructionClass);
            return this;
        }
    }
}
