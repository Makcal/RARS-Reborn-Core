package compilation.decoder;

import core.instruction.IInstruction;
import core.memory.IMemory;
import exceptions.execution.IllegalInstructionException;
import exceptions.memory.MemoryAccessException;

/**
 * A buffered decoder has a buffer of fixed size that takes memory and tries to decode an instruction from it.
 */
public interface IBufferedDecoder {
    /**
     * @return the number of bytes required to decode any instruction
     */
    int getBufferSize();

    IInstruction decodeNextInstruction(IMemory memory, long address) throws MemoryAccessException, IllegalInstructionException;
}
