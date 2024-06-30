package rarsreborn.core.simulator;

import rarsreborn.core.compilation.compiler.ICompiler;
import rarsreborn.core.compilation.decoder.DecodingResult;
import rarsreborn.core.compilation.decoder.IBufferedDecoder;
import rarsreborn.core.compilation.linker.ILinker;
import rarsreborn.core.core.instruction.IInstruction;
import rarsreborn.core.core.instruction.riscv.RiscV32InstructionHandler;
import rarsreborn.core.core.memory.Memory32;
import rarsreborn.core.core.program.IExecutable;
import rarsreborn.core.core.register.Register32;
import rarsreborn.core.core.register.Register32File;
import rarsreborn.core.exceptions.execution.EndOfExecutionException;
import rarsreborn.core.exceptions.execution.ExecutionException;
import rarsreborn.core.exceptions.memory.MemoryAccessException;

public class Simulator32 extends SimulatorBase {
    protected final Register32File registerFile;
    protected final Memory32 memory;
    protected final Register32 programCounter;

    protected long programLength;
    protected byte lastInstructionSize;
    protected long lastPcPosition;

    public Simulator32(
            ICompiler compiler,
            ILinker linker,
            IBufferedDecoder decoder,
            Register32File registerFile,
            Register32 programCounter,
            Memory32 memory
    ) {
        super(compiler, linker, decoder);
        this.registerFile = registerFile;
        this.memory = memory;
        this.programCounter = programCounter;
    }

    public Register32File getRegisterFile() {
        return registerFile;
    }

    public Memory32 getMemory() {
        return memory;
    }

    public Register32 getProgramCounter() {
        return programCounter;
    }

    @Override
    public void reset() {
        memory.reset();
        registerFile.reset();
    }

    @Override
    protected void loadProgram(IExecutable program) {
        memory.writeBytes(Memory32.DATA_SECTION_START, program.getData());

        byte[] programText = program.getText();
        memory.writeBytes(Memory32.TEXT_SECTION_START, programText);
        programLength = programText.length;
    }

    @Override
    protected IInstruction getNextInstruction() throws ExecutionException {
        if (programCounter.getValue() - Memory32.TEXT_SECTION_START >= programLength) {
            throw new EndOfExecutionException();
        }
        try {
            DecodingResult decoded = decoder.decodeNextInstruction(memory, programCounter.getValue());
            lastInstructionSize = decoded.bytesConsumed();
            lastPcPosition = programCounter.getValue();
            return decoded.instruction();
        } catch (MemoryAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void executeOneInstruction() throws ExecutionException {
        super.executeOneInstruction();
        if (lastPcPosition == programCounter.getValue())
            programCounter.setValue(programCounter.getValue() + lastInstructionSize);
    }

    public <TInstruction extends IInstruction> Simulator32 registerHandler(
            Class<TInstruction> instructionClass,
            RiscV32InstructionHandler<TInstruction> handler
    ) {
        handler.attachMemory(memory);
        handler.attachProgramCounter(programCounter);
        handler.attachRegisters(registerFile);
        super.addHandler(instructionClass, handler);
        return this;
    }
}
