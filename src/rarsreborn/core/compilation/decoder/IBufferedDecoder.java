package rarsreborn.core.compilation.decoder;

import rarsreborn.core.core.memory.IMemory;
import rarsreborn.core.exceptions.execution.IllegalInstructionException;
import rarsreborn.core.exceptions.memory.MemoryAccessException;

/**
 * A buffered decoder has a buffer of fixed size that takes memory and tries to decode an instruction from it.
 */
public interface IBufferedDecoder {
    /**
     * @return the number of bytes required to decode any instruction
     */
    int getBufferSize();

    DecodingResult decodeNextInstruction(IMemory memory, long address)
        throws MemoryAccessException, IllegalInstructionException;
}
