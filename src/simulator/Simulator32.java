package simulator;

import compilation.compiler.ICompiler;
import compilation.decoder.DecodingResult;
import compilation.decoder.IBufferedDecoder;
import compilation.linker.ILinker;
import core.instruction.IInstruction;
import core.instruction.riscv.RiscV32InstructionHandler;
import core.memory.Memory32;
import core.program.IExecutable;
import core.register.Register32;
import core.register.Register32File;
import exceptions.execution.EndOfExecutionException;
import exceptions.execution.ExecutionException;
import exceptions.memory.MemoryAccessException;

public class Simulator32 extends SimulatorBase {
    protected final Register32File registerFile;
    protected final Memory32 memory;
    public final Register32 programCounter;

    protected long programLength;

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
            programCounter.setValue(programCounter.getValue() + decoded.bytesConsumed());
            return decoded.instruction();
        } catch (MemoryAccessException e) {
            throw new RuntimeException(e);
        }
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
