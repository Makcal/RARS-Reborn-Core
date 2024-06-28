package simulator;

import compilation.compiler.ICompiler;
import compilation.decoder.DecodingResult;
import compilation.decoder.IBufferedDecoder;
import core.instruction.IInstruction;
import core.instruction.IInstructionHandler;
import core.memory.Memory32;
import core.register.Register32;
import core.register.Register32File;
import core.riscvprogram.IDataBlock;
import core.riscvprogram.IProgram;
import exceptions.execution.EndOfExecutionException;
import exceptions.execution.ExecutionException;
import exceptions.memory.MemoryAccessException;

import java.util.List;

public class Simulator32 extends SimulatorBase {
    protected final Register32File register32File;
    protected final Memory32 memory;
    protected final Register32 programCounter;

    protected long programLength;

    public Simulator32(
            ICompiler compiler,
            IBufferedDecoder decoder,
            Register32File registerFile,
            Memory32 memory
    ) {
        super(compiler, decoder);
        this.register32File = registerFile;
        this.memory = memory;
        this.programCounter = new Register32(32, "pc", Memory32.TEXT_SECTION_START);
    }

    @Override
    protected void loadProgram(IProgram program) {
        loadInstructions(program.getText());
        loadData(program.getData());
    }

    private void loadInstructions(List<IInstruction> instructions) {
        long loaderPointer = Memory32.TEXT_SECTION_START;
        for (IInstruction instruction : instructions) {
            byte[] serialized = instruction.serialize();
            memory.writeBytes(loaderPointer, serialized);
            loaderPointer += serialized.length;
        }
        programLength = loaderPointer - Memory32.TEXT_SECTION_START;
    }

    protected void loadData(List<IDataBlock> data) {
        long pointer = Memory32.DATA_SECTION_START;
        for (IDataBlock block : data) {
            if (pointer % block.alignment() != 0)
                pointer += block.alignment() - (pointer % block.alignment());
            byte[] value = block.value();
            memory.writeBytes(pointer, value);
            pointer += value.length;
        }
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
            IInstructionHandler<TInstruction> handler
    ) {
        super.addHandler(instructionClass, handler);
        return this;
    }
}
